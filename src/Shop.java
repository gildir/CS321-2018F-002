import java.util.ArrayList;

/**
 * 
 */

/**
 * @author Jonathan
 *
 */
public class Shop {
 
 private ArrayList<String> playersInShop;
 private ArrayList<Item> items;
 private int maxSize = 10;
 private double cur_value;
 
 /**
  * 
  * @author Jonathan
  * helper class for Shop to store items with an associated number in stock
  */
 private class ItemStock{
  private String itemName;
  private int amount;
  private double value;
  
  public ItemStock(String itemName, int amount, double value) {
   this.itemName = itemName;
   this.amount = amount;
   this.value = value;
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
  items = new ArrayList<Item>();
 }
 
 /**
  * Called from GameCore.java to keep track of items sold to shop
  * @param name
  */
 public void sellItem(Item obj) {
  if(items.size() >= maxSize) {
   items.remove(0);
   items.add(obj);
   items.trimToSize();
   return;
  }
  items.add(obj);
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
  ArrayList<ItemStock> itemList = new ArrayList<ItemStock>();
  for(Item item : this.items) {
   ItemStock iStock = new ItemStock(item.getItemName(), 1, (item.getItemValue()*1.2));
   if(!(itemList.contains(iStock))){
    itemList.add(iStock);
   }
   else {
    ItemStock incumbent = itemList.get(itemList.indexOf(iStock));
    incumbent.amount++;
   }
  }
  String result = "\nAMOUNT ................ ITEM/PRICE\n";
  if(items.size() == 0)
   result += "shop is empty\n";
  for(ItemStock item : itemList) {
   result += "" + item.amount + " ..................... " + item.itemName + " $" + item.value+"\n";
  }
  result += "To sell an item enter SELL <ITEM>\n\n";
  return result;
 }
}
