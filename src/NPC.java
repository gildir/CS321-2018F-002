
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
  private int currentRoomId;
  private int pastRoomId;
  private long lastAiTime;
  private long aiPeriodSeconds;
  protected final GameCore gameCore;

  public NPC(GameCore gameCore, String name, int roomId, long aiPeriodSeconds) {
    this.name = name;
    this.currentRoomId = roomId;
    this.pastRoomId = 0;
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
  
  public int getCurrentRoomId(){
    return this.currentRoomId;
  }

  public Room getCurrentRoom() {
      return gameCore.getMap().findRoom(currentRoomId);
  }

  /**
   * @return the last room this NPC was in.
   * If this is the first room the NPC has been in, returns 0.
   */
  public int getPastRoomId(){
    return this.pastRoomId;
  }
  
  protected void setCurrentRoomId(int newRoomId){
    synchronized (this) {
      pastRoomId = currentRoomId;
      currentRoomId = newRoomId;
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

  protected void moveRandomly() {
    synchronized (this) {
      Exit exit = getCurrentRoom().getRandomValidExit(getPastRoomId());
      if (exit == null) {
        System.err.println("Resetting " + getName() + " to its previous room");
        getCurrentRoom().broadcast(name + " teleported away using hax");
        setCurrentRoomId(getPastRoomId());
      } else {
        getCurrentRoom().broadcast(name + " walked off to the " + exit.getDirection());
        setCurrentRoomId(exit.getRoom());
      }
      getCurrentRoom().broadcast(name + " walked into the area");
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