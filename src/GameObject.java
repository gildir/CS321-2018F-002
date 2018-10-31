

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject;

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

    public String gift(String playerName, String name) throws RemoteException; 

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
    * Whispers "message" to specified player.
    * @param name1 Name of players sending message
    * @param name2 Name of player receiving message
    * @param message Message to whisper
    * @return Message showing success.
    */
    public String whisper(String name1, String name2, String message) throws RemoteException;


    /**
    * Sends a whisper the last player that whispered.
    * @param name Name of player replying to whisper
    * @param message Message to be whispered
    * @return Message showing success.
    */
    public String reply(String name, String message) throws RemoteException;
    
    @Override
    public String say(String name, String message) throws RemoteException {
        return core.say(name, message);
    }
      
    /**
     * Attempts to enter <location>. Use if entering a room that is part of another
     * room, instead of using move to walk to a separate room
     * @param name Name of the player to enter
     * @param location The place to enter
     * @return Message showing success
     * @throws RemoteException 
     */
    public String enter(String name, String location) throws RemoteException;
    
    /**
     * Makes player leave a room e.g shop
     * @param name Player Name
     * @return Message showing success
     * @throws RemoteException 
     */
    public String leaveRoom(String name) throws RemoteException;
    
    @Override
    public String move(String name, String direction) throws RemoteException {
        return core.move(name, direction);
    }
      

    /**
     * Attempts to drop off an object < object >. Will return a message on any success or failure.
     * @param name Name of the player to dropoff an object
     * @param object The case-insensitive name of the object to drop off.
     * @return Message showing success.
     * @throws RemoteException
     */
    public String dropoff(String name, String object) throws RemoteException;

     /**
     * @throws RemoteException 
     */    
    @Override
    public String pickup(String name, String target) throws RemoteException {
        return core.pickup(name, target);
    }    
    
    
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
    @Override
    public String inventory(String name) throws RemoteException {
        return core.inventory(name);
    }    
    
    /*
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

//Rock Paper Scissors Battle Code here
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
//Rock Paper Scissors Battle Code here
}
