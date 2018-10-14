/**
 *
 * @author Kevin
 */
public class Exit {
    private final Direction direction;
    private final int room;
    private final String message;
    
    public Exit(Direction direction, int room, String message) {
        this.direction = direction;
        this.room = room;
        this.message = message;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public int getRoom() {
        return this.room;
    }
    
    public String getMessage() {
        return this.message;
    }
}
