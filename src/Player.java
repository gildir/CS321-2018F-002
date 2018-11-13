
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Kevin
 */
public class Player {
    private GameCore gameCore;
    private LinkedList<Item> currentInventory;
    private String name;
    private String lastWhisperName;
    private int currentRoom;
    private Direction currentDirection;
    private PrintWriter replyWriter = null;
    private DataOutputStream outputWriter = null;
    private Money money;

    /* START 405_ignore variables*/
    private ArrayList<String> ignoreList;
    private ArrayList<String> ignoredByList;
    /* END 405_ignore variables*/

    public Player(GameCore gameCore, String name) {
        this.gameCore = gameCore;
        this.currentRoom = Map.SPAWN_ROOM_ID;
        this.currentDirection = Direction.NORTH;
        this.name = name;
        this.currentInventory = new LinkedList<>();
        this.money = new Money(20);
		/* START 405_ignore*/
        this.ignoreList = new ArrayList<String>();
        this.ignoredByList = new ArrayList<String>();
        /* END 405_ignore  */
    }

    /**
     * Output a message to this player
     * @param message to send
     */
    public void broadcast(String message) {
        synchronized (this) {
            if (replyWriter == null)
                System.err.println("Trying to broadcast to a player that doesn't have a ReplyWriter yet.");
            else
                replyWriter.println(message);
        }
    }

    /**
     * Output a message to all players in the same room as this player,
     * including outputting a message to this player.
     * @param message to send
     */
    public void broadcastToAllInRoom(String message) {
        gameCore.getMap().findRoom(currentRoom).broadcast(message);
    }

    /**
     * Output a message to all other players in the same room as this player,
     * not including outputting a message to this player.
     * @param message to send
     */
    public void broadcastToOthersInRoom(String message) {
        for (Player player : gameCore.getPlayerList()) {
            if (player.currentRoom == this.currentRoom && player != this)
                player.broadcast(message);
        }
    }

    public void turnLeft() {
        synchronized (this) {
            switch (this.currentDirection.toString()) {
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
    }

    public void turnRight() {
        synchronized (this) {
            switch (this.currentDirection.toString()) {
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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        synchronized (this) {
            this.name = name;
        }
    }

    public void setLastWhisperName(String name) {
        synchronized (this) {
            this.lastWhisperName = name;
        }
    }

    public String getLastWhisperName() {
        return this.lastWhisperName;
    }

    public LinkedList<Item> getCurrentInventory() {
        return currentInventory;
    }

    public void setCurrentInventory(LinkedList<Item> currentInventory) {
        synchronized (this) {
            this.currentInventory = currentInventory;
        }
    }

    public void addObjectToInventory(Item object) {
        synchronized (this) {
            this.currentInventory.add(object);
        }
    }

    public Item removeObjectFomInventory(String object) {
        synchronized (this) {
            for (Item obj : this.currentInventory) {
                if (obj.getItemName().equalsIgnoreCase(object)) {
                    this.currentInventory.remove(obj);
                    return obj;
                }
            }
            return null;
        }
    }

    /**
     * Allows an an object to be taken away from player's inventory.
     * @return Message showing success.
     */
    public String removeRandomItem() {
        synchronized (this) {
            if (this.currentInventory.isEmpty()) {
                return "nothing";
            }
            Random randInt = new Random();
            int randItem = randInt.nextInt(this.currentInventory.size());
            String itemName = this.currentInventory.remove(randItem).getItemName();
            setCurrentInventory(this.currentInventory);
            return itemName;
        }
    }

    public void setReplyWriter(PrintWriter writer) {
        synchronized (this) {
            this.replyWriter = writer;
        }
    }

    public PrintWriter getReplyWriter() {
        return this.replyWriter;
    }

    public void setOutputWriter(DataOutputStream writer) {
        synchronized (this) {
            this.outputWriter = writer;
        }
    }

    public DataOutputStream getOutputWriter() {
        return this.outputWriter;
    }

    public int getCurrentRoom() {
        return this.currentRoom;
    }

    public Room getCurrentRoomObject() {
        return gameCore.getMap().findRoom(currentRoom);
    }

    public void setCurrentRoom(int room) {
        synchronized (this) {
            this.currentRoom = room;
        }
    }

    public String getCurrentDirection() {
        return this.currentDirection.name();
    }

    public Direction getDirection() {
        return this.currentDirection;
    }
    public Money getMoney() {
      return this.money;
    }

    public void addMoney(double amount) {
        synchronized (this) {
            int dollars = (int) amount;
            Money amountAdded = new Money(dollars);
            double coins = amount - dollars;
            coins *= 100;
            for (int i = 0; i < coins; i++) {
                amountAdded.coins.add(new Penny());
            }
            acceptMoney(amountAdded);
        }
    }
    public String viewMoney() {
        return this.money.toString();
    }
    public void acceptMoney(Money moneyToAdd){
        synchronized (this) {
            this.money.dollars.addAll(moneyToAdd.getDollars());
            this.money.coins.addAll(moneyToAdd.getCoins());
        }
    }
    public void setDirection(Direction direction){
        synchronized (this) {
            this.currentDirection = direction;
        }
    }

    public Money giveMoney(Player giver,Player receiver,double value){
        synchronized (this) {
            Money moneyToGive = new Money();
            replyWriter.println("You are giving away " + value);

            if (this.money.sum() < value) {
                replyWriter.println("Not enough money!");
                return moneyToGive;
            }
            int i = 0;
            while (i < value) {
                receiver.money.dollars.add(this.money.dollars.remove(0));
                i++;
            }
            receiver.getReplyWriter().println("You received " + value + " dollars!");
            return moneyToGive;
        }
    }

    public String viewInventory() {
        synchronized (this) {
            String result = "";
            if (this.currentInventory.isEmpty() == true) {
                return " nothing.";
            } else {
                for (Item obj : this.currentInventory) {
                    result += " " + obj;
                }
                result += ".";
            }
            result += ".";
            return result;
        }
    }

    @Override
    public String toString() {
        synchronized (this) {
            return "Player " + this.name + ": " + currentDirection.toString();
        }
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
