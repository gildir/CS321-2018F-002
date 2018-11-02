public class PlayerScore {
	private String name;
	private int wins;

	public PlayerScore(String name) {
		this.name = name;
		this.wins = 0;
	}

	public String getName() {
		return this.name;
	}

	public int getWins() {
		return this.wins;
	}

	public void increment() {
		this.wins++;
	}
}
