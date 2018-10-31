# CS321-2018F-002
# Player Chat System ![CI status](https://img.shields.io/badge/build-passing-brightgreen.svg)
## Introduction
The game chat system allows players to communicate with one another in the world. As a player you can send a message to different game locations or specific players. Chat functionality has been expanded to WHISPER a message to a player, which behaves similar to a private message. A player’s REPLY to a WHISPER has been streamlined to avoid typing the recipient’s name. Players can also SHOUT, sending a message to every player in the game.

Also added is the ability to IGNORE all messages from a specific player, regardless of how the message was sent. Individual player ignore lists can be displayed to screen. If desired, the player that owns the ignore list can remove a specific player. 
To assist players in location, or searching for the correct spelling for a player name, players have the ability to view all players in the game world with the aptly named LISTPLAYERS command. 

## 401) Whisper ![chat](/images/comment-discussion.svg)
### Usage
The whisper command is used to send another player a private message. There is only one way to invoke a whisper:
1. **WHISPER playerName message**

For example. If p1 wants to whisper p2, p1 would use the WHISPER command as follows:

**WHISPER p2 What do you have in your inventory?**

On sucessfull command execution, p1 receives an onscreen message indicating the message was successfull sent.

### Comand Implementation
WHISPER reuses the following code in the GameCore class:

```java
public void broadcast(Player sendingPlayer, Player receivingPlayer, String message)
```

To following classes were edited to provide WHISPER functionality: GameCore.java, GameObject.java, GameObjectInterface.java. Of these, the bulk of the logic can be found at class GameCore.

#### Class GameCore, File: GameCore.java
The following function was added: 
```java
public String whisper(String name1, String name2, String message)
```
The function sends the private message to player name2, only after the following checks are made:
1. That neither name1 or name2 are null values.
2. That a player cannot whisper themselves.

On sucessfull execution, the function returns a string confirming success.

## 402) List Players in Game ![list](/images/database.svg)
### Description
You can list active players in the world. A world consists of several rooms, and these rooms are separated from one another. You might list players' name to be able to communicate with them with whisper functionality. ListPlayers has many functionalities. You might want to list players for shopping or challenging them into battles. It also gives you a good sense of your environment and online players in the game.  Listplayers is your personal contact list, so you want to keep it safe.
### Command
```
listplayers 
```
### Implementation
```java
class GameCore
{
    public String listAllPlayers(String palyer_name); 
}
class PlayerList
{
    private final LinkedList<Player> playerList;
    public String toString();
}
```
### Code Analysis 
PlayerList class keep a linked list of players called palyerList. toString methods returns the one string separated by space of all players in the linked list. 
GameCore class is responsible for underlying actions. ListAllPlayers is a method in this class that gets the player name, if the player name is valid then it returns a string of all players int the world using toString method of playerList.  
## 404) Reply
### Usage
There is one way to invoke the reply command:
1. **REPLY \<message>**

### Command Implmentation
When a player recieves a whipser the name of the player who sent it is tracked with a field in the Player class. The field stores only the name of the last person who sent a whisper to the player. If they recieve another whisper the most recent senders name will overwrite the old one.

To provide reply functionality the following classes were edited: Player, GameCore, GameClient, GameObjectInterface, GameObject. The logic for reply can be found in GameCore and utilizes the logic of the whisper command.

### Class: Player, File: Player.java
The following variable was added to track the name of the last player who whispered you:
```
private String lastWhisperName;
```

The variable can be accessed  with the following methods:
```
public void setLastWhisperName(String name)
public String getLastWhisperName()
```

### Class: GameCore, File: GameCore.java
The following changes were made to this class:
1. Function added
```java
public String reply(String name, String message)
```
The reply function only requires the name of the player sending the message and the message itself. This is because using the new variable in the Player class it can get the name of the player who will receive the reply, and then it can invoke the whisper command to actually send the message.

2. Function modified
```java
playerReceiving.setLastWhisperName(name1);
```
The above modification is right before the return in the whisper command. When a player is successfully able to send a whisper it will put their name in the lastWhisperName field in the Player class.

This is an example of reply in action:

![Image of reply](/images/404_reply.png)
## 405) Ignore ![mute](/images/mute.svg)
### Usage
There are two ways to ignore messages from a player:
1. **IGNORE -A <player_name>**
2. **/I -A <player_name>**

### Command Implementation
Ignored and ignoring players are tracked on separate lists residing in the Player object. If p1 wants to ignore p2, p2 would execute command “IGNORE –A p2”. This would add p2’s name to p1’s ignoreList, and would add p1’s name to p2’s ignoredByList. 

