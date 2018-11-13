import java.lang.RuntimeException;
import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;

public class CommandRunner {

    /**
     * Game interface
     */
    protected GameObjectInterface remoteGameInterface;
 
    /**
     * Wrap a lambda expression and allow it to throw a RemoteException
     */
    @FunctionalInterface
    private interface CommandFunction<U, V, W> {
        public W apply(U u, V v) throws RemoteException;
    }

    /**
     * Store command functions and preprocessing of arguments
     */
    private HashMap<String, CommandFunction<String, ArrayList<String>, String>> commandFunctions
            = new HashMap<String, CommandFunction<String, ArrayList<String>, String>>();
    private String lastCommand = "";
    private ArrayList<String> lastArgs = new ArrayList<String>();

    /**
     * For each command add it to the hashmap defining also a lambda expression
     * receiving the name of the player and the arguments parsed from the input.
     * In the expression catch. Do any preprocessing needed for the arguments
     * and call the command function in the RGI.
     */
    private void setupFunctions() {
        // Help command
        commandFunctions.put("HELP",    (name, args) -> listCommands() );
        commandFunctions.put("LOOK",    (name, args) -> remoteGameInterface.look(name));
        commandFunctions.put("LISTPLAYERS", (name, args) -> remoteGameInterface.listAllPlayers(name));
        commandFunctions.put("LEFT",    (name, args) -> remoteGameInterface.left(name));
        commandFunctions.put("RIGHT",   (name, args) -> remoteGameInterface.right(name));
        commandFunctions.put("SAY",     (name, args) -> {
            // Create empty string
            String message = String.join(" ", args);
            //System.out.println("[" + message + "]");

            if (message.equals("")) {
                return "[ERROR] Empty message";
            } else {
                return remoteGameInterface.say(name, message);
            }
        });
        commandFunctions.put("WHISPER", (name, args) -> {
            try {
                String receiver = args.remove(0);
                String message = String.join(" ", args);
                if (receiver.equals("")) {
                return "[ERROR] You need to specify another player to whisper.";
                }
                else if (message.equals("")) {
                    return "[ERROR] You need to include a message to whisper.";
                }
                else {
                    return remoteGameInterface.whisper(name, receiver, message);
                    //return null;
                }
            }
            catch(IndexOutOfBoundsException ex) {
                return "[ERROR] No name specified.";
            }
        });
        commandFunctions.put("REPLY", (name, args) -> {
            try {
                String message = String.join(" ", args);
                if(message.equals("")) {
                    return "[ERROR] You need to include a message to reply.";
                }
                else {
                    return remoteGameInterface.reply(name, message);
                }
            }
            catch(IndexOutOfBoundsException ex) {
                return "[ERROR] You need to include a message to reply.";
            }
        });
        commandFunctions.put("SHOUT",(name,args)->
        {
           try
           {
               String res = "Invalid Format";
               if(args.isEmpty())
               {
                   res = "You need to write a message to shout.";
               }
               else
               {
                   String message = String.join(" ", args);
                   res = remoteGameInterface.shout(name, message);

               }
               return res;
           }
           catch(Exception e){return "Invalid Format";}
        });
        commandFunctions.put("IGNORE",(name,args)->
                {
                    try
                    {
                        String res = "Invalid format";
                        if (args.isEmpty())
                        {
                            return "Please input a valid ignore command.";
                        }
                        else
                        {
                            String choice = args.remove(0);

                            switch (choice.toUpperCase())
                            {
                                case "-L":
                                    if (!args.isEmpty())
                                    {
                                        res = "[Invalid format] listing does not require arguments.";
                                    }
                                    else
                                    {
                                        res = remoteGameInterface.listIgnoredPlayers(name);
                                    }
                                    break;
                                case "-A":
                                    if (args.isEmpty())
                                    {
                                        res = "You need to specify a player to ignore.";
                                    }
                                    else
                                    {
                                        res = remoteGameInterface.ignore(name, args.remove(0));
                                    }
                                    break;
                                case "-R":
                                    if (args.isEmpty())
                                    {
                                        res = "You need to specify a player to unignore.";
                                    }
                                    else
                                    {
                                        res = remoteGameInterface.unIgnore(name, args.remove(0));
                                    }
                            }
                        }
                        return res;
                    }
                    catch(Exception e){return "Invalid format";}
                }
        );
        commandFunctions.put("MOVE",     (name, args) -> {
            try {
                String direction = args.get(0);

                if (direction.equals("")) {
                    return "[ERROR] No direction specified";
                } else {
                    return remoteGameInterface.move(name, direction);
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No direction specified";
            }
        });
        commandFunctions.put("PICKUP",    (name, args) -> {
            try {
                String object = args.get(0);
                for (int i = 1; i < args.size(); i++) {
                    object += " " + args.get(i);
                }

                if (object.equals("")) {
                    return "[ERROR] No object specified";
                } else {
                    return remoteGameInterface.pickup(name, object);
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No object specified";
            }
        });
		commandFunctions.put("DESCRIBE",    (name, args) -> {
            try {
                String object = args.remove(0);
                while (!args.isEmpty()) {
                    object += " " + args.remove(0);
                }

                if (object.equals("")) {
                    return "[ERROR] No object specified";
                } else {
                    return remoteGameInterface.describe(name, object);
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No object specified";
            }
        });
        commandFunctions.put("DROPOFF",   (name, args) -> {
            try {
                String object = args.remove(0);
                while (!args.isEmpty()) {
                    object += " " + args.remove(0);
                }

                if (object.equals("")) {
                    return "[ERROR] No object specified";
                } else {
                    return remoteGameInterface.dropoff(name, object);
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No object specified";
            }
        });
        commandFunctions.put("SORTINVENTORY",   (name, args) -> {
            try {
                String attribute = args.remove(0);
                while (!args.isEmpty()) {
                    attribute += " " + args.remove(0);
                }

                if (attribute.equals("")) {
                    return "[ERROR] No attribute specified";
                } else {
                    return remoteGameInterface.sortInventory(name, attribute);
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No attribute specified";
            }
        });
        commandFunctions.put("OFFERITEM",   (name, args) -> {
            if(args.isEmpty()) {
                return "You need to provide a player to offer an item.";
            }
            else {
                String offeredPlayer = args.remove(0);
                if(args.isEmpty()) {
                    return "You need to provide an item to offer.";
                }
                else {
                    String itemName = args.remove(0);
                    while (!args.isEmpty())
                    {
                        itemName += " " + args.remove(0);
                    }
                    return remoteGameInterface.offerItem(name, offeredPlayer, itemName);
                }
            }
        });
        commandFunctions.put("OFFERRESPONSE",   (name, args) -> {
            if(args.isEmpty()) {
                return "You need to either accept or refuse the offer.";
            }
            else {
                String decision = args.remove(0);
                return remoteGameInterface.offerResponse(name, decision);
            }
        });
        commandFunctions.put("INVENTORY", (name, args) -> remoteGameInterface.inventory(name));
	commandFunctions.put("REDO", (name, args) -> null);
        //commandFunctions.put("QUIT",      (name, args) -> { remoteGameInterface.leave(name); return null; });

        // PvP Commands
        commandFunctions.put("CHALLENGE",    (name, args) -> {
            try {
                String player = args.get(0);

                if (player.equals("")) {
                    return "[ERROR] No player specified";
                } else {
                    remoteGameInterface.challenge(name, player);
                    return null;
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No player specified";
            }
        });
        commandFunctions.put("ACCEPT",    (name, args) -> {
            try {
                String player = args.get(0);

                if (player.equals("")) {
                    return "[ERROR] No player specified";
                } else {
                    remoteGameInterface.accept(player, name);
                    return null;
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No player specified";
            }
        });
        commandFunctions.put("REFUSE",    (name, args) -> {
            try {
                String player = args.get(0);

                if (player.equals("")) {
                    return "[ERROR] No player specified";
                } else {
                    remoteGameInterface.refuse(player, name);
                    return null;
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No player specified";
            }
        });
        commandFunctions.put("ROCK",       (name, args) -> { remoteGameInterface.rock(name); return null; });
        commandFunctions.put("PAPER",      (name, args) -> { remoteGameInterface.paper(name); return null; });
        commandFunctions.put("SCISSORS",   (name, args) -> { remoteGameInterface.scissors(name); return null; });
        commandFunctions.put("LEADERBOARD",   (name, args) -> { remoteGameInterface.checkBoard(name); return null; });
        commandFunctions.put("TUTORIAL",   (name, args) -> { remoteGameInterface.tutorial(name); return null; });
        commandFunctions.put("GIFT", (name, args) -> {
            if(args.isEmpty()) {
                return "You need to provide a ghoul name and an object.";
            }
            else if (args.size() == 2){
                String ghoulName = args.remove(0);
                String target = args.remove(0);

                return remoteGameInterface.giftGhoul(name, ghoulName, target);
            }
            else{
                return "Gift command only takes two arguments <ghoul_name> <item_name>.";
            }
        });
        commandFunctions.put("POKE", (name, args) -> {
            if(args.isEmpty()) {
                return "You need to provide a ghoul name.";
            }
            else {
                return remoteGameInterface.pokeGhoul(name, args.remove(0));
            }
        });
        commandFunctions.put("ENTER", (name, args) -> { 
            if(args.size() != 1){
                return "Specify the room you want to enter";
            }
            else{
                return remoteGameInterface.enter(name, args.get(0)); 
            }
        });
        commandFunctions.put("LEAVE", (name, args) -> { return remoteGameInterface.leaveRoom(name); });
        commandFunctions.put("SELL",       (name, args) -> { 
            if(args.size() < 1)
                return "Specify an item to sell.";
            String item = args.remove(0);
            while(args.size() != 0){
                item += " " + args.remove(0);
            }
            return remoteGameInterface.sell(name, item);  });
        commandFunctions.put("BUY",        (name, args) -> { 
            if(args.size() < 1)
                return "Specify an item to buy.";
            String item = args.remove(0);
            while(args.size() != 0){
                item += " " + args.remove(0);
            }
            return remoteGameInterface.buy(name, item);  });
        commandFunctions.put("MONEY",      (name, args) -> { return remoteGameInterface.money(name);  });
        commandFunctions.put("GIFTABLE",   (name, args) -> { return remoteGameInterface.giftable(name);  });
        commandFunctions.put("GIVE",       (name, args) -> { 
            try {//merged with new command list 
                if(args.size() != 2){
                    System.out.println("Invalid name or value, please try again");
                    return null;
                }
                else{
                    String receiver = args.remove(0);
                    Double amount = Double.parseDouble(args.remove(0));
                    
                    if(amount > 0){
                        return remoteGameInterface.gift(name, receiver, amount);
                    }
                    else {
                        return "Amount of money gifted must be greater than 0";
                    }
                }
            } catch (NumberFormatException e){
                return "invalid amount of money specified";
            } 
        });

        commandFunctions.put("RECEIVE",   (name, args) -> { return remoteGameInterface.acceptGift(name);  });
        
        commandFunctions.put("DECLINE",   (name, args) -> { return remoteGameInterface.declineGift(name);  });

        commandFunctions.put("CHANGEPREFIX", (name, args) -> {
            try {
                String newprefix = args.remove(0);
                return remoteGameInterface.changeChatPrefix(newprefix);
            }
            catch(IndexOutOfBoundsException e) {
                return "You need to input 3 characters for your new prefix.";
            }
        });
        commandFunctions.put("WHITEBOARD",   (name, args) -> {
            if (args.isEmpty()) {
                return remoteGameInterface.displayWhiteboard(name);
            } else if (args.size() == 1 && args.get(0).equalsIgnoreCase("CLEAR")) {
                return remoteGameInterface.clearWhiteboard(name);
            } else if (args.size() >= 1 && args.get(0).equalsIgnoreCase("WRITE")) {
                if (args.size() > 1) {
                    args.remove(0);
                    return remoteGameInterface.writeWhiteboard(name, String.join(" ", args));
                } else {
                    return "[ERROR] You need to specify a MESSAGE to WRITE.";
                }
            } else {
                return "[ERROR] Couldn't parse WHITEBOARD command.";
            }
        });
	commandFunctions.put("MAP", (name, args) -> {return remoteGameInterface.map(name);});
    }

    /**
     * Helper class to store a commands ID (command caller), its definition and
     * the function to be called with the command
     */
    private class Command {
        private String id;
        private String arguments;
        private String description;
        private CommandFunction<String, ArrayList<String>, String> function;

        /**
         * @param id name of the command
         * @param arguments arguments of the command
         * @param description text description of the command
         * @param function the function to be executed when calling the command
         * @return new Command
         */
        public Command(String id, String arguments, String description, CommandFunction<String, ArrayList<String>, String> function) {
            this.id = id;
            this.arguments = arguments;
            this.description = description;
            this.function = function;
        }

        /**
         * @param name name of the command
         * @param args list of text arguments form input
         * @return the String returned by the execution of the command
         */
        public String run(String name, ArrayList<String> args) throws RemoteException {
            return function.apply(name, args);
        }

        /**
         * @return the id of this command
         */
        public String getId() {
            return id;
        }

        /**
         * @return the arguments of this command
         */
        public String getArguments() {
            return arguments;
        }

        /**
         * @return the description of this command
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Store commands in memory
     */
    private HashMap<String, Command> commands = new HashMap<String, Command>();

    /**
     * @param rgi remote game interface
     * @return new CommandRunner
     */
    public CommandRunner(GameObjectInterface rgi) {
        this.remoteGameInterface = rgi;
        setupFunctions();
        createCommands();
    }

    /**
     * @param rgi remote game interface
     * @param commandsFile path to file with command descriptions
     * @return new CommandRunner
     */
    public CommandRunner(GameObjectInterface rgi, String commandsFile) {
        this.remoteGameInterface = rgi;
        setupFunctions();

        // TODO: Read file, extract command descriptions and call createCommands(descriptions)
        try (Scanner file_commands = new Scanner(new File(commandsFile));) {
            HashMap<String, String[]> file_map = new HashMap<String, String[]>();

            while(file_commands.hasNextLine()){
                String currentline = file_commands.nextLine();
                String[] command_parts = currentline.split(",");

                String command_name = command_parts[0];
                String[] command_description = new String[]{ command_parts[1], command_parts[2] };

                file_map.put(command_name, command_description);
            }
            createCommands(file_map);
        } catch (IOException ex) {
            Logger.getLogger(CommandRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create sample descriptions for commands. Then use them to create the commands
     */
    private void createCommands() {
        HashMap<String, String[]> descriptions = new HashMap<String, String[]>();

        // Default commands
        descriptions.put("LOOK",      new String[]{"",         "Shows you the area around you"});
        descriptions.put("LISTPLAYERS",new String[]{"", "Shows a list of all the players in the world."});
        descriptions.put("LEFT",      new String[]{"",         "Turns your player left 90 degrees."});
        descriptions.put("RIGHT",     new String[]{"",         "Turns your player right 90 degrees."});
        descriptions.put("SAY",       new String[]{"WORDS",    "Says <WORDS> to any other players in the same area."});
        descriptions.put("WHISPER",   new String[]{"PLAYER MESSAGE", "Says <MESSAGE> to specified <PLAYER>."});
        descriptions.put("REPLY",     new String[]{"MESSAGE", "Says <MESSAGE> to last player who whispered you."});
        descriptions.put("MOVE",      new String[]{"DIRECTION","Tries to walk in a <DIRECTION>."});
        descriptions.put("PICKUP",    new String[]{"OBJECT",   "Tries to pick up an <OBJECT> in the same area."});
        descriptions.put("DROPOFF",   new String[]{"OBJECT",   "Tries to drop off an <OBJECT> in the same area."});
        descriptions.put("INVENTORY", new String[]{"",         "Shows you what objects you have collected."});
	descriptions.put("REDO",      new String[]{"",         "Performs the last command you entered."});
        descriptions.put("QUIT",      new String[]{"",         "Quits the game."});
        descriptions.put("HELP",      new String[]{"",         "Displays the list of available commands"});
        descriptions.put("SORTINVENTORY",      new String[]{"ATTRIBUTE",         "Sorts inventory by specified name, value or weight."});

        // Ghoul commands
        descriptions.put("POKE",      new String[]{"GHOUL",    "Pokes <GHOUL>"});
        descriptions.put("GIFT",      new String[]{"GHOUL, ITEM", "Gives your <ITEM> to <GHOUL>"});

        // PvP Commands
        descriptions.put("CHALLENGE", new String[]{"PLAYER",   "Challenges another <PLAYER> to a Rock Paper Scissors Battle."});
        descriptions.put("ACCEPT",    new String[]{"PLAYER",   "Accepts a Rock Paper Scissors Battle Challenge from a specified <PLAYER>."});
        descriptions.put("REFUSE",    new String[]{"PLAYER",   "Refuses a Rock Paper Scissors Battle Challenge from a specified <PLAYER>."});
        descriptions.put("ROCK",      new String[]{"",         "Play <ROCK> in your current Rock Paper Scissors Battle."});
        descriptions.put("PAPER",     new String[]{"",         "Play <PAPER> in your current Rock Paper Scissors Battle."});
        descriptions.put("SCISSORS",  new String[]{"",         "Play <SCISSORS> in your current Rock Paper Scissors Battle."});
        descriptions.put("LEADERBOARD",  new String[]{"",      "Display the current Rock Paper Scissors Leaderboard."});
        descriptions.put("TUTORIAL",  new String[]{"",         "Display a tutorial for Rock Paper Scissors."});

        //Shops & Money
        descriptions.put("ENTER",     new String[]{"SHOP",     "Enters shop at clock tower" });
        descriptions.put("LEAVE",     new String[]{"SHOP",     "Leaves shop" });
        descriptions.put("SELL",      new String[]{"ITEM",     "Sell item in your inventory to the shop" });
        descriptions.put("BUY",      new String[]{"ITEM",      "Buy an item from the shop" });
        descriptions.put("MONEY",     new String[]{"",         "Line-by-line display of money"});
        descriptions.put("GIFTABLE",  new String[]{"",         "List players in the same room that you can give money to"});
        descriptions.put("GIVE", new String[]{"GIFTEE","AMOUNT", "Give amount of money to a friend" });
        descriptions.put("RECEIVE", new String[]{"", "Receive a gift if someone has tried to gift you" });
	
	//World Command
	descriptions.put("MAP", new String[]{"", "Displays an ascii art map of the world."});

        //chat system
        descriptions.put("SHOUT",      new String[]{"MESSAGE", "Says <MESSAGE> to all players in the game."});
        descriptions.put("IGNORE",     new String[]{"-L;-A;-R PLAYER", "Use -A to add players to ignore list; -R to remove from list; -L with no player name to show list."});

        // Create them
        createCommands(descriptions);
    }

    /**
     * @param descriptions map with command names as keys and their descriptions as values
     */
    private void createCommands(HashMap<String, String[]> descriptions) {
        HashMap<String, String> aliasesMap = getAliasesFromFile();

        for (String key : descriptions.keySet()) {
            String arguments = descriptions.get(key)[0];
            String description = descriptions.get(key)[1];
            CommandFunction<String, ArrayList<String>, String> function = commandFunctions.get(key);

            if (function != null) {
                Command new_command = new Command(key, arguments, description, function);
                commands.put(key, new_command );
                String alias = aliasesMap.get(key);
                if (alias != null){
                    commands.put(alias.toUpperCase(), new_command);
                }
            }
        }
    }

    private HashMap<String, String> getAliasesFromFile() {
        String filePath = "aliases.csv";
        HashMap<String, String> map = new HashMap<String, String>();

        try{
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split(",", 2);
                if (parts.length >= 2)
                {
                    String key = parts[0];
                    String value = parts[1];
                    map.put(key, value);
                    //System.out.println(parts[0] +"," + parts[1]);
                }
            }

            // for (String key : map.keySet())
            // {
            //     System.out.println(key + "," + map.get(key));
            // }
            reader.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    /**
     * @param command name of the command to be run
     * @param args list of arguments from input
     * @param playerName name of player running the command
     * @throws RemoteException [description]
     */
    public void run(String command, ArrayList<String> args, String playerName) {
        // System.out.println(playerName + ": " + command + '(' + args + ')');
	
    	String cmdToRun = command;
    	ArrayList<String> argsToRun = args;
    	
    	if (cmdToRun.equalsIgnoreCase("REDO")){
    	    if (lastCommand.equals("")) {
                System.out.println("No previous command");
                return;
            }

            cmdToRun = lastCommand;
            argsToRun = lastArgs;
    	}
        Command cmd = commands.get(cmdToRun.toUpperCase());
	
        if (cmd != null) {

            try {
                lastCommand = cmdToRun;
                lastArgs = argsToRun;

                String result = cmd.run(playerName, argsToRun);
                if (result != null)
                    System.out.println(result);

                remoteGameInterface.logInteraction(playerName, cmdToRun, argsToRun, result);
            } catch (RemoteException ex) {
                Logger.getLogger(CommandRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // prompt command not found
        else{
            System.out.println("Command not found. Type HELP for command list.");
        }
    }

    /**
     * @return string with commands name, accepted arguments and descriptions
     */
    public String listCommands() {
        String s = "The game allows you to use the following commands:\n";

        for (String key : commands.keySet()) {
            Command command = commands.get(key);
            String line = String.format("- %-30s%s\n", command.getId() + " " + command.getArguments(), command.getDescription());
            s += line;
        }

        return s;
    }

   
}
