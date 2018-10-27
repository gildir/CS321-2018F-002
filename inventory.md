# Items, Player Inventory, Trade

## Table of Contents
1. Items
2. [Player Inventory](#player-inventory)
3. Trading
4. Item Creation
5. User Commands

## Player Inventory
The player’s inventory is currently a list of item objects that the player has acquired through various means. This inventory is limited to only ten items. If a player attempts to pickup any additional items, they will be greeted with a message informing them that they have too many items and are unable to pick up any more. Items can be moved into and out of a player’s inventory by picking them up off the ground, dropping them onto the ground, or through trade.

![Inventory](InventoryLinkedList.png "Inventory Linked List")
From a technical perspective, this list of items is implemented using Java’s linked list class. List operations are done using the methods given by this class. However, anyone wishing to manipulate the player's inventory should use the methods given by the Player class.

To add an object to a player's inventory, one must create an item object and pass it to the player's addItemToInventory(Item) method. To remove an object from a player's inventory, one must pass a string containing the item's name to the player's removeObjectFromInventory(String) method. The method will search the list for the first instance of an item with the given name and remove it.

The Room class manages items using the same implementation as the Player class. The only difference between the two implementations being that a Room's list of items is limited to five items instead of ten.
