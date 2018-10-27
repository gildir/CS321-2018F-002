
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public class GameCore implements GameCoreInterface {
    private final PlayerList playerList;
    private final Map map;
    
    private ArrayList<Battle> activeBattles; //Handles all battles for all players on the server.
    private ArrayList<Battle> pendingBattles;

	// Added by Brendan
	private Leaderboard leaderboard;

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

		// Added by Brendan
		this.leaderboard = new Leaderboard();

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
            if(otherPlayer != player && otherPlayer.getCurrentRoom() == player.getCurrentRoom()) {
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
        if(sendingPlayer != receivingPlayer) {
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

			// Added by Brendan
			this.leaderboard.addScore(name);

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
	
        if(playerSending != null && playerReceiving != null) {
            if(name1.equalsIgnoreCase(name2)) {
                return "You cannot whisper yourself.";
            }
            this.broadcast(playerSending, playerReceiving, playerSending.getName() + " whispers, \"" + message + "\"");
            playerReceiving.setLastWhisperName(name1);
            return "Message sent to " + playerReceiving.getName();
        }
        else {
            if(playerReceiving == null) {
                return "Could not find player online.";
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
     * Attempts to enter <location>. Use if entering a room that is part of another
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
        newID = 10;
      else
        return location + " is unknown.";
      //if player not near a shop, return.
      if(player.getCurrentRoom() != 1)
        return "Not near " + location;
      Room room = map.findRoom(player.getCurrentRoom());
      this.broadcast(player, player.getName() + " has walked off towards the shop");
      player.getReplyWriter().println("You enter the shop");
      player.setCurrentRoom(newID);
      this.broadcast(player, player.getName() + " just walked into the shop.");
      player.getReplyWriter().println(this.map.findRoom(player.getCurrentRoom()).toString(playerList, player));
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
      if(player.getCurrentRoom() == 10)
        newID = 1;
      else
        return "Can't leave, did you mean quit?";
      Room room = map.findRoom(player.getCurrentRoom());
      this.broadcast(player, player.getName() + " has left the shop");
      player.getReplyWriter().println("You leave the room");
      player.setCurrentRoom(newID);
      this.broadcast(player, player.getName() + " just walked into the area.");
      player.getReplyWriter().println(this.map.findRoom(player.getCurrentRoom()).toString(playerList, player));
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
        // System.out.print(target);
        if (target.equals("all")) {

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

        } else {
          Item object = room.removeObject(target);
          if (player.getCurrentInventory().size() >= 10)
          {
              this.broadcast(player, player.getName() + " tried to pick something up, but was holding too many items.");
              return "You try to pick up the " + target + ", but can't because you're holding too many items.";
          }
          if(object != null) {
            player.addObjectToInventory(object);
            this.broadcast(player, player.getName() + " bends over to pick up a " + target + " that was on the ground.");
            return "You bend over and pick up a " + target + ".";
          } else {
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
    @Override
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
    public String gift(String yourname ,String name){
        Player player = this.playerList.findPlayer(name); 
        Player you = this.playerList.findPlayer(yourname); 
      
        System.out.println("YOUR NAME IS: " + you);
        if(player != null){
            this.broadcast(you, you.getName() + " offers a gift to " + player.getName());
            return "You offer " + player.getName() + " a gift"; 
      }else{
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

    if(play2 == null)//other player doesnt exist
    {
      play1.getReplyWriter().println("That player doesn't exist.");
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
        play1.getReplyWriter().println("You can only be in one battle at a time. Finish the current one you are in before challenging someone else.");
        return;
      }
      if(b.containsPlayer(player2))// Other Player is already in an active battle.
      {
        play1.getReplyWriter().println("You cant challenge " + player2 + " right now, they're currently in a battle. ");
        return;
      }
    }

    for(Battle b : pendingBattles)//Challenger already asked this person to battle and is waiting for a response still.
    {
      if(b.hasPlayers(challenger,player2))
      {
        play1.getReplyWriter().println("You already have a pending challenge request with "+ player2 +".");
        return;
      }
    }
    play2.getReplyWriter().println(challenger + " has challenged you to a Rock Paper Scissors Battle. \n\nTo accept, type 'Accept " + challenger + "' and press ENTER." + "\n\nTo decline, type 'Refuse " + challenger + "' and press ENTER." );
    pendingBattles.add(new Battle(challenger, player2));
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

	  // Added by Brendan
	  this.leaderboard.incrementScore(play1.getName());

      return;
    }
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
