import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Leaderboard {
	private ArrayList<PlayerScore> board;

	public Leaderboard() {
		this.board = new ArrayList<PlayerScore>();
	}

	public void addScore(String name) {
		this.board.add(new PlayerScore(name));
	}

	public boolean checkForPlayer(String name)
	{
		for(PlayerScore p : board)
		{
			if(p.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	public PlayerScore incrementScore(String name, boolean winner) {
		for(PlayerScore score : this.board) {
			if(score.getName().equals(name)) {
				score.increment(winner);
				this.board.remove(score);
				for(PlayerScore other : this.board) {
					if(other.getScore() < score.getScore()) {
						this.board.add(this.board.indexOf(other), score);
						return score;
					}
				}
				this.board.add(score);
				return score;
			}
		}
		return null;
	}
	
	public String getBoard() {
		String head = "\n\n\n\n\n\n\nRock-Paper-Scissors Global Leaderboard:\n";
		
		//819
		Collections.sort(board, new Comparator<PlayerScore>(){
            public int compare(PlayerScore s1, PlayerScore s2) {
				Integer a,b;
				if (s1.getScore()!=s2.getScore()) {
					a = new Integer(s1.getScore());
	                b = new Integer(s2.getScore());	
				} 
				// if their scores are equal, compare their current winning streaks
				else if (s1.getCurrentWinStreak()!=s2.getCurrentWinStreak()) {
					a = new Integer(s1.getCurrentWinStreak());
					b = new Integer(s2.getCurrentWinStreak());	
				}
				// if their current winning streaks are equal, compare their longest winning streaks
				else {
					a = new Integer(s1.getLongestWinStreak());
					b = new Integer(s2.getLongestWinStreak());
				}
				return b.compareTo(a);
            }
        });
		
		PlayerScore playerScore = null;
		String rank = null;
		String title = null;
		String score = null;
		String longestWinStreak = null;
		//String currentWinStreak = null;
		String longestLossStreak = null;
		//String currentLossStreak = null;
		
		//819
		int p1WinStreak, p1LoseStreak, p2WinStreak, p2LoseStreak, p1Score, p2Score = 0;
		int pRank = 1;
		
		for(int i=0; i<this.board.size(); i++) {
			playerScore = this.board.get(i);
			
			//819s
			p1WinStreak  = playerScore.getCurrentWinStreak();
			p1LoseStreak = playerScore.getCurrentLossStreak();
			// check edge case = 1 && to prevent negative array index on other cases
			if (i==0) {
				rank = String.format("%-4d", pRank);
			}
			// if previous score greater than as this score, increment rank
            else if (this.board.get(i-1).getScore()>playerScore.getScore()){
				rank = String.format("%-4d", ++pRank);
            }
            // if this player's score equal previous player's score, compare their currentWin/currentLoss
            else {
				p2WinStreak = this.board.get(i-1).getCurrentWinStreak();
				if (p1WinStreak<p2WinStreak) {
					rank = String.format("%-4d", ++pRank);
				} 
				else {
					p2LoseStreak = this.board.get(i-1).getCurrentLossStreak();
					// pScore has 4 states {00_1=0,01_2<0,10_3>0,11_4=0}
					p1Score = p1WinStreak - p1LoseStreak; 
					p2Score = p2WinStreak - p2LoseStreak;
					// comparing 2 and 3
					if (p1Score<p2Score) {
						rank = String.format("%-4d", ++pRank);
					} 
					// 1 and 4 are equivalent, check their longest winning streak
					else {
						if (playerScore.getLongestWinStreak()<this.board.get(i-1).getLongestWinStreak()) {
							rank = String.format("%-4d", ++pRank);
						}
						else {
							rank = String.format("%-4d", pRank);
						}
					}
				}
            }
            
			//819: associate title to rank which means there CAN be duplicates 
			//getTitle(playerScore, i) => getTitle(playerScore, pRank-1)
			title = this.getTitle(playerScore, pRank-1);
			
			score = String.valueOf(playerScore.getScore());
			longestWinStreak = String.valueOf(playerScore.getLongestWinStreak());
			//currentWinStreak = String.format("%-2d", playerScore.getCurrentWinStreak());
			longestLossStreak = String.valueOf(playerScore.getLongestLossStreak());
			//currentLossStreak = String.format("%-2d", playerScore.getCurrentLossStreak());
			head+="\n===============================================================\n";
			head += ("Rank:" + rank + "    Name:" + playerScore.getName() +"\nLongest Win Streak:" + longestWinStreak + "      Current Win Streak:" +p1WinStreak+ " \nLongest Loss Streak:" + longestLossStreak +  "     Current Loss Streak:" +p1LoseStreak + "\nScore:" + score + "\nTitle: " + title);
			//head += ("Rank: " + rank + " | Longest Win Streak: " + longestWinStreak + " | Current Win Streak: " + currentWinStreak + " | Longest Loss Streak: " + longestLossStreak + " | Current Loss Streak: " + currentLossStreak + " | Score: " + score + "Title: " + title + " | Name: " + playerScore.getName() + "\n");
		}
		head+="\n===============================================================\n";
		return head;
	}
	
	public PlayerScore getPlayerScore(String name) {
		for (PlayerScore score : this.board) {
			if (score.getName().equals(name))
				return score;
		}
		return null;
	}
	
	public String getTitle(PlayerScore score, int rank) {
		String[] titles = new String[] {"Smitty Werbenjagermanjensen", ("(Select * From Winners Where name = " + score.getName() + ") -> 0 rows returned") , "Deleted Prod", "At Least You Tried.", "About to be Garbage Collected.", "noob"};
		if(rank >= titles.length)
			return "noob";
		return titles[rank];
	}
	
	// 819: changed all getWins() to new method getScore() from PlayerScore
    public String getTopTen() {
        String topTen = "\nRock-Paper-Scissors Global Top Ten RPS Ranking:\n\n";
        
        //sort leaderboard by number of wins in descending order
        Collections.sort(board, new Comparator<PlayerScore>(){
            public int compare(PlayerScore s1, PlayerScore s2) {
                Integer a = new Integer(s1.getScore());
                Integer b = new Integer(s2.getScore());
                return b.compareTo(a);
            }
        });
        
        PlayerScore score = null;
        String rank = null;
        String wins = null;
        
        // limit size of array list to no more than 10
        int size;
        if (board.size() >= 10) {
            size = 10;
        } else {
            size = board.size(); 
        }
        
        for(int i=0,j=1; i<size; i++) {
            score = this.board.get(i);
            
            // edge case when i=0, player's rank is 1 
            if(i==0) {
                rank = String.format("%-4d", j);
            }
            // if score from previous player is greater than current player, increment rank
            else if (board.get(i-1).getScore() > score.getScore())  {
                rank = String.format("%-4d", ++j);
            }
            // if current score is same as previous score, rank remain the same
            else {
                rank = String.format("%-4d", j);
            }
            wins = String.format("%-4d", score.getScore());
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

	public int getPlayerRank(String name) {
		for (PlayerScore score : this.board) {
			if (score.getName().equals(name))
				return board.indexOf(score)+1;
		}
		return -1;
	}
}
