
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

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
    }

//    private void decreaseAnger(){
//
//        if (anger <= 0){
//            anger = 0;
//        }
//        else{
//
//            anger -= getRandomNumberInRange(1,5);
//        }
//
//    }

    private void replyAnger(){
        super.broadcast("Grrrr, do not poke me! *the ghouls anger level rises*");
    }


    private void dragPlayer(){

    }

    // TODO
    /*
    private String pickPlayer(){

        return "";
    }
    */
    
    // TODO
    public void poke(){

    }
    
//    // TODO
//    public void give(Item object){
//
//    }
    

    // returns the anger level of the ghoul
    public int getAnger(){

        // positive anger
        if (anger > 0){
            return (anger);
        }
        // anger <= 0
        else{
            return 0;
        }
        
    }

//    private static int getRandomNumberInRange(int min, int max) {
//
//        if (min >= max) {
//            throw new IllegalArgumentException("max must be greater than min");
//        }
//
//        Random r = new Random();
//        return r.nextInt((max - min) + 1) + min;
//    }

}