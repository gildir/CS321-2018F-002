//Team 8

//Battle: manages variables used in Rock-Paper-Scissors battle between two players
public class Battle
{
  private String player1 = "";			//challenger
  private String player2 = "";			//opposing player
  private String currentStatus = "";	//current status of battle (active or pending)
  private int[] choicePlayer1;			//list of choices made each round by player1
  private int[] choicePlayer2;			//list of choices made each round by player2
  private int p1Score;					//player1 score across multiple rounds (not PlayerScore)
  private int p2Score;					//player2 score across multiple rounds (not PlayerScore)
  private int currentRound;				//current round in battle
  private int maxRounds;				//total rounds in battle

  //Constructor: new battle between challenger and player, with 'rounds' rounds
  public Battle(String challenger, String otherPlayer, int rounds)
  {
    player1 = challenger;
    player2 = otherPlayer;
    currentStatus = "pending";
    choicePlayer1 = new int[rounds];
    choicePlayer2 = new int[rounds];
    currentRound = 0;
    maxRounds = rounds;
  }

  //Setters
  
  public void setStatus(String newStatus)
  {
    currentStatus = newStatus;
  }

  public void setChoiceP1(int choice)
  {
    choicePlayer1[currentRound] = choice;
  }

  public void setChoiceP2(int choice)
  {
    choicePlayer2[currentRound] = choice;
  }

  //Getters
  
  public String getPlayer1()
  {
    return player1;
  }

  public String getPlayer2()
  {
    return player2;
  }

  public int[] getChoiceP1()
  {
    return choicePlayer1;
  }

  public int[] getChoiceP2()
  {
    return choicePlayer2;
  }

  public String getStatus()
  {
    return currentStatus;
  }

  public int getCurrentRound()
  {
    return currentRound;
  }
  
  public int getP1Score()
  {
    return p1Score;
  }
  
  public int getP2Score()
  {
    return p2Score;
  }
  
  public int getMaxRounds()
  {
    return maxRounds;
  }
  
  //Boolean Methods
  
  //Returns true if player1 and player2 match p1 and p2 respectively, else returns false
  public boolean hasPlayers(String p1, String p2)
  {
    return (player1.equalsIgnoreCase(p1)) && (player2.equalsIgnoreCase(p2));
  }

  //Returns true if player1 or player2 match p1, else returns false
  public boolean containsPlayer(String p)
  {
    return (player1.equalsIgnoreCase(p)) || (player2.equalsIgnoreCase(p));
  }

  //Returns true if battle is active, else returns false
  public boolean isActive()
  {
    return "active".equalsIgnoreCase(currentStatus);
  }

  //Returns true if battle is pending, else returns false
  public boolean isPending()
  {
    return "pending".equalsIgnoreCase(currentStatus);
  }
  
  //Incrementer Methods
  public void incP1Score()
  {
    p1Score++;
  }

  public void incP2Score()
  {
    p2Score++;
  }
  
  public void incrementRound()
  {
    currentRound++;
  }
}
