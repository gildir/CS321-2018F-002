### World Creation CS321-2018F-002
--------------------
##### Matthew Burnard
##### Chris McCready
##### David Furness
##### James Bruce

FOR ADMINS
--------------------

When starting a new server, make sure to signify the csv file containing the rooms for the world.
For example, from a unix terminal, type:

./clean.sh

./build.sh

./runServer.sh game_room_file.csv

This was a feature added to allow multiple servers to run with different worlds to explore.

It is imperative that if you are still using the XML format for rooms that you convert it to CSV format.
If you are unsure about how to do this please read the GenericRoomFormat.txt file.

 
 FOR USERS
--------------------

#### QUESTS AND OBJECTIVES
The game will soon have quests. Upon discovering a quest, you will be shown
the relevant current objectives. NOTE: you will only be shown current objectives (see example diagram below).
When all objectives of a quest have been completed, the quest is completed.

#### ROOMS
Every room in the game is classified as inside or outside in the csv file used to construct the map.
By using the look command, the player is able to see whether the current room is inside or outside. 

NPC's are only allowed to drop items on the ground if their is less than 5 items currently on the ground.
However, players are allowed to drop items without limitation.


FOR DEVELOPERS
--------------------

#### WORLD FILE

This is the .csv file you use when starting the server via

  ./runServer.sh your_room_file.csv
OR
  runServer.bat your_room_file.csv

and it contains all the rooms that make up the game world.

Each room contains 5 lines of data.
Rooms are separated using a single new line character.
The first room in the file is your starting room.

The first line of each room contains, in this order,
room id number, inside/outside, title, description

The inside/outside field is specified here.

The description can contain commas internally and is ended with a newline character.

The next 4 lines are the 4 cardinal exits to the room in the order off
  NORTH
  EAST
  WEST
  SOUTH

Chris likes it this way because it spells out NEWS.

Each exit line contains, in this order,
exit id number, exit message

The exit id number is the room id number of the room you're exiting into.

The exit message displays when you use that exit, can contain commas and ends with a newline character just like the room description on the first line.

If you have an exit that you're not using you should set it to a room that doesn't exist.
Additionally you can have a room loop back on its self by setting the exit id number to be the room id number. 
WARNING: This means the game will consider that a valid exit, and moving through that exit will count as walking to a new room.
	This can potentially screw things up, so be very careful using it.

I like set the start room as 1, reserving 0 for invalid exits, and sequentially increment room ID numbers.
So the first room is 1, the next room is 2, etc.

Your csv files can be almost as large as you want. George_Mason.csv contains 171 rooms, which is frankly way too many rooms,
but it doesn't noticeably slow down the build or runServer scripts.

#### GAMECORE

The item spawnrate can be modified by changing the min and max variables at the top of GameCore.java.

If both variables are relatively small numbers (less than 1000) than a high spawnrate is guaranteed.
If both variables are large numbers (around 5000) then the spawnrate will be fairly low.

As the names would imply, the maximum spawnrate can NOT be smaller than the minimum spawn rate, although it can be equal to it if a static spawnrate is desired.

#### ROOM.JAVA

Player drop limitation and NPC drop limitation were implemented by created seperate drop functions for each. The drop limit can be changed by editing the methods here.

#### MAP.JAVA

Map.java generates the world by reading through a provided csv file using a scanner.
We use a csv file to easily read in values using the comma character as a delimiter.
When reading what will be a String description of an area/world, we switch the delimiter
to a single newline character (that also "eat up" any amount of blank space on either side of the newline).
This prevents potential issues with having a description split into multiple tokens if it contains commas.
IMPORTANT: There must be no newline characters at the very beginning or the very end of the file.
See the file "GenericRoomFormat.txt"

#### QUESTS

A quest holds an array of the current objectives. The array will be updated to
contain the next concurrent objectives when the current objectives have all been completed.

* in this diagram, concurrent Objectives are shown on the same row
* 
*         0 (start)
* 		/   \
* 	   1     2     <-- not revealed until 0 has been completed
* 		\   /
* 		  3        <-- not revealed until 1 and 2 have been completed
* 	    / | \
* 	   4  5  6     <-- not revealed until 3 has been completed
* 	    \ | /
* 		  7 (end)  <-- not revealed until 4, 5, and 6 have been completed
* 
* 0 = [Talk to Professor Russell]
* 1 = [Find a pen]
* 2 = [Find a piece of paper]
* 3 = [Talk to Professor Russell]
* 4 = [Write your name down]
* 5 = [Write your G# down]
* 6 = [Write your Mason email address down]
* 7 = [Give the piece of paper to Professor Russell]

#### OBJECTIVES

An objective acts as a node in a tree, storing references to the NEXT concurrent objectives
There will be different types objectives that extend the Objective class.
There is a field "OBJECTIVE_TYPE" in the Objective class that denotes the type of the objective
(eg. location-based, item-based, type-based).