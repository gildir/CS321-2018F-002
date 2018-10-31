WORLD FILE
This is the .csv file you use when starting the server via

  ./runServer.sh your_room_file.csv
OR
  runServer.bat your_room_file.csv

and it contains all the rooms that make up the game world.

Each room contains 5 lines of data.
Rooms are separated using a single new line character.
The first room in the file is your starting room.

The first line of each room contains, in this order,
room id number, room type, title, description

The description can contain commas internally and is ended with a newline character.

The next 4 lines are the 4 cardinal exits to the room in the order off
  NORTH
  EAST
  WEST
  SOUTH

Chris likes this because it spells out NEWS.

Each exit line contains, in this order,
exit id number, exit message

The exit id number is the room id number of the room you're exiting into.

The exit message displays when you use that exit, can contain commas and ends with a newline character just like the room description on the first line.

If you have an exit that you're not using you should set it to a room that doesn't exist.
Additionally you can have a room loop back on its self by setting the exit id number to be the room id number. 
WARNING: This means the game will consider that a valid exit, and moving through that exit will count as walking to a new room. This can potentially screw things up, so be very careful using it.

I like set the start room as 1, reserving 0 for invalid exits, and sequentially increment room ID numbers. So the first room is 1, the next room is 2, etc.

Your csv files can be almost as large as you want. George_Mason.csv contains 171 rooms, which is frankly way too many rooms, but it doesn't noticeably slow down the build or runServer scripts.