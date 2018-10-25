# Accounts and Login

## Login/Logout Log File

This file keeps track of players logging in and logging out. Whenver a
player successfully logs into the game server, a message is written to the log
file saying that this player is online. When the user logs out, a message is then
written to the log file saying that player has logged off the game server. This is
useful as a developer/tester because you can easily keep track of who is on the
server and who is not. If you need to edit sections of the project that involve
players logging in and out, then you will know if you are successful or not by
checking the log file while testing your code.

**NOTE:** The log file is only written to when a player is added or removed from
the PlayerList class. If you believe the log file is being written to when it is
not suppose to be, please make sure you to check your code for any places that 
a player is being added to/removed from the player list when they should not be
added or removed.


![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/src/login_logout_log.PNG)

Written by: Joseph Saah