import java.lang.RuntimeException;
import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.Collections;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.FileInputStream;
import org.json.JSONTokener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class CommandRunner {

    /**
     * Game interface
     */
    protected GameObjectInterface remoteGameInterface;

    //START 409_censor
    protected ArrayList<String> censorList;
    //END 409_censor

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
    private ArrayList<JSONObject> commandsInfo;
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
        commandFunctions.put("HELP",    (name, args) -> helpDisplay() );
        commandFunctions.put("LOOK",    (name, args) -> remoteGameInterface.look(name));
        commandFunctions.put("LISTPLAYERS", (name, args) -> remoteGameInterface.listAllPlayers(name));
        commandFunctions.put("LEFT",    (name, args) -> remoteGameInterface.left(name));
        commandFunctions.put("RIGHT",   (name, args) -> remoteGameInterface.right(name));
	commandFunctions.put("QUESTS", (name, args) -> remoteGameInterface.availableQuests(name));
        commandFunctions.put("SAY",     (name, args) -> {
            // Create empty string
            String message = String.join(" ", args);
            //System.out.println("[" + message + "]");

            if (message.equals("")) {
                return "[ERROR] Empty message";
            } else {
                return remoteGameInterface.say(name, message, censorList);  //409_censor pass censor list
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
                    return remoteGameInterface.whisper(name, receiver, message, censorList);  //409_censor pass censor list
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
                    return remoteGameInterface.reply(name, message, censorList);  //409_censor pass censor list
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
                   res = remoteGameInterface.shout(name, message, censorList);  //409_censor pass censor list

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
	commandFunctions.put("USEITEM", (name, args) -> {
	    if(args.isEmpty()){
		    return "You need to specify an item to use.";
	    }
	    else {
		String item = args.remove(0);
		while(!args.isEmpty()){
			item = item + " " + args.remove(0);
		}
		return remoteGameInterface.useItem(name, item);
	    }
	});
        commandFunctions.put("INVENTORY", (name, args) -> remoteGameInterface.inventory(name));
        commandFunctions.put("REDO",      (name, args) -> null);
        commandFunctions.put("QUIT",      (name, args) -> null);

        // PvP Commands
        commandFunctions.put("CHALLENGE",    (name, args) -> {
                    try {
                        String player = args.get(0);
                        String rounds = args.get(1);

                        if (Integer.parseInt(rounds) != 1 && Integer.parseInt(rounds) != 3 && Integer.parseInt(rounds) != 5){
                            return "[ERROR] You must specify 1 3 or 5 as the number of rounds.";
                        }

                        if (player.equals("")) {
                            return "[ERROR] No player specified";
                        } else {
                            remoteGameInterface.challenge(name, player, Integer.parseInt(rounds));
                            return null;
                        }
                    } catch (IndexOutOfBoundsException ex) {
                        return (args.size() == 1) ? "[ERROR] Number of rounds unspecified." : "[ERROR] No player specified.\n[ERROR] Number of rounds unspecified.";
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
        commandFunctions.put("RANK",   (name, args) -> { remoteGameInterface.getRank(name); return null; });
        commandFunctions.put("TUTORIAL",   (name, args) -> { remoteGameInterface.tutorial(name); return null; });
        commandFunctions.put("TOPTEN",   (name, args) -> { remoteGameInterface.topTen(name); return null; });
        commandFunctions.put("GIFT", (name, args) -> {
            if(args.size() < 2) {
                return "You need to provide a ghoul name and the item to gift.";
            }
            else {
                String ghoulName = args.remove(0);
                String itemName = String.join(" ", args);
                return remoteGameInterface.giftGhoul(name, ghoulName, itemName);
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
        commandFunctions.put("CATCH", (name, args) -> {
            if(args.isEmpty()) {
                return "You need to provide a spirit name.";
            }
            else {
                return remoteGameInterface.catchSpirit(name, args.remove(0));
            }
        });
        commandFunctions.put("SPIRITALL",    (name, args) -> remoteGameInterface.getAllSpirits(name));
        commandFunctions.put("CAUGHTSPIRITS",    (name, args) -> remoteGameInterface.getCurrentSpirits(name));
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
	
	commandFunctions.put("TAKEQUEST", (name, args) -> {
		if(args.size() != 1){
			System.out.println("Invalid value, please try again");
			return null;
		}
		else{
			int questNumber = Integer.parseInt(args.remove(0));
			return remoteGameInterface.takeQuest(name, questNumber);
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
	/*
	 * @author James Bruce
	 * this is the map command
	 */
	commandFunctions.put("MAP", (name, args) -> {return remoteGameInterface.map(name);});
	commandFunctions.put("OBJECTIVES", (name, args) -> {return remoteGameInterface.objectives(name);});
	//416_GroupChat START
	commandFunctions.put("GROUPCHAT", (name, args) -> {
		//TODO:
		// 1. check that chat room name is not the same as an existing game command

		//expect only one argument with command
		if( args.size() != 1 )
			return "The command takes one argument. Correct usage is GROUPCHAT <Group Name>.";
        else
        {
            for( String key: commandFunctions.keySet())
                if(key.equalsIgnoreCase(args.get(0)))
                    return "[Invalid Format]. you cannot name your chat room to one of the commands";
        }
        return remoteGameInterface.createGroupChat( args.get(0), name);


	});
	commandFunctions.put("GROUPCHATPRINT", (name, args) -> {
		//This is primarily used for debugging
		//expect only one argument with command
		if( args.size() != 1 )
			return "The command takes one argument. Correct usage is GROUPCHATPRINT <Group Name>.";

		return remoteGameInterface.printGroupChat( args.get(0));

	});

	commandFunctions.put("JOIN", (name, args) -> {
		//expect only one argument with command
		if( args.size() != 1 )
			return "The command takes one argument. Correct usage is JOIN <Group Name>.";

		return remoteGameInterface.GCJoin( args.get(0), name);
	});

        commandFunctions.put("GROUPCHATHELP", (name, args) -> {
            //expect only one argument with command
            return remoteGameInterface.GCGetHelp(name);
        });
	//416_GroupChat END
    }

    /**
     * Helper class to store a commands ID (command caller), its definition and
     * the function to be called with the command
     */
    private class Command {
        private String id;
        private String alias = "";
        private CommandFunction<String, ArrayList<String>, String> function;

        /**
         * A constructed for the nested Command class that will initialize all variables for the class.
         * @param id name of the command
         * @param arguments arguments of the command
         * @param description text description of the command
         * @param function the function to be executed when calling the command
         * @return new Command
         */
        public Command(String id, CommandFunction<String, ArrayList<String>, String> function) {
            this.id = id;
            this.function = function;
        }

        /**
         * A method for running a command using the passed arraylist as the arguments for the command.
         * @param name name of the command
         * @param args list of text arguments form input
         * @return the String returned by the execution of the command
         */
        public String run(String name, ArrayList<String> args) throws RemoteException {
            return function.apply(name, args);
        }

        /**
         * Accessor for the id variable of a command
         * @return the id of this command
         */
        public String getId() {
            return id;
        }

        /**
         * Accessor for the arguments variable of a command
         * @return the arguments of this command
         */
        public String getAlias() {
            return alias;
        }

        /**
         * Accessor for the description variable of a command
         * @return the description of this command
         */
        public void setAlias(String newAlias) {
            alias = newAlias;
        }
    }

    /**
     * Store commands in memory
     */
    private HashMap<String, Command> commands = new HashMap<String, Command>();

    /**
     * A constructor for the CommandRunner class which initializes the remoteGameInterace variable and the commandsInfo variable, as well as, sets up the list of functions and commands.
     * @param rgi remote game interface
     * @param commandsFile path to file with command descriptions
     * @return new CommandRunner
     */
    public CommandRunner(GameObjectInterface rgi, String commandsFile) {
        this.remoteGameInterface = rgi;
        this.commandsInfo = parseCommandsFile(commandsFile);
        censorList = loadCensorList();    //409_censor load censor list
	this.remoteGameInterface = rgi;
        setupFunctions();
        createCommands();
    }

    /**
     * Creates a hashmap for the available commands as well as aliases for those commands and links each command to its description
     * @param descriptions map with command names as keys and their descriptions as values
     * @return the hashmap of commands and their descriptions
     */
    private void createCommands() {
        HashMap<String, String> aliasesMap = getAliasesFromFile();

        for (JSONObject cmdInfo : this.commandsInfo) {
            String name = ((String) cmdInfo.get("name")).toUpperCase();

            CommandFunction<String, ArrayList<String>, String> function = commandFunctions.get(name);

            if (function != null) {
                Command new_command = new Command(name, function);
                commands.put(name, new_command);

                String alias = aliasesMap.get(name);
                if (alias != null) {
                    new_command.setAlias(alias);
                    commands.put(alias.toUpperCase(), new_command);
                }
            } else {
                // Missing function
                System.out.println("[Warning] Command " + name.toUpperCase() + " has a description but no preprocessing function associated. It won't be added to the client.");
            }
        }

        createHelpUI(commandsInfo);
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

            reader.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    /**
     * A method for executing one of the commands for the game, done in a way such that any command could be run regardless of how it works.
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

    private String helpCommandUI;

    /**
     * Converts the available commands into a string and returns it to the user. 
     * @return string with commands name, accepted arguments and descriptions
     */
    public String helpDisplay() {
        return "The game lets you use the following commands:\n\n" + helpCommandUI;
    }

    private ArrayList<JSONObject> parseCommandsFile(String commandsFile) {
        ArrayList<JSONObject> commands = new ArrayList<JSONObject>();

        // Read the file
        try (InputStream fileStream = new FileInputStream(commandsFile)) {
            // Parse it into array
            JSONArray json = new JSONArray(new JSONTokener(fileStream));

            // Add each object to our ArrayList
            for (int i = 0; i < json.length(); i++) {
                commands.add(json.getJSONObject(i));
            }
        } catch (IOException | JSONException ex) {
            Logger.getLogger(CommandRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Return it
        return commands;
    }

    private java.util.Map<String, ArrayList<JSONObject>> parseCommandsCategories(ArrayList<JSONObject> commands) {
        java.util.Map<String, ArrayList<JSONObject>> categoriesMap = new HashMap<String, ArrayList<JSONObject>>();

        // Build string for each command
        for (JSONObject cmd : commands) {
            String cat = ((String) cmd.get("category")).toUpperCase();

            if (categoriesMap.get(cat) == null) {
                categoriesMap.put(cat, new ArrayList<JSONObject>());
            }

            categoriesMap.get(cat).add(cmd);
        }

        return categoriesMap;
    }

    private String getCommandHelpUI(JSONObject cmd) {
        StringBuilder uiBuild = new StringBuilder();

        // Add name
        String name = ((String) cmd.get("name")).toUpperCase();
        uiBuild.append(String.format("%-15s", name));

        // Get alias
        String alias = commands.get(name).getAlias();
        Boolean aliasSet = alias.equals("");

        // Add description
        String[] description = ((String) cmd.get("description")).split(" ");
        int count = -1;
        for (int i = 0; i < description.length; i++) {
            String next = description[i];
            int l = next.length();

            if ((count + l + 1) > 60) {
                // Add alias if there's a second line of description
                if (! aliasSet) {
                    String aliasAppend = "\n(alias: " + alias + ")";
                    uiBuild.append(aliasAppend + String.join("", Collections.nCopies((18 - aliasAppend.length()), " ")));
                    aliasSet = true;
                } else {
                    uiBuild.append("\n" + String.join("", Collections.nCopies(18, " ")));
                }
                count = 0;
                uiBuild.append(next);
            } else {
                uiBuild.append(" " + next);
            }

            count += (l + 1);
        }
        uiBuild.append("\n");

        // Add alias if not set yet
        if (! aliasSet) {
            uiBuild.append("(alias: " + alias + ")\n");
            aliasSet = true;
        }

        // Add uses if any
        JSONArray uses = (JSONArray) cmd.get("uses");
        if (uses.length() > 0) {

            for (int i = 0; i < uses.length(); i++) {
                JSONObject use = (JSONObject) uses.getJSONObject(i);

                // Add case
                String c = (String) use.get("case");
                if (c.equals("")) c = "(no args)";

                uiBuild.append("    ");
                uiBuild.append(String.format("%-25s", c));
                uiBuild.append(" -");

                // Add description
                String[] desc = ((String) use.get("description")).split(" ");
                count = -1;
                for (int j = 0; j < desc.length; j++) {
                    String next = desc[j];
                    int l = next.length();

                    if ((count + l + 1) > 44) {
                        uiBuild.append("\n" + String.join("", Collections.nCopies(32, " ")));
                        count = 0;
                        uiBuild.append(next);
                    } else {
                        uiBuild.append(" " + next);
                    }

                    count += (l + 1);
                }
                uiBuild.append("\n");
            }
        }

        // Add space and return
        uiBuild.append("\n");
        return uiBuild.toString();
    }

    private void createHelpUI(ArrayList<JSONObject> commands) {
        // parsed array
        java.util.Map<String, ArrayList<JSONObject>> categoriesMap = parseCommandsCategories(commands);

        // Build complete ui with categories
        StringBuilder uiBuild = new StringBuilder();
        for (String key : categoriesMap.keySet()) {
            // Don't do MISC here
            if (! key.equals("MISCELLANEOUS")) {
                // Add category header
                uiBuild.append(String.join("", Collections.nCopies(78, "=")) + "\n");
                uiBuild.append(String.join("", Collections.nCopies((int) (78 - key.length()) / 2, " ")) + key.toUpperCase() + "\n");
                uiBuild.append(String.join("", Collections.nCopies(78, "=")) + "\n");

                // Add commands strings
                for (JSONObject cmd : categoriesMap.get(key)) {
                    uiBuild.append(getCommandHelpUI(cmd));
                }
            }
        }
        // add MISC header
        String misc = "MISCELLANEOUS";
        uiBuild.append(String.join("", Collections.nCopies(78, "=")) + "\n");
        uiBuild.append(String.join("", Collections.nCopies((int) (78 - misc.length()) / 2, " ")) + misc.toUpperCase() + "\n");
        uiBuild.append(String.join("", Collections.nCopies(78, "=")) + "\n");

        // add MISC commands
        for (JSONObject cmd : categoriesMap.get("MISCELLANEOUS")) {
            uiBuild.append(getCommandHelpUI(cmd));
        }

        // Set ui variable
        helpCommandUI = uiBuild.toString();
    }
    
    //START 409_censor
    private ArrayList<String> loadCensorList(){
        Scanner fileIn = null;
        String tempStr = null;
	ArrayList<String> temp = new ArrayList<String>();
        try{
            fileIn = new Scanner( new FileReader( "censorlist.txt" ) );
            while( fileIn.hasNextLine() ){
                tempStr = fileIn.nextLine();
		//check if string from file is empty or all spaces
		//ignore if it is, add to ArrayList temp if it is not
                if( !tempStr.isEmpty() && !tempStr.replaceAll("\\s+","").isEmpty() )
			temp.add( tempStr );
            }
            if(false){    //Used for debugging
                System.out.println( "******Contents of censorList: " + temp.toString() );
            }
        }catch( IOException e ){
            System.out.println( e );
        }finally{
           if( fileIn != null )
                   fileIn.close();
        }
        return temp;    //return temp variable, it has all items in censorlist.txt
    }
    //END 409_censor
}