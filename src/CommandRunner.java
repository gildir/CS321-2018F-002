import java.lang.RuntimeException;
import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.File;
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
        commandFunctions.put("LEFT",    (name, args) -> remoteGameInterface.left(name));
        commandFunctions.put("RIGHT",   (name, args) -> remoteGameInterface.right(name));
        commandFunctions.put("SAY",     (name, args) -> {
            // Create empty string
            String message = String.join(" ", args);

            if (message.equals("")) {
                return "[ERROR] Empty message";
            } else {
                return remoteGameInterface.say(name, message);
            }
        });
        commandFunctions.put("MOVE",     (name, args) -> {
            String direction = args.get(0);

            if (direction.equals("")) {
                return "[ERROR] No direction specified";
            } else {
                return remoteGameInterface.move(name, direction);
            }
        });
        commandFunctions.put("PICKUP",    (name, args) -> {
            try {
                String object = args.get(0);

                if (object.equals("")) {
                    return "[ERROR] No object specified";
                } else {
                    return remoteGameInterface.pickup(name, object);
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] No object specified";
            }
        });
        commandFunctions.put("INVENTORY", (name, args) -> remoteGameInterface.inventory(name));
        commandFunctions.put("QUIT",      (name, args) -> { remoteGameInterface.leave(name); return null; });
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
        createCommands();

        // TODO: Read file, extract command descriptions and call createCommands(descriptions)
    }

    /**
     * Create sample descriptions for commands. Then use them to create the commands
     */
    private void createCommands() {
        HashMap<String, String[]> descriptions = new HashMap<String, String[]>();

        // Insert commands
        descriptions.put("LOOK",      new String[]{"",         "Shows you the area around you"});
        // descriptions.put("LEFT",      new String[]{"",         "Turns your player left 90 degrees."});
        // descriptions.put("RIGHT",     new String[]{"",         "Turns your player right 90 degrees."});
        descriptions.put("SAY",       new String[]{"WORDS",    "Says 'message' to any other players in the same area."});
        descriptions.put("MOVE",      new String[]{"DIRECTION", "Tries to walk in <direction>."});
        descriptions.put("PICKUP",    new String[]{"OBJECT",   "Tries to pick up an object in the same area."});
        descriptions.put("INVENTORY", new String[]{"",         "Shows you what objects you have collected."});
        descriptions.put("QUIT",      new String[]{"",         "Quits the game."});
        descriptions.put("HELP",      new String[]{"",         "Displays the list of available commands"});

        // Create them
        createCommands(descriptions);
    }
    /**
     *Creates a HashMap with the aliases of the commands
     */
    private HashMap getAliasesFromFile(){
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
    
        for (String key : map.keySet())
        {
            System.out.println(key + "," + map.get(key));
        }
        reader.close();
    }
    catch (Exception ex) {
        ex.printStackTrace();
     }
        return map;
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

    /**
     * @param command name of the command to be run
     * @param args list of arguments from input
     * @param playerName name of player running the command
     * @throws RemoteException [description]
     */
    public void run(String command, ArrayList<String> args, String playerName) {
        // System.out.println(playerName + ": " + command + '(' + args + ')');

        Command cmd = commands.get(command.toUpperCase());

        if (cmd != null) {

            try {
                String result = cmd.run(playerName, args);
                if (result != null)
                    System.out.println(result);
            } catch (RemoteException ex) {
                Logger.getLogger(CommandRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // TODO: prompt command not found
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
