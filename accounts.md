# Accounts and Login

## Player Database
Written by: Cody Kidwell

The player database was created for the storage of all user account information. This information will not be available to users to see but is stored on the server for the game's use. Currently usernames and passwords are stored on this database for account creation and logging in and out. In addition, the user account information is not permanent on the database. Users will be able to delete their account information at request upon logging out. Users should not worry about this feature as it is solely used by the game server to ensure the player is logged into their correct account.

**NOTE:** PlayerDatabase.java file was created for the use of all user account information needs. The class contains addPlayer(String name, String password) to add a new player to the database, isPlayer(String name) to check if the user already exists, isPassword(String name, String password) to ensure the password is correct to that user, and removePlayer(String name) to remove that player from the database. This class also includes other methods that are required for other features (well documented inside the Java file).


## Account Creation
Written by: Cody Kidwell

When a player first boots up the game they will be prompted to login or create a new account. New players will need to prompt the second option to create a new account. The game server will ask the user to input a new username they would like to have for their account. If the username is not available, it will tell them that that username is not available and prompt them to enter a new username. Otherwise, if the username is available they will be asked to verify that they want that username. Once they verify the username, they will enter their new password. Following this, they will be logged in and their account login information will be saved to the database for future login attempts. Previous users do not need to create a new account each time they boot the game, but now be able to login with their already created accounts.

**NOTE:** GameClient.java file uses the createAccount() method to complete the account creation request. The player's username is verified using boolean isPlayer().

## Logging In/Out
Written by: Haroon Tanveer

After an account has been created, the user can use those credentials to log back into their account. 
When the user runs the client and connects to the server, they can choose to 'login with a previously created account.' Once the user chooses this option, they go through the following steps:          
1. The user enters their account username
2. The username is matched with the Player Database to ensure that it exists. If not, the user is prompted to enter
3. The system ensures the user is not currently logged in 
4. If the username exists in the Database and is not logged in, the player will be prompted to enter their password
5. The password will be verified using the username and the stored password in the Database
6. If the password is incorrect, the user is prompted to enter the password
7. The system ensures the user is not currently logged in 

When the player enters the "QUIT" option to log out, they are given the option to permanently delete their account. If they choose no (N), they will immediately be logged out of the server.

**NOTE:** GameClient.java file uses the login() method to complete the login process. The player's credentials are verified using boolean isPlayer() and boolean isPassword() located in PlayerDatabase.java


Logging In:

![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/login.png)


Logging Out:

![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/logout.png)







## Permanently Removing Account
Written by: Brett Mullins

If the user wants to remove his or her character permanently from the game, then the user should follow these steps to accomplish deletion. It is important for the user to realize this process is PERMANTENT and once an account is deleted it can NEVER be recovered. First, if the user is not already logged in, log into the game as a user normally would. The next step would be to enter the quit command into the user prompt. At that point, the user will be prompted if he or she wants to permanently delete their player and account. The user should then type "Y" (for yes). The user will then be prompted for his or her password. This is to prevent unwanted or accidental deletion of the character and account. After the password is entered correctly, the removal process will take place. If the password is entered incorrectly, the user is not deleted and is logged out normally.

![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/removePlayer.png)



## Timeout Due to Inactivity
Written by: Haroon Tanveer

Before this feature was added, when a player exited the system (whether using the QUIT command or Ctrl + C), the user was never actually logged out of the system. This feature adds a timeout to each account if they have been inactive for 5 minutes. Each time the user types an input and hits enter, the time will reset back to 0 for that user. If that time reaches 5 minutes, the user will be "kicked off" the server. This enables users to log back in if they were accidentally signed off. During testing, the time may be set to a different amount (in milliseconds) to force the account to time out quicker or delay the time.

**NOTE:** To reset the timer after each input, the developer must add the following line:
new Time();
This line will reset the timer back to 0 indicating that the user has entered an input
and is still active on the server. The Time class has been implemented inside of
GameClient.java.


![alt text](https://github.com/htanvee/CS321-2018F-002/blob/G1_Readme/timeout.png)







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
