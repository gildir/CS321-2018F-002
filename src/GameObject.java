


import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public class GameObject extends UnicastRemoteObject implements GameObjectInterface {
    private final GameCore core;
    
    /**
     * Creates a new GameObject.  Namely, creates the map for the rooms in the game,
     *  and establishes a new, empty, player list.
     * @throws RemoteException 
     */
     //now takes a filename for the Map
    public GameObject(String filename) throws RemoteException {
        super();
        //filename for the Map
        core = new GameCore(filename);
    }

    /**
     * Links an asynchronous event message connection to a player.
     * @param playerName Player to link the reply socket with.
     * @param writer PrintWriter to use for asynchronous messages.
     * @return true if player is found, false otherwise.
     */
    public boolean setReplyWriter(String playerName, PrintWriter writer) {
        Player player = core.findPlayer(playerName);
        if(player != null && writer != null) {
            player.setReplyWriter(writer);
            return true;
        }
        return false;
    }    
  
    
    /**
     * Allows a player to join the game.  If a player with the same name (case-insensitive)
     *  is already in the game, then this returns false.  Otherwise, adds a new player of 
     *  that name to the game.  The next step is non-coordinated, waiting for the player
     *  to open a socket for message events not initiated by the player (ie. other player actions)
     * @param name
     * @return true is player is added, false if player name is already registered to someone else
     * @throws RemoteException 
     */
    @Override
    public boolean joinGame(String name) throws RemoteException {
        // Request join to the core and return the results back to the remotely calling method.
        return (core.joinGame(name) != null);
    }
        
    /**
     * Returns a look at the area of the specified player.
     * @param playerName Player Name
     * @return String representation of the current area the player is in.
     * @throws RemoteException 
     */
    @Override
    public String look(String playerName) throws RemoteException {
        return core.look(playerName);
    }       

    //Author Shayan AH
    public String listAllPlayers(String name)throws RemoteException
    {
        return core.listAllPlayers(name);
    } 
     
    /**
     * Turns the player left.
     * @param name Player Name
     * @return String message of the player turning left.
     * @throws RemoteException 
     */
    @Override
    public String left(String name) throws RemoteException {
        return core.left(name);
    }
       
    /**
     * Turns the player right.
     * @param name Player Name
     * @return String message of the player turning right.
     * @throws RemoteException 
     */
    @Override
    public String right(String name) throws RemoteException {
        return core.right(name);
    }    
       
    /**
     * Says "message" to everyone in the current area.
     * @param name Name of the player to speak
     * @param message Message to speak
     * @return Message showing success.
     * @throws RemoteException 
     */
    @Override
    public String say(String name, String message) throws RemoteException {
        return core.say(name, message);
    }

    /**
    * Whispers "message" to specified player.
    * @param name1 Name of player sending message
    * @param name2 Name of player receiving message
    * @param message Message to whisper
    * @return Message showing success.
    * @throws RemoteException
    */
    @Override
    public String whisper(String name1, String name2, String message) throws RemoteException
    {
        return core.whisper(name1, name2, message);
    }

    /**
    * Sends a whisper the last player that whispered.
    * @param name Name of player replying to whisper
    * @param message Message to be whispered
    * @return Message showing success.
    * @throws RemoteException
    */
    @Override
    public String reply(String name, String message) throws RemoteException
    {
        return core.reply(name, message);
    }
      
    /**
     * Attempts to walk forward < distance > times.  If unable to make it all the way,
     *  a message will be returned.  Will display LOOK on any partial success.
     * @param name Name of the player to move
     * @param distance Number of rooms to move forward through.
     * @return Message showing success.
     * @throws RemoteException 
     */
    @Override
    public String move(String name, String direction) throws RemoteException {
        return core.move(name, direction);
    }
 
    /**
     * Attempts to enter <location> shop. Use if entering a room that is part of another
     * room, instead of using move to walk to a separate room
     * @param name Name of the player to enter
     * @param location The place to enter
     * @return Message showing success
     * @throws RemoteException
     */
    @Override
    public String enter(String name, String location) throws RemoteException{
     return core.enter(name, location);
    }
    
    /**
     * Makes player leave a room e.g shop
     * @param name Player Name
     * @return Message showing success
     */
    public String leaveRoom(String name) {
     return core.leaveRoom(name);
    }

    /**
     * Attempts to pick up an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to pickup.
     * @return Message showing success.
     * @throws RemoteException 
     */    
    @Override
    public String pickup(String name, String target) throws RemoteException {
        return core.pickup(name, target);
    }  

	/**
     * Attempts to pick up an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to pickup.
     * @return Message showing success.
     * @throws RemoteException 
     */    
    @Override
    public String describe(String name, String target) throws RemoteException {
        return core.describe(name, target);
    }
	
    /**
     * Attempts to drop off an object < target >. Will return a message on any success or failure.
     * @param name Name of the player to move
     * @param target The case-insensitive name of the object to dropoff.
     * @return Message showing success.
     * @throws RemoteException
     */
    @Override
    public String dropoff(String name, String target) throws RemoteException {
        return core.dropoff(name, target);
    }
    /**
     * Attempts to offer an item < target > from a player < player > to a player < nameOffered >. Will return a message on success or failure.
     * @param player The player offering the item
     * @param nameOffered Name of the person being offered an item
     * @param target The name of the item to offer
     * @return A message showing success.
     * @throws RemoteException
     */
    public String offerItem(String playerName, String nameOffered, String target) throws RemoteException {
        return core.offerItem(playerName, nameOffered, target);
    }

    /**
     * Player pokes a ghoul that is in the same room.
     * @param ghoulName Name of the ghoul that is poked
     * @param playerName Name of the player that pokes the ghoul.
     * @return Message showing success or failure of the poking action.
     */
    public String pokeGhoul(String playerName, String ghoulName) throws RemoteException {
        return core.pokeGhoul(playerName, ghoulName);
    }

    /**
     * Player gifts a ghoul that is in the same room an object. This action decreases the ghoul's aggression.
     * @param playerName Name of the player that gifts the ghoul.
     * @param target The case-insensitive name of the object that is gifted.
     * @param ghoulName Name of the ghoul that receives the gift.
     * @return Message showing success or failure of the gifting action.
     */
    public String giftGhoul(String playerName, String ghoulName, String target) throws RemoteException {
        return core.giftGhoul(playerName, ghoulName, target);
    }
    

    @Override
    public String gift(String yourname,String name, double amount) throws RemoteException {
     return core.gift(yourname, name,amount);   
    }
    
    @Override
    public String money(String name) throws RemoteException {
        return core.money(name);
    }
    
    /**
     * Returns a string representation of all objects you are carrying.
     * @param name Name of the player to move
     * @return Message showing success.
     * @throws RemoteException 
     */    
    @Override
    public String inventory(String name) throws RemoteException {
        return core.inventory(name);
    }    
    
     /**
     * Returns a list of nearby players you can gift
     * @param name Player Name
     * @return String representation of nearby players.
     * @throws RemoteException 
     */
    @Override
    public String giftable(String playerName) throws RemoteException {
        return core.giftable(playerName);
    } 
    
    /**
     * Sell an item to the shop the player is currently in
     * @param playerName player who is selling
     * @param itemName item to sell
     * @return A string indicating success or failure
     */
    public String sell(String playerName, String itemName) throws RemoteException{
     return core.sell(playerName, itemName);
    }
    
     /**
     * Leaves the game.
     * @param name Name of the player to leave
     * @throws RemoteException 
     */    
    @Override
    public void leave(String name) throws RemoteException {
        Player player = core.leave(name);
        if(player != null) {
            player.getReplyWriter().close();
        }
    }    

    /**
     * Logs a player interaction with the world, ie the execution of a command.
     *
     * @param  name    Name of the player
     * @param  command String containing the command called
     * @param  args    Array containing the arguments as strings
     * @param  output  String containing the result of executing the command
     * @throws RemoteException
     */
    @Override
    public void logInteraction(String name, String command, ArrayList<String> args, String output) throws RemoteException {
        StringBuilder sb = new StringBuilder();

        // Add timestamp and name of the player to begining of string
        sb.append("[" + new java.util.Date() + "] [" + name.toUpperCase() + "]\n");

        // Append command and args. If no args append null
        if (args != null)   sb.append("\t-> " + command + "(" + String.join(", ", args) + ")\n");
        else                sb.append("\t-> " + command + "()\n");

        // Append output. If no output append null
        if (output != null) sb.append("\t<- \"" + output + "\"\n");
        else                sb.append("\t<- \"null\"\n");

        String file = name + ".log";
        String log = sb.toString();
        
        core.log(file, log);
    }
	//Rock Paper Scissors Battle Code here--------------------------------------
	public void challenge(String challenger, String player2) throws RemoteException
	{
		core.challenge(challenger, player2);
	}

	public void accept(String challenger, String player2) throws RemoteException
	{
		core.accept(challenger,player2);
	}
	public void refuse(String challenger, String player2) throws RemoteException
	{
		core.refuse(challenger, player2);
	}
	public void rock(String player) throws RemoteException
	{
		core.rock(player);
	}
	public void paper(String player) throws RemoteException
	{
		core.paper(player);
	}
	public void scissors(String player) throws RemoteException
	{
		core.scissors(player);
	}
  public String tutorial(String name) throws RemoteException
  {
      return core.tutorial(name);
  }
	public void checkBoard(String player) throws RemoteException {
		core.checkBoard(player);
	}
  //Rock Paper Scissors Battle Code here--------------------------------------
}
