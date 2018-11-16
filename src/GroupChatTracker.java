import java.util.HashMap;
import java.util.ArrayList;

public class GroupChatTracker{
	private HashMap<String, GroupChat> Tracker;

	GroupChatTracker(){
		System.out.println("This is a test from inside GroupChatTracker");
		Tracker = new HashMap<String, GroupChat>();
	}

	private class GroupChat{
		String groupName;
		ArrayList<String> groupMembers;

		GroupChat( String playerName, String groupName){
			this.groupName = groupName;
			groupMembers.add(playerName);
		}

	}


}

