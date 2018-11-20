import java.util.*;
import java.io.*;
/**
 * This class parses a csv file and creates an array of item objects. 
 *
 */
public class ItemParser
{
	/**
	 * This method converts a csv file and returns an ArrayList of items. 
	 * @param fileName contains the path to a csv file
	 * @return ArrayList<Item> an array list of the items in the csv file
	 *
	 */
	public static ArrayList<Item> parse(String fileName)
	{
		File itemListFile = new File(fileName);
		ArrayList<Item> itemList = new ArrayList<>();
		try
		{
		   Scanner csvInputStream = new Scanner(itemListFile);
			csvInputStream.useDelimiter("\n");
		   while (csvInputStream.hasNext())
		   {
			String item = csvInputStream.next();
			String[] tokens = item.split(",");
			String name = tokens[0];
			String weightString = tokens[1];
			double weight = Double.parseDouble(weightString);
			String priceString = tokens[2];
			double price = Double.parseDouble(priceString);
			String title = tokens[3];
	
			String description = tokens[4];
			for (int i = 5; i < tokens.length; i++)
			{
				description += "," + tokens[i];
			}
		
			Item tempItem = new Item(name, description, weight, price, title);
			itemList.add(tempItem);
		   }
		}
		catch(FileNotFoundException e)
		{
		   e.printStackTrace();
		}
		finally
		{
		   return itemList;
		}
	}	
}
