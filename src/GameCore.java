
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.text.*;

/**
 *
 * @author Kevin
 */
public class GameCore implements GameCoreInterface {
    private final PlayerList playerList;
    private final NPCList npclist;
    private final NPCList nighttimeNpcList;
    private final List<Spirit> daySpirits;
    private final Map map;
    //Specifies a minimum and maximum amount of time until next item spawn
    private final int minimumSpawnTime=100, maximumSpawnTime=600;
    
    private final GiftsTracker giftsTracker;

    //Prefix that will help distinguish player chat from anything else
    private String chatPrefix;

    private final Shop shop;
    Date date;

    public static final boolean DAY = true;
    public static final boolean NIGHT = false;
    private boolean timeOfDay;
    private static final int LENGTH_OF_HALF_DAY_MILLISECONDS = 900000; // 900000 ms = 15 minutes

    private static final int NUM_OF_GHOULS = 8;
    private static final int GHOUL_AI_PERIOD_SECONDS_BASE = 25;
    private static final int NUM_OF_GHOSTS = 8;
    private static final int GHOST_AI_PERIOD_SECONDS_BASE = 20;

    private ArrayList<Battle> activeBattles; //Handles all battles for all players on the server.
    private ArrayList<Battle> pendingBattles;
    private Leaderboard leaderboard;

    private GroupChatTracker groupChatTracker;  //416_GroupChat


    private static Timer titleTimer;

    private static Timer bottleTimer;

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
        
        giftsTracker = new GiftsTracker();

        npclist = new NPCList();
        nighttimeNpcList = new NPCList();
        daySpirits = new ArrayList<Spirit>();
        groupChatTracker = new GroupChatTracker();  //416_GroupChat

        // Initialize Ghouls
        for (int i = 1; i <= NUM_OF_GHOULS; i++) {
            npclist.addNPC(new Ghoul(this, "Ghoul" + i, i * 2, GHOUL_AI_PERIOD_SECONDS_BASE + i));
            nighttimeNpcList.addNPC(new Ghoul(this, "Ghoul" + (i + NUM_OF_GHOULS), i * 2 + 1, GHOUL_AI_PERIOD_SECONDS_BASE + i));
        }
        // Initialize Ghosts
        for (int i = 1; i <= NUM_OF_GHOSTS; i++) {
            npclist.addNPC(new Ghost(this, "Ghost" + i, i * 2 + 1, GHOST_AI_PERIOD_SECONDS_BASE + i, new File("GhostSayings.txt")));
            nighttimeNpcList.addNPC(new Ghost(this, "Ghost" + (i + NUM_OF_GHOULS), i * 2, GHOST_AI_PERIOD_SECONDS_BASE + i, new File("GhostSayings.txt")));
        }

        nighttimeNpcList.addNPC(new Spirit(this, "SpookySpirit", 1, 45, Spirits.SPOOKY));
        nighttimeNpcList.addNPC(new Spirit(this, "AngrySpirit", 2, 31, Spirits.ANGRY));
        nighttimeNpcList.addNPC(new Spirit(this, "SadSpirit", 3, 15, Spirits.SAD));
        nighttimeNpcList.addNPC(new Spirit(this, "RudeSpirit", 4, 5, Spirits.RUDE));
        nighttimeNpcList.addNPC(new Spirit(this, "SickSpirit", 5, 10, Spirits.SICK));
        nighttimeNpcList.addNPC(new Spirit(this, "TiredSpirit", 6, 2, Spirits.TIRED));

        daySpirits.addAll(Arrays.asList(
                            new Spirit(this, "HappySpirit", 1, 20, Spirits.HAPPY),
                            new Spirit(this, "SadSpirit", 2, 25, Spirits.SAD),
                            new Spirit(this, "SarcasticSpirit", 3, 12, Spirits.SARCASTIC),
                            new Spirit(this, "ShySpirit", 4, 15, Spirits.SHY),
                            new Spirit(this, "FunnySpirit", 5, 14, Spirits.FUNNY),
                            new Spirit(this, "ClumsySpirit", 6, 23, Spirits.CLUMSY),
                            new Spirit(this, "StrongSpirit", 7, 10, Spirits.STRONG),
                            new Spirit(this, "NiceSpirit", 8, 15, Spirits.NICE),
                            new Spirit(this, "CrazySpirit", 9, 29, Spirits.CRAZY),
                            new Spirit(this, "SillySpirit", 10, 11, Spirits.SILLY),
                            new Spirit(this, "StupidSpirit", 11, 8, Spirits.STUPID),
                            new Spirit(this, "SmartSpirit", 12, 20, Spirits.SMART),
                            new Spirit(this, "MessySpirit", 13, 6, Spirits.MESSY),
                            new Spirit(this, "HungrySpirit", 14, 19, Spirits.HUNGRY),
                            new Spirit(this, "LovingSpirit", 15, 25, Spirits.LOVING)));

