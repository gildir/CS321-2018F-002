import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * 
 */

/**
 * @author Jonathan
 *
 */
public class Shop {
 
 private ArrayList<String> playersInShop; //track players in the shop room
 private ArrayList<Item> items; //items sold to shop
 private int maxSize = 10; //max items the shop holds
 private double cur_value;
 private ArrayList<Item> itemList; //list of all items in game (for random choosing)
 private ArrayList<Item> inDemand; //randomly chosen items to be in demand
 private int numInDemand = 2; //num items in demand
 private Random rand; //used to select a random item
 
 /**
  * 
  * @author Jonathan
  * helper class for Shop to store items with an associated number in stock
  * used to condense multiple items into an item and its multiplicity
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
  rand = new Random(); //used to get random items to be in demand
  items = new ArrayList<Item>(); //items sold to shop
  itemList = ItemParser.parse("./ItemListCSV.csv"); //list of all items
  inDemand = new ArrayList<Item>(numInDemand); //array to hold random items that are in demand
  for(int i = 0; i < numInDemand; i++) {
	  inDemand.add(itemList.get(rand.nextInt(itemList.size())));
  }
 }
 
 /**
  * Called from GameCore.java to keep track of items sold to shop
  * @param obj Item to be sold
  */
 public boolean sellItem(Item obj) {
  boolean demanded = false; //if the item is in demand, return true
  for(int i = 0; i < this.numInDemand; i++) { //is obj an item in demand
	  if(obj.getItemName().equalsIgnoreCase(this.inDemand.get(i).getItemName())) {
		  demanded = true; //obj was found in the demand list
		  //item no longer in demand, add a new random item to be in demand
		  this.inDemand.remove(i);
		  this.inDemand.add(i, this.itemList.get(this.rand.nextInt(itemList.size())));
		  break;
	  }
  }
  if(items.size() >= maxSize) { //if shop full, remove first item
   items.remove(0);
   items.add(obj);
   items.trimToSize();
   return demanded;
  }
  items.add(obj);
  return demanded;
 }
 
 public boolean buyItem(Player player, String itemName){
     Boolean did_buy = false;
     //Get player inventory
     LinkedList<Item> inventory = player.getCurrentInventory(); 
     //Iterate through shop items to try and find a match
     for(Item obj : this.items){
         if(obj.getItemName().equalsIgnoreCase(itemName)) {
             double cost = obj.getItemValue() * 1.2;
             if(player.getMoney().sum() >= cost){
                 //add item to player inventory and update
                 inventory.add(obj);
                 player.setCurrentInventory(inventory);
                 //remove money from player
                 player.removeMoney(cost);
                 did_buy = true;
                 //remove item from shop's list of items
                 items.remove(obj);
                 break;
             }
         }
     }
     return did_buy;
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
 public synchronized String displayShop() {
  //preprocessing to build up the list of items with their multiplicities using ItemStock
  //so that multiple items only show up once
  ArrayList<ItemStock> itemList = new ArrayList<ItemStock>();
  for(Item item : this.items) {
   ItemStock iStock = new ItemStock(item.getItemName(), 1, item.getItemValue() * 1.2);
   if(!(itemList.contains(iStock))){
    itemList.add(iStock);
   }
   else {
    ItemStock incumbent = itemList.get(itemList.indexOf(iStock));
    incumbent.amount++;
   }
  }
  String result = "\n";
  //display items in demand
  result += "Items in demand:\n";
  for(int i = 0; i < inDemand.size(); i++) {
	  result += " 	" + inDemand.get(i).getItemName() + "\n";
  }
  //display items currently in the shop
  result += "\nAMOUNT ................ ITEM/PRICE\n";
  if(items.size() == 0)
   result += "Shop is currently empty.\n";
  for(ItemStock item : itemList) {
   result += "" + item.amount + " ..................... " + item.itemName + " $" + (String.format("%.2f",item.value)) +"\n";
  }
  result += "To sell an item enter SELL <ITEM>\n" + "To buy an item enter BUY <ITEM>\n";
  result += "To exit the shop enter LEAVE SHOP or MOVE <ANY DIRECTION>\n\n";
  return result;
 }
 
 public ArrayList<Item> getInDemand(){
	 return this.inDemand;
 }
}
