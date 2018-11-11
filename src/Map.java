
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
*
* @author Kevin
*/
public class Map
{
	public static final int SPAWN_ROOM_ID = 1;

	private final GameCore gameCore;
	private final LinkedList<Room> map;

	//Constructor now takes a filename as an argument
	public Map(GameCore gameCore, String filename)
	{
		
		map = new LinkedList<>();
		this.gameCore = gameCore;
		try
		{
			
			// open a new scanner with the specified file as the input
			File mapFile = new File(filename);
			Scanner csvFileScanner = new Scanner(mapFile);
			

			while (csvFileScanner.hasNextLine())
			{
				
				Room newRoom;
				String title, description, message, location;
				int id, link;
				
				csvFileScanner.useDelimiter("\\s*,\\s*");	// use comma in csv file as delimiter (and consume whitespace on either side)
				id = csvFileScanner.nextInt();				// get the id of this room
				location = csvFileScanner.next();
				title = csvFileScanner.next();				// get the title of this room
				csvFileScanner.useDelimiter("\n");			// don't use comma as delimiter, we want the remainder of the line for the description (which may include commas)
				csvFileScanner.skip(", ");					// skip the characters ", " at the beginning of the next token
				description = csvFileScanner.next();		// get the description of this room
				newRoom = new Room(gameCore, id, title, description, location);
				
				/*
				ADD THE EXITS TO THIS ROOM
				(we always add exits in this direction: NORTH, EAST, WEST, SOUTH)
				0 = North
				1 = East
				2 = West
				3 = South
				*/
				for ( int i = 0 ; i < 4 ; i++ )
				{
					
					csvFileScanner.nextLine();
					csvFileScanner.useDelimiter("\\s*,\\s*");	// use comma in csv file as delimiter (and consume whitespace on either side)
					link = csvFileScanner.nextInt();			// get the room this room is linked to
					csvFileScanner.useDelimiter("\n");			// don't use comma as delimiter, we want the remainder of the line for the message (which may include commas)
					csvFileScanner.skip(", ");					// skip the characters ", " at the beginning of the next token
					message = csvFileScanner.next();			// get the "moving to next room" message for the connected room
					
					// add this exit to the room
					switch (i)
					{
					case 0:
						newRoom.addExit(Direction.NORTH, link, message);
						break;
					case 1:
						newRoom.addExit(Direction.EAST, link, message);
						break;
					case 2:
						newRoom.addExit(Direction.WEST, link, message);
						break;
					case 3:
						newRoom.addExit(Direction.SOUTH, link, message);
						break;
					default:
						System.out.println(String.format("Something broke...i = %d", i));
						break;
					}
					
				}
				
				// add the room to the map
				map.add(newRoom);
				
				// if there's another room, advance the scanner to the first line of the next room
				if (csvFileScanner.hasNextLine())
				{
					try{
						csvFileScanner.nextLine();
						// (there is an additional newline for human readability in the specified input file)
						csvFileScanner.nextLine();
					}catch(Exception e){}
				}
				
			}
			// close the scanner
			csvFileScanner.close();
			
		}
		
		catch (FileNotFoundException e)
		{
			System.out.println("File not found.");
		}
            }
    
    public Room findRoom(int roomId) {
        for(Room room : this.map) {
            if(room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }
    
    public Room randomRoom() {
        Random rand = new Random();
        return map.get(rand.nextInt(map.size()));
    }

}
