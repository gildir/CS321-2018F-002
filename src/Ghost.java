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
  
  public Ghost(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds, File sayings){
    super(gameCore, name,currentRoom, aiPeriodSeconds);
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
    int getter = index.nextInt(lines.size());
    String message = getLine(getter);
    gameCore.broadcast(gameCore.getMap().findRoom(getCurrentRoom()), message);
  }
  
  @Override
  public void moveRandomly() {
    synchronized (this) {
      Exit exit = gameCore.getMap().findRoom(getCurrentRoom()).randomValidExit();
      setCurrentRoom(exit.getRoom());
      this.broadcast();
    }
  }
} //EOF Ghost.java 