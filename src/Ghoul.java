
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

    Ghoul(){
        super("Ghoul", 1, 0);
    }


    private void decreaseAnger(){
        
    }

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
    
    // TODO
    public void give(Item object){

    }
    

    // returns the anger level of the ghoul out of the MAXANGER
    public int getAnger(){

        // positive anger
        if (anger > 0){
            return (anger/MAXANGER);
        }
        // anger <= 0
        else{
            return 0;
        }
        
    }

}