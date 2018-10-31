FOR USERS
 -------------------

 Every room in the game is classified as inside or outside in the csv file
 used to construct the map. By using the look command, the player is able to see whether the current room is inside or outside. 

 Item drop rate was changed, NPC's are only allowed to drop items on the ground if their is less than 5 items currently on the ground. However, players are allowed to drop items without limitation.

 FOR DEVELOPERS
 -------------------
 The inside/outside field is specified in the csv file that is loaded when the server is loaded. This is added to the room in Room.java.

 Player drop limitation and NPC drop limitation were implemented by created seperate drop functions for each. The drop limit can be changed by editing the methods in Room.java.
