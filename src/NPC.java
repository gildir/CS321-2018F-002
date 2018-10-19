
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
  private long lastAiTime;
  private long aiPeriodSeconds;
  private GameCore gameCore;

  public NPC(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds) {
    this.name = name;
    this.currentRoom = currentRoom;
    this.pastRoom = -1;
    this.lastAiTime = Instant.now().getEpochSecond();
    this.aiPeriodSeconds = aiPeriodSeconds;
    this.gameCore = gameCore;
  }

  public String getName(){
    return this.name;
  }
  
  public int getCurrentRoom(){
    return this.currentRoom;
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
  
  @Override
  public String toString() {
    return this.getName();
  }
  
  protected void broadcast(String message) {
    gameCore.broadcast(gameCore.getMap().findRoom(currentRoom), message);
  }

  
   /* The exit object returned from that call will include everything you need to output proper broadcast messages
   * before and after you call setCurrentRoom
   */
  protected void moveRandomly() {
    synchronized (this) {
      Exit exit = gameCore.getMap().findRoom(currentRoom).randomExit();
      broadcast(exit.getMessage());
      setCurrentRoom(exit.getRoom());
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