        Thread npcThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    synchronized (npclist) {
                        for (NPC npc : npclist)
                            npc.tryAi();
                    }
                }
            }
        });
        npcThread.setDaemon(true);
        npcThread.start();

        activeBattles = new ArrayList<Battle>();
        pendingBattles = new ArrayList<Battle>();
        this.leaderboard = new Leaderboard();
        Thread objectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                Room room;
                Item object;
                ArrayList<Item> objects = ItemParser.parse("./ItemListCSV.csv");
                while(true) {
                    try {
   //wait a random amount of time spawn another item
                       Thread.sleep((int)(Math.random()*(maximumSpawnTime+1))+minimumSpawnTime);
                        object = (Item)objects.get(rand.nextInt(objects.size())).clone();
                        room = map.randomRoom();
                        room.addObject(object);
                        
                        GameCore.this.broadcast(room, "You see a student rush past and drop a " + object.getItemName() + " on the ground.");

                    } catch (InterruptedException ex) {
                        Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        objectThread.setDaemon(true);
        objectThread.start();

        Thread daySpiritsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                Room room;
                Spirit spirit;
                while(true) {
                    try {
                      Thread.sleep((int)(Math.random()*(5000))+minimumSpawnTime);
                        spirit = (Spirit)daySpirits.get(rand.nextInt(daySpirits.size()));
                        room = map.randomRoom();
                        room.addSpirit(spirit);

                        GameCore.this.broadcast(room, "A wild " + spirit + " has appeared!");

                    } catch (InterruptedException ex) {
                        Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        daySpiritsThread.setDaemon(true);
        daySpiritsThread.start();


        timeOfDay = DAY;
        Thread dayNightCycleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep((LENGTH_OF_HALF_DAY_MILLISECONDS));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (timeOfDay == DAY) {
                        timeOfDay = NIGHT;
                        broadcastGlobal("Darkness falls on the world, be wary of monsters at night.");
                        synchronized (npclist) {
                            npclist.addAllNPC(nighttimeNpcList);
                        }
                    } else {
                        timeOfDay = DAY;
                        broadcastGlobal("The sun rises, illuminating the sky. Many monsters shriek and flee.");
                        synchronized (npclist) {
                            npclist.removeAllNPC(nighttimeNpcList);
                        }
                    }
                }
            }
        });
        dayNightCycleThread.setDaemon(true);
        dayNightCycleThread.start();

      date = new Date();
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
    public ArrayList<Battle> getActiveBattles(){
      return activeBattles;
    }
     public ArrayList<Battle> getPendingBattles(){
      return pendingBattles;
    }
    public NPCList getNpcList() {
        return npclist;
    }

    public List<Spirit> getDaySpirits() {
        return daySpirits;
    }

    public void setChatPrefix(String prefix) {
      this.chatPrefix = prefix;
    }

    /**
    * Changes the chat prefix to the new prefix specified by the player.
    * @param prefix New chat prefix to be set.
    * @return Returns message saying whether the prefix was successfully changed or not.
    */
    public String changeChatPrefix(String prefix) {
      if(prefix.length() != 3) {
        return "Prefix can only be 3 characters.";
      }
      try {
        FileWriter chatConfig = new FileWriter("chatConfig.txt");
        chatConfig.write(prefix);
        chatConfig.close();
        this.setChatPrefix(prefix);
      }
      catch(IOException e) {
        System.err.println("Couldn't save new chat prefix.");
      }
      return "Prefix set successfully.";
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
                          && !player.searchIgnoredBy( otherPlayer.getName() )) { // 405_ignore, don't broadcast to players ignoring you
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
     * Broadcast a message to all players in the game.
     * @param message to send.
     */
    public void broadcastGlobal(String message) {
        for (Player player : playerList)
            player.broadcast(message);
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
            newPlayer = new Player(this, name);
            this.playerList.addPlayer(newPlayer);
            if(!this.leaderboard.checkForPlayer(name))
            {
              this.leaderboard.addScore(name);
            }
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

    //402
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
    public String say(String name, String message, ArrayList<String> censorList) {
        Player player = this.playerList.findPlayer(name);
        if(player != null)
        {
            if(player.getIsDrunk())
            {
                message = drunkText(message);
            }

            String log = player.getName() + " says, \"" +
                    message + "\" in the room " + player.getCurrentRoom();
            add_chat_log(log);
            message = scrubMessage( message, censorList); //409_censor scrub message of unwanted words
            this.broadcast(player, chatPrefix + player.getName() + " says, \"" + message + "\"");
            return chatPrefix + "You say, \"" + message + "\"" + " " + date.toString();
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
    public String shout(String name, String message, ArrayList<String> censorList) {
        Player player = this.playerList.findPlayer(name);
        if(player != null)
        {
            if(player.getIsDrunk()){
                message = drunkText(message);
            }
            String log = player.getName() + " shouts, \"" + message + "\"";
            add_chat_log(log);
            message = scrubMessage( message, censorList); //409_censor scrub message of unwanted words
            this.broadcastShout(player, chatPrefix + player.getName() + " shouts, \"" + message + "\"");
            return chatPrefix + "You shout, \"" + message + "\"" + " " + date.toString();
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
    public String whisper(String name1, String name2, String message, ArrayList<String> censorList) {
        Player playerSending = this.playerList.findPlayer(name1);
        Player playerReceiving = this.playerList.findPlayer(name2);
        if(playerSending != null && playerReceiving != null)
        {

            if(name1.equalsIgnoreCase(name2))
                return "Cannot whisper yourself" + " " + date.toString();
            else
            {

		if(playerSending.searchIgnoredBy(name2)){
			return "Cannot whisper player that has ignored you";
	    	}
                if(!playerSending.searchIgnoredBy(playerReceiving.getName()))
                {
                    if(playerSending.getIsDrunk()){
                        message = drunkText(message);
                    }
                    String log = playerSending.getName() + " whispers, \"" + message + "\" to "
                            + playerReceiving.getName() + " " + date.toString();
                    add_chat_log(log);
                    message = scrubMessage( message, censorList); //409_censor scrub message of unwanted words
                    this.broadcast(playerSending, playerReceiving, chatPrefix + playerSending.getName() + " whispers, \"" + message + "\"");

                    playerReceiving.setLastWhisperName(name1);
                    return "Message sent to " + playerReceiving.getName() + " " + date.toString();
                }
                else {
                    return "";
                }
            }
        }
        else
        {
            if(playerReceiving == null)
                return "Couldn't find player online.";
            return null;
        }
    }

    /**
    * Sends a whisper the last player that whispered.
    * @param name Name of player replying to whisper
    * @param message Message to be whispered
    * @return Message showing success.
    */
    public String reply(String name, String message, ArrayList<String> censorList) {
        Player playerSending = this.playerList.findPlayer(name);
        if(playerSending.getLastWhisperName() == null) {
            return "You have not received a whisper to reply to.";
        }
        String name2 = playerSending.getLastWhisperName();
        Player playerReceiving = this.playerList.findPlayer(name2);
        if(playerSending.getIsDrunk()){
            message = drunkText(message);
        }
        return this.whisper(name, name2, message, censorList);    //409_censor whisper command scrubs message of unwanted words
    }

    /**
     * Attempts to walk forward < distance > times.  If unable to make it all the way,
     *  a message will be returned.  Will display LOOK on any partial success.
     * @param name Name of the player to move
     * @return Message showing success.
     */

    public void add_chat_log(String line)
    {

        try( FileOutputStream os =  new FileOutputStream(
                new File("chat_log.txt"),true ) )
        {

            OutputStreamWriter streamWriter = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(streamWriter);
            //print all chat logs (for admin)
            writer.println(line);

            writer.close();
            os.close();
        }
        catch(Exception e)
        {
            System.err.println("Something went wrong when recording");
        }

    }
    public void add_chat_log(List<String> lines)
    {

       try(FileOutputStream os =  new FileOutputStream(
               new File("chat_log.txt"),true))
       {

           OutputStreamWriter streamWriter = new OutputStreamWriter(os,StandardCharsets.UTF_8);

           PrintWriter writer = new PrintWriter(streamWriter);
           //print all chat logs (for admin)
           for(String line: lines)
           {
               writer.println(line);
           }
           writer.close();
           os.close();
       }
       catch(Exception e)
       {
           System.err.println("Something went wrong when recording");
       }

    }
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
    public synchronized String enter(String name, String location) {
      Player player = this.playerList.findPlayer(name);
      if(player == null) return null;
      int newID;
      //add more if statements for different shops
      if(location.equalsIgnoreCase("shop"))
        newID = 182;
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
      try{Thread.sleep(500);}
      catch (InterruptedException e){
        return "thread exception!";
      }
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
      if(player.getCurrentRoom() == 182)
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
     * Player can catch a spirit if in same room
     * @param name Name of player
     * @param target Name of spirit
     * @return Message showing success
     */
    public String catchSpirit(String name, String target) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            Room room = map.findRoom(player.getCurrentRoom());
              Spirit spirit = room.removeSpirit(target);
              if(player.getCurrentSpirits().contains(spirit)) {
                  this.broadcast(player, player.getName() + " tries to catch a spirit, but they already caught it before!");
                  return "You already caught a " + target + ".";
              }
              else if(spirit != null) {
                  player.addSpiritToSpirits(spirit);
                  this.broadcast(player, player.getName() + " catches a " + target + "!");
                  return "You caught a " + target + "!";
              }
              else {
                  this.broadcast(player, player.getName() + " tries to catch something, but doesn't seem to find anything there.");
                  return "You look around for a " + target + ", but can't find one.";
              }
            }
        else {
            return null;
        }
    }

    /**
     * Gets all possible spirits
     * @param playerName Name of player
     * @return String representation of all possible spirits
     */
    public String getAllSpirits(String playerName) {
        Player player = this.playerList.findPlayer(playerName);
        return player.getAllSpirits();
    }

    /**
     * Gets current spirits caught by player
     * @param playerName Name of player
     * @return String representation of caught spirits
     */
    public String getCurrentSpirits(String playerName) {
        Player player = this.playerList.findPlayer(playerName);
        return player.viewCurrentSpirits();
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
     * Attempts to pick up an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to pickup.
     * @return Message showing success.
     */
    public String describe(String name, String target) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            LinkedList<Item> playerInventory = player.getCurrentInventory();

   for(Item obj : playerInventory){
    if(obj.getItemName().equalsIgnoreCase(target)){
                    return obj.getItemDescrip();
    }
            }
            return "Hey uh...you can't ask me to describe something you don't own y'know?";
  }

  return null;
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
                object.setItemValue(object.getItemValue() * 0.8);
                room.addObjectFromPlayer(object);
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
     * Sorts inventory by the specified item attribute
     * @param name Name of the player who's inventory will be sorted
     * @param attribute the item attribute to sort the inventory
     * @return Message showing success.
     */
    public String sortInventory(String name, String attribute) {
        Player player = this.playerList.findPlayer(name);
            if( (!player.getCurrentInventory().isEmpty()) && (!attribute.isEmpty()) && ((attribute.equalsIgnoreCase("name") || attribute.equalsIgnoreCase("weight") || attribute.equalsIgnoreCase("value")))) {
                player.sortInventoryItems(attribute);
            }
            else {
                if(player.getCurrentInventory().isEmpty())
                    return "Can't sort empty inventory";
              return "Invalid attribute";
            }
            return null;
    }
    /**
     * Attempts to offer an item < target > from a player < player > to a player < nameOffered >. Will return a message on success or failure.
     * @param playerName The player offering the item
     * @param nameOffered Name of the person being offered an item
     * @param target The name of the item to offer
     * @return A message showing success.
     *
     */
    public String offerItem(String playerName, String nameOffered, String target) {
        Player player = this.playerList.findPlayer(playerName);
        Player playerOffered = this.playerList.findPlayer(nameOffered);
        boolean hasItem = false;
        if(player != null){
            LinkedList<Item> playerInventory = player.getCurrentInventory();
            if(playerOffered != null) {
                if (player == playerOffered)
                {
                    return "You can't offer yourself an item.";
                }
                if(playerOffered.getInTradeWithName() != null)
                {
                    return "This player is already in a trade.";
                }
                for(Item obj : playerInventory){
                    if(obj.getItemName().equalsIgnoreCase(target)){
                        hasItem = true;
                        break;
                    }
                }
                if(hasItem) {
                    playerOffered.setInTradeWithName(playerName);
              playerOffered.setInTradeWithItem(target);
              playerOffered.getReplyWriter().println(playerName + " offered you a " + target);
                    return "You just offered " + nameOffered + " a " + target + " from your inventory.";
                }
                else {
                    return "You just tried to offer " + nameOffered + " a " + target + ", but you don't have one.";
                }
            }
            else {
                return "You just tried to offer " + nameOffered + " a " + target + ", but " + nameOffered + " is not here.";
            }
        }
        else {
            return null;
        }
    }
    /**
     * Attempts to have a player <playerName> answer an offering player with <response>. Will return a message on success or failure.
     * @param playerName The player responding to the offer
     * @param response The response that the player is sending
     * @return A message showing success.
     *
     */
    public String offerResponse(String playerName, String response){
        Player player = this.playerList.findPlayer(playerName);
        String nameOffering = player.getInTradeWithName();
        String target = player.getInTradeWithItem();
        if(nameOffering == null || target == null){
            return "You are not in a valid trade";
        }
        Player playerOffering = this.playerList.findPlayer(nameOffering);
        boolean hasItem = false;
        if(player != null){
                LinkedList<Item> playerInventory = playerOffering.getCurrentInventory();
                if(playerOffering != null) {
                    if (player == playerOffering)
                    {
                        return "You can't accept an item from yourself.";
                    }
                    for(Item obj : playerInventory){
                        if(obj.getItemName().equalsIgnoreCase(target)){
                            hasItem = true;
                            break;
                        }
                    } 
                    if(hasItem) {
                        player.setInTradeWithName(null);
                        player.setInTradeWithItem(null);

                        if(response.equalsIgnoreCase("Accept")){
                            if(player.getCurrentInventory().size() < 10){
                                Item object = playerOffering.removeObjectFomInventory(target);
                                object.setItemValue(object.getItemValue() * 0.8);
                                player.addObjectToInventory(object);
                                playerOffering.getReplyWriter().println(playerName + " accepted your " + target);
                                return playerName + " got a " + target + " from " + nameOffering + ".";
                            }
                            else{
                                playerOffering.getReplyWriter().println(playerName + " tried to accept your " + target + " but failed.");
                                return playerName + " tried to accept a " + target + " from " + nameOffering + " but their inventory is full.";
                            }
                        }
                        else if(response.equalsIgnoreCase("Refuse")){
                                playerOffering.getReplyWriter().println(playerName + " refused your " + target);
                                return playerName + " refused a " + target + " from " + nameOffering + ".";
                        }
                        else{
                            return "Invalid response.";
                        }
                    }
                    else {
                        return "You just tried to respond to " + nameOffering + " about the " + target + ", but they don't have one.";
                    }
                }
                else {
                    return "You just tried to respond to " + nameOffering + " about the " + target + ", but " + nameOffering + " is not here.";
                }
            }
            else {
                return null;
            }

    }

    /**
     * Attempts to use an item the player has called < itemName >. Will return a message on any success or failure.
     * @param playerName Name of the player to use the item
     * @param itemName The case-insensitive name of the item to use
     * @return Message showing success.
     */
    public String useItem(String playerName, String itemName){
 Player player = this.playerList.findPlayer(playerName);
        if(player != null) {
            Item object = player.removeObjectFomInventory(itemName);
            if(object != null) {
                player.setTitle(object.getItemTitle());
                if(itemName.equalsIgnoreCase("rathskeller bottle"))
                {
                    player.setIsDrunk(true);
                    bottleTimerUpdate(player);
                }
          titleTimerUpdate(player);
                this.broadcast(player, player.getName() + " has used a " + itemName + " from personal inventory.");
                return "You just used a " + itemName + ".";
            }
            else {
                this.broadcast(player, player.getName() + " tried to use something, but doesn't seem to find what they were looking for.");
                return "You just tried to use a " + itemName + ", but you don't have one.";
            }
        }
        else {
            return null;
        }

    }
    /**
     * Updates a timer for the title
     */
    public void titleTimerUpdate(Player player){
        TimerTask timerTask = new TimerTask(){
            public void run(){
                player.getReplyWriter().println("Your title ran out");
                player.setTitle(null);
                titleTimer.cancel();
            }
        };
        if(titleTimer != null){
            titleTimer.cancel();
            titleTimer.purge();
        }
        titleTimer = new Timer();
        titleTimer.schedule(timerTask, 120000); // sets title for 2 minutes
    }

    /**
     * A timer that makes you sober after a minute of using the rathskeller bottle
     *
     */
    public void bottleTimerUpdate(Player player){
        TimerTask bottleTask = new TimerTask(){
            public void run(){
                player.getReplyWriter().println("Your not drunk anymore =[");
                player.setIsDrunk(false);
                bottleTimer.cancel();

            }
        };
        if(bottleTimer != null){
            bottleTimer.cancel();
            bottleTimer.purge();
        }
        bottleTimer = new Timer();
        bottleTimer.schedule(bottleTask, 60000); // stop being drunk after 1 minute

    }

    /**
     * This method turns all punctuation into either a ? or !, it also
     * turns the message to lower case.
     * @param message a String message we need to scramble
     * @return message scrambled message string
     */
    private static String drunkText(String message)
    {
        message = message.toLowerCase();
        char arry[] = {'!','?'};
        Random randy = new Random();
        message = message.replace('?', arry[randy.nextInt(arry.length)]);
        message = message.replace('!', arry[randy.nextInt(arry.length)]);
        message = message.replace('.', arry[randy.nextInt(arry.length)]);
        message = message.replace(':', arry[randy.nextInt(arry.length)]);
        message = message.replace(',', arry[randy.nextInt(arry.length)]);
        message = message.replace(';', arry[randy.nextInt(arry.length)]);
        message = message.replace('\'', arry[randy.nextInt(arry.length)]);
        message = message.replace('\"', arry[randy.nextInt(arry.length)]);
        message = message.replace('(', arry[randy.nextInt(arry.length)]);
        message = message.replace(')', arry[randy.nextInt(arry.length)]);
        message = message.replace('[', arry[randy.nextInt(arry.length)]);
        message = message.replace(']', arry[randy.nextInt(arry.length)]);
        message = message.replace('{', arry[randy.nextInt(arry.length)]);
        message = message.replace('}', arry[randy.nextInt(arry.length)]);
        message = message.replace('-', arry[randy.nextInt(arry.length)]);
        message = message.replace('\\', arry[randy.nextInt(arry.length)]);
        message = message.replace('@', arry[randy.nextInt(arry.length)]);
        message = message.replace('&', arry[randy.nextInt(arry.length)]);
        message = message.replace('*', arry[randy.nextInt(arry.length)]);
        message = message.replace('_', arry[randy.nextInt(arry.length)]);
        message = message.replace('/', arry[randy.nextInt(arry.length)]);
        message = message.replace('>', arry[randy.nextInt(arry.length)]);
        message = message.replace(',', arry[randy.nextInt(arry.length)]);


        return message;
    }



    /**
     * Player pokes a ghoul that is in the same room.
     * @param playerName Name of the player that pokes the ghoul.
     * @param ghoulName Name of the ghoul that is poked
     * @return null
     */
    public String pokeGhoul(String playerName, String ghoulName) {
        Player player = playerList.findPlayer(playerName);
        Room room = player.getCurrentRoomObject();

        for (Ghoul ghoul: room.getGhouls()){
            if (ghoul.getName().equalsIgnoreCase(ghoulName)){
                synchronized (ghoul) {
                    player.broadcast("You poked " + ghoul.getName() + ".");
                    player.broadcastToOthersInRoom(player.getName() + " poked " + ghoul.getName() + ".");
                    ghoul.poke();
                }
                return null;
            }
        }
        player.broadcast("Can't find a ghoul named " + ghoulName + " to poke.");
        return null;
    }

    /**
     * Player gifts a ghoul that is in the same room an object. This action decreases the ghoul's aggression.
     * @param playerName Name of the player that gifts the ghoul.
     * @param ghoulName Name of the ghoul to give the item to.
     * @param itemName Name of the item to give to the ghoul.
     * @return null
     */
    public String giftGhoul(String playerName, String ghoulName, String itemName) {
        Player player = playerList.findPlayer(playerName);
        Room room = player.getCurrentRoomObject();

        for (Ghoul ghoul : room.getGhouls()) {
            if (ghoul.getName().equalsIgnoreCase(ghoulName)) {
                synchronized (ghoul) {
                    Item item = player.removeObjectFomInventory(itemName);
                    if (item == null)
                        player.broadcast("Can't find an item named " + itemName + " in your inventory");
                    else {
                        player.broadcast("You gave " + ghoul.getName() + " a " + item.getItemName() + ".");
                        player.broadcastToOthersInRoom(player.getName() + " gave " + ghoul.getName() +
                                " a " + item.getItemName() + ".");
                        ghoul.give(item);
                    }
                }
                return null;
            }
        }
        player.broadcast("Can't find a ghoul named " + ghoulName + " to give an item to.");
        return null;
    }

    public boolean getTimeOfDay() {
        return timeOfDay;
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
     * @param playerName Player Name
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
     if(yourname.toLowerCase().equals(name.toLowerCase()))
      return "Can't trade yourself, silly! Get some friends!";
     Player tradee = this.playerList.findPlayer(name);
        Player trader = this.playerList.findPlayer(yourname);
        if(trader == null || tradee == null)
         return "" + name + " does not exist!";
        if(trader.getCurrentRoom() != tradee.getCurrentRoom())
         return "You are not close enough to give!";
        if(amount <= 0)
         return "Must gift an amount greater than 0!";
        if(trader.getMoney().sum() < amount)
         return "You don't have that much money, silly!";
        if(!(trader.hasUnits(amount)))
         return "You don't have the right money units, silly!";
        boolean result = this.giftsTracker.trackGift(trader, tradee, amount);
        if(result == false)
         return "" + tradee.getName() + " already has an open trade!";
        tradee.getReplyWriter().println("" + trader.getName() + " wants to gift you $" + String.format("%1$,.2f", amount) + "!\nEnter RECEIVE GIFT to accept.");
        return "You try to gift " + tradee.getName() + " $" +   String.format("%1$,.2f", amount);
    }
    
    public String acceptGift(String name) {
     Player tradee = this.playerList.findPlayer(name);
     if(tradee == null)
      return null;
     if(!(this.giftsTracker.hasOpenRequest(tradee))) {
      return "Nobody has gifted you anything! Maybe wait till Christmas!";
     }
     GiftsTracker.GiftRequest request = this.giftsTracker.getRequest(tradee);
     if(request == null)
      return null;
     Player trader = request.getTrader();
     if(trader == null)
      return null;
     double giftAmount = request.getAmount();
     if(trader.getMoney().sum() < giftAmount) {
      this.giftsTracker.close(request);
      return "" + trader.getName() + " ran out of money!";
     }
     trader.giveMoney(trader, tradee, giftAmount);
     trader.getReplyWriter().println(tradee.getName() + " has accepted your gift!");
     this.giftsTracker.close(request);
     return "You have receieved the gift!";
    }
    
    public String declineGift(String name) {
     Player player = playerList.findPlayer(name);
     if(player == null)
      return null;
     if(!(giftsTracker.hasOpenRequest(player)))
      return "Nobody has gifted you anything! Maybe wait till Christmas!";
     GiftsTracker.GiftRequest request = this.giftsTracker.getRequest(player);
     if(request == null)
      return null;
     Player trader = request.getTrader();
     if(trader == null) {
      giftsTracker.close(request);
      return "You declined the gift.";
     }
     trader.getReplyWriter().println(player.getName() + " has declined your gift.");
     giftsTracker.close(request);
     return "You declined the gift.";
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

    //405
    public String ignore(String name, String ignoreName) {
              if( name.equalsIgnoreCase(ignoreName) )
                      return "You can't ignore yourself.";

              //verify player being ignored exists
              Player ignoredPlayer = this.playerList.findPlayer(ignoreName);
              if( ignoredPlayer == null )
                      return "Player " + ignoreName + " is not in the game.";

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

    //407
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
    //408
    public String unIgnore(String name, String unIgnoreName) {
              if( name.equalsIgnoreCase(unIgnoreName) )
                      return "You can't unignore yourself since you can't ignore yourself...";

              //verify player being unignored exists
              Player unIgnoredPlayer = this.playerList.findPlayer(unIgnoreName);
              if( unIgnoredPlayer == null )
                      return "Player " + unIgnoreName + " is not in the game.";

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

/**
     * Sell an item to the shop the player is currently in
     * @param playerName player who is selling
     * @param itemName item to sell
     * @return A string indicating success or failure
     */
 public synchronized String sell(String playerName, String itemName) {
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
      try{Thread.sleep(500);}
      catch (InterruptedException e){
        return "thread exception!";
      }
      return "You have sold " + itemName + " to the shop.";
  }
}

 public synchronized String buy(String playerName, String itemName) {
     //format user input for item
     itemName = itemName.toLowerCase();
     itemName = itemName.substring(0, 1).toUpperCase() + itemName.substring(1);
     //check if player not in shop or does not have item
     if(!shop.playerInShop(playerName)) {
         return "You cannot buy if you are not in a shop!";
     }
     Player player = this.playerList.findPlayer(playerName);
     if(player == null)
         return null;
     //buyItem() will handle removing money since we do not have an Item obj
     Boolean did_buy = shop.buyItem(player, itemName);
     player.getReplyWriter().println(shop.displayShop());
     try{Thread.sleep(500);}
      catch (InterruptedException e){
        return "thread exception!";
      }
     if(did_buy == false){
         return "You cannot buy " + itemName + "!";
     }
     if(did_buy == true){
       this.broadcast(player, itemName + " has been bought by " + player.getName());
     }
     return "You have bought a " + itemName + " from the shop.";
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
  public void challenge(String challenger, String player2, int rounds)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);

    String battletype = MessageFormat.format( (rounds == 1) ? " ":" best {0} out of {1} ",(int)Math.ceil(rounds/2.0),rounds);

    if(play2 == null)//other player doesnt exist
    {
      play1.getReplyWriter().println("\nThat player doesn't exist.");
      return;
    }

    if(challenger.equalsIgnoreCase(player2)) // Challenger is challenging himself. Stupid challenger...
    {
      play1.getReplyWriter().println("\nYou can't challenge yourself.\n");
      return;
    }

    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(challenger))// Challenger is already in an active battle
      {
        play1.getReplyWriter().println("\nYou can only be in one battle at a time. Finish the current one you are in before challenging someone else.");
        return;
      }
      if(b.containsPlayer(player2))// Other Player is already in an active battle.
      {
        play1.getReplyWriter().println("\nYou cant challenge " + player2 + " right now, they're currently in a battle. ");
        return;
      }
    }

      LinkedList<Item> inventory = play2.getCurrentInventory();  //you can only challenge a player if they have a rock paper or scissors
      int hasBattleItemP2 = 0;
      for (Item obj : inventory) {
          if (obj.getItemName().equals("Rock") || obj.getItemName().equals("Paper") || obj.getItemName().equals("Scissors")) {
              hasBattleItemP2 ++;


          }
      }

      if (hasBattleItemP2 == 0) {

          play1.getReplyWriter().println("\nThe other person does not have a battle item");
          return;
      }

      LinkedList<Item> inventory2 = play1.getCurrentInventory(); //you can only challenge if you have a rock, paper, or scissors
      int hasBattleItemP1 = 0;
      for (Item obj : inventory2) {
          if (obj.getItemName().equals("Rock") || obj.getItemName().equals("Paper") || obj.getItemName().equals("Scissors")) {
              hasBattleItemP1 ++;


          }
      }

      if (hasBattleItemP1 == 0) {

          play1.getReplyWriter().println("\nYou do not have a battle item");
          return;
      }


      for(Battle b : pendingBattles)//Challenger already asked this person to battle and is waiting for a response still.
    {
      if(b.hasPlayers(challenger,player2))
      {
        play1.getReplyWriter().println("\nYou already have a pending challenge request with "+ player2 +".");
        return;
      }
    }
    play2.getReplyWriter().println("\n" + challenger + " has challenged you to a" + battletype + "Rock Paper Scissors Battle. \n\nTo accept, type 'Accept "
                  + challenger + "' and press ENTER." + "\n\nTo decline, type 'Refuse " + challenger + "' and press ENTER." );
    pendingBattles.add(new Battle(challenger, player2, rounds));
    System.out.println("Player: " + challenger + " Challenged: " + player2);
  }

  public void accept(String challenger, String player2)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);


    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player2))
      {
        play2.getReplyWriter().println("You're already in a Rock Paper Scissors challenge with someone." +
                  "\nMake a choice of 'rock', 'paper', or 'scissors' \nand wait for the challenge to end before trying to accept another.\n");
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
      LinkedList<Item> inventory = p.getCurrentInventory();
      int hasBattleItem = 0;
      for (Item obj : inventory) {
          if (obj.getItemName().equals("Rock")) {
              hasBattleItem ++;


          }
      }
    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player))
      {
        if(b.getPlayer1().equalsIgnoreCase(player))
        {
          if (hasBattleItem == 0){

            p.getReplyWriter().println("You don't have that item, pick again");
                return;
          }
          p.removeObjectFomInventory("Rock");
          b.setChoiceP1(1);

          p.getReplyWriter().println("You Chose Rock.\n");
          if(b.getPlayer2().length() >= 5 && b.getPlayer2().substring(0,5).equals("Ghoul")){
            Ghoul ghoul = (Ghoul)this.npclist.findNPC(b.getPlayer2());
            ghoul.doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1()[b.getCurrentRound()], b.getChoiceP2()[b.getCurrentRound()], b);
            return;
          }


          p.getReplyWriter().println("You Chose Rock.\nIf you wish to change your choice, type 'Rock' or 'Paper' or 'Scissors again and press ENTER.'\n");
          if((b.getChoiceP1()[b.getCurrentRound()] != 0) && (b.getChoiceP2()[b.getCurrentRound()] != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b, b.getMaxRounds());
          }
          return;
        }
        if(b.getPlayer2().equalsIgnoreCase(player))
        {
            if(hasBattleItem == 0){

                p.getReplyWriter().println("You don't have that item, pick again");
                return;
            }
          p.removeObjectFomInventory("Rock");
          b.setChoiceP2(1);
          p.getReplyWriter().println("You Chose Rock.\nIf you wish to change your choice, type 'Rock' or 'Paper' or 'Scissors again and press ENTER.'\n");
          if((b.getChoiceP1()[b.getCurrentRound()] != 0) && (b.getChoiceP2()[b.getCurrentRound()] != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b, b.getMaxRounds());
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

    LinkedList<Item> inventory = p.getCurrentInventory();
    int hasBattleItem = 0;
    for (Item obj : inventory) {
        if (obj.getItemName().equals("Paper")) {
            hasBattleItem ++;


        }
    }
    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player))
      {
        if(b.getPlayer1().equalsIgnoreCase(player))
        {
            if (hasBattleItem == 0){

                p.getReplyWriter().println("You don't have that item, pick again");
                return;

            }
          p.removeObjectFomInventory("Paper");
          b.setChoiceP1(2);

          p.getReplyWriter().println("You Chose Paper.\n");
          if(b.getPlayer2().length() >= 5 && b.getPlayer2().substring(0,5).equals("Ghoul")){
            Ghoul ghoul = (Ghoul)this.npclist.findNPC(b.getPlayer2());
            ghoul.doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1()[b.getCurrentRound()], b.getChoiceP2()[b.getCurrentRound()], b);
            return;
          }


          p.getReplyWriter().println("You Chose Paper.\nIf you wish to change your choice, type 'Rock' or 'Paper' or 'Scissors again and press ENTER.'\n");
          if((b.getChoiceP1()[b.getCurrentRound()] != 0) && (b.getChoiceP2()[b.getCurrentRound()] != 0))

          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b, b.getMaxRounds());
          }
          return;
        }
        if(b.getPlayer2().equalsIgnoreCase(player))
        {
            if (hasBattleItem == 0){

                p.getReplyWriter().println("You don't have that item, pick again");
                return;

            }
          p.removeObjectFomInventory("Paper");
          b.setChoiceP2(2);
          p.getReplyWriter().println("You Chose Paper.\nIf you wish to change your choice, type 'Rock' or 'Paper' or 'Scissors again and press ENTER.'\n");
          if((b.getChoiceP1()[b.getCurrentRound()] != 0) && (b.getChoiceP2()[b.getCurrentRound()] != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b, b.getMaxRounds());
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
      LinkedList<Item> inventory = p.getCurrentInventory();
      int hasBattleItem = 0;
      for (Item obj : inventory) {
          if (obj.getItemName().equals("Scissors")) {
              hasBattleItem ++;


          }
      }
    for(Battle b : activeBattles)
    {
      if(b.containsPlayer(player))
      {
        if(b.getPlayer1().equalsIgnoreCase(player))
        {
            if (hasBattleItem == 0){

                p.getReplyWriter().println("You don't have that item, pick again");
                return;

            }
          p.removeObjectFomInventory("Scissors");
          b.setChoiceP1(3);

          p.getReplyWriter().println("You Chose Scissors.\n");
          if(b.getPlayer2().length() >= 5 && b.getPlayer2().substring(0,5).equals("Ghoul")){
            Ghoul ghoul = (Ghoul)this.npclist.findNPC(b.getPlayer2());
            ghoul.doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1()[b.getCurrentRound()], b.getChoiceP2()[b.getCurrentRound()], b);
            return;
          }


          p.getReplyWriter().println("You Chose Scissors.\nIf you wish to change your choice, type 'Rock' or 'Paper' or 'Scissors again and press ENTER.'\n");
          if((b.getChoiceP1()[b.getCurrentRound()] != 0) && (b.getChoiceP2()[b.getCurrentRound()] != 0))

          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b, b.getMaxRounds());
          }
          return;
        }
        if(b.getPlayer2().equalsIgnoreCase(player))
        {
            if (hasBattleItem == 0){

                p.getReplyWriter().println("You don't have that item, pick again");
                return;

            }
          p.removeObjectFomInventory("Scissors");
          b.setChoiceP2(3);
          p.getReplyWriter().println("You Chose Scissors.\nIf you wish to change your choice, type 'Rock' or 'Paper' or 'Scissors again and press ENTER.'\n");
          if((b.getChoiceP1()[b.getCurrentRound()] != 0) && (b.getChoiceP2()[b.getCurrentRound()] != 0))
          {
            doBattle(b.getPlayer1(), b.getPlayer2(), b.getChoiceP1(), b.getChoiceP2(), b, b.getMaxRounds());
          }
          return;
        }
      }
    }
    p.getReplyWriter().println("You aren't in any Rock Paper Scissors Battles currently.");
  }

  public void doBattle(String challenger, String player2, int[] p1, int[] p2, Battle b, int rounds)
  {
    Player play1 = this.playerList.findPlayer(challenger);
    Player play2 = this.playerList.findPlayer(player2);
    String message = "";
    String roundResult  = "";
    if(p1[b.getCurrentRound()] == p2[b.getCurrentRound()])
    {
      //tie
      switch(p1[b.getCurrentRound()])
      {
        case 1:
          roundResult = "You Both Tied.";
          play1.getReplyWriter().println("You both chose Rock. The match is a tie!\n");
          play2.getReplyWriter().println("You both chose Rock. The match is a tie!\n");

          if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
          {
              String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
              message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                        MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
              this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
              activeBattles.remove(b);
              updateLeaderboard(b);
              writeLog(b);
          }
          else
          {
              String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                    "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
              play1.getReplyWriter().println(broadcast);
              play2.getReplyWriter().println(broadcast);
              b.incrementRound();
          }
          return;
        case 2:
          play1.getReplyWriter().println("You both chose Paper. The match is a tie!\n");
          play2.getReplyWriter().println("You both chose Paper. The match is a tie!\n");

          if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
          {
              String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
              message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                        MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
              this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
              activeBattles.remove(b);
              updateLeaderboard(b);
              writeLog(b);
          }
          else
          {
              String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                    "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
              play1.getReplyWriter().println(broadcast);
              play2.getReplyWriter().println(broadcast);
              b.incrementRound();
          }
          return;
        case 3:
          play1.getReplyWriter().println("You both chose Scissors. The match is a tie!\n");
          play2.getReplyWriter().println("You both chose Scissors. The match is a tie!\n");

          if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
          {
              String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
              message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                        MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
              this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
              activeBattles.remove(b);
              updateLeaderboard(b);
              writeLog(b);
          }
          else
          {
              String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                    "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
              play1.getReplyWriter().println(broadcast);
              play2.getReplyWriter().println(broadcast);
              b.incrementRound();
          }
          return;
      }
    }
    else if(p1[b.getCurrentRound()] == 1 && p2[b.getCurrentRound()] == 2)
    {
      //rock paper
      play1.getReplyWriter().println("You chose Rock. " + player2 + " chose Paper. \nYou lose.\n");
      play2.getReplyWriter().println("You chose Paper. " + challenger + " chose Rock. \nYou win.\n");


   // Added by Brendan
   this.leaderboard.incrementScore(play1.getName(), false);
   this.leaderboard.incrementScore(play2.getName(), true);

      b.incP2Score();

      if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
      {
          String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
          message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                    MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          updateLeaderboard(b);
          writeLog(b);
      }
      else
      {
          String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
          play1.getReplyWriter().println(broadcast);
          play2.getReplyWriter().println(broadcast);
          b.incrementRound();
      }


      return;
    }
    else if(p1[b.getCurrentRound()] == 1 && p2[b.getCurrentRound()] == 3)
    {
      //rock scissors
      play1.getReplyWriter().println("You chose Rock. " + player2 + " chose Scissors. \nYou win.\n");
      play2.getReplyWriter().println("You chose Scissors. " + challenger + " chose Rock. \nYou lose.\n");

      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(b);

   // Added by Brendan
   this.leaderboard.incrementScore(play1.getName(), true);
   this.leaderboard.incrementScore(play2.getName(), false);



      b.incP1Score();

      if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
      {
          String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
          message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                    MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          updateLeaderboard(b);
          writeLog(b);
      }
      else
      {
          String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
          play1.getReplyWriter().println(broadcast);
          play2.getReplyWriter().println(broadcast);
          b.incrementRound();
      }

      return;
    }
    else if(p1[b.getCurrentRound()] == 2 && p2[b.getCurrentRound()] == 1)
    {
      //paper rock
      play1.getReplyWriter().println("You chose Paper. " + player2 + " chose Rock. \nYou win.\n");
      play2.getReplyWriter().println("You chose Rock. " + challenger + " chose Paper. \nYou lose.\n");

      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(b);

   // Added by Brendan
   this.leaderboard.incrementScore(play1.getName(), true);
   this.leaderboard.incrementScore(play2.getName(), false);



      b.incP1Score();

      if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
      {
          String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
          message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                    MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          updateLeaderboard(b);
          writeLog(b);
      }
      else
      {
          String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
          play1.getReplyWriter().println(broadcast);
          play2.getReplyWriter().println(broadcast);
          b.incrementRound();
      }

      return;
    }
    else if(p1[b.getCurrentRound()] == 2 && p2[b.getCurrentRound()] == 3)
    {
      //paper scissors
      play1.getReplyWriter().println("You chose Paper. " + player2 + " chose Scissors. \nYou lose.\n");
      play2.getReplyWriter().println("You chose Scissors. " + challenger + " chose Paper. \nYou win.\n");

      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(b);

   // Added by Brendan
   this.leaderboard.incrementScore(play1.getName(), false);
   this.leaderboard.incrementScore(play2.getName(), true);



      b.incP2Score();

      if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
      {
          String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
          message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                    MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          updateLeaderboard(b);
          writeLog(b);
      }
      else
      {
          String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
          play1.getReplyWriter().println(broadcast);
          play2.getReplyWriter().println(broadcast);
          b.incrementRound();
      }

      return;
    }
    else if(p1[b.getCurrentRound()] == 3 && p2[b.getCurrentRound()] == 1)
    {
      //scissors rock
      play1.getReplyWriter().println("You chose Scissors. " + player2 + " chose Rock. \nYou lose.\n");
      play2.getReplyWriter().println("You chose Rock. " + challenger + " chose Scissors. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
      activeBattles.remove(b);
      writeLog(b);

   // Added by Brendan
   this.leaderboard.incrementScore(play1.getName(), false);
   this.leaderboard.incrementScore(play2.getName(), true);



      b.incP2Score();

      if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
      {
          String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
          message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                    MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          updateLeaderboard(b);
          writeLog(b);
      }
      else
      {
          String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
          play1.getReplyWriter().println(broadcast);
          play2.getReplyWriter().println(broadcast);
          b.incrementRound();
      }

      return;
    }
    else if(p1[b.getCurrentRound()] == 3 && p2[b.getCurrentRound()] == 2)
    {
      //scissors paper
      play1.getReplyWriter().println("You chose Scissors. " + player2 + " chose Paper. \nYou win.\n");
      play2.getReplyWriter().println("You chose Paper. " + challenger + " chose Scissors. \nYou lose.\n");

      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      this.broadcast(map.findRoom(play1.getCurrentRoom()),message);;
      activeBattles.remove(b);
      writeLog(b);

   // Added by Brendan
   this.leaderboard.incrementScore(play1.getName(), true);
   this.leaderboard.incrementScore(play2.getName(), false);



      b.incP1Score();

      if(b.getCurrentRound()+1 == b.getMaxRounds() || b.getP1Score() >= Math.ceil(b.getMaxRounds()/2.0) || b.getP2Score() >= Math.ceil(b.getMaxRounds()/2.0))
      {
          String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() == b.getP2Score()) ? "it was a tie, nobody": b.getPlayer2());
          message = challenger + " and " + player2 + ((b.getMaxRounds() == 1) ? MessageFormat.format(" had a Rock Paper Scissors Battle, {0} won.\n",result) :
                    MessageFormat.format(" had a best {2} out of {0} Rock Paper Scissors Battle, {1} won.\n",b.getMaxRounds(),result,(int)Math.ceil(b.getMaxRounds()/2.0)));
          this.broadcast(map.findRoom(play1.getCurrentRoom()),message);
          activeBattles.remove(b);
          updateLeaderboard(b);
          writeLog(b);
      }
      else
      {
          String broadcast = MessageFormat.format("\nThat was round {0}, You have {1} more round(s) to go. Please choose 'Rock' 'Paper' or 'Scissors' for the next round." +
                "\nOnce both of you have sent in your choices, the next round will be triggered.",b.getCurrentRound()+1, b.getMaxRounds() - (b.getCurrentRound()+1));
          play1.getReplyWriter().println(broadcast);
          play2.getReplyWriter().println(broadcast);
          b.incrementRound();
      }

      return;
    }
  }

  public void writeLog(Battle b)
  {
       try(BufferedWriter writer = new BufferedWriter(new FileWriter("rps.log",true)))
       {
           SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
           Date date = new Date();
           String result = (b.getP1Score() > b.getP2Score()) ? b.getPlayer1() : ((b.getP1Score() < b.getP2Score()) ? b.getPlayer2() : "TIE");
           String logItem = MessageFormat.format("[{0}] [{4} Round RPS Battle] - Player1: {1} (Challenger) | Player2: {2} (Challenged) | Result: {3}\n",date,b.getPlayer1(),b.getPlayer2(),result,b.getMaxRounds());
           writer.write(logItem);
           writer.close();
       }
       catch(IOException e) {
         System.err.println("Function writeLog encountered a IOException.");
       }
  }

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
      player.getReplyWriter().println(message);
      return "";
  }

    //409_censor START
    private String scrubMessage( String message, ArrayList<String> censorList ){
        if( message == null || message.equals(' ') )
                return message;
        if( censorList == null || censorList.size()==0)
                return message;
        for( String word:censorList){
            String censor = "*";
            for( int x = 1; x<word.length();x++)
                    censor+="*";
            message = message.replaceAll( "(?i)(" + word.toString() + ")", censor);
        }
        return message;
    }
    //409_censor END

  //Added by An
  public void topTen(String name) {
      Player player = this.playerList.findPlayer(name);
      if(player == null)
          return;
      String topTenLeaderBoard = this.leaderboard.getTopTen();
      player.getReplyWriter().println(topTenLeaderBoard);
  }

  public void getRank(String player)
  {
    Player p = this.playerList.findPlayer(player);
    p.getReplyWriter().println("Your current RPS Leaderboard rank is: "+ leaderboard.getPlayerRank(player));
  }

  public void updateLeaderboard(Battle b)
  {

    if(b.getP1Score() > b.getP2Score()){
      this.leaderboard.incrementScore(b.getPlayer1(), true);
      this.leaderboard.incrementScore(b.getPlayer2(), false);
    }
    else if(b.getP1Score() < b.getP2Score()){
      this.leaderboard.incrementScore(b.getPlayer1(), false);
      this.leaderboard.incrementScore(b.getPlayer2(), true);
    }
  }
