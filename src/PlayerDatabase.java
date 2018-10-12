
//import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class PlayerDatabase {
	
	public static boolean addPlayer(String name, String password){
		
		if(isPlayer(name))return false;
		try{
			PrintWriter csv = new PrintWriter(new File("database.csv"));
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(",");
			sb.append(password);
			sb.append("\n");
			csv.write(sb.toString());
			csv.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		return true;
		
		
	}
	
	public static boolean isPlayer(String name){
		try{
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream("database.csv")));
			
			String line;
			
			while((line = buffer.readLine())!= null){
				String[] info = line.split(",");
				if(info[0].equals(name))return true;
			}
		}
		catch(IOException e){
			e.printStackTrace();
			
			try{
				File file = new File("database.csv");
				file.createNewFile();
			}
			catch(NullPointerException b){
				b.printStackTrace();
				System.out.println("File Cannot Be Created");
			}
			catch(IOException c){
				c.printStackTrace();
			}
		}
	

		return false;
	}
	
	
	//checks if user is a existing user, and checks if the user has the correct password
	public static boolean checkPassword(String name, String Password){
		
		if(!isPlayer(name)) return false;
		
		return true;
	}
}
