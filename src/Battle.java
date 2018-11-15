public class Battle
{
  private String player1 = "";//Challenger
  private String player2 = "";//Challenged
  private String currentStatus = "";//status of battle (Active, Pending)
  private int[] choicePlayer1;
  private int[] choicePlayer2;
  private int p1Score;
  private int p2Score;
  private int currentRound;
  private int maxRounds;

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

  public void setStatus(String newStatus)
  {
    currentStatus = newStatus;
  }

  public void setChoiceP1(int choice)
  {
    choicePlayer1[currentRound] = choice;
  }

  public String getPlayer1()
  {
    return player1;
  }

  public String getPlayer2()
  {
    return player2;
  }

  public void setChoiceP2(int choice)
  {
    choicePlayer2[currentRound] = choice;
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

  public boolean hasPlayers(String p1, String p2)
  {
    return (player1.equalsIgnoreCase(p1)) && (player2.equalsIgnoreCase(p2));
  }

  public boolean containsPlayer(String p)
  {
    return (player1.equalsIgnoreCase(p)) || (player2.equalsIgnoreCase(p));
  }

  public boolean isActive()
  {
    return "active".equalsIgnoreCase(currentStatus);
  }

  public boolean isPending()
  {
    return "pending".equalsIgnoreCase(currentStatus);
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
}
