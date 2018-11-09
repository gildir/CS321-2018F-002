public class PlayerScore {
	private String name;
	private int wins;
	private int losses;

	public PlayerScore(String name) {
		this.name = name;
		this.wins = 0;
		this.losses = 0;
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
