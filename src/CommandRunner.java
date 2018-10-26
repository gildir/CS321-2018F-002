import java.lang.RuntimeException;
import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.io.*;

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
        commandFunctions.put("CHALLENGE", (name, args) -> {
            try {
                String opponent = args.get(0);

                if (opponent.equals("")) {
                    return "[ERROR] You need to specify another player to challenge.";
                } else {
                    remoteGameInterface.challenge(name, opponent);
                    return null;
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] You need to specify another player to challenge.";
            }
        });
        commandFunctions.put("ACCEPT", (name, args) -> {
            try {
                String opponent = args.get(0);

                if (opponent.equals("")) {
                    return "[ERROR] You need to specify the player whose challenge you are accepting.";
                } else {
                    remoteGameInterface.accept(opponent, name);
                    return null;
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] You need to specify the player whose challenge you are accepting.";
            }
        });
        commandFunctions.put("REFUSE", (name, args) -> {
            try {
                String opponent = args.get(0);

                if (opponent.equals("")) {
                    return "[ERROR] You need to specify the player whose challenge you are refusing.";
                } else {
                    remoteGameInterface.refuse(opponent, name);
                    return null;
                }
            } catch (IndexOutOfBoundsException ex) {
                return "[ERROR] You need to specify the player whose challenge you are refusing.";
            }
        });
        commandFunctions.put("ROCK", (name, args) -> { remoteGameInterface.rock(name); return null; });
        commandFunctions.put("PAPER", (name, args) -> { remoteGameInterface.paper(name); return null; });
        commandFunctions.put("SCISSORS", (name, args) -> { remoteGameInterface.scissors(name); return null; });
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

        descriptions.put("CHALLENGE", new String[]{"PLAYER",   "Challenges another player to a Rock Paper Scissors Battle"});
        descriptions.put("ACCEPT",    new String[]{"PLAYER",   "Accepts a Rock Paper Scissors Battle Challenge from a specified player"});
        descriptions.put("REFUSE",    new String[]{"PLAYER",   "Refuses a Rock Paper Scissors Battle Challenge from a specified player"});

        descriptions.put("ROCK",      new String[]{"",         "Play ROCK in your current Rock Paper Scissors Battle"});
        descriptions.put("PAPER",     new String[]{"",         "Play PAPER in your current Rock Paper Scissors Battle"});
        descriptions.put("SCISSORS",  new String[]{"",         "Play SCISSORS in your current Rock Paper Scissors Battle"});
        // Create them
        createCommands(descriptions);
    }

    /**
     * @param descriptions map with command names as keys and their descriptions as values
     */
    private void createCommands(HashMap<String, String[]> descriptions) {
        for (String key : descriptions.keySet()) {
            String arguments = descriptions.get(key)[0];
            String description = descriptions.get(key)[1];
            CommandFunction<String, ArrayList<String>, String> function = commandFunctions.get(key);

            if (function != null) {
                commands.put(key, new Command(key, arguments, description, function));
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
