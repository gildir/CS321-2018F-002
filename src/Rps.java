import java.util.ArrayList;


public class Rps
{

    private ArrayList<Battle> battleList;


    public Rps()
    {
      battleList = new ArrayList<Battle>();
    }

    public Battle getBattle(String player1, String player2)
    {
      for (Battle b : battleList)
      {
        if(b.hasPlayer(player1) && b.hasPlayer(player2))
        {
          return b;
        }
      }
      return null;
    }

    public void addBattle(String player1, String player2)
    {
      Battle b = new Battle(player1, player2);
      battleList.add(b);
    }
}
