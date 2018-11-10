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
		String longestWinStreak = null;
		for(int i = 0; i < this.leaderboard.size(); i++) {
			score = this.leaderboard.get(i);
			rank = String.format("%-4d", (i+1));
			wins = String.format("%-4d", score.getWins());
			longestWinStreak = String.format("%-4d", score.getWinStreak());
			board += ("Rank: " + rank + " | Longest Win Streak: " + longestWinStreak + " | Score: " + wins + " | Name: " + score.getName() + "\n");
		}
		return board;
	}
	public PlayerScore getScore(String name) {
		PlayerScore score = null;
		for (int i = 0; i < this.leaderboard.size(); i++) {
			score = this.leaderboard.get(i);
			if (score.getName() == name) {
				break;
			}
		}
		return score;
	}
}
