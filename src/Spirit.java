public class Spirit extends NPC {
    
    Spirits spiritType;

    Spirit(GameCore gameCore, String name, int currentRoom, long aiPeriodSeconds, Spirits spiritType) {
        super(gameCore, name, currentRoom, aiPeriodSeconds);
        this.spiritType = spiritType;
    }
}