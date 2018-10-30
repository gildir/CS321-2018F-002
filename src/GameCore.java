


import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
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
    private final Map map;
    
    private ArrayList<Battle> activeBattles; //Handles all battles for all players on the server.
    private ArrayList<Battle> pendingBattles;
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
     * Attempts to pick up an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to pickup.
     * @return Message showing success. 
     */    
    public String pickup(String name, String target) {
        Player player = this.playerList.findPlayer(name);
        if(player != null) {
            Room room = map.findRoom(player.getCurrentRoom());
            System.out.print(target);
            if(target.equals("all")){
                
              int obj_count = 0;
              Item object;
              String AllObjects = room.getObjects();
              while((object = room.getLastObject()) != null){
                player.addObjectToInventory(object);
                obj_count++;
              }
              if(obj_count > 0)
                return "You bend over and pick up all the objects";
              else
                return "No objects in this room";
            }
            else{
              
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
      return;
    }
  }
//Rock Paper Scissors Battle Methods -------------------------------------------

}
