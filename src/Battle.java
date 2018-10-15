public class Battle
{
  private String player1 = "";//Challenger
  private String player2 = "";//Challenged
  private String currentStatus = "";//status of battle (Active, Pending)

  public Battle(String challenger, String otherPlayer)
  {
    player1 = challenger;
    player2 = otherPlayer;
    currentStatus = "pending";
  }

  public void setStatus(String newStatus)
  {
    currentStatus = newStatus;
  }

  public String getStatus()
  {
    return currentStatus;
  }

  public boolean hasPlayer(String player)
  {
    return (player1.equalsIgnoreCase(player)) || (player2.equalsIgnoreCase(player));
  }

  public boolean isActive()
  {
    return "active".equalsIgnoreCase(currentStatus);
  }

  public boolean isPending()
  {
    return "pending".equalsIgnoreCase(currentStatus);
  }


}
