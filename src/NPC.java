//@author: Thomas Washington
//Team 6

import java.time.Instant;
import java.util.*;


abstract class NPC {
  
//Basic fields for NPC, which movoes on its own.
  private final String name;
  private int currentRoom; // initialized via constructor
  private int pastRoom;
  private long lastAiTime;
  private long aiPeriodSeconds;
  private Direction currentDirection;
  private LinkedList<Exit> exits;
  private GameCore object;
  
  public NPC(String name, int currentRoom, long aiPeriodSeconds){
    this.name = name;
    this.currentRoom = currentRoom;
    this.currentDirection = Direction.NORTH;
    exits = new Room(currentRoom, "", "").getExitsList();
    this.aiPeriodSeconds = aiPeriodSeconds;
  }
  
//Simple getters and setters
  public String getName(){
    return this.name;
  }
  public int getPastRoom(){
    return this.pastRoom;
  }
  
  protected void setCurrentRoom(int room){
    int temp = currentRoom;
    this.currentRoom = room;
    pastRoom = temp;
  }
  
  public int getCurrentRoom(){
    return this.currentRoom;
  }
  
  
  @Override
  public String toString() {
    return this.getName();
  }
  
  public void broadcast(Room room, String message) {
    for(Player player : object.getPlayerList()) {
      if(player.getCurrentRoom() == room.getId()) {
        player.getReplyWriter().println(message);
      }
    }
  }
// AI movement methods
  protected LinkedList<Exit> getCurrentExits(){
    LinkedList<Exit> exits = new LinkedList<Exit>();
    
    for (int i = 0; i < 4; i++){
      
    }
    return exits;
  }
  protected int getRandomRoom(){
    int randomRoom = exits.get(new Random().nextInt(exits.size())).getRoom();
    return randomRoom;
  }
  
  protected void moveRandomly() {
    synchronized (this) {
      setCurrentRoom(getRandomRoom());
      exits = getCurrentExits();
    }
  }
  
  public boolean tryAi() {
    // synchronized(this)
    final long secondsSinceLastAi = Instant.now().getEpochSecond() - lastAiTime;
    if (secondsSinceLastAi > aiPeriodSeconds) {
      doAi();
      lastAiTime = Instant.now().getEpochSecond();
      return true;
    } else
      return false;
  }
  
  protected void doAi() {
    moveRandomly();
  }
  
}
