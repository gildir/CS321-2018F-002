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
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Kevin
 */
public class GameClient {
    // Control flag for running the game.
    private boolean runGame;

    // Remote object for RMI server access
    protected GameObjectInterface remoteGameInterface;

    // Helper Object to run commands in the game
    protected CommandRunner commandRunner;

    // Members for running the remote receive connection (for non-managed events)
    private boolean runListener;
    protected ServerSocket remoteListener;
    private Thread remoteOutputThread;

    // Members related to the player in the game.
    protected String playerName;
    protected String playerPassword;

    //Member related to timer
    public static Timer timer;

    /**
     * Creates the initial timer
     * Only should be called once when the game is beginning
     */
    public void gameTimer(){
        TimerTask timerTask = new TimerTask(){
            public void run(){
                try{
                    remoteGameInterface.leave(playerName);
                    runListener = false;
                    System.out.println("User has been inactive for 5 minutes.. logging off");
                    timer.cancel();
                    System.exit(-1);
                }
                catch (RemoteException ex) {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 300000);
    }

    /**
     * Updates and resets the timer
     * Should be called after every input using update();
     */
    public void update(){
        TimerTask timerTask = new TimerTask(){
            public void run(){
                try{
                    remoteGameInterface.leave(playerName);
                    runListener = false;
                    System.out.println("User has been inactive for 5 minutes.. logging off");
                    timer.cancel();
                    System.exit(-1);
                }
                catch (RemoteException ex) {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        timer.cancel();
        timer.purge();
        timer = new Timer();
        timer.schedule(timerTask, 300000); //resets timer to 5 minutes
    }

    /**
     * Main class for running the game client.
     */
    public GameClient(String host) {
        this.runGame = true;

        System.out.println("Welcome to the client for an RMI based online game.\n");
        System.out.println("This game allows you to connect to a server an walk around a virtual,");
        System.out.println(" text-based version of the George Mason University campus.\n");
        System.out.println("You will be asked to create a character momentarily.");
        System.out.println("When you do, you will join the game at the George Mason Clock, in the main quad.");
        System.out.println("You will be able to see if any other players are in the same area as well as what");
        System.out.println("objects are on the ground and what direction you are facing.\n");

        gameTimer(); //creates the main timer

        // Set up for keyboard input for local commands.
        InputStreamReader keyboardReader = new InputStreamReader(System.in);
        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
        String keyboardStatement;

        try {
            // Establish RMI connection with the server
            System.setSecurityManager(new SecurityManager());
            String strName = "rmi://"+host+"/GameService";
            remoteGameInterface = (GameObjectInterface) Naming.lookup(strName);

            // Start by remotely executing the joinGame method.
            //   Lets the player choose a name and checks it with the server.  If the name is
            //    already taken or the user doesn't like their input, they can choose again.

            boolean acctConf;
            try{
                do{ //do-while block ensure correct input is entered to direct user through login
                    acctConf = true;
                    System.out.println("Enter 1 to login with an existing account");
                    System.out.println("Enter 2 to create an account");
                    System.out.print("> ");
                    String acct = keyboardInput.readLine(); update();

                    if(acct.equals("1")){
                        if(PlayerDatabase.hasAccount()) login();
                        else{
                            System.out.println("No account currently exists, must create one first");
                            acctConf = false;
                        }
                    }
                    else if(acct.equals("2")) createAccount();
                    else{
                        System.out.println("Please enter a correct input\n");
                        acctConf = false;
                    }
                }while(acctConf == false);
            }catch (IOException ex) {
                System.err.println("[CRITICAL ERROR] Error at reading any input properly.  Terminating the client now.");
                System.exit(-1);
            }

            // Player has joined, now start up the remote socket.
            this.runListener = true;
            remoteOutputThread = new Thread(new GameClient.ReplyRemote(host));
            remoteOutputThread.setDaemon(true);
            remoteOutputThread.start();

            // Init the CommandRunner
            commandRunner = new CommandRunner(remoteGameInterface, "commands.csv");
            // commandRunner.run("help", null, this.playerName);
            System.out.println(commandRunner.listCommands());

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
     * If the user does not have an account, this method will help the user create a username and password
     * that will be stored in the database
     */
    private void createAccount(){
        String password = "", question1, ans1, question2, ans2, question3, ans3;
        
        InputStreamReader keyboardReader = new InputStreamReader(System.in);
        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
        boolean nameSat = false; boolean nameConf = false;
        try{
            do{//do-while block will re-prompt user to enter a username if needed
                do{//do-while block ensure username entered is unique
                    System.out.println("Please enter a username");
                    System.out.print("> ");
                    this.playerName = keyboardInput.readLine(); update();
                    if(!PlayerDatabase.isUname(playerName)){
                        nameSat = false; continue;
                    }
                    if(PlayerDatabase.isPlayer(playerName))
                    {
                        System.out.println("Username already exits... Please enter a new username\n");
                        nameSat = false;
                    }
                    else nameSat = true;
                }while(!nameSat);
                nameConf = false;
                while(!nameConf){ //while loop will repeat if user does not enter a proper entry to confirm name
                    update();
                    System.out.println("Welcome, " + this.playerName + ". Are you sure you want to use this username?");
                    System.out.print("(Y/N) > ");
                    String entry = keyboardInput.readLine(); update();
                    if(entry.equalsIgnoreCase("Y")) {
                        // Attempt to join the server
                        if(remoteGameInterface.joinGame(this.playerName) == false) {
                            System.out.println("I'm sorry, " + this.playerName + ", but someone else is already logged in with your name. Please pick another.");
                            nameSat = false; nameConf = true;
                        }
                        else{
                            nameConf = true; nameSat = true;
                        }
                    }
                    else if (entry.equalsIgnoreCase("N")){
                        nameConf = true; nameSat = false;
                    }
                    else{
                        nameConf = false; //Will reprompt confirmation
                    }
                }
            }while(!nameSat); //will repeat until broken out

            //User creates a password that can be used to log in
            boolean isPassword = false;
            while(!isPassword){
                System.out.println("Please enter a password.");
                System.out.print("> ");
                password = keyboardInput.readLine(); update();
                if (password.length() == 0){
                    System.out.println("Password must contain at least one character");
                    isPassword = false;
                }
                else{
                    isPassword = true;
                    //PlayerDatabase.addPlayer(this.playerName, password);
                }
            }
            
            //User enters and answers recovery questions
            System.out.println("Setting up 3 recovery questions for your account");
            System.out.println("Note: Answers are Case Sensitive");
            System.out.println("Please enter the first recovery question.");
            System.out.print("> ");
            question1 = keyboardInput.readLine(); update();
            System.out.println("Now enter an answer to that question.");
            System.out.print("> ");
            ans1 = keyboardInput.readLine(); update();
            
            System.out.println("Please enter the second recovery question.");
            System.out.print("> ");
            question2 = keyboardInput.readLine(); update();
            System.out.println("Please answer your question.");
            System.out.print("> ");
            ans2 = keyboardInput.readLine(); update();
            
            System.out.println("Please enter one last recovery question.");
            System.out.print("> ");
            question3 = keyboardInput.readLine(); update();
            System.out.println("Please answer your question.");
            System.out.print("> ");
            ans3 = keyboardInput.readLine(); update();
            
            //Write player information to the player database
            PlayerDatabase.addPlayer(this.playerName, password, question1, ans1, question2, ans2, question3, ans3);
        }catch (IOException ex) {
            System.err.println("[CRITICAL ERROR] Error at reading any input properly.  Terminating the client now.");
            System.exit(-1);
        }
    }

    /**
     * Method helps the user login with their username and password
     */
    private void login(){
    	int passwordsEnteredCount = 0;
        InputStreamReader keyboardReader = new InputStreamReader(System.in);
        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
        try{
            boolean newuser = false;
            do{
                do{//loop repeats if an active username is entered
                    System.out.println("Please enter your username");
                    System.out.print("> ");
                    this.playerName = keyboardInput.readLine(); update();
                    if(PlayerDatabase.isPlayer(playerName)) break;
                    else System.out.println("Username is incorrect... Please enter a new username");
                }while(true); //exits the loop only through a break

                boolean conf = false; newuser = false; boolean isPassword = false;
                while(!conf){ //While loop verifies user password
                    isPassword = false;
                    while(!isPassword){
                        System.out.println("Please enter your password");
                        System.out.print("> ");
                        this.playerPassword = keyboardInput.readLine(); update();
                        if (playerPassword.length() == 0){
                            System.out.println("Password must contain at least one character");
                            isPassword = false;
                        }
                        else isPassword = true;
                    }
                    if(PlayerDatabase.isPassword(playerName, playerPassword) == true){

                        if(remoteGameInterface.joinGame(this.playerName) == false){
                            System.out.println("User is already online...login with different account");
                            newuser = true;
                        }
                        else System.out.println("Login Successful");
                        conf = true;
                    }
                    else {
                    	System.out.println("Password does not match");
                    	passwordsEnteredCount++;
                    	if (passwordsEnteredCount == 3) {
                            System.out.println("Please use your security questions to reset password");
                            System.out.println("NOTE: Answers are Case-Sensitive");
                    		if(PlayerDatabase.checkSecurityQestions(playerName)) {
                    			update();
                    			if(PlayerDatabase.changePassword(playerName)) {
                    				update();
                    				System.out.println("Password has been updated.");
                                    passwordsEnteredCount = 0;
                    			}
                    			else System.out.println("Password update failed.");
                    		}
                    		else System.out.println("Security question(s) answered incorectly.");
                    	}
                    }
                }
            }while(newuser == true);
        }catch (IOException ex) {
            System.err.println("[CRITICAL ERROR] Error at reading any input properly.  Terminating the client now.");
            System.exit(-1);
        }
    }

    /**
     * Method called when player is exiting that prompts if the user wants to delete
     * his or her character then proceeds to remove the user name and password if prompted to
     */
    private void deleteCharacter() {
        InputStreamReader keyboardReader = new InputStreamReader(System.in);
        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
        String keyboardStatement = "";
        boolean removeApproval = false;

        try {
            do {
                if (keyboardStatement.equalsIgnoreCase("Y")) {
                    System.out.print("Enter password: ");
                    keyboardStatement = keyboardInput.readLine();
                    update();
                    if(PlayerDatabase.isPassword(playerName, keyboardStatement)){
                        removeApproval = true;
                        break;
                    }
                    else System.out.println("Password incorrect.");
                } else if (keyboardStatement.equalsIgnoreCase("N")) {
                    break;
                }
                System.out.print("Would you like to permanently delete your player and account? (Y/N)");
                keyboardStatement = keyboardInput.readLine();
                update();
            } while (true);
        }  catch (IOException ex) {
            System.err.println("[CRITICAL ERROR] Error at reading any input properly.  Terminating the client now.");
            System.exit(-1);
        }
        if(removeApproval) {
            if(PlayerDatabase.removePlayer(playerName))
                System.out.println(playerName + " has been removed.");
            else System.out.println(playerName + " could not be removed.");
        }
        try{
            remoteGameInterface.leave(playerName);
            runListener = false;
            System.exit(-1);
        }catch(RemoteException re){
            System.exit(-1);
        }
    }

    /**
     * Simple method to parse the local input and remotely execute the RMI commands.
     * @param input
     */
    private void parseInput(String input) {
        boolean reply;
        update();
        // First, tokenize the raw input.
        StringTokenizer commandTokens = new StringTokenizer(input);
        ArrayList<String> tokens = new ArrayList<>();
        while(commandTokens.hasMoreTokens()) {
            tokens.add(commandTokens.nextToken());
        }

        if(tokens.isEmpty()) {
            System.out.println("The keyboard input had no commands.");
            return;
        }

        String command = tokens.remove(0);
        if(command.equalsIgnoreCase("Quit")){
            deleteCharacter();
        }
        update();
        commandRunner.run(command, tokens, this.playerName);
        update();
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
