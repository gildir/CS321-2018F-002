
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;
import java.util.LinkedList;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;


/**
 *
 * @author Kevin
 */
public class GameCore implements GameCoreInterface {
    private final PlayerList playerList;
    private final Set<NPC> npcSet;
    private final Map map;

    private final Shop shop;

    private ArrayList<Battle> activeBattles; //Handles all battles for all players on the server.
    private ArrayList<Battle> pendingBattles;

    /**
     * Creates a new GameCoreObject. Namely, creates the map for the rooms in the game,
     *  and establishes a new, empty, player list.
     *
     * This is the main core that both the RMI and non-RMI based servers will interface with.
     */
     //now takes filename for Map
    public GameCore(String filename) {

        // Generate the game map. with the proper filename!
        map = new Map(this, filename);
        playerList = new PlayerList();

        shop = new Shop();

        npcSet = new HashSet<>();

        // Initialize starting NPCs
        npcSet.addAll(Arrays.asList(new Ghoul(this, "Ghoul1", 1, 20),
                                    new Ghoul(this, "Ghoul2", 3, 25)));

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

        activeBattles = new ArrayList<Battle>();
        pendingBattles = new ArrayList<Battle>();

        Thread objectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                Room room;
                Item object;
                Item[] objects = {new Item("Flower", 0.26, 1.5), new Item("Textbook", 4.8, 300), new Item("Phone", 0.03, 100), new Item("Newspaper", 0.06, 1)};

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
    * Broadcasts a message to the specified player.
    * @param sendingPlayer Player sending message
    * @param receivingPlayer Player receiving message
    * @param message Message to broadcast
    */
    public void broadcast(Player sendingPlayer, Player receivingPlayer, String message) {
        if(sendingPlayer != receivingPlayer) { //405_ignore, don't broadcast to players ignoring you
            receivingPlayer.getReplyWriter().println(message);
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

    //author Shayan AH
    public String listAllPlayers(String name)
    {
        Player player = this.playerList.findPlayer(name);
        String l = "Players in the world: ";
         if(player != null)
        {
            l += playerList.listOfPlayers();
            return l;
        }
        else
            {
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
    * Whispers "message" to a specified player.
    * @param name1 Name of player sending whisper
    * @param name2 Name of player receiving whisper
    * @param message Message to whisper
    * @return Message showing success.
    */
    public String whisper(String name1, String name2, String message) {
        Player playerSending = this.playerList.findPlayer(name1);
        Player playerReceiving = this.playerList.findPlayer(name2);

        if(playerSending != null && playerReceiving != null) {
            if(name1.equalsIgnoreCase(name2)){
                return "Cannot whisper yourself";
            }
            this.broadcast(playerSending, playerReceiving, playerSending.getName() + " whispers, \"" + message + "\"");
            playerReceiving.setLastWhisperName(name1);
            return "message sent to " + playerReceiving.getName();
        }
        else {
            if(playerReceiving == null) {
                return "Couldn't find player online.";
            }
            return null;
        }
    }

    /**
    * Sends a whisper the last player that whispered.
    * @param name Name of player replying to whisper
    * @param message Message to be whispered
    * @return Message showing success.
    */
    public String reply(String name, String message) {
        Player playerSending = this.playerList.findPlayer(name);
        if(playerSending.getLastWhisperName() == null) {
            return "You have not received a whisper to reply to.";
        }
        String name2 = playerSending.getLastWhisperName();
        Player playerReceiving = this.playerList.findPlayer(name2);
        return this.whisper(name, name2, message);
    }

    /**
     * Attempts to walk forward < distance > times.  If unable to make it all the way,
     *  a message will be returned.  Will display LOOK on any partial success.
     * @param name Name of the player to move
     * @return Message showing success.
     */

    public String move(String name, String direction) {
        Player player = this.playerList.findPlayer(name);
        if(player == null) {
            return null;
        }

        Room room;
        room = map.findRoom(player.getCurrentRoom());

        switch(direction.toUpperCase()){
          case "NORTH":
            player.setDirection(Direction.NORTH);
            break;
          case "EAST":
            player.setDirection(Direction.EAST);
            break;
          case "WEST":
            player.setDirection(Direction.WEST);
            break;
          case "SOUTH":
            player.setDirection(Direction.SOUTH);
            break;
          default:
            return "Please enter a valid direction. Valid directions are North, South, East, or West.";
        }
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
        return "You stop moving and begin to stand around again.";
    }

    /**
     * Attempts to enter <location> shop. Use if entering a room that is part of another
     * room, instead of using move to walk to a separate room
     * @param name Name of the player to enter
     * @param location The place to enter
     * @return Message showing success
     */
    public String enter(String name, String location) {
      Player player = this.playerList.findPlayer(name);
      if(player == null) return null;
      int newID;
      //add more if statements for different shops
      if(location.equalsIgnoreCase("shop"))
        newID = 172;
      else
        return location + " is unknown.";
      //if player not near a shop, return.
      if(player.getCurrentRoom() != 1)
        return "Not near " + location;
      Room room = this.map.findRoom(player.getCurrentRoom());
      this.broadcast(player, player.getName() + " has walked off towards the shop");
      player.getReplyWriter().println("You enter the shop");
      player.setCurrentRoom(newID);
      this.broadcast(player, player.getName() + " just walked into the shop.");
      player.getReplyWriter().println(this.map.findRoom(player.getCurrentRoom()).toString(this.playerList, player));
      shop.addPlayer(name);
      player.getReplyWriter().println(shop.displayShop());
      return "You stop moving and begin to stand around again.";
    }

    /**
     * Makes player leave a room e.g shop
     * @param name Player Name
     * @return Message showing success
     */
    public String leaveRoom(String name) {
      Player player = this.playerList.findPlayer(name);
      if(player == null) return null;
      int newID;
      //add more if statements for different shops
      if(player.getCurrentRoom() == 172)
        newID = 1;
      else
        return "Can't leave, did you mean quit?";
      Room room = this.map.findRoom(player.getCurrentRoom());
      this.broadcast(player, player.getName() + " has left the shop");
      player.getReplyWriter().println("You leave the room");
      player.setCurrentRoom(newID);
      this.broadcast(player, player.getName() + " just walked into the area.");
      player.getReplyWriter().println(this.map.findRoom(player.getCurrentRoom()).toString(this.playerList, player));
      shop.removePlayer(name);
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
            if(target.equals("all")){

              int obj_count = 0;
              Item object;
              String AllObjects = room.getObjects();
              while((object = room.getLastObject()) != null){
                if (player.getCurrentInventory().size() >= 10)
                {
                  room.addObject(object); //Adds the removed objecct back in
                  return "Could not pickup every object, there was not enough room in your inventory";
                }
                player.addObjectToInventory(object);
                obj_count++;
              }
              if(obj_count > 0)
                return "You bend over and pick up all the objects";
              else
                return "No objects in this room";
            }
            else{

              if (player.getCurrentInventory().size() >= 10)
              {
                  this.broadcast(player, player.getName() + " tried to pick something up, but was holding too many items.");
                  return "You try to pick up the " + target + ", but can't because you're holding too many items.";
              }
              Item object = room.removeObject(target);
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
        }
        else {
            return null;
        }
    }
    /**
     * Attempts to drop off an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to dropoff.
     * @return Message showing success.
     */
    public String dropoff(String name, String target) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            Item object = player.removeObjectFomInventory(target);
            Room room = map.findRoom(player.getCurrentRoom());
            if(object != null) {
                room.addObject(object);
                this.broadcast(player, player.getName() + " has dropped off a " + target + " from personal inventory.");
                return "You just dropped off a " + target + ".";
            }
            else {
                this.broadcast(player, player.getName() + " tried to drop off something, but doesn't seem to find what they were looking for.");
                return "You just tried to drop off a " + target + ", but you don't have one.";
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
            npcsFound = room.getNamesOfNpcs(npcSet);
            if (npcsFound != null){
                //checking to see if the ghoulName matches any ghouls in the same room
                for (int i = 0; i < npcsFound.size(); i++){
                    if (ghoulName.equalsIgnoreCase(npcsFound.get(i))){
                        return playerName + " POKED " + npcsFound.get(i) + "\n" + player.removeRandomItem();
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
        LinkedList<Item> playerIn = player.getCurrentInventory();
        //check if inventory is empty
        if (player.getCurrentInventory().isEmpty()){
            return "Inventory is empty.";
        }
        //check if player exists
        if (player != null){
            Room room = map.findRoom(player.getCurrentRoom());
            //find all the NPCs in the room that the player's in
            ArrayList<String> npcsFound = new ArrayList<>();
            npcsFound = room.getNamesOfNpcs(npcSet);
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
                        if (itemName.equalsIgnoreCase(playerIn.get(i).getItemName())){
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
     * Returns a list of nearby players you can gift
     * @param name Player Name
     * @return String representation of nearby players.
     */
    public String giftable(String playerName) {
        Player player = playerList.findPlayer(playerName);
        if(player != null) {
            // Find the room the player is in.
            Room room = this.map.findRoom(player.getCurrentRoom());

            // Return a string representation of players in teh same room
            String gift_list = "\nGiftable players near you: " + room.getPlayers(this.playerList);
            gift_list = gift_list.replace(playerName, "");
            return gift_list;
      }
      // No such player exists
      else {
            return null;
      }
    }

    public String money(String name) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            return player.viewMoney();
        }
        else {
            return null;
        }
    }
    @Override
    public String gift(String yourname ,String name, double amount){
        Player receiver = this.playerList.findPlayer(name);
        Player you = this.playerList.findPlayer(yourname);
        if(receiver != null){
          if(you.getMoney().sum() < amount){
           return "NOT ENOUGH MONEY!";
          }
            this.broadcast(you, you.getName() + " offers a gift to " + receiver.getName());
           //Scanner read = new Scanner(System.in);

            receiver.getReplyWriter().println("Accept gift? (y/n):");

           /*String input = read.nextLine();

           if(input.toLowerCase().equals("y")) {

            receiver.acceptMoney(you.giveMoney(you,receiver,amount));

           return "User accepted gift!";*/
            return "";
           //}
      }else{
            return "NO USER WITH THAT NAME";
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
     * Sell an item to the shop the player is currently in
     * @param playerName player who is selling
     * @param itemName item to sell
     * @return A string indicating success or failure
     */
 public String sell(String playerName, String itemName) {
  //format user input for item
  itemName = itemName.toLowerCase();
  itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
  //check if player not in shop or does not have item
  if(!shop.playerInShop(playerName)) {
   return "You cannot sell if you are not in a shop!";
  }
  Player player = this.playerList.findPlayer(playerName);
  if(player == null)
   return null;
  LinkedList<Item> inventory = player.getCurrentInventory();
  Item object = player.removeObjectFomInventory(itemName);
  if(object == null) {
      return "You do not have " + itemName + " in your inventory!";
  }
  else {
      //remove item from inventory, update player inventory, increase money
      //inventory.remove(itemName);
      player.setCurrentInventory(inventory);
      shop.sellItem(object);
      player.addMoney(object.getItemValue());
      player.getReplyWriter().println(shop.displayShop());
      return "You have sold " + itemName + " to the shop.";
  }
}
  /**
     * Logs a string into a file
     * @param fileName name of the file to log in
     * @param log      message to log
     */
    @Override
    public void log(String fileName, String log) {
        String PATH = "log";

        try {
            // Check if log directory exists
            File directory = new File(PATH);
            if (! directory.exists()) directory.mkdirs();

            // Check for file
            File file = new File(PATH + "/" + fileName);
            if (! file.exists()) file.createNewFile();

            // Write to file
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(log);
            bw.close();
        } catch (IOException ex){
            Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


//Rock Paper Scissors Battle Methods -------------------------------------------

  // Broadcasts to player2
  /*
  [Player1] challenges you to a Rock Paper Scissors Battle!
  Type 'Accept [Player1]' to accept the challenge.
  OR
  Type 'Refuse [Player1]' to refuse the challenge.
  */
  //Edge case: If one player challenges another but the other player challenges back, this is counted as an acceptance of battle in place of an 'accept [player1]'.
  //Checks if player2 exists, if not broadcasts to challenger "That player doesnt exist." and returns false.
  //if player2 does exist, broadcast to challenger "Request sent. You will be notified when they respond."
  public void challenge(String challenger, String player2)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);

    if(challenger.equalsIgnoreCase(player2))
    {
      play1.getReplyWriter().println("\nYou can't challenge yourself.\n");
      return;
    }

    if(play2 == null)//other player doesnt exist
    {
      play1.getReplyWriter().println("That player doesn't exist.");
    }
    else
    {
      for(Battle b : pendingBattles)
      {
        if(b.hasPlayers(challenger,player2))
        {
          play1.getReplyWriter().println("You already have a pending challenge request with "+ player2 +".");
          return;
        }
      }
      play2.getReplyWriter().println(challenger + " has challenged you to a Rock Paper Scissors Battle. \nTo accept, type 'Accept " + challenger + "' and press ENTER." + "\nTo decline, type 'Refuse " + challenger + "' and press ENTER." );
      pendingBattles.add(new Battle(challenger, player2));
      System.out.println("Player: " + challenger + " Challenged: " + player2);
    }
  }

  public void accept(String challenger, String player2)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);


    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player2))
      {
        play2.getReplyWriter().println("You're already in a Rock Paper Scissors challenge with someone. \nMake a choice of 'rock', 'paper', or 'scissors' \nand wait for the challenge to end before trying to accept another.\n");
        return;
      }
    }

    if(play1 == null)//other player doesnt exist
    {
      play2.getReplyWriter().println("You can't accept a challenge from a player that doesn't exist.");
    }
    else
    {
      for(Battle b : pendingBattles)
      {
        if(b.hasPlayers(challenger,player2) && b.getStatus().equalsIgnoreCase("pending"))
        {
          play1.getReplyWriter().println(player2 + " has accepted your Rock Paper Scissors challenge. \nType 'rock' to choose rock.\nType 'paper' to chose paper.\nType 'scissors' to choose scissors.");
          play2.getReplyWriter().println("You have accepted " + challenger + "'s Rock Paper Scissors challenge." + "\nType 'rock' to choose rock.\nType 'paper' to chose paper.\nType 'scissors' to choose scissors.");
          Battle temp = b;
          temp.setStatus("active");
          activeBattles.add(temp);
          pendingBattles.remove(b);
          return;
        }
      }
      play2.getReplyWriter().println("You don't have any pending requests from that player.");
    }
    System.out.println("Player: " + player2 + " Accepted " + challenger + "'s' Challenge.");
  }

  public void refuse(String challenger, String player2)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);

    if(play1 == null)//other player doesnt exist
    {
      play2.getReplyWriter().println("You can't refuse a challenge from a player that doesn't exist.");
    }
    else
    {
      for(Battle b : pendingBattles)
      {
        if(b.hasPlayers(challenger,player2) && b.getStatus().equalsIgnoreCase("pending"))
        {
          play1.getReplyWriter().println(player2 + " has refused your Rock Paper Scissors Challenge. :(");
          play2.getReplyWriter().println("You have refused "+challenger+"'s Rock Paper Scissors Challenge.");
          pendingBattles.remove(b);
          return;
        }
      }
      play2.getReplyWriter().println("You don't have any pending requests from that player.");
    }
    System.out.println("Player: " + player2 + " Refused " + challenger + "'s' Challenge.");
  }

  public void rock(String player)
  {
    Player p = this.playerList.findPlayer(player);
    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player))
      {
        if(b.getPlayer1().equalsIgnoreCase(player))
        {
          b.setChoiceP1(1);
          p.getReplyWriter().println("You Chose Rock.\n");
          if((b.getChoiceP1() != 0) && (b.getChoiceP2() != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b);
          }
          return;
        }
        if(b.getPlayer2().equalsIgnoreCase(player))
        {
          b.setChoiceP2(1);
          p.getReplyWriter().println("You Chose Rock.\n");
          if((b.getChoiceP1() != 0) && (b.getChoiceP2() != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b);
          }
          return;
        }
      }
    }
    p.getReplyWriter().println("You aren't in any Rock Paper Scissors Battles currently.");
  }

  public void paper(String player)
  {
    Player p = this.playerList.findPlayer(player);
    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player))
      {
        if(b.getPlayer1().equalsIgnoreCase(player))
        {
          b.setChoiceP1(2);
          p.getReplyWriter().println("You Chose Paper.\n");
          if((b.getChoiceP1() != 0) && (b.getChoiceP2() != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b);
          }
          return;
        }
        if(b.getPlayer2().equalsIgnoreCase(player))
        {
          b.setChoiceP2(2);
          p.getReplyWriter().println("You Chose Paper.\n");
          if((b.getChoiceP1() != 0) && (b.getChoiceP2() != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b);
          }
          return;
        }
      }
    }
    p.getReplyWriter().println("You aren't in any Rock Paper Scissors Battles currently.");
  }

  public void scissors(String player)
  {
    Player p = this.playerList.findPlayer(player);
    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player))
      {
        if(b.getPlayer1().equalsIgnoreCase(player))
        {
          b.setChoiceP1(3);
          p.getReplyWriter().println("You Chose Scissors.\n");
          if((b.getChoiceP1() != 0) && (b.getChoiceP2() != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b);
          }
          return;
        }
        if(b.getPlayer2().equalsIgnoreCase(player))
        {
          b.setChoiceP2(3);
          p.getReplyWriter().println("You Chose Scissors.\n");
          if((b.getChoiceP1() != 0) && (b.getChoiceP2() != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b);
          }
          return;
        }
      }
    }
    p.getReplyWriter().println("You aren't in any Rock Paper Scissors Battles currently.");
  }

  public void doBattle(String challenger, String player2, int p1, int p2, Battle b)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);
    String message = "";
    if(p1 == p2)
    {
      //tie
      switch(p1)
      {
        case 1:
          play1.getReplyWriter().println("You both chose Rock. The match is a tie!\n");
          play2.getReplyWriter().println("You both chose Rock. The match is a tie!\n");
          message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \nIt was a tie.\n";
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          return;
        case 2:
          play1.getReplyWriter().println("You both chose Paper. The match is a tie!\n");
          play2.getReplyWriter().println("You both chose Paper. The match is a tie!\n");
          message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \nIt was a tie.\n";
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          return;
        case 3:
          play1.getReplyWriter().println("You both chose Scissors. The match is a tie!\n");
          play2.getReplyWriter().println("You both chose Scissors. The match is a tie!\n");
          message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \nIt was a tie.\n";
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          return;
      }
    }
    else if(p1 == 1 && p2 == 2)
    {
      //rock paper
      play1.getReplyWriter().println("You chose Rock. " + player2 + " chose Paper. \nYou lose.\n");
      play2.getReplyWriter().println("You chose Paper. " + challenger + " chose Rock. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(challenger, player2, "Rock", "Paper", player2 + " winning");

	  // Added by Brendan
	  this.leaderboard.incrementScore(play2.getName());

      return;
    }
    else if(p1 == 1 && p2 == 3)
    {
      //rock scissors
      play1.getReplyWriter().println("You chose Rock. " + player2 + " chose Scissors. \nYou win.\n");
      play2.getReplyWriter().println("You chose Scissors. " + challenger + " chose Rock. \nYou lose.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(challenger, player2, "Rock", "Scissors", challenger + " winning");

	  // Added by Brendan
	  this.leaderboard.incrementScore(play1.getName());

      return;
    }
    else if(p1 == 2 && p2 == 1)
    {
      //paper rock
      play1.getReplyWriter().println("You chose Paper. " + player2 + " chose Rock. \nYou win.\n");
      play2.getReplyWriter().println("You chose Rock. " + challenger + " chose Paper. \nYou lose.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(challenger, player2, "Paper", "Rock", challenger + " winning");

	  // Added by Brendan
	  this.leaderboard.incrementScore(play1.getName());

      return;
    }
    else if(p1 == 2 && p2 == 3)
    {
      //paper scissors
      play1.getReplyWriter().println("You chose Paper. " + player2 + " chose Scissors. \nYou lose.\n");
      play2.getReplyWriter().println("You chose Scissors. " + challenger + " chose Paper. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(challenger, player2, "Paper", "Scissors", player2 + " winning");

	  // Added by Brendan
	  this.leaderboard.incrementScore(play2.getName());

      return;
    }
    else if(p1 == 3 && p2 == 1)
    {
      //scissors rock
      play1.getReplyWriter().println("You chose Scissors. " + player2 + " chose Rock. \nYou lose.\n");
      play2.getReplyWriter().println("You chose Rock. " + challenger + " chose Scissors. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(challenger, player2, "Scissors", "Rock", player2 + " winning");

	  // Added by Brendan
	  this.leaderboard.incrementScore(play2.getName());

      return;
    }
    else if(p1 == 3 && p2 == 2)
    {
      //scissors paper
      play1.getReplyWriter().println("You chose Scissors. " + player2 + " chose Paper. \nYou win.\n");
      play2.getReplyWriter().println("You chose Paper. " + challenger + " chose Scissors. \nYou lose.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);;
      activeBattles.remove(b);
      writeLog(challenger, player2, "Scissors", "Paper", challenger + " winning");

	  // Added by Brendan
	  this.leaderboard.incrementScore(play1.getName());

      return;
    }
  }

    public void writeLog(String play1, String play2, String p1, String p2, String winner)
    {
         try(BufferedWriter writer = new BufferedWriter(new FileWriter("battlelog.txt",true)))
         {
             String str = play1 + " Challenged " + play2 + " picking " + p1 + " against " + p2 + " resulting in " + winner + "\r\n\n";
             writer.write(str);
             writer.close();
         }
         catch(IOException e) {}
    }
//Rock Paper Scissors Battle Methods -------------------------------------------

	// Added by Brendan
    public void checkBoard(String name) {
        Player player = this.playerList.findPlayer(name);
        if(player == null)
            return;
		String board = this.leaderboard.getBoard();
        player.getReplyWriter().println(board);
    }

  public String tutorial(String name)
  {
      Player player = this.playerList.findPlayer(name);
      String message = "";
      if(player != null) {
          message += ("\\  \\     / /__| | ___  ___  _ __ ___   ___ \n" +
                  " \\ \\ /\\ / / _ \\ |/ _  / _ \\| '_ ` _ \\ / _ \\ \n" +
                  "  \\ V  V /  __/ | (_ | (_) | | | | | |  __/\n" +
                  "   \\_/\\_/ \\___|_|\\___ \\___/|_| |_| |_|\\___|\n");
          message += ("This is your Rock-Paper-Scissors Tutorial with me, the Professor.\n" +
                  "This is the basic rock paper scissors game that everyone knows and loves. Two players each pick one of rock, paper, and scissors.\n" +
                  "Rock beats scissors, scissors beats paper, paper beats rock, and a mirror matchup is always a tie.\n\n" +
                  "\t\t\t How to Play:\n" +
                  "If you want to play someone, you have to challenge them.\n" +
                  "You can challenge someone by using the 'CHALLENGE' command and entering the name of the player you wish to challenge.\n" +
                  "In order to help you challenge someone, you can see the list of names of players in the same room as you and pick one as the player to challenge.\n" +
                  "If you enter a name that does not belong to any player or belongs to a player that isn't in the same room as you, you will be prompted to enter another command.\n" +
                  "For example, if you're 'p1' and you see someone named 'p2' that you want to challenge, entering 'CHALLENGE p2' will send p2 a challenge.\n" +
                  "If you enter 'CHALLENGE p3' instead and there is nobody with the name 'p3', you'll be prompted to enter another command.\n" +
                  "Likewise, if there is a 'p3' but he's in a different room, you'll be prompted to enter another command\n" +
                  "\nIf you get challenged by someone else, you can either accept or refuse the challenge request.\n" +
                  "In order to accept, you have to enter 'ACCEPT playername' where playername is the name of the person that challenged you.\n" +
                  "In order to refuse, you have to enter 'REFUSE playername' where playername is the name of the person that challenged you.\n" +
                  "For example, if you get challenged by Bob, you can accept by entering 'ACCEPT Bob' or refuse by entering 'REFUSE Bob'");
      }
      return message;
  }
}
