import java.util.ArrayList;
import java.util.Collections;

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
	
	public int getPlayerRank(String name) {
		for (PlayerScore score : this.leaderboard) {
			if (score.getName().equals(name))
				return leaderboard.indexOf(score)+1;
		}
		return -1;
	}
	
}
