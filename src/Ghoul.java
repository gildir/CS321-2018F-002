
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
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

    Ghoul(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds){

        super(gameCore, name, currentRoom, aiPeriodSeconds);
        this.anger = 0;
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

    private void increaseAnger(){

        if (anger < MAXANGER){
            anger += getRandomNumberInRange(1,5);
        }

    }

    // Calls the NPC broading method which calls GameCore
    private void replyAnger(){
        super.broadcast("Grrrr, do not poke me! *the ghouls anger level rises*");
    }


    // TODO WAIT FOR THAOVY
    private void dragPlayer(){
        //gameCore.dragPlayer();
    }

    private void resetAnger(){
        anger = 0;
    }

    // TODO
    /*
    private String pickPlayer(){

        return "";
    }
    */
    
    public void poke(){
        increaseAnger();
        if (anger >= MAXANGER){
            resetAnger();
            dragPlayer();
        }
    }
    
    // if an item is gifted to the ghoul, decrease their anger
    public void give(){
        decreaseAnger();
    }
    

    // returns the anger level of the ghoul
    public int getAnger(){
        return anger;
    }


   private static int getRandomNumberInRange(int min, int max) {

       if (min >= max) {
           throw new IllegalArgumentException("max must be greater than min");
       }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
   }
}
