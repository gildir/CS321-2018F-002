public class PlayerScore {
	private String name;
	private int wins;
	private int longestWinStreak; //keeps track of longest win streak, updated if a longer one occurs
	private int currentWinStreak;


	public PlayerScore(String name) {
		this.name = name;
		this.wins = 0;
		this.longestWinStreak = 0;
		this.currentWinStreak = 0;

	}

	public String getName() {
		return this.name;
	}

	public int getWins() {
		return this.wins;
	}

	public void increment() {
		this.wins++;
		this.currentWinStreak++;
		if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak;
	}

	public int getWinStreak() {return this.longestWinStreak;}

	//public void setLongestWingStreak(){if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak;}

	public void resetWinStreak() { this.currentWinStreak = 0;}

}