//Rock Paper Scissors Battle Methods -------------------------------------------
  // Whiteboards
  /**
   * 
   * @param  playerName
   * @return the room object where the player is
   * @throws RemoteException
   */
  public Room getPlayerRoom(String playerName) {
    Player player = findPlayer(playerName);
    if (player == null) return null;

    int roomId = player.getCurrentRoom();
    // if (roomId == null) return null;

    Room room = map.findRoom(roomId);
    return room;
  }

  /**
   * Returns a string displaying the Whiteboard of the room the player is in.
   * @param  playerName
   * @return message to be displayed to player
   * @throws RemoteException
   */
  public String displayWhiteboard(String playerName) {
    Room room = getPlayerRoom(playerName);
    WhiteBoard wb = room.getWB();

    if (wb == null) {
      return "This room doesn't have a whiteboard! Go to an indoor room instead.";
    }

    return wb.display();
  }
  
  /**
   * Method that players can use to call the erase method of the whiteboard in the same room that they are in, which replaces the message of that whiteboard with the empty string.
   * @param  playerName
   * @return message to be displayed to player
   * @throws RemoteException
   */
  public String clearWhiteboard(String playerName) {
    Room room = getPlayerRoom(playerName);
    WhiteBoard wb = room.getWB();

    if (wb == null) {
      return "This room doesn't have a whiteboard! Go to an indoor room instead.";
    }

    return wb.erase();
  }
  
  /**
   * Allows a player to access the write method of the whiteboard that is in the same room as them, allowing them to change the message stored in that whiteboard to the message that the player enters.
   * @param  playerName
   * @param  message
   * @return message to be displayed to player
   * @throws RemoteException
   */
  public String writeWhiteboard(String playerName, String message) {
    Room room = getPlayerRoom(playerName);
    WhiteBoard wb = room.getWB();

    if (wb == null) {
      return "This room doesn't have a whiteboard! Go to an indoor room instead.";
    }

    return wb.write(message);
  }

  /**
     * Opens WhiteBoard.csv file and stores whiteboards messages there
     * @throws RemoteException
     */
    public void saveWhiteboards() {
      StringBuilder sb = new StringBuilder();

      LinkedList<Room> roomList = map.getMap();
      Iterator rooms = roomList.listIterator(1);

      while(rooms.hasNext()){ 
        Room r = (Room) rooms.next();
        // System.out.println(r.getId());
        
        WhiteBoard wb = r.getWB();

        if (wb != null) {
          String msg = wb.getMessage();

          if (! msg.equals("")) {
            sb.append(r.getId());
            sb.append(", ");
            sb.append(msg);
            sb.append("\n");
          }
        }
      }

      try {
          // Check for file
          File file = new File("./WhiteBoard.csv");
          if (! file.exists()) file.createNewFile();

          // Write to file
          FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
          BufferedWriter bw = new BufferedWriter(fw);

          bw.write(sb.toString());
          bw.close();
      } catch (IOException ex){
          Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

/*
 * @author James Bruce
 * if an exit exists in a direction from a room, then its title is returned
 * @param r a Room to branch off of
 * @param s a String to parse directions from
 * @return a String representing the Room in the direction(s) given
 */
  private String SingleExit(Room r, String s)
  {
   List<Direction> l=new ArrayList<Direction>();
 //parse string for directions
 for(int i=0; i<s.length(); i++)
  if(s.charAt(i)=='n')
   l.add(Direction.NORTH);
  else if(s.charAt(i)=='w')
   l.add(Direction.WEST);
  else if(s.charAt(i)=='e')
   l.add(Direction.EAST);
  else
   l.add(Direction.SOUTH);
 //for each direction found
 for(Direction d: l)
  if(r.canExit(d))
   r=map.findRoom(r.getLink(d));//shift the current room to the next room
  else//not a valid set of directions
   return "";
 return r.getTitle()+"("+s+")";
}

/*
 * @author James Bruce
 * returns all exit strings in a set of directions
 * @param r a Room to branch off of
 * @param a the vertical distance to branch off from the room
 * @param b the horizontal distance to branch off from the room
 * @return a String containing all exits branching off from a Room
 */
private String ExitString(Room r, int a, int b)
{
 String s="", e, t;
 //convert coordinates to directions
 for(; a<1; a++)
  s+='n';
 for(; a>1; a--)
  s+='s';
 for(; b<1; b++)
  s+='w';
 for(; b>1; b--)
  s+='e';
 e=SingleExit(r, s);
 //check permutations of directions to find different possible locations
 for(int i=0; i<s.length(); i++)
  for(int j=i+1; j<s.length(); j++)
  {
   t=SingleExit(r, s.substring(0, i)+s.charAt(j)+s.substring(i+1, j)+s.charAt(i)+s.substring(j+1));//check a new permutation
   if(t.length()>0&&!e.contains(t.substring(0, t.indexOf("("))))//if we found a new, valid location
    e+=" or "+t;
  }
 if(e.startsWith(" or "))//if the first location wasn't valid
  e=e.substring(4);
 return e;
}

/*
 * @author James Bruce
 * returns a room String in a more ASCII-friendly format
 * @param s a String representing the Room name
 * @param l the length to pad the String to
 * @return an array of Strings representing an ASCII representation of a Room
 */
private String[] RoomStrings(String s, int l)
{
 String[] r=new String[3];
 r[0]="";
 r[1]=s;
 for(int i=l-s.length(); i>0; i--)
  r[1]=" "+r[1];//put spaces until the length requirement is met
 if(s.length()==0)//nothing here
  return new String[]{r[1], r[1], r[1]};//return a bunch of spaces
 for(int i=0; i<l; i++)
  r[0]+="-";//put dashes to surround the Room name
 r[1]="|"+r[1].substring(2)+"|";
 r[2]=r[0];
 return r;
}

/*
 * @author James Bruce
 * given a player name, returns an ascii map of the world surrounding them
 * @param name the name of the Player using the command
 * @return an ASCII representation of a map of the world surrounding the player
 */
public String map(String name)
{
   Room r=map.findRoom(this.playerList.findPlayer(name).getCurrentRoom());//get the room the player is in
 //get the title of all exits
 String[][] a=new String[3][3];//initialize the rooms
 for(int i=0; i<3; i++)
  for(int j=0; j<3; j++)
   a[i][j]=ExitString(r, i, j);
 a[1][1]=r.getTitle();
 //get the longest length in each column for spacing
 int[] l=new int[3];
 for(int i=0; i<3; i++)
  for(int j=0; j<3; j++)
   l[j]=Math.max(l[j], a[i][j].length());
 //build the map String
 String m="";
 String[][] t=new String[3][3];
 for(int i=0; i<3; i++)
 {
  for(int j=0; j<3; j++)
   t[j]=RoomStrings(a[i][j], l[j]+2);
  for(int j=0; j<3; j++)
  {
   for(int k=0; k<3; k++)
    m+=t[k][j]+" ";
   m+="\n";
  }
 }
 return m;
}

    //416_GroupChat Start
    //declared at top: private GroupChatTracker groupChatTracker;  //416_GroupChat
    public String createGroupChat( String groupChatName, String playerName){
        return groupChatTracker.createGroupChat( groupChatName, playerName);
    }

    public String printGroupChat( String groupChatName ){
        return groupChatTracker.printGroup( groupChatName);
    }

    public boolean checkGCExists(String groupChatName){
        return groupChatTracker.checkGroupExists( groupChatName );
    }

    public String GCInvite( String groupName, String playerInvited, String playerInviting)
    {
        //TO DO:

        Player pInvited =  playerList.findPlayer( playerInvited );
        //check that playerInvited is in the game
        if( pInvited == null )
        {
            return "Player " + playerInvited + " is not in the game.";
        }

        //check that playerInviting is in the group
        if( !groupChatTracker.checkMembership( groupName, playerInviting) )
        {
            //return "You can't invite a player to group [" + groupName + "].";
            return "You're not in group [" + groupName + "].";
        }

        //check if player has already been invited
        if( groupChatTracker.checkInvite( groupName, playerInvited) )
        {
            //player already invited
            return "Player already invited to group";

        }

        //check if inviting and invited player are the same
        if( playerInvited.equalsIgnoreCase( playerInviting ) )
        {
            return "You are already in group [" + groupName + "].";

        }

        //Track the invite
        groupChatTracker.trackInvite( groupName, playerInvited );

        //Send the invite through a broadcast
        //The broadcast function used takes into account ignore lists, this was a design decision
        String message = "Player "+ playerInviting + " has invited you to group chat [" + groupName.toLowerCase() +
                "].\nEnter \"JOIN " + groupName.toLowerCase() + "\" to join the group.";

        pInvited.getReplyWriter().println(message);
        return "[" + groupName.toLowerCase() + "] invite set to " + playerInvited + ".";
    }

    public void GCMessage( String groupName, String playerName, String rawInput)
    {
        //rawInput has the groupName at the start, it needs to be removed
        String message = rawInput.replaceAll( "^" + groupName, "[" + groupName.toLowerCase() + "] " + playerName + " : " );

        //Get list of all members in group chat
        ArrayList<String> group = groupChatTracker.groupMembers( groupName );

        //send message to all members of group chat
        Player temp;
        for( String member : group )
        {
            temp = playerList.findPlayer( member );
            temp.getReplyWriter().println(message);
        }
    }

    public String GCLeave( String groupName, String playerName)
    {
        //check that playerName is in the group
        if( !groupChatTracker.checkMembership( groupName, playerName) )
        {
            return "You're not in group [" + groupName + "].";
        }

        //remove player from group
        groupChatTracker.removeMember( groupName, playerName);

        return "You've left chat group [" + groupName + "].";

    }

    public String GCJoin( String groupName, String playerName)
    {
        //GroupChatTracker.acceptInvite() checks if player is invited
        return groupChatTracker.acceptInvite( groupName, playerName);
    }

    public boolean checkGCMembership( String groupName, String playerName)
    {
        return groupChatTracker.checkMembership( groupName, playerName);
    }
    public String GCGetHelp(String name)
    {
        return groupChatTracker.getHelp();
    }
    //416_GroupChat End
}
