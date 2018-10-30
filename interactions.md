# Player-World Interactions
TODO: Explain what this covers


## How to use it

### Interacting with the game (Joseph)
TODO: how to enter commands

### Available commands (Andrew)
TODO: which commands available. include latests commands!

### Command Aliases (Laura)
TODO: explain how to use the alias file



## Notes for developers

### Client-Server command execution (Kevin)
![Client-Server Interaction Diagram](./server-client-interaction-diagram.png)

The game works by making use of Java's RMI. Specifically, there's an RMI server running alongside the `GameServer`. Said server is bound to a `GameObject`, which acts as a middleman between *Server* and *Client*.

When a player starts the `GameClient`, this connects to the RMI server to get the *Object* intance and to the *Server* to request a web socket to allow for communication between clients.

The *Client* makes then use of the `GameObject` interface to call interactions from the user to the *world*. The middleman then calls to the `GameCore`, who actually performs the interactions. If there's anything needed to be returned to the *Client*, the *Core* will give it back to the *Object*, and this one to the *Client*. After this, the result will be treated as defined by the processing of the command that is being executed (see [Registering new commands](#Registering-new-commands-Kevin)).

### Commands for interacting (Andrew)
TODO: which methods are available for commands

### Registering new commands (Kevin)
To register a new command in the game, several steps must be followed.

First, functionality for the command must be implemented. This should usually be done in `GameCore`. However, a middleman invocation should exist in `GameObject` too (as to be able for the `GameClient` to call it.

Once this is done, the command must be registered for the *Client* to be able to recognize it. To do so:

#### 1. Register Preprocessing
Add the command preprocessing. This is done in `setupFunctions()` method in `CommandRunner.java` (this method is around the top of the file). To do so, you need to include a line similar to the following:
```java
commandFunctions.put("", (name, args) -> { <code> } );
```
The purpose of this is to make all the commands have a common way of being called so we can allow `GameClient` to not know anything about the commands. What we do, instead, is parse the input read from console into `command: String` and `args: ArrayList`. Then we call the command with `name: String`, which is the name of the player, and the `args`. After that, each command will do the preprocessing needed (the `<code>` part above).
It might be that your command doesn't need processing, such as LOOK:
```java
commandFunctions.put("LOOK", (name, args) -> remoteGameInterface.look(name));
```
In this case you can just call the method needed from the remoteGameInterface. **BEWARE!** This needs to return a `String`. In the case of **LOOK** it returns whatever the player should get in the console. If your command handles console output getting the player's writer and **doesn't return a string**, then you need to do something like the **ROCK** command and return `null`:
```java
commandFunctions.put("ROCK", (name, args) -> {
    remoteGameInterface.rock(name);
    return null;
});
```
If in fact you need to do any preprocessing, you are gonna end up with something like the **MOVE** command:
```java
commandFunctions.put("MOVE", (name, args) -> {
    String direction = args.get(0);

    if (direction.equals("")) {
        return "[ERROR] No direction specified";
    } else {
        return remoteGameInterface.move(name, direction);
    }
});
```
You can refer to other commands in the `CommandRunner.java` file for more examples.

#### 2. Include a description
(see next section)

### Configuring commands (Laura)
TODO: how to use file to change descriptions/arguments

### Debugging interactions (Joseph)

TODO: explain how logging works

