
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Kevin
 */
public class Player {
    private LinkedList<Item> currentInventory;
    private String name;
    private int currentRoom;
    private Direction currentDirection;
    private PrintWriter replyWriter = null;
    private DataOutputStream outputWriter = null;

    public Player(String name) {
        this.currentRoom = 1;
        this.currentDirection = Direction.NORTH;
        this.name = name;
        this.currentInventory = new LinkedList<>();
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

    public LinkedList<Item> getCurrentInventory() {
        return currentInventory;
    }

    public void setCurrentInventory(LinkedList<Item> currentInventory) {
        this.currentInventory = currentInventory;
    }

    public void addObjectToInventory(Item object) {
        this.currentInventory.add(object);
    }
    public Item removeObjectFomInventory(String object) {
        for(Item obj : this.currentInventory) {
            if(obj.getItemName().equalsIgnoreCase(object)) {
                this.currentInventory.remove(obj);
                return obj;
              }
            }
        return null;
    }

    /**
     * Allows an an object to be taken away from player's inventory.
     * @return Message showing success.
     */
    public String removeRandomItem()  {
        if (this.currentInventory.isEmpty()){
            return "You have no items in your inventory.";
        }
        Random randInt = new Random();
        int randItem = randInt.nextInt(this.currentInventory.size());
        String targetItem = this.currentInventory.remove(randItem).getItemName();
        setCurrentInventory(this.currentInventory);
        return targetItem + " was removed from your inventory.";
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

    public void setDirection(Direction direction){
        this.currentDirection = direction;
    }

    public String viewInventory() {
        String result = "";
        if(this.currentInventory.isEmpty() == true) {
            return " nothing.";
        }
        else {
            for(Item obj : this.currentInventory) {
                result += " " + obj;
            }
            result += ".";
        }
        result += ".";
        return result;
    }

    @Override
    public String toString() {
        return "Player " + this.name + ": " + currentDirection.toString();
    }
}
