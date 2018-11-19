import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Kevin
 */

public class Room {
    private final int NONEXISTANT_EXIT_ID = 0;

    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private final LinkedList<Item> objects;
    private final LinkedList<Spirit> spirits;
    private final LinkedList<Exit> exits;
    private final GameCore gameCore;
    private WhiteBoard whiteboard;

    public Room(GameCore gameCore, int id, String title, String description, String location) {
        this.objects = new LinkedList<>();
        this.exits = new LinkedList<>();
        this.spirits = new LinkedList<>();
        this.id = id;
        this.title = title;
        this.description = description;
     this.location = location;
        this.gameCore = gameCore;

        if (this.location.equalsIgnoreCase("inside")) {
            this.whiteboard = new WhiteBoard(this.id);
        }
    }

    /**
     * Output a message to all players in this room.
     * @param message to send
     */
    public void broadcast(String message) {
        for (Player player : gameCore.getPlayerList()) {
            if (player.getCurrentRoom() == id)
                player.broadcast(message);
        }
    }
    
    public String toString(PlayerList playerList, Player player) {
        String result = ".-------------------------\n";
        result += "| " + this.getTitle() + "\n";
        result += "-------------------------\n";
        result += this.getDescription() + "\n";
        result += "...................\n";
        result += "It is currently " + (gameCore.getTimeOfDay() == GameCore.DAY ? "daytime.\n" : "nighttime.\n");
        result += "...................\n";
        result += "This room is " + this.getLocation() + "\n";
        result += "Objects in the area: " + this.getObjects() + "\n";
        result += "Players in the area: " + this.getPlayers(playerList) + "\n";
//        result += "Ghouls in the area: " + this.getGhoulsString() + "\n";
//        result += "Spirits in the area: " + this.getSpiritsString() + "\n";
        result += "Monsters in the area: " + this.getNPCsString() + " " + this.getSpiritsString() + "\n";
        result += "You see paths in these directions: " + this.getExits() + "\n";
        result += "...................\n";
        result += "You are facing: " + player.getCurrentDirection() + "\n";
        if(player.getCurrentRoom() == 1){
            result += "You are near the shop, type ENTER SHOP to enter.\n";
        }
        if(player.getCurrentRoom() == 10){
            result += "Type LEAVE SHOP to leave.\n";
        }
        return result;
    }
    
    public int getId() {
        return this.id;
    }

    public Room getRoom(int id){
      return this;
    }

    public WhiteBoard getWhiteBoard(){
        return this.whiteboard;
      }

    public String getExits() {
        String result = "";
        for(Exit exit : this.exits) {
            if(exit.getRoom() != NONEXISTANT_EXIT_ID) {
                result += exit.getDirection().name() + " ";
            }
        }
        return result;
    }
    
    public void addExit(Direction direction, int room, String message) {
        exits.add(new Exit(direction, room, message));
    }
    
    public boolean canExit(Direction direction) {
        for(Exit exit : this.exits) {
            if(exit.getDirection() == direction) {
                return exit.getRoom() != NONEXISTANT_EXIT_ID;
            }
        }
        return false;
    }
    
    public String exitMessage(Direction direction) {
        for(Exit exit : this.exits) {
            if(exit.getDirection() == direction) {
                return exit.getMessage();
            }
        }
        return null;
    }
    
    public int getLink(Direction direction) {
        for(Exit exit : this.exits) {
            if(exit.getDirection() == direction) {
                return exit.getRoom();
            }
        }
        return NONEXISTANT_EXIT_ID;
    }

