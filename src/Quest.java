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
	
	/**
	 * A counter that is incremented each time a Quest or Objective is instantiated (used for generating Quest or Objective unique ID numbers)
	*/
	static int idGenerator;
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
	private Objective activeObjectives[] = new Objective[5];
	/**
	 * The boolean status of whether this Quest has been completed (false until updated)
	*/
	private boolean questComplete = false;
	
	/**
	 * Constructor for a Quest
	 * 
	 * Creates a new Quest object given an input file
	 * @param questFile the file containing the quest information
	*/
	public Quest(File questFile) throws FileNotFoundException
	{
		Scanner questScanner = new Scanner(questFile);
		// set the delimiter to single newline characters with any amount of whitespace on either side
		questScanner.useDelimiter("\\s*\n\\s*");
		if (questScanner.hasNext())
		{
			try
			{
				this.QUEST_ID = questScanner.nextInt();
				this.QUEST_NAME = questScanner.next();
				this.QUEST_DESCRIPTION = questScanner.next();
				if ( !(questScanner.hasNext()) )
				{
					throw new NoSuchElementException("No Such Element Exception: no Quest Objectives specified in input file");
				}
				int objectivesAdded = 0;
				while (questScanner.hasNext())
				{
					// FIXME: Add Objectives here once specific Objective classes (that implement Objective.java) have been added
					System.out.println(String.format("ADD OBJECTIVE: %s", questScanner.next()));
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
	 * Checks if all active Objectives have been completed, and if so, updates the active Objectives to the next Objectives.
	 * If there are no further Objectives, the Quest is marked as completed.
	*/
	private void updateQuest()
	{
		// if we have no more active Objectives, the Quest is complete
		if (activeObjectives[0] == null)
		{
			this.questComplete = true;
			return;
		}
		
		// start by assuming we are ready to advance to the next Quest Objectives
		boolean readyToAdvance = true;
		for ( int i = 0 ; i < 5 ; i++ )
		{
			// if this Objective isn't null, check if it has been completed
			if (activeObjectives[i] != null)
			{
				// if this Objective hasn't been completed, we're not ready to advance to the next Objective(s)
				readyToAdvance = activeObjectives[i].getObjectiveComplete();
			}
			// if we're not ready to advance, we can stop checking the rest of the Objectives
			if ( !(readyToAdvance) )
			{
				break;
			}
		}
		// if we've verified all active Objectives have been completed, we are ready to advance to the next Objective(s)
		if (readyToAdvance)
		{
			// set the active Objectives to the next Objectives
			for ( int i = 0 ; i < 5 ; i++ )
			{
				activeObjectives[i] = activeObjectives[0].getNextObjective(i);
			}
		}
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
	
}