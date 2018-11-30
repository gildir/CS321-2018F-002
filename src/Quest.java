import java.io.File;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;

/**
 * A Quest in which the Player must complete Objectives to complete the Quest
 * 
 * The Quest class keeps track of what the CURRENT active Objectives are.
*/
public class Quest
{
	
	public final Player OWNER;
	/**
	 * A counter that is incremented each time a Quest or Objective is instantiated (used for generating Quest or Objective unique ID numbers)
	*/
	private static int idGenerator = 0;
	/**
	 * The unique ID number of this Quest
	*/
	public final int QUEST_ID;
	/**
	 * The name of this Quest
	*/
	public final String QUEST_NAME;
	/**
	 * The description of this quest
	*/
	public final String QUEST_DESCRIPTION;
	/**
	 * The array storing the active Objectives (there should never be more than 5 active objectives)
	*/
	public final Objective activeObjectives[] = new Objective[5];
	/**
	 * The boolean status of whether this Quest has been completed (false until updated)
	*/
	private boolean questComplete = false;
	
	private final double REWARD_AMOUNT;
	
	/**
	 * Constructor for a Quest
	 * 
	 * Creates a new Quest object given an input file. 
	 * @param questFile the file containing the quest information
	*/
	public Quest(Player p, File questFile) throws FileNotFoundException
	{
		this.OWNER = p;
		Scanner questScanner = new Scanner(questFile);
		// set the delimiter to single newline characters with any amount of whitespace on either side
		questScanner.useDelimiter("\\s*\n\\s*");
		if (questScanner.hasNext())
		{
			try
			{
				// generate an ID for the quest
				this.QUEST_ID = generateID();
				// read in the Quest name
				this.QUEST_NAME = questScanner.next();
				// read in the Quest description
				this.QUEST_DESCRIPTION = questScanner.next();
				// read in the Quest reward amount
				this.REWARD_AMOUNT = questScanner.nextDouble();
				// throw an exception if there are no Quest Objectives specified in the input file
				if ( !(questScanner.hasNext()) )
				{
					throw new NoSuchElementException("No Such Element Exception: no Quest Objectives specified in input file");
				}
				// the array we are storing new Objectives from the input file
				Objective storeNewObjectivesHere[] = this.activeObjectives;
				int objectivesAdded = 0;
				// while there are more Objectives specified in the Quest file
				while (questScanner.hasNext())
				{
					String objType = questScanner.next();
					// if we see the special string signifying we're moving to the next "tier" of objectives
					if (objType.equals("NEXT OBJECTIVES"))
					{
						objectivesAdded = 0;
						objType = questScanner.next();
						storeNewObjectivesHere = storeNewObjectivesHere[0].NEXT_OBJECTIVES;
					}
					Objective scannedObjective = null;
					// read in the new Objective from the Scanner
					// Add new cases based on new Objective types that get created
					switch (objType)
					{
						case "A to B":
							scannedObjective = new AToBObjective(this, questScanner);
							scannedObjective.printObjective();
							break;
						case "purchase":
							scannedObjective = new purchaseObjective(this, questScanner);
							scannedObjective.printObjective();
							break;
					}
					storeNewObjectivesHere[objectivesAdded] = scannedObjective;
					objectivesAdded++;
				}
			}
			catch (InputMismatchException im)
			{
				throw new InputMismatchException("Input Mismatch Exception: input file formatted incorrectly");
			}
			catch (NoSuchElementException nse)
			{
				throw new NoSuchElementException("No Such Element Exception: input file formatted incorrectly");
			}
		}
		else
		{
			throw new NoSuchElementException("No Such Element Exception: input file empty");
		}
		questScanner.close();
	}
	
	/**
	 * Returns the status of whether this Quest has been completed.
	*/
	public boolean getQuestComplete()
	{
		return this.questComplete;
	}
	