    /**
     * Gets a random valid exit to this room, ignoring a specified room id if possible.
     * Pass Room.NONEXISTANT_EXIT_ID for the ignoreRoomId parameter to not ignore any rooms.
     * @param ignoreRoomId A room id to not get the exit for if possible.
     * @return a random exit to this room.
     */
    public Exit getRandomValidExit(int ignoreRoomId) {
        List<Exit> validExits = new LinkedList<>(exits);
        validExits.removeIf(exit -> exit.getRoom() == NONEXISTANT_EXIT_ID);
        if (validExits.size() == 0) {
            System.err.println("Room " + getId() + ": " + getTitle() + " does not have any valid exits." );
            return null;
        }
        if (validExits.size() > 1)
            validExits.removeIf(exit -> exit.getRoom() == ignoreRoomId);
        int randomIndex = new Random().nextInt(validExits.size());
        return validExits.get(randomIndex);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getTitle() {
        return this.title;
    }

    public String getLocation() {
 return this.location;
    }
    
    public String getObjects() {
        if(this.objects.isEmpty()) {
            return "None.";
        }
        else {
            return this.objects.toString();
        }
    }
    
    public void addObject(Item obj) {
        if(this.objects.size() < 5) {
            this.objects.add(obj);
        }
    }

    public void addObjectFromPlayer(Item obj) {
 this.objects.add(obj);
    }
    
    public Item removeObject(String target) {
        for(Item obj : this.objects) {
            if(obj.getItemName().equalsIgnoreCase(target)) {
                this.objects.remove(obj);
                return obj;
            }
        }
        return null;
    }
    
    public Item getLastObject() {
        if(this.objects.isEmpty()) 
            return null;
        else
            return this.objects.removeLast();    
    }
    
    public String getPlayers(PlayerList players) {
        String localPlayers = "";
        for(Player player : players) {
          System.err.println("Checking to see if " + player.getName() + " in room " + player.getCurrentRoom() + " is in this room (" + this.id + ")");
            if(player.getCurrentRoom() == this.id) {
                localPlayers += player.getName() + " ";
            }
        }
        if(localPlayers.equals("")) {
            return "None.";
        }
        else {
            return localPlayers;
        }
    }
    
    public Player getRandomPlayer(){
        List<Player> playersInRoom = new ArrayList<>();
        for (Player player : gameCore.getPlayerList()) {
            if (player.getCurrentRoom() == this.getId())
                playersInRoom.add(player);
        }

        if (playersInRoom.size() == 0)
            return null;

        Random r = new Random();
        int randomPlayerIndex = r.nextInt(playersInRoom.size());
        return playersInRoom.get(randomPlayerIndex);
    }

    /**
     * @return a set of all the NPCs in this room.
     * If there are no NPCs in this room, returns an empty set of NPCs.
     */
    public Set<NPC> getNPCs() {
        Set<NPC> npcs = new HashSet<>();
        for (NPC npc : gameCore.getNpcList()) {
            if (npc.getCurrentRoomId() == id) {
                npcs.add(npc);
            }
        }
        return npcs;
    }

    /**
     * @return a set of all the Ghouls in this room.
     * If there are no ghouls in this room, returns an empty set of Ghouls.
     */
    public Set<Ghoul> getGhouls() {
        Set<Ghoul> ghouls = new HashSet<>();
        for (NPC npc : this.getNPCs()) {
            if (npc instanceof Ghoul) {
                ghouls.add((Ghoul) npc);
            }
        }
        return ghouls;
    }

    @Deprecated // no longer in used in look command
    public String getGhoulsString() {
        Set<Ghoul> ghouls = getGhouls();
        if (ghouls.isEmpty())
            return "None";
        else {
            List<String> ghoulNames = ghouls.stream().map(Ghoul::toString).collect(Collectors.toList());
            return String.join(" ", ghoulNames);
        }
    }

    public String getNPCsString() {
        Set<NPC> npcs = getNPCs();
        if (npcs.isEmpty() && this.spirits.isEmpty())
            return "None";
        else {
            List<String> npcNames = npcs.stream().map(NPC::toString).collect(Collectors.toList());
            return String.join(", ", npcNames);
        }
    }

    /**
     * @return a set of all the spirits in this room.
     * If there are no spirits in this room, returns an empty set of spirits.
     */
    public Set<Spirit> getSpirits() {
        Set<Spirit> spirits = new HashSet<>();
        for (Spirit spirit : this.spirits) {
            spirits.add(spirit);
        }
        return spirits;
    }

    @Deprecated // no longer in used in look command
    public String getSpiritsString() {
        Set<Spirit> spirits = getSpirits();
        String spiritsString;
        if (spirits.isEmpty())
            spiritsString = "";
        else {
            List<String> spiritNames = spirits.stream().map(Spirit::toString).collect(Collectors.toList());
            spiritsString = String.join(", ", spiritNames);
        }
        return spiritsString;
    }
    
    public void addSpirit(Spirit spirit) {
        if(this.spirits.size() < 5) {
            this.spirits.add(spirit);
        }
    }
    
    public Spirit removeSpirit(String target) {
        for(Spirit spirit : this.spirits) {
            if(spirit.getName().equalsIgnoreCase(target)) {
                this.spirits.remove(spirit);
                return spirit;
            }
        }
        return null;
    }

    public WhiteBoard getWB(){
     return this.whiteboard;
    }
}
