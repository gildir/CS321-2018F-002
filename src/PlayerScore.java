public class PlayerScore {
	private String name;
	private int wins;
<<<<<<< HEAD
	private int losses;
=======
	private int longestWinStreak; //keeps track of longest win streak, updated if a longer one occurs
	private int currentWinStreak;
	private int longestLossStreak;
	private int currentLossStreak;

>>>>>>> origin/816_RPS_Losing_Streak

	public PlayerScore(String name) {
		this.name = name;
		this.wins = 0;
<<<<<<< HEAD
		this.losses = 0;
=======
		this.longestWinStreak = 0;
		this.currentWinStreak = 0;
		this.longestLossStreak = 0;
		this.currentLossStreak = 0;
>>>>>>> origin/816_RPS_Losing_Streak
	}

	public String getName() {
		return this.name;
	}

	public int getWins() {
		return this.wins;
	}

<<<<<<< HEAD
	public int getLosses() {
		return this.losses;
=======
	public void increment() {
		this.wins++;
		this.currentWinStreak++;
		if (currentWinStreak > longestWinStreak)
		{
			longestWinStreak = currentWinStreak;
		}
		this.currentLossStreak = 0;
	}

	public void lossesIncrement() {
		this.currentLossStreak++;
		if(currentLossStreak > longestLossStreak)
		{
			longestLossStreak = currentLossStreak;
		}
		this.currentWinStreak = 0;
	}
	public int getLongestWinStreak()
	{
		return this.longestWinStreak;
	}

	public void setLongestWinStreak(int s)
	{
		this.longestWinStreak = s;
	}

	public int getCurrentWinStreak()
	{
		return this.currentWinStreak;
	}

	public void setCurrentWinStreak(int s)
	{
		this.currentWinStreak = s;
	}
	//public void setLongestWingStreak(){if (currentWinStreak > longestWinStreak) longestWinStreak = currentWinStreak;}

	public void resetWinStreak()
	{
		this.currentWinStreak = 0;
	}

	public void resetLossStreak()
	{
		this.currentLossStreak = 0;
	}

	public int getLongestLossStreak()
	{
		return this.longestLossStreak;
	}

	public void setLongestLossStreak(int s)
	{
		this.longestLossStreak = s;
	}

	public int getCurrentLossStreak()
	{
		return this.currentLossStreak;
	}

	public void setCurrentLossStreak(int s)
	{
		this.currentLossStreak = s;
>>>>>>> origin/816_RPS_Losing_Streak
	}

	public int getScore() {
		if(this.losses == 0)
			return this.wins;
		return (this.wins / this.losses);
	}

	public void increment(boolean winner) {
		if(winner)
			this.wins++;
		else
			this.losses++;
	}
	
}
