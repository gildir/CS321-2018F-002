import java.lang.RuntimeException;
import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * that receives a String with the name of the player and a List with the
     * rest of arguments parsed from the command input. In the expression catch
     * Checked Expressions and throw a Runtime one instead to allow to store
     * the functions in the hashmap. Inside the try block do any preprocessing
     * needed for the arguments and call the needed function in the RGI.
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
            try {
                int distance = Integer.parseInt(args.get(0));
                return remoteGameInterface.move(name, distance);
            } catch (Exception e) {
                // System.err.println(e);
                return "[ERROR] " + e.getMessage();
                return "[ERROR] Couldn't parse arguments";
            }
        });
        commandFunctions.put("PICKUP",    (name, args) -> {
            String object = args.get(0);

            if (object.equals("")) {
                return "[ERROR] No object specified";
            } else {
                return remoteGameInterface.pickup(name, object);
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
        private String description;
        private CommandFunction<String, ArrayList<String>, String> function;

        /**
         * @param id name of the command
         * @param description text description of the command
         * @param function the function to be executed when calling the command
         * @return new Command
         */
        public Command(String id, String description, CommandFunction<String, ArrayList<String>, String> function) {
            this.id = id;
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
        HashMap<String, String> descriptions = new HashMap<String, String>();

        // Insert commands
        descriptions.put("LOOK",      "Shows you the area around you");
        descriptions.put("LEFT",      "Says 'message' to any other players in the same area.");
        descriptions.put("RIGHT",     "Turns your player left 90 degrees.");
        descriptions.put("SAY",       "Turns your player right 90 degrees.");
        descriptions.put("MOVE",      "Tries to walk forward <distance> times.");
        descriptions.put("PICKUP",    "Tries to pick up an object in the same area.");
        descriptions.put("INVENTORY", "Shows you what objects you have collected.");
        descriptions.put("QUIT",      "Quits the game.");
        descriptions.put("HELP",      "Displays the list of available commands");

        // Create them
        createCommands(descriptions);
    }

    /**
     * @param descriptions map with command names as keys and their descriptions as values
     */
    private void createCommands(HashMap<String, String> descriptions) {
        for (String key : descriptions.keySet()) {
            String description = descriptions.get(key);
            CommandFunction<String, ArrayList<String>, String> function = commandFunctions.get(key);

            if (function != null) {
                commands.put(key, new Command(key, description, function));
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
            String line = String.format("%15s - %s\n", command.getId(), command.getDescription());
            s += line;
        }

        return s;
    }
}
