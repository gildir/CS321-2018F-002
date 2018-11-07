import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.lang.StringBuilder;

import javax.annotation.processing.FilerException;

/**
 * This class will handle all player database interactions
 
 * NOTE: All methods in this class are static, do not attempt to
 *       create an instance of this class
 *
 * Authors: Cody Kidwell, Joseph Saah, Brett Mullins, and Haroon Tanveer
 */
public class PlayerDatabase {

   /*
    * File name of player database 
    */
   public static final String DATABASE_FILE = "player_database.csv";
   public static final String LOG_FILE = "login_logout_log.txt";

   /*
   *Determines if an account currently exists in the database
   */
    public static boolean hasAccount(){
        try(FileInputStream fis = new FileInputStream(DATABASE_FILE);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr)) {
         
        //reads database line by line
        String line;
		if((line = br.readLine()) != null) {
            //stores username and password from current line into an array
            return true;        
        }
        else return false;
	}
      catch(IOException e) {
      }
      return false;
    }

   /**
    * Adds a player's username and password to the database
    * if the username does not already exist in the system
    *
    * @param name Player's username
    * @param password Player's password
    * @param question1 Player's first security question
    * @param ans1 Player's answer to their first provided question
    * @param question2 Player's second security question
    * @param ans2 Player's answer to their second provided question
    * @param question3 Player's third security question
    * @param ans3 Player's answer to their third provided question
    * @return true if player is added to database, false otherwise 
    */
	public static boolean addPlayer(String name, String password, String question1, String ans1,
                                   String question2, String ans2, String question3, String ans3){
   
      //build comma-seperated inputs for database with user's name and password
      try(FileOutputStream fos = new FileOutputStream(DATABASE_FILE, true)) {
         
         //add given values to database
         StringBuilder sb = new StringBuilder(name);
         
         sb.append(","); sb.append(password);
         sb.append(","); sb.append(question1);
         sb.append(","); sb.append(ans1);
         sb.append(","); sb.append(question2);
         sb.append(","); sb.append(ans2);
         sb.append(","); sb.append(question3);
         sb.append(","); sb.append(ans3);
         sb.append("\n");
         
         //write inputs to database
         fos.write(sb.toString().getBytes());
      }
      catch(IOException e) {
         e.printStackTrace();
         return false;
      }

		return true;
	}
	
   /**
    * Checks if username already exists in the database
    *
    * @param name The username to check for in the database
    * @return true if username exists, false otherwise
    */
	public static boolean isPlayer(String name) {
      try(FileInputStream fis = new FileInputStream(DATABASE_FILE);
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader br = new BufferedReader(isr)) {
          
          //reads database line by line
          String line;
			 while((line = br.readLine()) != null) {
             //stores username and password from current line into an array
				 String[] info = line.split(",");
             
             //checks if the username on this line is equal to the given username
				 if(info[0].equals(name)) {
                    return true;
             }
			 }
		}
      catch(IOException e) {
         return false;
      }
      return false;
    }
    
    /**
     * Checks if the password for the given user matches the the password put in
     * @param name
     * @param password
     * @return true if the passwords match
     */
    public static boolean isPassword(String name, String password){
        try(FileInputStream fis = new FileInputStream(DATABASE_FILE);
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader br = new BufferedReader(isr)) {
          
          //reads database line by line
          String line;
			 while((line = br.readLine()) != null) {
             //stores username and password from current line into an array
				 String[] info = line.split(",");
             
             //checks if the username on this line is equal to the given username
			if(info[0].equals(name)) {
                if(info[1].equals(password))
                    return true;
             }
			 }
		}catch(IOException e) {
         return false;
      }
      return false;
    }

	/**
	 * Removes a player's username and password from the database if the username
	 * does already exist in the system
	 * 
	 * @param name
	 *            The username to be removed
	 * @return true if username has been removed, false if otherwise
	 */
	public static boolean removePlayer(String name) {
		// first checks if player exists
		if (!isPlayer(name)) {
			System.out.println(name + " does not exist in database.");
			return false;
		}
		// removal process here
		else {
			String line;
			StringBuilder lines = new StringBuilder();
			try (FileInputStream fis = new FileInputStream(DATABASE_FILE);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader br = new BufferedReader(isr)) {

				// reads database line by line adding lines in to a collective string of all the lines
				while ((line = br.readLine()) != null) {
					
					String[] info = line.split(",");

					// if username does not match to the one read it will rewrite it to the file
					if (!info[0].equals(name)) lines.append(line + "\n");
				
				}
			} catch (IOException e) {
				return false;
			}
			try (FileOutputStream fos = new FileOutputStream(DATABASE_FILE)){
				
				//overwrite the old file
				fos.write(lines.toString().getBytes());
				return true;
			
			} catch (FilerException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
    }
	
	/**
	 * Checks the security questions for a given player using in method keyboard inputs
	 * @param name Player's name
	 * @return
	 */
	public static boolean checkSecurityQestions(String name) {
		try(FileInputStream fis = new FileInputStream(DATABASE_FILE);
		    InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr)) {
			
			InputStreamReader keyboardReader = new InputStreamReader(System.in);
	        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
			String[] info = null;
			String line;
			boolean check = false; //check default is false assuming that the answers are wrong
			
			//find the user
			/*do {
				line = br.readLine();
				info = line.split(",");
			} while(info[0] != name);
			*/
			
			while((line = br.readLine()) != null) {
				info = line.split(",");
				if (!info[0].equals(name)) continue;
				else break;
			}
			
			//prompt for all three security question answers
			System.out.println(info[2]); String ans1 = keyboardInput.readLine();
			System.out.println(info[4]); String ans2 = keyboardInput.readLine();
			System.out.println(info[6]); String ans3 = keyboardInput.readLine();
			
			//check their answers versus the ones in the csv file
			if(ans1.equals(info[3])) {
				if(ans2.equals(info[5])) {
					if(ans3.equals(info[7])) {
						check = true;
					}
				}
			}
			
			return check;
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Changes the password for a given Player using an in method keyboard entry
	 * @param name Player's name
	 * @return
	 */
	public static boolean changePassword (String name) {
				String line;
				String newLine = "";
				StringBuilder lines = new StringBuilder();
				InputStreamReader keyboardReader = new InputStreamReader(System.in);
		        BufferedReader keyboardInput = new BufferedReader(keyboardReader);
				try (FileInputStream fis = new FileInputStream(DATABASE_FILE);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr)) {
					
					System.out.print("Please enter your new password: ");
					String newPassword = keyboardInput.readLine();
					
					// reads database line by line adding lines in to a collective string of all the lines
					while ((line = br.readLine()) != null) {
						
						String[] info = line.split(",");

						// if username does not match to the one read it will rewrite it to the file
						// else rewrite the line with the new password instead
						if (!info[0].equals(name)) lines.append(line + "\n");
						else {
							for (int i = 0; i < info.length; i++) {
								if(i != 1) newLine = newLine + info[i] + ",";
								else newLine = newLine + newPassword + ",";
							}
							lines.append(newLine + "\n");
						}
					
					}
				} catch (IOException e) {
					return false;
				}
				try (FileOutputStream fos = new FileOutputStream(DATABASE_FILE)){
					
					//overwrite the old file
					fos.write(lines.toString().getBytes());
					return true;
				
				} catch (FilerException e) {
					return false;
				} catch (IOException e) {
					return false;
				}
	}

    /**
    * Writes to a log file everytime a player logs in/out
    *
    * @param name Player name
    * @param isLoggingIn Whether the player is logging in or logging out
    * @return true if log message is written successfully, false otherwise
    */
   public static boolean loginLog(String name, boolean isLoggingIn) {
    String log = name;
    
    try(FileOutputStream fos = new FileOutputStream(LOG_FILE, true)) {
       if(isLoggingIn)
          log += " logged in.\n";
       else
          log += " logged out.\n"; 
       
       //write log message to log file
       fos.write(log.getBytes());
    }
    catch(IOException e) {
       e.printStackTrace();
       return false;
    }
    
    return true;
 }
}
