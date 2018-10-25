# Accounts and Login

## Player Database
Written by: Cody Kidwell



## Account Creation
Written by: Cody Kidwell



## Logging In/Out
Written by: Haroon Tanveer



## Permanently Removing Account
Written by: Brett Mullins



## Timeout Due to Inactivity
Written by: Haroon Tanveer

Before this feature was added, when a player exited the system (whether using the 
QUIT command or Ctrl + C), the user was never actually logged out of the system. 
This feature adds a timeout to each account if they have been inactive for 5 minutes. 
Each time the user types an input and hits enter, the time will reset back to 0
for that user. If that time reaches 5 minutes, the user will be "kicked off" the 
server. This enables users to log back in if they were accidentally signed off.
During testing, the time may be set to a different amount (in milliseconds) to force 
the account to time out quicker or delay the time.

**NOTE:** To reset the timer after each input, the developer must add the following line:
new Time();
This line will reset the timer back to 0 indicating that the user has entered an input
and is still active on the server. The Time class has been implemented inside of
GameClient.java.


![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/timeout.PNG)


## Login/Logout Log File
Written by: Joseph Saah

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


![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/login_logout_log.PNG)
