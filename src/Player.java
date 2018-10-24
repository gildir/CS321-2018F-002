
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ArrayList;

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
    // add a money field to track player money
    private Money money;

    public Player(String name) {
        this.currentRoom = 1;
        this.currentDirection = Direction.NORTH;
        this.name = name;
        this.currentInventory = new LinkedList<>();
        // set a default amount of money for each player
        this.money = new Money(20);
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
    // get money 
    public Money getMoney() {
      return this.money;
    }
    //add money
    public void addMoney(double amount) {

     int dollars = (int) amount;
     Money amountAdded = new Money(dollars);
     double coins = amount - dollars;
     coins *= 100;
     for(int i = 0; i < coins; i++){
         amountAdded.coins.add(new Penny());
     }
     acceptMoney(amountAdded);
    }
    // return a string to print to the screen when player wants to view money
    public String viewMoney() {
      return this.money.toString();
    }
    // allows a player to accept money from another player
    public void acceptMoney(Money moneyToAdd){
      this.money.dollars.addAll(moneyToAdd.getDollars());
      this.money.coins.addAll(moneyToAdd.getCoins());
    }
    
    public Money giveMoney(double value){
//      if(this.money.sum() < value){
//        // disallow giving money
//      }
      // otherwise, player has enough to give
      Money moneyToGive = new Money();
        //moneyToGive.dollars.add(this.money.dollars[])
        // use value to figure out how you'll split up into dollars and coins
        // moneyToGive has an ArrayList called dollars so you can store dollars
        // it also has an ArrayList called coins so you can store coins
        // call this.money.dollars.remove() to get rid of the dollars you're giving away
        // call this.money.coins.remove() to get rid of coins you're giving away
        // return moneyToGive in the end 
      return moneyToGive;
    }
    
    public String viewInventory() {
        String result = "";
        if(this.currentInventory.isEmpty() == true) {
            return "nothing.";
        }
        else {
            for(Item obj : this.currentInventory) {
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
}
