
import java.time.Instant;
import java.util.*;

/**
 * @author Thomas Washington
 * @author Kevin Rickard
 */

/**
 * Basic NPC, which moves on its own.
 */
abstract class NPC {

  private String name;
  private int currentRoom; // initialized via constructor
  private int pastRoom;
  // TODO remove plz
  private Direction currentDirection;

  private long lastAiTime;
  private long aiPeriodSeconds;
  private GameCore gameCore;

  public NPC(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds) {
    this.name = name;
    this.currentRoom = currentRoom;
    this.pastRoom = -1;
    // TODO remove plz
    this.currentDirection = Direction.NORTH;

    this.lastAiTime = Instant.now().getEpochSecond();
    this.aiPeriodSeconds = aiPeriodSeconds;
    this.gameCore = gameCore;
  }

  // TODO remove plz
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

  // TODO remove plz
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

  public String getName(){
    return this.name;
  }

  public int getPastRoom(){
    return this.pastRoom;
  }
  
  protected void setCurrentRoom(int newRoom){
      synchronized (this) {
          pastRoom = currentRoom;
          currentRoom = newRoom;
      }
  }
  
  public int getCurrentRoom(){
    return this.currentRoom;
  }

  // TODO remove plz
  public String getCurrentDirection() {
    return this.currentDirection.name();
  }

  // TODO remove plz
  public Direction getDirection() {
    return this.currentDirection;
  }
  
  @Override
  public String toString() {
    return this.getName() + ": " + currentDirection.toString();
  }
  
  protected void broadcast(String message) {
    gameCore.broadcast(gameCore.getMap().findRoom(currentRoom), message);
  }

  /* TODO fix moveRandomly. This will currently setCurrentRoom to a random number in the range of the size of the exits list?
   *
   * I would suggest making a method in the Room class that returns a random exit object when given a current room.
   *
   * The exit object returned from that call will include everything you need to output proper broadcast messages
   * before and after you call setCurrentRoom
   */
  protected void moveRandomly() {
    synchronized (this) {
      setCurrentRoom(new Random().nextInt(gameCore.getMap().findRoom(currentRoom).getExitsList().size()));
      broadcast(this.getName() + " has moved to the " + this.getCurrentRoom());
    }
  }
  
  public boolean tryAi() {
    final long secondsSinceLastAi = Instant.now().getEpochSecond() - lastAiTime;
    if (secondsSinceLastAi > aiPeriodSeconds) {
      doAi();
      lastAiTime = Instant.now().getEpochSecond();
      return true;
    } else
      return false;
  }
  
  protected void doAi() {
      synchronized (this) {
          moveRandomly();
      }
  }
}
