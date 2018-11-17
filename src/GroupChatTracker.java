import java.util.HashMap;
import java.util.ArrayList;

public class GroupChatTracker{

	//Tracker keeps track of all group chats
	//Group chats are a combination of name 
	// GroupChat object can be retrieves from HashMap using group chat name
	// NOTE: Group chat names will always be saved in lowercase and need to be used in lowercase
	private HashMap< String, ArrayList<String> > Tracker;

	GroupChatTracker(){
		System.out.println("This is a test from inside GroupChatTracker");
		Tracker = new HashMap<String, ArrayList<String> >();
	}

	//track a new group chats
	//return true if group created, false if not created
	public String createGroupChat( String groupChatName , String playerName ){
		//TO DO:
		// 1. return false if room with that name already exists. Ignore case.
		
		String groupChatNameLC = groupChatName.toLowerCase();
		ArrayList<String> temp = new ArrayList<String>();
		temp.add( playerName );
		Tracker.put( groupChatNameLC, temp );
		return "Group chat " + groupChatNameLC + " created";
	}
	
	//print group chat tracking object, used for debugging
	public String printGroup( String groupChatName ){
		String groupChatNameLC = groupChatName.toLowerCase();
		return groupChatName + " contents: " + Tracker.get(groupChatName);
	}

	/*****************************************/
	/*private class GroupChat{
		String groupName;
		ArrayList<String> groupMembers;
		
		//Constructor initializes class variables
		// do not add to groupMembers here, leave it to parent class to call addMember()
		GroupChat( String groupName){
			this.groupName = groupName;
			groupMembers = new ArrayList<String>();
		}

		//add group member
		//returns true on successfull add, false on failure
		boolean addMember(String playerName){
			//TO DO:
			//1. only add player if not in group. Ignore name case.
			groupMembers.add(playerName);
			return true;
		}


	}*/


}

