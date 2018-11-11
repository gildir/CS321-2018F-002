import java.util.ArrayList;
import java.util.Collections;

public class Leaderboard {
	private ArrayList<PlayerScore> board;

	public Leaderboard() {
		this.board = new ArrayList<PlayerScore>();
	}

	public void addScore(String name) {
		this.board.add(new PlayerScore(name));
	}

	public PlayerScore incrementScore(String name, boolean winner) {
		for(PlayerScore score : this.board) {
			if(score.getName() == name) {
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
		String head = "Rock-Paper-Scissors Global Leaderboard:\n\n";
		PlayerScore playerScore = null;
		String rank = null;
		String title = null;
		String score = null;
		String longestWinStreak = null;
		//String currentWinStreak = null;
		String longestLossStreak = null;
		//String currentLossStreak = null;
		for(int i = 0; i < this.board.size(); i++) {
			playerScore = this.board.get(i);
			rank = String.format("%-4d", (i+1));
			title = String.format("%-32s", this.getTitle(playerScore, i));
			score = String.format("%-4d", playerScore.getScore());
			longestWinStreak = String.format("%-2d", playerScore.getLongestWinStreak());
			//currentWinStreak = String.format("%-2d", playerScore.getCurrentWinStreak());
			longestLossStreak = String.format("%-2d", playerScore.getLongestLossStreak());
			//currentLossStreak = String.format("%-2d", playerScore.getCurrentLossStreak());
			head += ("Rank: " + rank + " | Longest Win Streak: " + longestWinStreak + " | Longest Loss Streak: " + longestLossStreak + " | Score: " + score + "Title: " + title + " | Name: " + playerScore.getName() + "\n");
			//head += ("Rank: " + rank + " | Longest Win Streak: " + longestWinStreak + " | Current Win Streak: " + currentWinStreak + " | Longest Loss Streak: " + longestLossStreak + " | Current Loss Streak: " + currentLossStreak + " | Score: " + score + "Title: " + title + " | Name: " + playerScore.getName() + "\n");
		}
		return head;
	}
	
	public PlayerScore getPlayerScore(String name) {
		for (PlayerScore score : this.board) {
			if (score.getName() == name)
				return score;
		}
		return null;
	}
	
	public String getTitle(PlayerScore score, int rank) {
		if(score.getScore() > 0) {
			if(rank == 0)
				return "Smitty Werbenjagermanjensen";
			else if(rank == 1)
				return "Pokémon";
			else if(rank == 2)
				return "Mario";
			else if(rank == 3)
				return "Call of Duty";
			else if(rank == 4)
				return "Pac-Man";
			else if(rank == 5)
				return "Space Invaders";
			else if(rank == 6)
				return "Wii";
			else if(rank == 7)
				return "Street Fighter";
			else if(rank == 8)
				return "Dungeon Fighter Online";
			else if(rank == 9)
				return "Final Fantasy";
			else if(rank == 10)
				return "Warcraft";
			else if(rank == 11)
				return "CrossFire";
			else if(rank == 12)
				return "FIFA";
			else if(rank == 13)
				return "Lineage";
			else if(rank == 14)
				return "Grand Theft Auto";
			else if(rank == 15)
				return "Monster Strike";
			else if(rank == 16)
				return "Puzzle & Dragons";
			else if(rank == 17)
				return "Digimon";
			else if(rank == 18)
				return "Sonic the Hedgehog";
			else if(rank == 19)
				return "Clash of Clans";
			else if(rank == 20)
				return "Dragon Quest";
			else if(rank == 21)
				return "League of Legends";
			else if(rank == 22)
				return "Westward Journey";
			else if(rank == 23)
				return "Pro Evolution Soccer";
			else if(rank == 24)
				return "Halo";
			else if(rank == 25)
				return "Star Wars";
			else if(rank == 26)
				return "Arena of Valor";
			else if(rank == 27)
				return "Candy Crush Saga";
			else if(rank == 28)
				return "Assassin's Creed";
			else if(rank == 29)
				return "Madden NFL";
			else if(rank == 30)
				return "Need for Speed";
			else if(rank == 31)
				return "Gran Turismo";
			else if(rank == 32)
				return "Dragon Ball";
			else if(rank == 33)
				return "Skylanders";
			else if(rank == 34)
				return "Resident Evil";
			else if(rank == 35)
				return "The Legend of Zelda";
			else if(rank == 36)
				return "MapleStory";
			else if(rank == 37)
				return "Battlefield";
			else if(rank == 38)
				return "Fate";
			else if(rank == 39)
				return "Monster Hunter";
			else if(rank == 40)
				return "Metal Gear";
			else if(rank == 41)
				return "The Sims";
			else if(rank == 42)
				return "Super Smash Bros.";
			else if(rank == 43)
				return "Minecraft";
			else if(rank == 44)
				return "Tomb Raider";
			else if(rank == 45)
				return "Clash Royale";
			else if(rank == 46)
				return "Guitar Hero";
			else if(rank == 47)
				return "Lego";
			else if(rank == 48)
				return "Mortal Kombat";
			else if(rank == 49)
				return "Onimusha";
			else if(rank == 50)
				return "Tetris";
			else if(rank == 51)
				return "Harry Potter";
			else if(rank == 52)
				return "PlayerUnknown's Battlegrounds";
			else if(rank == 53)
				return "One Piece";
			else if(rank == 54)
				return "The Elder Scrolls";
			else if(rank == 55)
				return "Spider-Man";
			else if(rank == 56)
				return "Fortnite";
			else if(rank == 57)
				return "Tom Clancy";
			else if(rank == 58)
				return "Gundam";
			else if(rank == 59)
				return "Diablo";
			else if(rank == 60)
				return "StarCraft";
			else if(rank == 61)
				return "Forza";
			else if(rank == 62)
				return "Gears of War";
			else if(rank == 63)
				return "Bubble Witch";
			else if(rank == 64)
				return "Pet Rescue";
			else if(rank == 65)
				return "Farm Heroes";
			else if(rank == 66)
				return "Destiny";
			else if(rank == 67)
				return "Heroes of the Storm";
			else if(rank == 68)
				return "Overwatch";
			else
				return "Shrek";
		}
		else
			return "Noob";
	}
}
