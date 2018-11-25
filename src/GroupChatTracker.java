import java.util.HashMap;
import java.util.ArrayList;

//TODO:
// 1. remove player from Invites and Tracker after quitting
//    currently only being removed after explicitly leaving and joining (accepting invite)
// 2. Ability for a user to check invites 

public class GroupChatTracker{

	//Group chats are a combination of name 
	// GroupChat object can be retrieved from HashMaps using group chat name
	// NOTE: Group chat names will always be saved in lowercase and need to be used in lowercase
	private HashMap< String, ArrayList<String>> Tracker;  //Tracker keeps track of all group chats
	private HashMap< String, ArrayList<String>> Invites;  //Invites keeps track of all group chat invites
	private ArrayList<String> groups; //tracks group chat names
		
	GroupChatTracker(){
		Tracker = new HashMap<String, ArrayList<String> >();
		Invites = new HashMap<String, ArrayList<String> >();
		groups = new ArrayList<String>();
	}

	//track a new group chats
	//Prior to calling this function, the following conditions are checked in GameCore:
	// 1.group name cannot have spaces
	public String createGroupChat( String groupChatName , String playerName ){
		//TO DO:

		String groupChatNameLC = groupChatName.toLowerCase();
		
		//check if group already exists
		if( this.checkGroupExists( groupChatName ) )
			return "Group chat [" + groupChatNameLC + "] already exists.";

		ArrayList<String> tempT = new ArrayList<String>();
		ArrayList<String> tempI = new ArrayList<String>();
		tempT.add( playerName );
		Tracker.put( groupChatNameLC, tempT );
		Invites.put( groupChatNameLC, tempI );
		groups.add(groupChatNameLC);
		return "Group chat [" + groupChatNameLC + "] created.";
	}

	//No need to check if GroupChat exists. In Game, this is a group chat command, which implies group exists.
	// Game invite command makes use of this function.
	//Prior to calling this function, the following conditions are checked in GameCore:
	// 1. valid playerName
	// 2. player already invited
	public void trackInvite( String groupChatName, String playerName){
		String groupChatNameLC = groupChatName.toLowerCase();
		Invites.get(groupChatNameLC).add( playerName );
	}

	

	//Prior to calling this function, the following conditions are checked in GameCore:
	// 1. valid playerName
	public String acceptInvite( String groupChatName, String playerName){
		String groupChatNameLC = groupChatName.toLowerCase();
		//check if invite exists
		if( !checkInvite( groupChatNameLC , playerName) )
			return "You can't join this group.";
		

		//add player to group
		Tracker.get( groupChatName.toLowerCase() ).add( playerName);
		//remove invite
		removeInvite( groupChatName, playerName);

		return "You've joined chat group [" + groupChatName.toLowerCase() + "].";
		
	}

	public boolean checkMembership( String groupChatName, String playerName){
		//check that group exists
		if( !checkGroupExists( groupChatName ) )
			return false;
		
		ArrayList<String> members = Tracker.get( groupChatName.toLowerCase() );
		for( String member : members ){
			if(member.equalsIgnoreCase( playerName ) ){
				return true;
			}

		}
		return false;
	}

	//No need to check if GroupChat exists. In Game, this is a group chat command, which implies group exists.
	//  function called when user leaves a group.
	//Prior to calling leaveGroup(), the following conditions are checked in GameCore:
	// 1. player already in group 
	public void removeMember( String groupChatName, String playerName){
		//TODO:
		
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
		
		//if the group doesn't have any members, delete the group and its invites
		if( Tracker.get( groupChatNameLC ).size() == 0 ){
			Tracker.remove( groupChatNameLC ); 
			Invites.remove( groupChatNameLC );
			groups.remove( groupChatNameLC );
		}


	}

	public boolean checkGroupExists( String groupChatName){
		String groupChatNameLC = groupChatName.toLowerCase();
		return Tracker.containsKey( groupChatNameLC );
	}

	//print group chat tracking object, used for debugging
	public String printGroup( String groupChatName ){
		String groupChatNameLC = groupChatName.toLowerCase();
		ArrayList<String> temp = Tracker.get( groupChatNameLC );
		if (temp == null)
			return "[" + groupChatName + "] does not exist."; 
		return "[" + groupChatName + "] contents: " + temp;
	}

	//checks if playerName has been invited to groupChatName
	//returns false if invite does not exist, or if group does not exit
	public boolean checkInvite( String groupChatName, String playerName){
		String groupChatNameLC = groupChatName.toLowerCase();
		//check that group exists
		if( !checkGroupExists( groupChatNameLC ) )
			return false;

		ArrayList<String> members = Invites.get( groupChatNameLC );
		for( String member : members ){
			if(member.equalsIgnoreCase( playerName ) ){
				return true;
			}

		}
		return false;
	}

	//return group members ArrayList
	public ArrayList<String> groupMembers (String groupName){
            return Tracker.get( groupName.toLowerCase() );
	}

	//this function is called when a player accepts an invite
	private void removeInvite( String groupChatNameLC, String playerName){

		//find location of playerName in Invites ArrayList
		int position = 0;
		//String groupChatNameLC = groupChatName.toLowerCase();
		ArrayList<String> members = Invites.get( groupChatNameLC );
		for( String member : members ){
			if(member.equalsIgnoreCase( playerName ) ){
				break;
			}
			position++;

		}
		//remove player invites
		Invites.get( groupChatNameLC ).remove(position);
	}
	public String getHelp()
	{
		String help = "GROUPCHAT <name>\tThis command creates a private group chat. The creator is automatically added to the group. Only the creator can invite people to the group.\n" +
				"GROUPCHATPRINT <name>\tThis was used for debugging but i left it. It prints the members of private group <name>.\n" +
				"JOIN <group name>\tOnce a player receives an invitation to join group chat <name>, they can type join the group with this command.\n";
		help +="\n\n<group name> /invite <player name>\tinvite player with playerName to the group chat. \n" +
				"<group name> /leave\tplayer leaves group\n" +
				"<group name> <message>\tplayer sends message to cs321 group.\n";
		return help;
	}

	//when a player quits, this function removes the player from group chats and invite lists
	public void playerQuit( String playerName){
		//get chat room names
		ArrayList<String> groupsTemp = new ArrayList<String>( groups );
		for( String group: groupsTemp){
			//if player is in group, remove player
			if( this.checkMembership(group, playerName) ){
				this.removeMember( group, playerName);
			}
			if( this.checkInvite(group, playerName) ){
				this.removeInvite( group, playerName );
			}
		}
		
	}

}

