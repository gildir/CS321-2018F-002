
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
  protected final GameCore gameCore;

  public NPC(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds) {
    this.name = name;
    this.currentRoom = currentRoom;
    this.pastRoom = 0;
    resetLastAiTime();
    this.aiPeriodSeconds = aiPeriodSeconds;
    this.gameCore = gameCore;
  }

  protected long getCurrentTime() {
      return Instant.now().getEpochSecond();
  }

  protected void resetLastAiTime() {
      lastAiTime = getCurrentTime();
  }

  public String getName(){
    return this.name;
  }
  
  public int getCurrentRoom(){
    return this.currentRoom;
  }

  /**
   * @return the last room this NPC was in.
   * If this is the first room the NPC has been in, returns 0.
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

  protected long getLastAiTime() {
      return lastAiTime;
  }

  protected long getAiPeriodSeconds() {
      return aiPeriodSeconds;
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
    gameCore.getMap().findRoom(currentRoom).broadcast(message);
  }

  protected void moveRandomly() {
    synchronized (this) {
      Exit exit = gameCore.getMap().findRoom(currentRoom).randomValidExit();
      broadcast(name + " walked off to the " + exit.getDirection());
      setCurrentRoom(exit.getRoom());
      broadcast(name + " walked into the area");
    }
  }
  
  public boolean tryAi() {
    synchronized (this) {
        final long secondsSinceLastAi = getCurrentTime() - getLastAiTime();
        if (secondsSinceLastAi > getAiPeriodSeconds()) {
            doAi();
            resetLastAiTime();
            return true;
        } else
            return false;
    }
  }
  
  protected void doAi() {
    synchronized (this) {
        moveRandomly();
    }
  }
} //EOF