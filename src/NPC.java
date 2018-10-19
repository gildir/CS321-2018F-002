//@author: Thomas Washington
//Team 6

import java.time.Instant;
import java.util.*;


abstract class NPC {
  
//Basic fields for NPC, which movoes on its own.
  private String name;
  private int currentRoom; // initialized via constructor
  private int pastRoom;
  private Direction currentDirection;
  private long lastAiTime;
  private long aiPeriodSeconds;
  private GameCore object;
  
  public NPC(String name){
    this.name = name;
    this.currentRoom = 1;
    this.currentDirection = Direction.NORTH;
  }
  public NPC(String name, int currentRoom, long aiPeriodSeconds){
    this.name = name;
    this.currentRoom = currentRoom;
    this.currentDirection = Direction.NORTH;
    this.aiPeriodSeconds = aiPeriodSeconds;
  }
  
  public void turnLeft() {
    switch(this.currentDirection.toString()) {
      case "North":
        this.currentDirection = Direction.WEST;
        break;
      case "South":
        this.currentDirection = Direction.EAST;
        break;
      case "East":
        this.currentDirection = Direction.NORTH;
        break;
      case "West":
        this.currentDirection = Direction.SOUTH;
        break;                
    }
  }
  
  public void turnRight() {
    switch(this.currentDirection.toString()) {
      case "North":
        this.currentDirection = Direction.EAST;
        break;
      case "South":
        this.currentDirection = Direction.WEST;
        break;
      case "East":
        this.currentDirection = Direction.SOUTH;
        break;
      case "West":
        this.currentDirection = Direction.NORTH;
        break;                
    }
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
  
  public String getCurrentDirection() {
    return this.currentDirection.name();
  }
  
  public Direction getDirection() {
    return this.currentDirection;
  }
  
  @Override
  public String toString() {
    return this.getName() + ": " + currentDirection.toString();
  }
  
  private void broadcast(String message) {
    object.broadcast(object.getMap().findRoom(currentRoom), message);
  }
  
// AI movement methods
  
  protected int getRandomRoom(){
    int randomRoom = 0;
//exits.get(new Random().nextInt(exits.size())).getRoom();
    return randomRoom;
  }
  
  protected void moveRandomly() {
    synchronized (this) {
      setCurrentRoom(getRandomRoom());
      //exits = new Room(currentRoom, "", "").getExitsList();
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
