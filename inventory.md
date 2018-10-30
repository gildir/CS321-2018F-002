# Items, Player Inventory, Trade


## Item Creation

### 	New Items can be created or destroyed by editing a csv file. The order of the input for every item in the csv file should be the item’s name, followed by the item’s weight, followed by the item’s value. The ItemParser class is used to interpret the csv file input, and only contains a static method called parse. The parse method accepts a string in its parameter. This string should contain the file path of the csv file. The method then reads in the csv file and extracts each item’s information. The parse method creates a new item object for each item in the csv file and stores it in an ArrayList. Once each item is created and stored, the parse method returns the ArrayList of the new items created.

### The following image depicts the proper order items should be entered in the csv file, along with thier proper attributes:

![CSV Example](https://github.com/torourk/CS321-2018F-002/blob/items_inventory_doc_itemsCreation/csvPic.png?raw=true)
