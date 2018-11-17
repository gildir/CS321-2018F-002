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

	//function does not report back to the game
	//not adding logic to check for groups existence, only a memmber of the group can invite another player
	//Prior to calling this function, the following conditions are checked in GameCore:
	// 1. valid playerName
	// 2. player already in group 
	public void addMember( String groupChatName, String playerName){
		Tracker.get( groupChatName.toLowerCase() ).add( playerName);
		
	}

	public boolean checkMembership( String groupChatName, String playerName){
		ArrayList<String> members = Tracker.get( groupChatName.toLowerCase() );
		for( String member : members ){
			if(member.equalsIgnoreCase( playerName ) ){
				return true;
			}

		}
		return false;
	}
	
	
	//Prior to calling leaveGroup(), the following conditions are checked in GameCore:
	// 1. player already in group 
	public void removeMember( String groupChatName, String playerName){
		//find location of playerName in groupChat ArrayList
		int position = 0;
		String groupChatNameLC = groupChatName.toLowerCase();
		ArrayList<String> members = Tracker.get( groupChatNameLC );
		for( String member : members ){
			if(member.equalsIgnoreCase( playerName ) ){
				break;
			}
			position++;

		}
		//remove player from group
		Tracker.get( groupChatNameLC ).remove(position);
		//if the group doesn't have any members, delete the group
		if( Tracker.get( groupChatNameLC ).size() == 0 )
			Tracker.remove( groupChatNameLC); 

	}

	public boolean checkGroupExists( String groupChatName){
		String groupChatNameLC = groupChatName.toLowerCase();
		return Tracker.containsKey( groupChatNameLC );
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

