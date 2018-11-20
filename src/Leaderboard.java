//Team 8

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Leaderboard: manages list of all RPS player scores
public class Leaderboard {
	private ArrayList<PlayerScore> board;	//list of PlayerScores

	//Constructor: one board per server
	public Leaderboard() {
		this.board = new ArrayList<PlayerScore>();
	}

	//Add score to board whenever a new player joins the game
	public void addScore(String name) {
		this.board.add(new PlayerScore(name));
	}

	//Return true if player 'name' has a score, else returns false
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

	//Increment score of player 'name' based on value of winner
	public PlayerScore incrementScore(String name, boolean winner) {
		//search for score
		for(PlayerScore score : this.board) {
			if(score.getName().equals(name)) {
				//increment score based on value of winner
				score.increment(winner);
				//sort board by score, descending
				this.board.remove(score);
				for(PlayerScore other : this.board) {
					if(other.getScore() < score.getScore()) {
						this.board.add(this.board.indexOf(other), score);
						return score;
					}
				}
				this.board.add(score);
				//score found
				return score;
			}
		}
		//score not found
		return null;
	}
	
	//Return string representation of Leaderboard
	public String getBoard() {
		
		String head = "\n\n\n\n\n\n\nRock-Paper-Scissors Global Leaderboard:\n";		//leaderboard string
		PlayerScore playerScore = null;													//current player score object
		String rank = "";																//player rank string
		String title = "";																//player title string
		String score = "";																//player score string
		String longestWinStreak = "";													//win streak string
		String longestLossStreak = "";													//lose streak string
		int p1WinStreak, p1LoseStreak, p2WinStreak, p2LoseStreak, p1Score, p2Score = 0;	//score ints
		int pRank = 1;																	//rank int
		
		//Sort scores in leaderboard
		Collections.sort(board, new Comparator<PlayerScore>(){
            public int compare(PlayerScore s1, PlayerScore s2) {
				Integer a,b;
				//compare scores
				if (s1.getScore()!=s2.getScore()) {
					a = new Integer(s1.getScore());
	                b = new Integer(s2.getScore());	
				} 
				// if scores are equal, compare current winning streaks
				else if (s1.getCurrentWinStreak()!=s2.getCurrentWinStreak()) {
					a = new Integer(s1.getCurrentWinStreak());
					b = new Integer(s2.getCurrentWinStreak());	
				}
				// if current winning streaks are equal, compare longest winning streaks
				else {
					a = new Integer(s1.getLongestWinStreak());
					b = new Integer(s2.getLongestWinStreak());
				}
				return b.compareTo(a);
            }
        });
		
		//add each score to head
		for(int i=0; i<this.board.size(); i++) {
			playerScore = this.board.get(i);
			p1WinStreak  = playerScore.getCurrentWinStreak();
			p1LoseStreak = playerScore.getCurrentLossStreak();
			
			//check edge case = 1 to prevent negative array index on other cases
			if (i==0) {
				rank = String.format("%-4d", pRank);
			}
			//if previous score greater than this score, increment rank
            else if (this.board.get(i-1).getScore()>playerScore.getScore()){
				rank = String.format("%-4d", ++pRank);
            }
            //if this score equals previous score, compare currentWin/currentLoss
            else {
				p2WinStreak = this.board.get(i-1).getCurrentWinStreak();
				if (p2WinStreak>p1WinStreak) {
					rank = String.format("%-4d", ++pRank);
				} 
				else {
					p2LoseStreak = this.board.get(i-1).getCurrentLossStreak();
					//pScore has 4 states {00_1=0,01_2<0,10_3>0,11_4=0}
					p1Score = p1WinStreak - p1LoseStreak; 
					p2Score = p2WinStreak - p2LoseStreak;
					//comparing 2 and 3
					if (p2Score>p1Score) {
						rank = String.format("%-4d", ++pRank);
					} 
					//1 and 4 are equivalent, check their longest winning streak
					else {
						if (this.board.get(i-1).getLongestWinStreak()>playerScore.getLongestWinStreak()) {
							rank = String.format("%-4d", ++pRank);
						}
						else {
							rank = String.format("%-4d", pRank);
						}
					}
				}
            }
            
			//title based on rank
			title = this.getTitle(playerScore, pRank-1);
			
			//add score information to head
			score = String.valueOf(playerScore.getScore());
			longestWinStreak = String.valueOf(playerScore.getLongestWinStreak());
			longestLossStreak = String.valueOf(playerScore.getLongestLossStreak());
			head+="\n===============================================================\n";
			head += ("Rank:" + rank + "    Name:" + playerScore.getName() +"\nLongest Win Streak:" + longestWinStreak + "      Current Win Streak:" +p1WinStreak+ " \nLongest Loss Streak:" + longestLossStreak +  "     Current Loss Streak:" +p1LoseStreak + "\nScore:" + score + "\nTitle: " + title);
		}
		
		head+="\n===============================================================\n";
		return head;
	}
	
	//Return PlayerScore tied to player 'name'
	public PlayerScore getPlayerScore(String name) {
		for (PlayerScore score : this.board) {
			if (score.getName().equals(name))
				return score;
		}
		return null;
	}
	
	//Return title associated with given rank, based on score
	public String getTitle(PlayerScore score, int rank) {
		String[] titles = new String[] {"Smitty Werbenjagermanjensen", ("(Select * From Winners Where name = " + score.getName() + ") -> 0 rows returned") , "Deleted Prod", "At Least You Tried.", "About to be Garbage Collected.", "noob"};
		if(rank >= titles.length)
			return "noob";
		return titles[rank];
	}

	//Return list of top ten player scores
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

	//Return rank of player 'name'
	public int getPlayerRank(String name) {
		for (PlayerScore score : this.board) {
			if (score.getName().equals(name))
				return board.indexOf(score)+1;
		}
		return -1;
	}
}
