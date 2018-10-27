

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
    * Broadcasts a message to a specified player.
    * @param sendingPlayer Player sending message
    * @param receivingPlayer Player receiving message
    * @param message Message to broadcast
    */
    public void broadcast(Player sendingPlayer, Player receivingPlayer, String message);
    
    /**
     * Broadcasts a message to all players in the specified room.
     * @param room Room to broadcast the message to.
     * @param message Message to broadcast.
     */   
    public void broadcast(Room room, String message);
    
    public String gift(String playerName, String name); 
    
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
    public String say(String name, String message);
    
    /**
    * Whispers "message" to specified player.
    * @param name1 Name of player sending message
    * @param name2 Name of player receiving message
    * @param message Message to whisper
    * @return Message Showing success.
    */
    public String whisper(String name1, String name2, String message);
    
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
     * Returns a string representation of money you have
     * @param name Name of the player 
     * @return Player who wants to see his/her money 
     */ 
    public String money(String name);
    

     */    
    public Player leave(String name);    
//Rock Paper Scissors Battle Code here--------------------------------------
public void challenge(String challenger, String player2);
public void accept(String challenger, String player2);
public void refuse(String challenger, String player2);
public void doBattle(String challenger, String player2, int p1, int p2, Battle b);
public void rock(String player);
public void paper(String player);
public void scissors(String player);
//Rock Paper Scissors Battle Code here--------------------------------------
public void checkBoard(String player);
public String tutorial(String name);
}
