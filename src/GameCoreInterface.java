import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public interface GameCoreInterface {
    
    /**
     * Broadcasts a message to all other players in the same room as player.
     * @param player Player initiating the action.
     * @param message Message to broadcast.
     */   
    public void broadcast(Player player, String message);
    
    /**
     * Broadcasts a message to all players in the specified room.
     * @param room Room to broadcast the message to.
     * @param message Message to broadcast.
     */   
    public void broadcast(Room room, String message);
    

    public String gift(String playerName, String name, double amount);

    public String acceptGift(String name);

    public String declineGift(String name);

    /**
     * Returns the player with the given name or null if no such player.
     * @param name Name of the player to find.
     * @return Player found or null if none.
     */
    public Player findPlayer(String name);
    
    /**
     * Allows a player to join the game.  If a player with the same name (case-insensitive)
     *  is already in the game, then this returns false.  Otherwise, adds a new player of 
     *  that name to the game.  The next step is non-coordinated, waiting for the player
     *  to open a socket for message events not initiated by the player (ie. other player actions)
     * @param name
     * @return Player that is added, null if player name is already registered to someone else
     */
    public Player joinGame(String name);
    
    /**
     * Returns a look at the area of the specified player.
     * @param playerName Player Name
     * @return String representation of the current area the player is in.
     */
    public String look(String playerName);
    
    /**
     * Turns the player left.
     * @param name Player Name
     * @return String message of the player turning left.
     */
    public String left(String name);
    
    /**
     * Turns the player right.
     * @param name Player Name
     * @return String message of the player turning right.
     */
    public String right(String name);    
    
    /**
     * Says "message" to everyone in the current area.
     * @param name Name of the player to speak
     * @param message Message to speak
     * @return Message showing success.
     */
    public String say(String name, String message, ArrayList<String> censorList);

    /**
    * Whispers "message" to specified player.
    * @param name1 Name of player sending message
    * @param name2 Name of player receiving message
    * @param message Message to whisper
    * @return Message Showing success.
    */
    public String whisper(String name1, String name2, String message, ArrayList<String> censorList);
    
    /**
     * Returns a string representation of all objects you are carrying.
     * @param name Name of the player to move
     * @return Message showing success.
     */    
    public String inventory(String name);
    
    /**
     * Returns a list of nearby players you can gift
     * @param name Player Name
     * @return String representation of nearby players.
     */
    public String giftable(String name);

    /**
     * Leaves the game.
     * @param name Name of the player to leave
     * @return Player that was just removed.
     */    
    public Player leave(String name);

    /**
     * Logs a string into a file
     * @param fileName name of the file to log in
     * @param log      message to log
     */
    public void log(String fileName, String log);

//Rock Paper Scissors Battle Code here--------------------------------------
public void challenge(String challenger, String player2, int rounds);
public void accept(String challenger, String player2);
public void refuse(String challenger, String player2);
public void doBattle(String challenger, String player2, int[] p1, int[] p2, Battle b, int rounds);
public void rock(String player);
public void paper(String player);
public void scissors(String player);
public void checkBoard(String player);
public String tutorial(String name);
public void topTen(String name);
public void getRank(String player);

//Rock Paper Scissors Battle Code here--------------------------------------
	/**
	 * @author James Bruce
	 * gives an ASCII art map of the world surrounding a player
	 * @param player the name of a player
	 * @return the ASCII art map
	 */
	public String map(String player);

    /* START 405_ignore */
    /**
     * Ignores player messages and whispers
     * @param name Name of the player ignoring
     * @param ignorePlayerName Name of player being ignored
     * @return Message showing success.
     */
    public String ignore(String name, String ignorePlayerName);
    /* END 405_ignore */

    /**
     * Ignores player messages and whispers
     * @param name Name of the current player
     * @param ignorePlayerName Name of player to unignore
     * @return Message showing success.
     */
    public String unIgnore(String name, String ignorePlayerName);

    // Whiteboards
    /**
     * Returns a string displaying the Whiteboard of the room the player is in.
     * @param  playerName
     * @return message to be displayed to player
     * @throws RemoteException
     */
    public String displayWhiteboard(String playerName);

    /**
     * [clearWhiteboard description]
     * @param  playerName
     * @return message to be displayed to player
     * @throws RemoteException
     */
    public String clearWhiteboard(String playerName);

    /**
     * [writeWhiteboard description]
     * @param  playerName
     * @param  message
     * @return message to be displayed to player
     * @throws RemoteException
     */
    public String writeWhiteboard(String playerName, String message);

    /* START 416_GroupChat */
    /**
     * Creates group chat
     * @return Success or Failure message.
     */
    public String createGroupChat(String groupChatName, String playerName);

    /**
     * @return print out of groupChatName HashMap entry.
     */
    public String printGroupChat(String groupChatName);


    /**
     *  check if group chat exists
     *  @return true if group with name exists, otherwise false
     */
    public boolean checkGCExists(String groupChatName);

     /**
     * Invites player to join a group chat
     * @return feed back to the user.
     */
    public String GCInvite( String groupName, String playerInvited, String playerInviting);

    public void GCMessage( String groupName, String playerName, String rawInput);

    public String GCLeave( String groupName, String playerName);

    public String GCJoin( String groupName, String playerName);

    public boolean checkGCMembership( String groupName, String playerName);
    
    public String GCGetHelp(String name);
    
    public void GCPlayerQuit( String playerName);
    /* END 416_GroupCat */

    /* START 409_censor */
    public void setPlayerCensorList( ArrayList<String> censorList, String playerName );
    
    public ArrayList<String> getPlayerCensorList( String playerName );
    /* END 409_censor */
}
