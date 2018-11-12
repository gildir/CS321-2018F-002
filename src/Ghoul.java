
import java.util.Random;

/**
 *
 * @author Adam Denton
 */

public class Ghoul extends NPC {

    // anger level
    private int anger = 0;
    // total anger level
    public static final int MAXANGER = 18;

    // Calling NPC's constructor
    Ghoul(GameCore gameCore, String name, int roomId, long aiPeriodSeconds){


        super(gameCore, name, roomId, aiPeriodSeconds);
    }

    // If anger goes below 0 and into the negative it will go back to zero
    // (possibly use negative int numbers as a friendly aggro or revamp)
    private void decreaseAnger(){
        if (anger <= 0){
            anger = 0;
        }
        else{
            anger -= getRandomNumberInRange(1,5);
        }
        //getCurrentRoom().broadcast(angerString()); // debugging message
    }

    private void increaseAnger(){
        anger += getRandomNumberInRange(1,5);
        //getCurrentRoom().broadcast(angerString()); // debugging message
    }
    
    // If poked, increase anger, and if that anger goes over the
    // threshold, reset the anger and call gameCore.dragPlayer()
    // Used in gameCore.pokeGhoul()
    public void poke(){
        synchronized (this) {
            getCurrentRoom().broadcast("\"Grrrr, do not poke me!\", said " + getName() + ", who looks a little more angry.");
            increaseAnger();
            if (anger >= MAXANGER) {
                getCurrentRoom().broadcast("\"BLLLAAAARGH I've had it!\", said " + getName() + ".");
                Player player = getCurrentRoom().getRandomPlayer();
                dragPlayer(player);
                anger = 0;
            }
        }
    }

    private void dragPlayer(Player player) {
        synchronized (player) {
            player.broadcast(getName() + " grabs you by the legs and drags you to " +
                             gameCore.getMap().findRoom(Map.SPAWN_ROOM_ID).getTitle() + ".");
            player.broadcastToOthersInRoom(getName() + " grabs " + player.getName() +
                                           " by the legs and hobbles off, dragging " + player.getName() +
                                           ", who is shrieking like a schoolgirl.");
            player.setCurrentRoom(Map.SPAWN_ROOM_ID);
            this.setCurrentRoomId(Map.SPAWN_ROOM_ID);
            player.broadcastToOthersInRoom(getName() + " hobbles into the area dragging " + player.getName() +
                                           " behind them, and tosses " + player.getName() +
                                           " into the center of the room.");
        }
    }
    
    // If an item is gifted to the ghoul, decrease their anger
    // Use in gameCore.giftGhoul()
    public void give(Item item){
        synchronized (this) {
            getCurrentRoom().broadcast("\"Oooh, a " + item.getItemName() + ", my favorite!\", said " + getName()
                                       + ", who looks a bit more calm.");
            decreaseAnger();
        }
    }
    
    // Getter for anger
    public int getAnger(){
        return anger;
    }

    // String representation of Ghouls anger
    public String angerString(){
        return String.format("%d/%d", anger, MAXANGER);
    }

    // Random number generator in the given range of min through max
    private static int getRandomNumberInRange(int min, int max) {

       if (min >= max) {
           throw new IllegalArgumentException("max must be greater than min");
       }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
   }
   
} //EOF Ghoul.java
