


import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class GameCore implements GameCoreInterface {
    private final PlayerList playerList;
    private final Map map;
    
    /**
     * Creates a new GameCoreObject.  Namely, creates the map for the rooms in the game,
     *  and establishes a new, empty, player list.
     * 
     * This is the main core that both the RMI and non-RMI based servers will interface with.
     */
    public GameCore() {
        
        // Generate the game map.
        map = new Map();
        
        playerList = new PlayerList();
        
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
     * Broadcasts a message to all other players in the same room as player.
     * @param player Player initiating the action.
     * @param message Message to broadcast.
     */   
    @Override
    public void broadcast(Player player, String message) {
        for(Player otherPlayer : this.playerList) {
            if(otherPlayer != player && otherPlayer.getCurrentRoom() == player.getCurrentRoom()
			    && !player.searchIgnoredBy( otherPlayer.getName() )) {	// 405_ignore, don't broadcast to players ignoring you
                otherPlayer.getReplyWriter().println(message);
            }
        }
    }

    /**
    * Broadcasts a message to all players in the world.
    * @param player Player shouting the message
    * @param message Message to broadcast
    */
    public void broadcastShout(Player player, String message) {
        for(Player otherPlayer : this.playerList) {
            if(otherPlayer != player && !player.searchIgnoredBy( otherPlayer.getName())) {
                otherPlayer.getReplyWriter().println(message);
            }
        }
    }

    /**
    * Broadcasts a message to the specified player.
    * @param sendingPlayer Player sending message
    * @param receivingPlayer Player receiving message
    * @param message Message to broadcast
    */
    @Override
    public void broadcast(Player sendingPlayer, Player receivingPlayer, String message) {
        if(sendingPlayer != receivingPlayer
			&& !sendingPlayer.searchIgnoredBy( receivingPlayer.getName() )) { //405_ignore, don't broadcast to players ignoring you
            receivingPlayer.getReplyWriter().println(message);
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
    * Shouts "message" to everyone in the world.
    * @param name Name of the player shouting
    * @param message Message that will be shouted
    * @return Message showing success.
    */
    public String shout(String name, String message) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            this.broadcastShout(player, player.getName() + " shouts, \"" + message + "\"");
            return "You shout, \"" + message + "\"";
        }
        else {
            return null;
        }
    }

    //author Shayan AH
    public String listAllPlayers(String name)
    {
        Player player = this.playerList.findPlayer(name);
        String l = "Players in the world: ";

        if(player != null)
        {
            l += playerList.toString();
            return l;
        }
        else
        {
            return null;
        }
    }
    /**
    * Whispers "message" to a specified player.
    * @param name1 Name of player sending whisper
    * @param name2 Name of player receiving whisper
    * @param message Message to whisper
    * @return Message showing success.
    */
    @Override
    public String whisper(String name1, String name2, String message)
    {
        Player playerSending = this.playerList.findPlayer(name1);
        Player playerReceiving = this.playerList.findPlayer(name2);
        if(playerSending != null && playerReceiving != null)
        {
            if(name1.equalsIgnoreCase(name2))
                return "You cannot whisper yourself";
            else
            {
                this.broadcast(playerSending, playerReceiving, playerSending.getName() + " whispers, \"" + message + "\"");
                return "message sent to " + playerReceiving.getName();
            }
        }
        else
        {
            if(playerReceiving == null)
                return "That player isn't online.";
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

    /* START 405_ignore */
    /**
     * Update ignore and ignoredBy lists, depending on player
     * @param name Name of player committing ignore
     * @param name Name of player being ignored
     * @return Message showing succiess/failure
     */
    @Override
    public String ignore(String name, String ignoreName) {
		if( name.equalsIgnoreCase(ignoreName) )
			return "You can't ignore yourself.";
	
		//verify player being ignored exists
		Player ignoredPlayer = this.playerList.findPlayer(ignoreName);
		if( ignoredPlayer == null )
			return "Player " + ignoreName + "is not in the game.";
	
		Player thisPlayer = this.playerList.findPlayer(name);
		//verify player is not already in ignore list
		if( thisPlayer.searchIgnoreList(ignoreName) )
			return "Player " + ignoreName + " is in ignored list.";

		//add ignoreName to ignore list
		thisPlayer.ignorePlayer(ignoreName);

		//add ignoring player to ignored players ignoredBy list
		ignoredPlayer.addIgnoredBy(name);
		return ignoreName + " added to ignore list.";
    }  

    /* STOP 405_ignore */
    public String listIgnoredPlayers(String name)
    {
        Player player = this.playerList.findPlayer(name);
        String l = "Ignored Players: ";

        if(player != null)
        {
            l += player.showIgnoreList();
            return l;
        }
        else
        {
            return null;
        }
    }
    /* STOP 405_ignore */	


    /* START 408_ignore */
    /**
     * Update ignore and ignoredBy lists, depending on player
     * @param name Name of player committing unignore
     * @param name Name of player being unignored
     * @return Message showing success/failure
     */
    @Override
    public String unIgnore(String name, String unIgnoreName) {
		if( name.equalsIgnoreCase(unIgnoreName) )
			return "You can't unignore yourself since you can't ignore yourself...";
	
		//verify player being unignored exists
		Player unIgnoredPlayer = this.playerList.findPlayer(unIgnoreName);
		if( unIgnoredPlayer == null )
			return "Player " + unIgnoreName + "is not in the game.";
	
		Player thisPlayer = this.playerList.findPlayer(name);

		//verify player is in Ignore list
		if( !thisPlayer.searchIgnoreList(unIgnoreName) )
			return "Player " + unIgnoreName + " is not in ignored list.";

		//remove ignoreName in ignore list
		thisPlayer.unIgnorePlayer(unIgnoreName);

		//add ignoring player to ignored players ignoredBy list
		unIgnoredPlayer.removeIgnoredBy(name);
		return unIgnoreName + " removed from ignore list.";
    }  
    /* STOP 408_ignore */	
}
