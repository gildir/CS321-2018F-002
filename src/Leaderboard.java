import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Leaderboard {
    private ArrayList<PlayerScore> leaderboard;
    
    public Leaderboard() {
        this.leaderboard = new ArrayList<PlayerScore>();
    }
    
    public void addScore(String name) {
        this.leaderboard.add(new PlayerScore(name));
    }
    
    public void incrementScore(String name) {
        PlayerScore score = null;
        for(int i = 0; i < this.leaderboard.size(); i++) {
            score = this.leaderboard.get(i);
            if(score.getName() == name) {
                score.increment();
                for(int j = i-1; j >= 0; j--) {
                    if(this.leaderboard.get(j).getWins() < score.getWins())
                        Collections.swap(this.leaderboard, i, j);
                    else
                        return;
                }
                return;
            }
        }
    }
    
    public String getBoard() {
        String board = "Rock-Paper-Scissors Global Leaderboard:\n\n";
        PlayerScore score = null;
        String rank = null;
        String wins = null;
        for(int i = 0; i < this.leaderboard.size(); i++) {
            score = this.leaderboard.get(i);
            rank = String.format("%-4d", (i+1));
            wins = String.format("%-4d", score.getWins());
            board += ("Rank: " + rank + " | Score: " + wins + " | Name: " + score.getName() + "\n");
        }


        return board;
    }
    
    // Added by An
    public String getTopTen() {
        String topTen = "\nRock-Paper-Scissors Global Top Ten RPS Ranking:\n\n";
        
        //sort leaderboard by number of wins in descending order
        Collections.sort(leaderboard, new Comparator<PlayerScore>(){
            public int compare(PlayerScore s1, PlayerScore s2) {
                Integer a = new Integer(s1.getWins());
                Integer b = new Integer(s2.getWins());
                return b.compareTo(a);
            }
        });
        //Collections.reverse(leaderboard);
        
        PlayerScore score = null;
        String rank = null;
        String wins = null;
        
        // limit size of array list to no more than 10
        int size;
        if (leaderboard.size() >= 10) {
            size = 10;
        } else {
            size = leaderboard.size(); 
        }
        
        for(int i=0,j=1; i<size; i++) {
            score = this.leaderboard.get(i);
            
            // edge case when i=0, player's rank is 1 
            if(i==0) {
                rank = String.format("%-4d", j);
            }
            // if score from previous player is greater than current player, increment rank
            else if (leaderboard.get(i-1).getWins() > score.getWins())  {
                rank = String.format("%-4d", ++j);
            }
            // if current score is same as previous score, rank remain the same
            else {
                rank = String.format("%-4d", j);
            }
            wins = String.format("%-4d", score.getWins());
            topTen += ("Rank: " + rank + " |   Score: " + wins + " |   Name: " + score.getName() + "\n");
        }
        //add string buffer for empty player 
        if (size < 10) {
            for (int i=size; i<10; i++) {
                topTen += ("Rank: _    |   Score: _    |   Name: N/A\n");
            }
        }
        return topTen;
    }
}
