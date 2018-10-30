# Items, Player Inventory, Trade
## Table of Contents
1. Items
2. Player Inventory
3. Trading
4. Item Creation
5. User Commands
## Items
Items are within the game, obtainiable for pickup by a player. These items have the same properties, a name, a weight, and a value, 
each with unique values. These values are fixed and do not change upon initial retrieval of the item, though will later be subject 
to change over the course of playing.

For the technical aspect, an item is a simple class aptly named Item, with corresponding fields for the name (a string), the weight 
(a double), and the value (also a double). It has a constructor which when this class is instatiated, sets a value for each field.
To go along with this are getter methods to obtain values from elsewhere, but no setters since these values will not change initially.

![Below is an image of the class diagram for the Item Class:](https://github.com/torourk/CS321-2018F-002/blob/items_inventory_doc_itemsActual/Item_Class_UML_Diagram-1.png)


