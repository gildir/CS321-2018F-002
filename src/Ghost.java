import java.util.*;
import java.io.*;
/**
 *@author: Thomas Washington
 *
 */
/**
 * Basic Ghost class, which extends NPC
 * Works the same, but overrides methods that broadcast movements.
 * Also says a line from time to time.
 */
public class Ghost extends NPC{
  
  private ArrayList<String> lines;
  
  public Ghost(GameCore gameCore, String name, int roomId, long aiPeriodSeconds, File sayings){
    super(gameCore, name, roomId, aiPeriodSeconds);
    lines = new ArrayList<String>();
    Scanner scan;
    try{
      scan = new Scanner(sayings);
    }
    catch(FileNotFoundException e){
      scan = null;
    }
    
    while(scan != null && scan.hasNextLine()){
      lines.add(scan.nextLine()); 
    }  
  }
  
  public ArrayList<String> getLines(){
    return this.lines;
  }
  
  public String getLine(int index){
    return getLines().get(index);
  }
  
  /**
   * Output a ghost saying to everyone in the same room as this ghost.
   * @overrides
   */
  public void broadcast() {
    Random index = new Random();
    int randomLineIndex = index.nextInt(lines.size());
    String message = getLine(randomLineIndex);
    getCurrentRoom().broadcast(message + ", said " + getName() + ".");
  }
  
  @Override
  public void moveRandomly() {
    synchronized (this) {
      Exit exit = getCurrentRoom().getRandomValidExit();
      if (exit == null) {
        System.err.println("Resetting " + getName() + " to its previous room");
        getCurrentRoom().broadcast(getName() + " teleported away using hax");
        setCurrentRoomId(getPastRoomId());
      } else {
        // System.out.println("Moving " + getName() + " to " + exit.getRoom()); // debug message
        setCurrentRoomId(exit.getRoom());
      }
      this.broadcast();
    }
  }
} //EOF Ghost.java 