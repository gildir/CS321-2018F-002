


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
    public GameObject() throws RemoteException {
        super();
        
        core = new GameCore();
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
//Rock Paper Scissors Battle Code here--------------------------------------
}
