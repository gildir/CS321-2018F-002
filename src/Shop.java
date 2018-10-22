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
	
	public void addPlayer(String name) {
		playersInShop.add(name);
	}
	
	public void removePlayer(String name) {
		playersInShop.remove(name);
	}
	
	public boolean playerInShop(String name) {
		return playersInShop.contains(name);
	}
	
	public String displayShop() {
		String result = "AMOUNT ................ ITEM\n";
		if(items.size() == 0)
			result += "shop is empty\n";
		for(ItemStock item : items) {
			result += "" + item.amount + " ................ " + item.itemName + "\n";
		}
		return result;
	}
}
