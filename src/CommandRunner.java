import java.lang.RuntimeException;
import java.rmi.RemoteException;
import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandRunner {

    // Access the game
    protected GameObjectInterface remoteGameInterface;

    // Default command functions and preprocessing of arguments
    private HashMap<String, BiFunction<String, ArrayList<String>, String>> commandFunctions
        = new HashMap<String, BiFunction<String, ArrayList<String>, String>>();

    //
    // For each command add it to the hashmap defining also a lambda expression
    // that receives a String with the name of the player and a List with the
    // rest of arguments parsed from the command input. In the expression catch
    // Checked Expressions and throw a Runtime one instead to allow to store
    // the functions in the hashmap. Inside the try block do any preprocessing
    // needed for the arguments and call the needed function in the RGI.
    //
    private void setupFunctions() {
        commandFunctions.put("LOOK", (String name, ArrayList<String> args) -> {
            try {
                return remoteGameInterface.look(name);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        commandFunctions.put("LEFT", (String name, ArrayList<String> args) -> {
            try {
                return remoteGameInterface.left(name);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        commandFunctions.put("RIGHT", (String name, ArrayList<String> args) -> {
            try {
                return remoteGameInterface.right(name);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        commandFunctions.put("SAY", (String name, ArrayList<String> args) -> {
            try {
                // Create empty string
                String message = String.join(" ", args);

                if (message.equals("")) {
                    return "[ERROR] Empty message";
                } else {
                    return remoteGameInterface.say(name, message);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        commandFunctions.put("MOVE", (String name, ArrayList<String> args) -> {
            try {
                int distance = Integer.parseInt(args.get(0));
                return remoteGameInterface.move(name, distance);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                // System.err.println(e);
                return "[ERROR] " + e.getMessage();
            }
        });

        commandFunctions.put("PICKUP", (String name, ArrayList<String> args) -> {
            try {
                String object = args.get(0);

                if (object.equals("")) {
                    return "[ERROR] No object specified";
                } else {
                    return remoteGameInterface.pickup(name, object);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        commandFunctions.put("INVENTORY", (String name, ArrayList<String> args) -> {
            try {
                return remoteGameInterface.inventory(name);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        commandFunctions.put("QUIT", (String name, ArrayList<String> args) -> {
            try {
                remoteGameInterface.leave(name);
                return "";
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        // Help command
        commandFunctions.put("HELP", (String name, ArrayList<String> args) -> listCommands());
    }

    // Helper class to store a commands ID (command caller), its definition and
    // the function to be called with the command
    private class Command {
        private String id;
        private String description;
        private BiFunction<String, ArrayList<String>, String> function;

        public Command(String id, String description, BiFunction<String, ArrayList<String>, String> function) {
            this.id = id;
            this.description = description;
            this.function = function;
        }

        public String run(String name, ArrayList<String> args) {
            return function.apply(name, args);
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }
    }

    // List of commands in memory
    private HashMap<String, Command> commands = new HashMap<String, Command>();

    // Public constructors
    public CommandRunner(GameObjectInterface rgi) {
        this.remoteGameInterface = rgi;
        setupFunctions();
        createCommands();
    }

    public CommandRunner(GameObjectInterface rgi, String commandsFile) {
        this.remoteGameInterface = rgi;
        setupFunctions();
        createCommands();

        // TODO: Read file, extract command descriptions and call createCommands(descriptions)
    }

    // Creates Sample Descriptions and calls createCommands(descriptions)
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

    //
    private void createCommands(HashMap<String, String> descriptions) {
        for (String key : descriptions.keySet()) {
            String description = descriptions.get(key);
            BiFunction<String, ArrayList<String>, String> function = commandFunctions.get(key);

            if (function != null) {
                commands.put(key, new Command(key, description, function));
            }
        }
    }

    // Runs a specific command if found in memory.
    public void run(String command, ArrayList<String> args, String playerName) throws RemoteException {
        // System.out.println(playerName + ": " + command + '(' + args + ')');

        String result = commands.get(command.toUpperCase()).run(playerName, args);
        System.out.println(result);
    }

    // Prints list of commands and descriptions
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
