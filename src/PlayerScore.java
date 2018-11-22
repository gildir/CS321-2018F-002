public class PlayerScore {
	private String name;
	private int wins;
	private int losses;
	private int longestWinStreak;
	private int currentWinStreak;
	private int longestLoseStreak;
	private int currentLoseStreak;

	public PlayerScore(String name) {
		this.name = name;
		this.wins = 0;
		this.losses = 0;
		this.longestWinStreak = 0;
		this.currentWinStreak = 0;
		this.longestLoseStreak = 0;
		this.currentLoseStreak = 0;
	}

	public String getName() {
		return this.name;
	}

	public int getWins() {
		return this.wins;
	}

	public int getLosses() {
		return this.losses;
	}

	public void increment(boolean winner) {
		if(winner) {
			this.wins++;
			this.currentWinStreak++;
			if(this.currentWinStreak > this.longestWinStreak)
				this.longestWinStreak = this.currentWinStreak;
			this.currentLoseStreak = 0;
		}
		else {
			this.losses++;
			this.currentLoseStreak++;
			if(this.currentLoseStreak > this.longestLoseStreak)
				this.longestLoseStreak = this.currentLoseStreak;
			this.currentWinStreak = 0;
		}
	}

	public int getLongestWinStreak()
	{
		return this.longestWinStreak;
	}

	public int getCurrentWinStreak()
	{
		return this.currentWinStreak;
	}

	public int getLongestLossStreak()
	{
		return this.longestLoseStreak;
	}

	public int getCurrentLossStreak()
	{
		return this.currentLoseStreak;
	}

	public int getScore() {
		if(this.losses == 0)
			return this.wins;
		return (this.wins - this.losses);
	}
}
