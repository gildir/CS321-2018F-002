

import java.rmi.Remote;
import java.rmi.RemoteException;

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
    */
    public String whisper(String name1, String name2, String message) throws RemoteException;
    
    /**
     * Attempts to walk forward < distance > times.  If unable to make it all the way,
     *  a message will be returned.  Will display LOOK on any partial success.
     * @param name Name of the player to move
     * @param distance Number of rooms to move forward through.
     * @return Message showing success.
     * @throws RemoteException 
     */
    public String move(String name) throws RemoteException;

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
     * Leaves the game.
     * @param name Name of the player to leave
     * @throws RemoteException 
     */    
    public void leave(String name) throws RemoteException;       
//Rock Paper Scissors Battle Code Here---------------------------------
    public void challenge(String challenger, String player2) throws RemoteException;
    public void accept(String challenger, String player2) throws RemoteException;
    public void refuse(String challenger, String player2) throws RemoteException;
    public void rock(String player) throws RemoteException;
    public void paper(String player) throws RemoteException;
    public void scissors(String player) throws RemoteException;
//Rock Paper Scissors Battle Code Here---------------------------------
}