	// checks if all objectives have been completed, and if so, marks the quest as complete.
	/**
	 * Updates active Quest Objectives and the status of whether a Quest has been completed
	 * 
	 * Checks if all active Objectives have been completed, and if so, updates the active
	 * Objectives to the next Objectives. If there are no further Objectives, the Quest
	 * is marked as completed.
	*/
	public void updateQuest()
	{
		if (this.questComplete)
		{
			return;
		}
		// else if we have more active Objectives, the Quest is currently incomplete
		else if (activeObjectives[0] != null)
		{
			// start by assuming we are ready to advance to the next Quest Objectives
			boolean readyToAdvance = true;
			for ( int i = 0 ; i < 5 ; i++ )
			{
				// if this Objective isn't null, check if it has been completed
				if (activeObjectives[i] != null)
				{
					// update this Objective
					activeObjectives[i].updateObjectiveCompletion();
					// if this (or any) Objective hasn't been completed,
					// we're not ready to advance to the next Objective(s)
					readyToAdvance = activeObjectives[i].getObjectiveComplete();
				}
				// if we're not ready to advance, we can stop checking the rest of the Objectives
				if ( !(readyToAdvance) )
				{
					break;
				}
			}
			// if we've verified all active Objectives have been completed,
			// we are ready to advance to the next Objective(s)
			if (readyToAdvance)
			{
				// set the active Objectives to the next Objectives
				// IMPORTANT: must iterate backwards through array,
				// must be able to access activeObjectives[0] all 5 times this is called
				for ( int i = 4 ; i >= 0 ; i-- )
				{
					activeObjectives[i] = activeObjectives[0].getNextObjective(i);
				}
			}
			if (activeObjectives[0] == null)
			{
				this.questComplete = true;
<<<<<<< HEAD
				OWNER.getReplyWriter().println(String.format("\nQuest completed: %s", this.QUEST_NAME));
				giveQuestReward();
=======
        
				OWNER.getReplyWriter().println(String.format("\nQuest completed: %s", this.QUEST_NAME));
				giveQuestReward();

>>>>>>> 365be86cf7b8f19c52923d6793651a9d73187a0f
			}
		}
		else
		{
			this.questComplete = true;
<<<<<<< HEAD
=======

>>>>>>> 365be86cf7b8f19c52923d6793651a9d73187a0f
			OWNER.getReplyWriter().println(String.format("\nQuest completed: %s", this.QUEST_NAME));
			giveQuestReward();
		}
	}
	
	private void giveQuestReward()
	{
		OWNER.addMoney(REWARD_AMOUNT);
		OWNER.getReplyWriter().println(String.format("Quest reward added: $%f\n", this.REWARD_AMOUNT));
	}
	
	/**
	 * Generates unique Quest or Objective ID numbers
	*/
	public static int generateID()
	{
		idGenerator++;
		return idGenerator;
	}
	
	/**
	 * Prints out an informative representation of this Quest
	 */
	public void printQuest()
	{
		System.out.println();
		System.out.println("------------QUEST INFORMATION------------");
		System.out.println(String.format("Quest OWNER: %s", this.OWNER.toString()));
		System.out.println(String.format("Quest ID: %d", this.QUEST_ID));
		System.out.println(String.format("Quest Name: %s", this.QUEST_NAME));
		System.out.println(String.format("Quest Description: %s", this.QUEST_DESCRIPTION));
		System.out.println(String.format("Quest Completion Status: %b", this.questComplete));
		System.out.println("-----------------------------------------");
		System.out.println();
	}
	
	/**
	 * Returns a String representation of this Quest
	 * @return a String representation of this Quest
	*/
	@Override
	public String toString()
	{
		String status = "incomplete";
		if (this.questComplete)
		{
			status = "complete";
		}
		return String.format("Quest: %s\nDescription: %s\nStatus: %s", this.QUEST_NAME, this.QUEST_DESCRIPTION, status);
	}

	public String getQuestName()
	{
		return QUEST_NAME;
	} 
	
}
