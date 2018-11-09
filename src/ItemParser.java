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
		   csvInputStream.useDelimiter(",|\\n");
		   while (csvInputStream.hasNext())
		   {
			String name = csvInputStream.next();
			String weightString = csvInputStream.next();
			double weight = Double.parseDouble(weightString);
			String priceString = csvInputStream.next();
			double price = Double.parseDouble(priceString);
			String descrip = csvInputStream.next();
			Item tempItem = new Item(name, descrip, weight, price);
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
