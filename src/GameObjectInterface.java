

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public interface GameObjectInterface extends Remote {

    /**
     * Sends a request to join the game.  
     * Returns with the status of the join.  On true, the server expects the client
     *  will initiate a socket connection, to serve as an asynchronous, unsolicited
     *  String transfer mechanism.
     * @param name Player Name. 
     * @return true if name is available and join is successful, false otherwise.
     * @throws RemoteException 
     */
    public boolean joinGame(String name) throws RemoteException;

    public String gift(String playerName, String name, double amount) throws RemoteException;

    public void setChatPrefix(String prefix) throws RemoteException;

    /**
    * Changes the chat prefix to the new prefix specified by the player.
    * @param prefix New chat prefix to be set.
    * @return Returns message saying whether the prefix was successfully changed or not.
    * @throws RemoteException
    */
    public String changeChatPrefix(String prefix) throws RemoteException;

    /**
     * Returns a look at the area of the specified player.
     * @param name Player Name
     * @return String representation of the current area the player is in.
     * @throws RemoteException 
     */
    public String look(String name) throws RemoteException;

    //author Shayan AH
    public String listAllPlayers(String name) throws RemoteException;

    /**
     * Turns the player left.
     * @param name Player Name
     * @return String message of the player turning left.
     * @throws RemoteException 
     */
    public String left(String name) throws RemoteException;
    
     /**
     * Turns the player right.
     * @param name Player Name
     * @return String message of the player turning right.
     * @throws RemoteException 
     */
    public String right(String name) throws RemoteException;
   
    /**
     * Says "message" to everyone in the current area.
     * @param name Name of the player to speak
     * @param message Message to speak
     * @return Message showing success.
     * @throws RemoteException 
     */
    public String say(String name, String message) throws RemoteException;

    /**
    * Shouts "message" to everyone in the game.
    * @param name Name of the player shouting
    * @param message Message to be shouted
    * @return Message showing success
    * @throws RemoteException
    */
    public String shout(String name, String message) throws RemoteException;

    /**
    * Whispers "message" to specified player.
    * @param name1 Name of players sending message
    * @param name2 Name of player receiving message
    * @param message Message to whisper
    * @return Message showing success.
    * @throws RemoteException
    */
    public String whisper(String name1, String name2, String message) throws RemoteException;

    /**
    * Sends a whisper the last player that whispered.
    * @param name Name of player replying to whisper
    * @param message Message to be whispered
    * @return Message showing success.
    */
    public String reply(String name, String message) throws RemoteException;

    /**
     * Attempts to walk forward < distance > times.  If unable to make it all the way,
     *  a message will be returned.  Will display LOOK on any partial success.
     * @param name Name of the player to move
     * @return Message showing success.
     * @throws RemoteException 
     */
    public String move(String name, String direction) throws RemoteException;

    /**
     * Attempts to pick up an object < object >. Will return a message on any success or failure.
     * @param name Name of the player to pickup an object
     * @param object The case-insensitive name of the object to pickup.
     * @return Message showing success.
     * @throws RemoteException 
     */    
    public String pickup(String name, String object) throws RemoteException;
	
	/**
     * Attempts to pick up an object < object >. Will return a message on any success or failure.
     * @param name Name of the player to pickup an object
     * @param object The case-insensitive name of the object to pickup.
     * @return Message showing success.
     * @throws RemoteException 
     */    
    public String describe(String name, String object) throws RemoteException;
	
    /**
     * Attempts to drop off an object < object >. Will return a message on any success or failure.
     * @param name Name of the player to dropoff an object
     * @param object The case-insensitive name of the object to drop off.
     * @return Message showing success.
     * @throws RemoteException
     */
    public String dropoff(String name, String object) throws RemoteException;
    /**
     * Attempts to offer an item < target > from a player < player > to a player < nameOffered >. Will return a message on success or failure.
     * @param player The player offering the item
     * @param nameOffered Name of the person being offered an item
     * @param target The name of the item to offer
     * @return A message showing success.
     * @throws RemoteException
     */
    public String offerItem(String playerName, String nameOffered, String target) throws RemoteException;

     /**
     * Returns a string representation of all objects you are carrying.
     * @param name Name of the player to view their inventory
     * @return Message showing success.
     * @throws RemoteException
     */
    public String inventory(String name) throws RemoteException;

    /**
     * Player pokes a ghoul that is in the same room.
     * @param ghoulName Name of the ghoul that is poked
     * @param playerName Name of the player that pokes the ghoul.
     * @return Message showing success or failure of the poking action.
     */
    public String pokeGhoul(String playerName, String ghoulName) throws RemoteException;

    /**
     * Player gifts a ghoul that is in the same room an object. This action decreases the ghoul's aggression.
     * @param playerName Name of the player that gifts the ghoul.
     * @param target The case-insensitive name of the object that is gifted.
     * @param ghoulName Name of the ghoul that receives the gift.
     * @return Message showing success or failure of the gifting action.
     */
    public String giftGhoul(String playerName, String ghoulName, String target) throws RemoteException;

    /**
     * Returns a list of nearby players you can gift.
     * @param name Player Name
     * @return String representation of nearby players.
     * @throws RemoteException
     */
    public String giftable(String name) throws RemoteException;

    /**
     * Returns a string representation of money you have
     * @param name Name of the player to view their money
     * @return Message containing player money
     * @throws RemoteException
     */
    public String money(String name) throws RemoteException;

    /**
     * Sell an item to the shop the player is currently in
     * @param playerName player who is selling
     * @param itemName item to sell
     * @return A string indicating success or failure
     * @throws RemoteException
     */
    public String sell(String playerName, String itemName) throws RemoteException;
    
    /**
     * Buy an item from the shop the player is currently in
     * @param playerName player who is selling
     * @param itemName item to buy
     * @return A string indicating success or failure
     */
    public String buy(String playerName, String itemName) throws RemoteException;

    /**
     * Leaves the shop.
     * @param name Name of the player to leave
     * @throws RemoteException
     */
    public String leaveRoom(String playerName) throws RemoteException;

    /**
     * Enters the shop.
     * @param name Name of the player to enter, shop for location
     * @throws RemoteException
     */
    public String enter(String playerName, String location) throws RemoteException;


    /**
     * Leaves the game.
     * @param name Name of the player to leave
     * @throws RemoteException 
     */    
    public void leave(String name) throws RemoteException;

    /**
     * Logs a player interaction with the world, ie the execution of a command.
     *
     * @param  name    Name of the player
     * @param  command String containing the command called
     * @param  args    Array containing the arguments as strings
     * @param  output  String containing the result of executing the command
     * @throws RemoteException
     */
    public void logInteraction(String name, String command, ArrayList<String> args, String output) throws RemoteException;

//Rock Paper Scissors Battle Code Here---------------------------------
    public void challenge(String challenger, String player2) throws RemoteException;
    public void accept(String challenger, String player2) throws RemoteException;
    public void refuse(String challenger, String player2) throws RemoteException;
    public void rock(String player) throws RemoteException;
    public void paper(String player) throws RemoteException;
    public void scissors(String player) throws RemoteException;
    public String tutorial(String name) throws RemoteException;
    public void checkBoard(String player) throws RemoteException;
//Rock Paper Scissors Battle Code Here---------------------------------
  
	/**
	 * gives an ASCII art map of the world surrounding a player
	 * @param player the name of a player
	 * @return the ASCII art map
	 */
	public String map(String player) throws RemoteException;
  
    //405
    public String ignore(String name, String ignoreName) throws RemoteException;
    //407
    public String listIgnoredPlayers(String playerName)throws RemoteException;
    //408
    public String unIgnore(String name, String ignoreName) throws RemoteException;

    // Whiteboards
    /**
     * Returns a string displaying the Whiteboard of the room the player is in.
     * @param  playerName
     * @return message to be displayed to player
     * @throws RemoteException
     */
    public String displayWhiteboard(String playerName) throws RemoteException;
    
    /**
     * [clearWhiteboard description]
     * @param  playerName
     * @return message to be displayed to player
     * @throws RemoteException
     */
    public String clearWhiteboard(String playerName) throws RemoteException;
    
    /**
     * [writeWhiteboard description]
     * @param  playerName
     * @param  message
     * @return message to be displayed to player
     * @throws RemoteException
     */
    public String writeWhiteboard(String playerName, String message) throws RemoteException;
}
