
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Kevin
 */
public class Map {   
    private final LinkedList<Room> map;
    
    public Map() {
        map = new LinkedList<>();
        try {
            File mapFile = new File("./rooms.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(mapFile);
            
            document.getDocumentElement().normalize();
            NodeList xmlRooms = document.getElementsByTagName("room");
            NodeList xmlExits;
            
            String title, description;
            String message;
            int id, link;
            Element roomElement;
            Element exitElement;
            Direction exitId;
            
            Room newRoom;
            Exit newExit;
            
            for(int i = 0; i < xmlRooms.getLength(); i++) {
                roomElement = (Element) xmlRooms.item(i);
                
                id = Integer.parseInt(roomElement.getAttribute("id"));
                title = roomElement.getElementsByTagName("title").item(0).getTextContent();
                description = roomElement.getElementsByTagName("description").item(0).getTextContent();
                
//                System.out.println("Adding Room " + id + " with Title " + title + ": " + description);
                newRoom = new Room(id, title, description);
                
                xmlExits = roomElement.getElementsByTagName("exit");
                for(int j = 0; j < xmlExits.getLength(); j++) {
                    exitElement = (Element) xmlExits.item(j);
                    
                    exitId = Direction.valueOf(exitElement.getAttribute("id"));
                    link = Integer.parseInt(exitElement.getElementsByTagName("link").item(0).getTextContent());
                    message = exitElement.getElementsByTagName("message").item(0).getTextContent();
//                    System.out.println("... Adding Exit " + exitId + " to " + link + ": " + message);
                    newRoom.addExit(exitId, link, message);
                }                
                
                map.add(newRoom);
            }
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
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
