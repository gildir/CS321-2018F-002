

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin
 */
public class GameClient {
    // Control flag for running the game.
    private boolean runGame;

    // Remote object for RMI server access
    protected GameObjectInterface remoteGameInterface;
    
    // Members for running the remote receive connection (for non-managed events)
    private boolean runListener;
    protected ServerSocket remoteListener;
    private Thread remoteOutputThread;  
    
    // Members related to the player in the game.
    protected String playerName;
    
    /** 
     * Main class for running the game client.
     */
    public GameClient(String host) {
        this.runGame = true;
        boolean nameSat = false;
        
        System.out.println("Welcome to the client for an RMI based online game.\n");
        System.out.println("This game allows you to connect to a server an walk around a virtual,");
        System.out.println(" text-based version of the George Mason University campus.\n");
        System.out.println("You will be asked to create a character momentarily.");
        System.out.println("When you do, you will join the game at the George Mason Clock, in the main quad.");
        System.out.println("You will be able to see if any other players are in the same area as well as what");
        System.out.println("objects are on the ground and what direction you are facing.\n");
        System.out.println("The game allows you to use the following commands:");
        System.out.println("  LOOK          - Shows you the area around you");
        System.out.println("  SAY message   - Says 'message' to any other players in the same area.");
        System.out.println("  LEFT          - Turns your player left 90 degrees.");
        System.out.println("  RIGHT         - Turns your player right 90 degrees.");
        System.out.println("  MOVE distance - Tries to walk forward <distance> times.");
        System.out.println("  PICKUP obect  - Tries to pick up an object in the same area.");
        System.out.println("  INVENTORY     - Shows you what objects you have collected.");
        System.out.println("  GIFT          - Offer to give another player money.");
        // allow user to see how much money they have
        System.out.println("  MONEY         - Shows you how much money you have.");
        System.out.println("  QUIT          - Quits the game.");
        System.out.println();
        

        // Set up for keyboard input for local commands.
        InputStreamReader keyboardReader = new InputStreamReader(System.in);
        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
        String keyboardStatement;

        try {
            // Establish RMI connection with the server
            System.setSecurityManager(new SecurityManager());
            String strName = "rmi://"+host+"/GameService";
            // GameObject extends GameObjectInterface, GameObject has a GameCore which calls 
            // Player methods 
            remoteGameInterface = (GameObjectInterface) Naming.lookup(strName);

            // Start by remotely executing the joinGame method.  
            //   Lets the player choose a name and checks it with the server.  If the name is
            //    already taken or the user doesn't like their input, they can choose again.
            while(nameSat == false) {
                try {
                    System.out.println("Please enter a name for your player.");
                    System.out.print("> ");
                    this.playerName = keyboardInput.readLine();
                    System.out.println("Welcome, " + this.playerName + ". Are you sure you want to use this name?");
                    System.out.print("(Y/N) >");
                    if(keyboardInput.readLine().equalsIgnoreCase("Y")) {
                        // Attempt to join the server
                        if(remoteGameInterface.joinGame(this.playerName) == false) {
                            System.out.println("I'm sorry, " + this.playerName + ", but someone else is already logged in with your name. Please pick another.");
                        }
                        else {
                            nameSat = true;
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("[CRITICAL ERROR] Error at reading any input properly.  Terminating the client now.");
                    System.exit(-1);
                }
            }

            // Player has joined, now start up the remote socket.
            this.runListener = true;
            remoteOutputThread = new Thread(new GameClient.ReplyRemote(host));
            remoteOutputThread.setDaemon(true);
            remoteOutputThread.start();

            // Collect input for the game.
            while(runGame) {
                try {
                    keyboardStatement = keyboardInput.readLine();
                    parseInput(keyboardStatement);
                } catch (IOException ex) {
                    System.err.println("[CRITICAL ERROR] Error at reading any input properly.  Terminating the client now.");
                    System.exit(-1);
                }
            }                
        } catch (NotBoundException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch(RemoteException re) {
            System.err.println("[CRITICAL ERROR] There was a severe error with the RMI mechanism.");
            System.err.println("[CRITICAL ERROR] Code: " + re);
            System.exit(-1);
        }        
    }
    
    /** 
     * Simple method to parse the local input and remotely execute the RMI commands.
     * @param input 
     */
    private void parseInput(String input) {
        boolean reply;
        
        // First, tokenize the raw input.
        StringTokenizer commandTokens = new StringTokenizer(input);
        ArrayList<String> tokens = new ArrayList<>();
        while(commandTokens.hasMoreTokens() == true) {
            tokens.add(commandTokens.nextToken());
        }

        if(tokens.isEmpty()) {
            System.out.println("The keyboard input had no commands.");
            return;
        }
        
        String message = "";

        try {
            switch(tokens.remove(0).toUpperCase()) {

                case "LOOK":
                    System.out.println(remoteGameInterface.look(this.playerName));
                    break;
                case "LEFT":
                    System.out.println(remoteGameInterface.left(this.playerName));
                    break;
                case "RIGHT":
                    System.out.println(remoteGameInterface.right(this.playerName));
                    break;
                case "SAY":
                    if(tokens.isEmpty()) {
                        System.err.println("You need to say something in order to SAY.");
                    }
                    else {
                        while(tokens.isEmpty() == false) {
                            message += tokens.remove(0);
                            if(tokens.isEmpty() == false) {
                                message += " ";
                            }
                        }                        
                        System.out.println(remoteGameInterface.say(this.playerName, message));
                    }
                    break;
                case "MOVE":
                    if(tokens.isEmpty()) {
                        System.err.println("You need to provide a distance in order to move.");
                    }
                    else {
                        System.out.println(remoteGameInterface.move(this.playerName, Integer.parseInt(tokens.remove(0))));
                    }
                    break;
                case "ENTER":
                 if(tokens.isEmpty()) {
                  System.err.println("You need to provide a place to enter");
                 }
                 else {
                  System.out.println(remoteGameInterface.enter(this.playerName, tokens.remove(0)));
                 }
                 break;
                case "LEAVE":
                    System.out.println(remoteGameInterface.leaveRoom(this.playerName));
                    break;
                case "PICKUP":
                    if(tokens.isEmpty()) {
                        System.err.println("You need to provide an object to pickup.");
                    }
                    else {
                        System.out.println(remoteGameInterface.pickup(this.playerName, tokens.remove(0)));
                    }
                    break;
                case "INVENTORY":
                    System.out.println(remoteGameInterface.inventory(this.playerName));
                    break; 
                // add in the case of printing money 
                case "MONEY":
                    System.out.println(remoteGameInterface.money(this.playerName));
                    break; 
                case "GIFT":
                if(tokens.isEmpty()){
                    System.err.println("You need to provide a player name to gift.");
                }else{                 
                  System.out.println(remoteGameInterface.gift(this.playerName, tokens.remove(0)));  
                }
                break;
                
                case "QUIT":
                    remoteGameInterface.leave(this.playerName);
                    runListener = false;
                    break;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
  if(args.length < 1) {
   System.out.println("[SHUTDOWN] .. This program requires one argument. Run as java -Djava.security.policy=game.policy GameClient hostname");
   System.exit(-1);
  }
  
        System.out.println("[STARTUP] Game Client Now Starting...");
        new GameClient(args[0]);
    }

    /**
     * Inner class to handle remote message input to this program.  
     *  - Runs as a separate thread.  Interrupt it to kill it.
     *  - Spawns multiple threads, one for each remote connection.
     */
    public class ReplyRemote implements Runnable {
  private String host;
  
  public ReplyRemote(String host) {
   this.host = host;
  }
  
        @Override
        public void run() {
            // This thread is interruptable, which will allow it to clean up before
            
            // Attempt communcations with the server.
            try (Socket remoteMessageSocket = new Socket(host, 13500)) {
                
                // Get stream reader and writer. 
                //  Writer is only used once, to register this socket with a player.
                //  Otherwise, this is read only to receive non-locally generated event notifications.
                BufferedReader remoteReader = new BufferedReader(new InputStreamReader(remoteMessageSocket.getInputStream()));
                PrintWriter remoteWriter = new PrintWriter(remoteMessageSocket.getOutputStream(), true);
                
                // Register the socket with the player.
                remoteWriter.println(GameClient.this.playerName);
                remoteReader.readLine();

                // As long as this program is running, print all messages directly to output.
                String message;
                while(runListener == true) {
                    message = remoteReader.readLine();
                    if(message == null) {
                        System.err.println("The remote server has closed its connection!  Shutting down.");
                        System.exit(-1);
                    }
                    System.out.println(message);
                }                
            
                // Close the socket
                remoteMessageSocket.close();
            } catch(ConnectException ex) {
                System.err.println("[FAILURE] The connection has been refused.");
                System.err.println("          As this communication is critical, terminating the process.");
                System.exit(-1);
            } catch (IOException ex) {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }    
    
}
