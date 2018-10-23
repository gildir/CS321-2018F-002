
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
/* START 405_ignore */
import java.util.ArrayList;
/* END 405_ignore */

/**
 *
 * @author Kevin
 */
public class Player {
    private LinkedList<String> currentInventory;
    private String name;
    private int currentRoom;
    private Direction currentDirection;
    private PrintWriter replyWriter = null;
    private DataOutputStream outputWriter = null;
    /* START 405_ignore variables*/
    private ArrayList<String> ignoreList;
    private ArrayList<String> ignoredByList;
    /* END 405_ignore variables*/
	
    public Player(String name) {
        this.currentRoom = 1;
        this.currentDirection = Direction.NORTH;
        this.name = name;
        this.currentInventory = new LinkedList<>();
		/* START 405_ignore*/
        this.ignoreList = new ArrayList<String>();
        this.ignoredByList = new ArrayList<String>();
        /* END 405_ignore  */
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
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<String> getCurrentInventory() {
        return currentInventory;
    }

    public void setCurrentInventory(LinkedList<String> currentInventory) {
        this.currentInventory = currentInventory;
    }
    
    public void addObjectToInventory(String object) {
        this.currentInventory.add(object);
    }
    
    public void setReplyWriter(PrintWriter writer) {
        this.replyWriter = writer;
    }
    
    public PrintWriter getReplyWriter() {
        return this.replyWriter;
    }
    
    public void setOutputWriter(DataOutputStream writer) {
        this.outputWriter = writer;
    }
    
    public DataOutputStream getOutputWriter() {
        return this.outputWriter;
    }
    
    public int getCurrentRoom() {
        return this.currentRoom;
    }
    
    public void setCurrentRoom(int room) {
        this.currentRoom = room;
    }
    
    public String getCurrentDirection() {
        return this.currentDirection.name();
    }
    
    public Direction getDirection() {
        return this.currentDirection;
    }
    
    public String viewInventory() {
        String result = "";
        if(this.currentInventory.isEmpty() == true) {
            return "nothing.";
        }
        else {
            for(String obj : this.currentInventory) {
                result += " " + obj;
            }
            result += ".";
        }
        return result;
    }

    @Override
    public String toString() {
        return "Player " + this.name + ": " + currentDirection.toString();
    }
	
	/* START 405_ignore */
    public void ignorePlayer(String name) {
		ignoreList.add(name);
    }
    
    public void addIgnoredBy( String name) {
		ignoredByList.add(name);
    }

    public boolean searchIgnoredBy(String name) {
    	int listSize = ignoredByList.size();
		for( int x = 0; x < listSize; x++){
			if( name.equalsIgnoreCase(ignoredByList.get(x)))
				return true;
		}	
		return false;
    }

    public boolean searchIgnoreList(String name) {
    	int listSize = ignoreList.size();
		for( int x = 0; x < listSize; x++){
			if( name.equalsIgnoreCase(ignoreList.get(x)))
				return true;
		}
		return false;
    }
    /* END 405_ignore */
    //407
    public String showIgnoreList()
    {
        String res = "";
        for(int i = 0; i < ignoreList.size(); i++)
            res += ignoreList.get(i) + " ";
        return res;
    }

   public void unIgnorePlayer(String name) {
		ignoreList.remove(name);
    }
   
 public void removeIgnoredBy( String name) {
		ignoredByList.remove(name);
    }
}
