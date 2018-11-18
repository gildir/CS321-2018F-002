import java.util.*;

public class questNPC{

	ArrayList<Quest> availableQuest = new ArrayList<Quest>();

	String location;

	public questNPC(String location){
		this.location = location;
	}

	public boolean addQuest(Quest newQuest){
		if(newQuest == null)
			return false;
		availableQuest.add(newQuest);
		return true;
	}

	public String printQuests(){
		String result = "";
		result += "Here is a list of quests for you to take:\n";
		for(int i = 0; i < availableQuest.size(); i++){
			result += i+1 + ": " + availableQuest.get(i).QUEST_NAME + "\n";
		}
		if(availableQuest.size() == 0)
			result += "No quests are currently available.\n";
		return result;
	}

	public Quest getQuest(int questNumber){
		Quest quest = availableQuest.get(questNumber-1);
		availableQuest.remove(questNumber-1);
		return quest;
	}

	public void clearQuests(int numQuests){
		for(int i = 0; i < numQuests; i++)
			availableQuest.remove(0);
	}
	
	public String getLocation(){
		return location;
	}

	public int getNumQuests(){
		return availableQuest.size();
	}
}
