import java.util.*;
import java.io.*;

public class ItemParser
{
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
			Item tempItem = new Item(name, weight, price);
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
