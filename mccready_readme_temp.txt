FOR USERS
--------------------

WORLD GENERATION FILE
Originally, the game would accept an xml document containing information about the world to
generate the world--now it accepts a csv document instead.

QUESTS AND OBJECTIVES
The game will soon have quests. Upon discovering a quest, you will be shown
the relevant current objectives. NOTE: you will only be shown current objectives (see example diagram below).
When all objectives of a quest have been completed, the quest is completed.

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

FOR DEVELOPERS
--------------------

MAP.JAVA
Map.java generates the world by reading through a provided csv file using a scanner.
We use a csv file to easily read in values using the comma character as a delimiter.
When reading what will be a String description of an area/world, we switch the delimiter
to a single newline character (that also "eat up" any amount of blank space on either side of the newline).
This prevents potential issues with having a description split into multiple tokens if it contains commas.
IMPORTANT: There must be no newline characters at the very beginning or the very end of the file.
See the file "GenericRoomFormat.txt"

QUESTS

A quest holds an array of the current objectives. The array will be updated to
contain the next concurrent objectives when the current objectives have all been completed.

OBJECTIVES

An objective acts as a node in a tree, storing references to the NEXT concurrent objectives
There will be different types objectives that extend the Objective class.
There is a field "OBJECTIVE_TYPE" in the Objective class that denotes the type of the objective
(eg. location-based, item-based, type-based).