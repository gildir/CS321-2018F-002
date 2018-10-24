

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;

/**
 *
 * @author Kevin
 */
public class GameCore implements GameCoreInterface {
    private final PlayerList playerList;
    private final Map map;
    private final Set<NPC> npcSet;

    /**
     * Creates a new GameCoreObject. Namely, creates the map for the rooms in the game,
     *  and establishes a new, empty, player list.
     * 
     * This is the main core that both the RMI and non-RMI based servers will interface with.
     */
    public GameCore() {
        
        // Generate the game map.
        map = new Map();
        playerList = new PlayerList();
        npcSet = new HashSet<>();

        // Initialize starting NPCs
        npcSet.addAll(Arrays.asList(new Ghoul(this, "Ghoul1", 1, 14),
                                    new Ghoul(this, "Ghoul2", 2, 14),
                                    new Ghoul(this, "Ghoul3", 3, 14)));

        Thread npcThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    synchronized (npcSet) {
                        for (NPC npc : npcSet)
                            npc.tryAi();
                    }
                }
            }
        });
        npcThread.setDaemon(true);
        npcThread.start();
        
        Thread objectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                Room room;
                String object;
                String[] objects = {"Flower", "Textbook", "Phone", "Newspaper"};
                while(true) {
                    try {
                        Thread.sleep(rand.nextInt(60000));
                        object = objects[rand.nextInt(objects.length)];
                        room = map.randomRoom();
                        room.addObject(object);
                        
                        GameCore.this.broadcast(room, "You see a student rush past and drop a " + object + " on the ground.");

                    } catch (InterruptedException ex) {
                        Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        objectThread.setDaemon(true);
        objectThread.start();

    }

    /**
     * Basic getter methods for GameCore.
     */ 
    public PlayerList getPlayerList(){
      return this.playerList;
    }

    public Map getMap(){
      return this.map;
    }

    public Set<NPC> getNpcSet() {
        return npcSet;
    }

    /**
     * Broadcasts a message to all other players in the same room as player.
     * @param player Player initiating the action.
     * @param message Message to broadcast.
     */   
    @Override
    public void broadcast(Player player, String message) {
        for(Player otherPlayer : this.playerList) {
            if(otherPlayer != player && otherPlayer.getCurrentRoom() == player.getCurrentRoom()) {
                otherPlayer.getReplyWriter().println(message);
            }
        }
    }
  
    /**
     * Broadcasts a message to all players in the specified room.
     * @param room Room to broadcast the message to.
     * @param message Message to broadcast.
     */   
    @Override
    public void broadcast(Room room, String message) {
        for(Player player : this.playerList) {
            if(player.getCurrentRoom() == room.getId()) {
                player.getReplyWriter().println(message);
            }
        }
    }
    
    /**
     * Returns the player with the given name or null if no such player.
     * @param name Name of the player to find.
     * @return Player found or null if none.
     */
    @Override
    public Player findPlayer(String name) {
        for(Player player : this.playerList) {
            if(player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * Allows a player to join the game.  If a player with the same name (case-insensitive)
     *  is already in the game, then this returns false.  Otherwise, adds a new player of 
     *  that name to the game.  The next step is non-coordinated, waiting for the player
     *  to open a socket for message events not initiated by the player (ie. other player actions)
     * @param name
     * @return Player is player is added, null if player name is already registered to someone else
     */
    @Override
    public Player joinGame(String name) {
        // Check to see if the player of that name is already in game.
        Player newPlayer;
        if(this.playerList.findPlayer(name) == null) {
            // New player, add them to the list and return true.
            newPlayer = new Player(name);
            this.playerList.addPlayer(newPlayer);
            
            // New player starts in a room.  Send a message to everyone else in that room,
            //  that the player has arrived.
            this.broadcast(newPlayer, newPlayer.getName() + " has arrived.");
            return newPlayer;
        }
        // A player of that name already exists.
        return null;
    }
   
    /**
     * Returns a look at the area of the specified player.
     * @param playerName Player Name
     * @return String representation of the current area the player is in.
     */
    @Override
    public String look(String playerName) {
        Player player = playerList.findPlayer(playerName);

        if(player != null) {        
            // Find the room the player is in.
            Room room = this.map.findRoom(player.getCurrentRoom());

            // Send a message to all other players in the room that this player is looking around.
            this.broadcast(player, player.getName() + " takes a look around.");

            // Return a string representation of the room state.
            return room.toString(this.playerList, player);
        }
        // No such player exists
        else {
            return null;
        }
    }        
   
    /**
     * Turns the player left.
     * @param name Player Name
     * @return String message of the player turning left.
     */
    @Override
    public String left(String name) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            // Compel the player to turn left 90 degrees.
            player.turnLeft();
            
            // Send a message to every other player in the room that the player has turned left.
            this.broadcast(player, player.getName() + " turns to the left.");
            
            // Return a string back to the calling function with an update.
            return "You turn to the left to face " + player.getCurrentDirection();
        }
        else {
            return null;
        }
    }
    
    /**
     * Turns the player right.
     * @param name Player Name
     * @return String message of the player turning right.
     */
    @Override
    public String right(String name) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            // Compel the player to turn left 90 degrees.
            player.turnRight();
            
            // Send a message to every other player in the room that the player has turned right.
            this.broadcast(player, player.getName() + " turns to the right.");
            
            // Return a string back to the calling function with an update.
            return "You turn to the right to face " + player.getCurrentDirection();
        }
        else {
            return null;
        }
    }    
    
    /**
     * Says "message" to everyone in the current area.
     * @param name Name of the player to speak
     * @param message Message to speak
     * @return Message showing success.
     */
    @Override
    public String say(String name, String message) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            this.broadcast(player, player.getName() + " says, \"" + message + "\"");
            return "You say, \"" + message + "\"";
        }
        else {
            return null;
        }
    }  
    
    /**
     * Attempts to walk forward < distance > times.  If unable to make it all the way,
     *  a message will be returned.  Will display LOOK on any partial success.
     * @param name Name of the player to move
     * @param distance Number of rooms to move forward through.
     * @return Message showing success.
     */
    public String move(String name, int distance) {
        Player player = this.playerList.findPlayer(name);
        if(player == null || distance <= 0) {
            return null;
        }
        Room room;
        while(distance-- != 0) {
            room = map.findRoom(player.getCurrentRoom());
            if(room.canExit(player.getDirection())) {
                this.broadcast(player, player.getName() + " has walked off to the " + player.getCurrentDirection());
                player.getReplyWriter().println(room.exitMessage(player.getDirection()));
                player.setCurrentRoom(room.getLink(player.getDirection()));
                this.broadcast(player, player.getName() + " just walked into the area.");
                player.getReplyWriter().println(this.map.findRoom(player.getCurrentRoom()).toString(playerList, player));
            }
            else {
                player.getReplyWriter().println(room.exitMessage(player.getDirection()));
                return "You grumble a little and stop moving.";
            }
        }
        return "You stop moving and begin to stand around again.";
    }
    
    /**
     * Attempts to pick up an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to pickup.
     * @return Message showing success. 
     */    
    public String pickup(String name, String target) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            Room room = map.findRoom(player.getCurrentRoom());
            String object = room.removeObject(target);
            if(object != null) {
                player.addObjectToInventory(object);
                this.broadcast(player, player.getName() + " bends over to pick up a " + target + " that was on the ground.");
                return "You bend over and pick up a " + target + ".";
            }
            else {
                this.broadcast(player, player.getName() + " bends over to pick up something, but doesn't seem to find what they were looking for.");
                return "You look around for a " + target + ", but can't find one.";
            }
        }
        else {
            return null;
        }
    }

    /**
     * Player pokes a ghoul that is in the same room.
     * @param playerName Name of the player that pokes the ghoul.
     * @param ghoulName Name of the ghoul that is poked
     * @return Message showing success or failure of poke action.
     */
    public String pokeGhoul(String playerName, String ghoulName) {
        Player player = this.playerList.findPlayer(playerName);
        ArrayList<String> npcsFound = new ArrayList<>();
        //check if player exists
        if (player != null){
            Room room = map.findRoom(player.getCurrentRoom());
            //find all the NPCs in the room that the player's in
            npcsFound = room.getLocalNPC(npcSet);
            if (npcsFound != null){
                //checking to see if the ghoulName matches any ghouls in the same room
                for (int i = 0; i < npcsFound.size(); i++){
                    if (ghoulName.equalsIgnoreCase(npcsFound.get(i))){
                        return playerName + " POKED " + npcsFound.get(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Player gifts a ghoul that is in the same room an object. This action decreases the ghoul's aggression.
     * @param playerName Name of the player that gifts the ghoul.
     * @param ghoulName Name of the ghoul to give the item to.
     * @param itemName Name of the item to give to the ghoul.
     * @return Message showing success or failure of the gifting action.
     */


    public String giftGhoul(String playerName, String ghoulName, String itemName) {

        Player player = this.playerList.findPlayer(playerName);
        boolean ghoulNotFound = true;
        LinkedList<String> playerIn = player.getCurrentInventory();
        //check if inventory is empty
        if (player.getCurrentInventory().isEmpty()){
            return "Inventory is empty.";
        }
        //check if player exists
        if (player != null){
            Room room = map.findRoom(player.getCurrentRoom());
            //find all the NPCs in the room that the player's in
            ArrayList<String> npcsFound = new ArrayList<>();
            npcsFound = room.getLocalNPC(npcSet);
            if (npcsFound != null){
                //checking to see if the ghoulName matches any ghouls in the same room
                for (int i = 0;(i < npcsFound.size() && ghoulNotFound); i++){
                    if (ghoulName.equalsIgnoreCase(npcsFound.get(i))){
                        i = 9999;
                        ghoulNotFound = false;
                    }
                }
                if (!ghoulNotFound){
                    //check if the player has the object in their inventory
                    for (int i = 0; i < playerIn.size(); i++){
                        if (itemName.equalsIgnoreCase(playerIn.get(i))){
                            playerIn.remove(i);
                            player.setCurrentInventory(playerIn);//updating the inventory
                            return playerName + " gifted " + ghoulName + " a " + itemName;
                        }
                    }
                    return "Player doesn't have a " + itemName + " in their inventory.";
                }
                else{
                    return ghoulName + " is not in the same room as you.";
                }
            }
        }
        return null;
    }

    /**
     * Returns a string representation of all objects you are carrying.
     * @param name Name of the player to move
     * @return Message showing success.
     */    
    @Override
    public String inventory(String name) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            this.broadcast(player, "You see " + player.getName() + " looking through their pockets.");
            return "You look through your pockets and see" + player.viewInventory();
        }
        else {
            return null;
        }
    }    

     /**
     * Leaves the game.
     * @param name Name of the player to leave
     * @return Player that was just removed.
     */    
    @Override
    public Player leave(String name) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            this.broadcast(player, "You see " + player.getName() + " heading off to class.");
            this.playerList.removePlayer(name);
            return player;
        }
        return null;
    }


    
    /**
     * Ghoul "drags" player.
     * @param name Name of the player to "drag"
     * @return void.
     */
     /*  
    public void dragPlayer(int currentRoom) {
       
        // Find the room the Ghoul is in.
        Room room = this.map.findRoom(Ghoul.getCurrentRoom());

            // Send a message to all other players in the room that this player is looking around.
            this.broadcast(player, player.getName() + " takes a look around.");

            // Return a string representation of the room state.
            return room.toString(this.playerList, player);
        }
        // No such player exists
        else {
            return null;
        }

    }
    */
}
