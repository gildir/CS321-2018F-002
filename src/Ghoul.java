
import java.util.Random;

/**
 *
 * @author Adam Denton
 */

public class Ghoul extends NPC {

    // anger level
    private int anger = 0;
    // total anger level
    public static final int MAXANGER = 100;

    // Calling NPC's constructor
    Ghoul(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds){

        super(gameCore, name, currentRoom, aiPeriodSeconds);
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
        
    }

    // Randomly increases the Ghouls anger between 1 through 5
    private void increaseAnger(){

        if (anger < MAXANGER){
            anger += getRandomNumberInRange(1,5);
        }

    }

    // Calls the NPC broadcast method which calls GameCore
    private void replyAnger(){
        getCurrentRoom().broadcast("Grrrr, do not poke me! *the ghouls anger level rises*");
    }

    // When called resets the ghouls anger back to the initial state
    private void resetAnger(){
        anger = 0;
    }
    
    // If poked, increase anger, and if that anger goes over the
    // threshold, reset the anger and call gameCore.dragPlayer()
    // Used in gameCore.pokeGhoul()
    public void poke(){
        synchronized (this) {
            increaseAnger();
            if (anger >= MAXANGER) {
                resetAnger();
                // Player player = getCurrentRoom().getRandomPlayer();
                // dragPlayer(player);
            }
        }
    }

    private void dragPlayer(Player player) {
        synchronized (player) {
            player.broadcast(getName() + " grabs you by the legs and drags you to the Clock");
            player.broadcastToOthersInRoom(getName() + " grabs " + player.getName() +
                                           " by the legs and hobbles off, dragging " + player.getName() +
                                           ", who is shrieking like a schoolgirl.");
            player.setCurrentRoom(1);
            this.setCurrentRoomId(1);
            player.broadcastToOthersInRoom(getName() + " hobbles into the area dragging " + player.getName() +
                                           " behind them, and tosses " + player.getName() +
                                           " at the base of the clock post.");
        }
    }
    
    // If an item is gifted to the ghoul, decrease their anger
    // Use in gameCore.giftGhoul()
    public void give(){
        decreaseAnger();
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
