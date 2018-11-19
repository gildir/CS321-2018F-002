
import java.util.Random;
import java.util.Scanner;

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
      player.broadcast("Now's your chance to fight off the ghoul!");
      Battle fight = new Battle(player.getName(), getName());
     // gameCore.accept(player.getName(), getName());
      fight.setStatus("active");
      gameCore.getActiveBattles().add(fight);
      gameCore.getPendingBattles().remove(fight);
      player.getReplyWriter().println("\nType 'rock' to choose rock.\nType 'paper' to chose paper.\nType 'scissors' to choose scissors.");
//      boolean blank = false;
      fight.setChoiceP2(new Random().nextInt(3) + 1);
//      
//      Scanner scan = new Scanner(System.in);
//      String input = scan.next();
//      
//      while(input.toLowerCase() != "rock" || input.toLowerCase() != "paper" || input.toLowerCase() != "scissors"){
//        player.getReplyWriter().println("Not the right input. Please try again");
//        input = scan.next();
//      }
//      if (input.toLowerCase().equals("rock")){
//        fight.setChoiceP1(1);
//         player.getReplyWriter().println("You Chose Rock.\n");
//      }
//      if (input.toLowerCase().equals("paper")){
//        fight.setChoiceP1(2);
//         player.getReplyWriter().println("You Chose Paper.\n");
//      }
//      if (input.toLowerCase().equals("scissors")){
//        fight.setChoiceP1(3);
//         player.getReplyWriter().println("You Chose Scissors.\n");
//      }
      
     // blank = doBattle(player.getName(), getName(), fight.getChoiceP1(), fight.getChoiceP2(), fight);

    }
  }
  
  public void doBattle(String challenger, String player2, int p1, int p2, Battle b)
  {
    Player play1 = gameCore.getPlayerList().findPlayer(challenger);
    String message = "";
    boolean check = false;
    if(p1 == p2)
    {
      
      //tie
      switch(p1)
      {
        case 1:
          play1.getReplyWriter().println("You both chose Rock. The match is a tie!\n");
          message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \nIt was a tie.\n";
          play1.broadcastToOthersInRoom(message);
          gameCore.getActiveBattles().remove(b);
          check = false;
        case 2:
          play1.getReplyWriter().println("You both chose Paper. The match is a tie!\n");
          message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \nIt was a tie.\n";
          play1.broadcastToOthersInRoom(message);
          gameCore.getActiveBattles().remove(b);
          check = false;
        case 3:
          play1.getReplyWriter().println("You both chose Scissors. The match is a tie!\n");
          message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \nIt was a tie.\n";
          play1.broadcastToOthersInRoom(message);
          gameCore.getActiveBattles().remove(b);
          check = false;
      }
    }
    else if(p1 == 1 && p2 == 2)
    {
      //rock paper
      play1.getReplyWriter().println("You chose Rock. " + player2 + " chose Paper. \nYou lose.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      play1.broadcastToOthersInRoom(message);
      gameCore.getActiveBattles().remove(b);
      gameCore.writeLog(challenger, player2, "Rock", "Paper", player2 + " winning");
      check = false;
    }
    else if(p1 == 1 && p2 == 3)
    {
      //rock scissors
      play1.getReplyWriter().println("You chose Rock. " + player2 + " chose Scissors. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      play1.broadcastToOthersInRoom(message);
      gameCore.getActiveBattles().remove(b);
      gameCore.writeLog(challenger, player2, "Rock", "Scissors", challenger + " winning");
      
      check = true;
    }
    else if(p1 == 2 && p2 == 1)
    {
      //paper rock
      play1.getReplyWriter().println("You chose Paper. " + player2 + " chose Rock. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      play1.broadcastToOthersInRoom(message);
      gameCore.getActiveBattles().remove(b);
      gameCore.writeLog(challenger, player2, "Paper", "Rock", challenger + " winning");    
      check = true;
    }
    else if(p1 == 2 && p2 == 3)
    {
      //paper scissors
      play1.getReplyWriter().println("You chose Paper. " + player2 + " chose Scissors. \nYou lose.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      play1.broadcastToOthersInRoom(message);
      gameCore.getActiveBattles().remove(b);
      gameCore.writeLog(challenger, player2, "Paper", "Scissors", player2 + " winning");
      check = false;
    }
    else if(p1 == 3 && p2 == 1)
    {
      //scissors rock
      play1.getReplyWriter().println("You chose Scissors. " + player2 + " chose Rock. \nYou lose.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + player2 + " won.\n";
      play1.broadcastToOthersInRoom(message);
      gameCore.getActiveBattles().remove(b);
      gameCore.writeLog(challenger, player2, "Scissors", "Rock", player2 + " winning");
      check = false;
    }
    else if(p1 == 3 && p2 == 2)
    {
      //scissors paper
      play1.getReplyWriter().println("You chose Scissors. " + player2 + " chose Paper. \nYou win.\n");
      message = challenger + " and " + player2 + " had a Rock Paper Scissors Battle. \n" + challenger + " won.\n";
      play1.broadcastToOthersInRoom(message);
      gameCore.getActiveBattles().remove(b);
      gameCore.writeLog(challenger, player2, "Scissors", "Paper", challenger + " winning");
      check = true;
    }
     if (check){
        play1.broadcast("Awesome! You beat the ghoul and managed to escape his grasp. He seems to be calm now.");
        play1.broadcastToOthersInRoom(play1.getName() + " managed to fend off " + getName() + "! It has calmed down after a tough battle.");
      }
      else{
         String item = play1.removeRandomItem();
         play1.broadcast(getName() + " grabs you by the legs and drags you to " +
                         gameCore.getMap().findRoom(Map.SPAWN_ROOM_ID).getTitle() +
                         ", and takes a " + item + " from you.");
        play1.broadcastToOthersInRoom(getName() + " grabs " + play1.getName() +
                                       " by the legs and hobbles off, dragging " + play1.getName() +
                                       ", who is shrieking like a schoolgirl.");
        play1.setCurrentRoom(Map.SPAWN_ROOM_ID);
        this.setCurrentRoomId(Map.SPAWN_ROOM_ID);
        
        play1.broadcastToOthersInRoom(getName() + " hobbles into the area dragging " + play1.getName() +
                                       " behind them, and tosses " + play1.getName() +
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
