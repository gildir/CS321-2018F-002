import java.util.ArrayList;

/**
 * 
 */

/**
 * @author Jonathan
 *
 */
public class Shop {
	
	ArrayList<String> playersInShop;
	
	public Shop() {
		playersInShop = new ArrayList<String>();
	}
	
	public void addPlayer(String name) {
		playersInShop.add(name);
	}
	
	public void removePlayer(String name) {
		playersInShop.remove(name);
	}
	
	public boolean playerInShop(String name) {
		return playersInShop.contains(name);
	}
}
