FOR SERVER HOSTERS
--------------------

When starting a new server, make sure to signify the csv file containing the rooms for the world. For example, from a unix terminal, type:

./clean.sh
./build.sh
./runServer.sh game_room_file.csv

This was a feature added to allow multiple servers to run with different worlds to explore.

It is imperative that if you are still using the XML format for rooms that you convert it to CSV format. If you are unsure about how to do this please read the GenericRoomFormat.txt file.

FOR DEVELOPERS
--------------------

The item spawnrate can be modified by changing the min and max variables at the top of GameCore.java.

If both variables are relatively small numbers (less than 1000) than a high spawnrate is guaranteed.
If both variables are large numbers (around 5000) then the spawnrate will be fairly low.

As the names would imply, the maximum spawnrate can NOT be smaller than the minimum spawn rate, although it can be equal to it if a static spawnrate is desired.
