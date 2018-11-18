//Team 8

//PlayerScore: manages variables used to keep track of player wins, losses, and streaks
public class PlayerScore {
	private String name;			//player name
	private int wins;				//total wins
	private int losses;				//total losses
	private int longestWinStreak;	//longest win streak
	private int currentWinStreak;	//current win streak
	private int longestLoseStreak;	//longest lose streak
	private int currentLoseStreak;	//current lose streak

	//Constructor: new score for player 'name'
	public PlayerScore(String name) {
		this.name = name;
		this.wins = 0;
		this.losses = 0;
		this.longestWinStreak = 0;
		this.currentWinStreak = 0;
		this.longestLoseStreak = 0;
		this.currentLoseStreak = 0;
	}

	//Getters
	
	public String getName() {
		return this.name;
	}

	public int getWins() {
		return this.wins;
	}

	public int getLosses() {
		return this.losses;
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

	//Returns difference between wins and losses
	public int getScore() {
		return (this.wins - this.losses);
	}
	
	//If winner, increment wins and currentWinStreak, reset currentLoseStreak, set longestWinStreak if necessary
	//	Else, increment losses and currentLoseStreak, reset currentWinStreak, set longestLoseStreak if necessary
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
}
