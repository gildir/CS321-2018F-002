
import java.time.Instant;

/**
 * @author Thomas Washington
 * @author Kevin Rickard
 */

/**
 * Basic NPC, which moves on its own.
 */
abstract class NPC {

  private String name;
  private int currentRoom;
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

  /**
   * @return the last room this NPC was in.
   * If this is the first room the NPC has been in, returns -1.
   */
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

  /**
   * Output a message to all players in the same room as this NPC.
   * @param message to output.
   */
  public void broadcast(String message) {
    gameCore.broadcast(gameCore.getMap().findRoom(currentRoom), message);
  }

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