To provide the ignore functionality these classes were edited: Player, GameCore, GameClient, GameCoreInterface, GameObject, GameObjectInterface. Of these, the bulk of the logic can be found in classes Player and GameCore

#### Class: Player, File:Player.java
The following variables were added to track ignored and ignoring players:
```java
private ArrayList<String> ignoreList;
private ArrayList<String> ignoredByList;
```

Adding names to the ignore lists is done through the following methods:
```
public void ignorePlayer(String name)
public void addIgnoredBy( String name)
```

The following methods are used to search the lists. If a player is found in a specific list, the corresponding function returns a true, otherwise false.
```java
public boolean searchIgnoredBy(String name)
public boolean searchIgnoreList(String name)
```

#### Class: GameCore, File:Player.java
The following changes were made to this class:
1. Function added
```java
public  String  ignore (String  name , String  ignoreName )                                  
```
Updates ignore lists, after performing the following checks:  
a. Can't add self to ignore list 
b. Verifies that player being ignored exists 
c. Verifies player is not already in ignore list |

1. Function Changed
```java
public  void  broadcast (Player player , String message  )
```
Does not broadcast to ignoring players  

3. Function Changed
```java
public  void  broadcast (Player  sendingPlayer , Player  receivingPlayer , String  message )
```
Does not broadcast to ignoring players




## 407) List Ignore Users ![list](/images/database.svg)
### Description
You want to check all the players you have ignored. Ignore -l List all players who are ignored by you. Basically, this function allows you to list all the players that you muted. You might mute players due to misbehavior. You might to use this functionality to find out about those players in case you want to unmute them back or just for checking. List ignore users is your personal blacklist so don’t share it with any one. 
### Command
```
IGNORE -l 
```
### Implementation
```java
class GameCore
{
     public String listIgnoredPlayers(String player_name);
}
class Player
{
     private ArrayList<String> ignoreList;
     public String showIgnoreList();
}
```
### Code Analysis
In the class Player we keep track of ignored players in an arraylist called ignoreList. We update it with add/remove functionality provided in the same context. The showIgnoreList is a wrapper that prints out all ignored player as a string. This public method can get used by GameCore to print the ignored list.  
In the class GameCore we list ignored players by calling showIgnoreList, which granted the permission to access ignored players' names. Beware that the order of printing the players name is based on the order you have added them to your list.  
By issuing ignore -l you are able to use this functionality.

## 408) Unignore ![unmute](/images/unmute.svg)
### Description
Unignore a player that is currently in your ignore list. When player is unignored you are able to see their chat logs and their whisper logs in-game. 
### Command
```
IGNORE -r <player_name>
```
### Implementation
The following methods were added in order to implement the unignore feature.
```java
class GameCore
{
	public String unIgnore(String name, String unIgnoreName)
}
class GameObject
{

	public String unIgnore(String name, String ignoreName) throws RemoteException{ return core.unIgnore(name,ignoreName);

}
```
### Code Analysis
The unignore feature essentially takes in two arguments, name of player as well as unignore name. First, our function checks whether the player is attempting to unignore himself in which case an exception occurs, and if not true, the function begins to verify if the ignored player even exists in the game. If the player that the current player wants to ignore exists and is in their ignored list, then the player becomes unignored and we remove them from the ignored list within the player class.


## 412) Shout ![shout](/images/megaphone.svg)
### Usage
There is one way to invoke the shout command:
1. **SHOUT \<message>**

### Command Implementation
The shout command works very similarly to the say command, only it doesn't check if players are in the same room. When a player uses the shout command everyone in the game will see the message. Unless they have ignored the player of course.

To provide shout functionality the following classes were edited: GameClient, GameCore, GameObject, GameObjectInterface. The main logic for shout resides in the GameCore.java file where two new methods were added.

### Class: GameCore, File: GameCore.java
The following changes were made to this class:
1. Function added
```java
public String shout(String name, String message)
```
The function for shout is pretty much identical to say. It only rquires a name for the player shouting and their message. It will then call a broadcast function to write the message to other players. The broadcast function used is explained just below.

2. Function added
```java
public void broadcastShout(Player player, String message)
```
The shout command required a new broadcast function with a specific name. This broadcastShout method is similar to the broadcast method used by say. The difference is that this method doesn't check if the players are in the same room as the one shouting since shouts should be heard everywhere. Due to this difference we were unable to get say and shout to work with the same broadcast method, so we created this one.

## Team Members
* Shayan Amirhosseini
* Thomas Klock
* Amilcar Martinez
* Julius Ahenkora

## License
[GNU GENERAL PUBLIC LICENSE](http://fsf.org/)
