/**
 *
 * @author Kevin
 */
public enum Direction {
    NORTH("North"), SOUTH("South"), EAST("East"), WEST("West");
    
    private String directionName;
    
    Direction(String name) {
        directionName = name;
    }
    
    public static Direction toValue(String string) {
        if(string != null) {
            for(Direction direction : Direction.values()) {
                if(string.equalsIgnoreCase(direction.directionName)) {
                    return direction;
                }
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return directionName;
    }
}
