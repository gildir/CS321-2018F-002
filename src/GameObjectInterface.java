

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
    * Whispers "message" to specified player.
    * @param name1 Name of players sending message
    * @param name2 Name of player receiving message
    * @param message Message to whisper
    * @return Message showing success.
    * @throws RemoteException
    */
    public String whisper(String name1, String name2, String message) throws RemoteException;
    
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
     * Attempts to drop off an object < object >. Will return a message on any success or failure.
     * @param name Name of the player to dropoff an object
     * @param object The case-insensitive name of the object to drop off.
     * @return Message showing success.
     * @throws RemoteException
     */
    public String dropoff(String name, String object) throws RemoteException;
    
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
//Rock Paper Scissors Battle Code Here---------------------------------
}
