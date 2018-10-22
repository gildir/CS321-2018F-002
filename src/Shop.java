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
	ArrayList<ItemStock> items;
	
	/**
	 * 
	 * @author Jonathan
	 * helper class for Shop to store items with an associated number in stock
	 */
	private class ItemStock{
		private String itemName;
		private int amount;
		
		public ItemStock(String itemName, int amount) {
			this.itemName = itemName;
			this.amount = amount;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof ItemStock))
				return false;
			ItemStock i = (ItemStock) o;
			return this.itemName.equals(i.itemName);
		}
	}
	
	public Shop() {
		playersInShop = new ArrayList<String>();
		items = new ArrayList<ItemStock>();
	}
	
	/**
	 * Called from GameCore.java to keep track of items sold to shop
	 * @param name
	 */
	public void sellItem(String name) {
		ItemStock newItem = new ItemStock(name, 1);
		if(items.contains(newItem)) {
			int index = items.indexOf(newItem);
			items.get(index).amount++;
			return;
		}
		if(items.size() >= 10) {
			items.remove(0);
			items.add(newItem);
			return;
		}
		items.add(newItem);
	}
	
	/**
	 * Adds a player to the list of players in this shop
	 * @param name
	 */
	public void addPlayer(String name) {
		playersInShop.add(name);
	}
	
	/**
	 * Removes player from list of players in this shop
	 * @param name
	 */
	public void removePlayer(String name) {
		playersInShop.remove(name);
	}
	
	/**
	 * Determines if the player is in this shop
	 * @param name
	 * @return true or false if in or not in shop
	 */
	public boolean playerInShop(String name) {
		return playersInShop.contains(name);
	}
	
	/**
	 * Builds the string to represent the shop
	 * @return String representation of shop
	 */
	public String displayShop() {
		String result = "\nAMOUNT ................ ITEM\n";
		if(items.size() == 0)
			result += "shop is empty\n";
		for(ItemStock item : items) {
			result += "" + item.amount + " ................ " + item.itemName + "\n";
		}
		result += "To sell an item enter SELL <ITEM>\n\n";
		return result;
	}
}
