import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

/**
* An Objective (something that requires action) in a Quest
* 
* An Objective is something a Player must do to satisfy part of a Quest.
* We "hide" an Objective from the player until it becomes relevant.
* EXAMPLE: We "hide" [Write your name] if the Player must first complete the Objectives [Find up a pen] and [Find a piece of paper].
* An active Objective is an Objective that the player is aware of.
* Objectives act similarly to a Linked List and form a directed acyclic graph (DAG).
* Each Objective stores references to the next Objective(s) (directed)
* Objectives do NOT store references to the previous Objective(s) (acyclic)
* 
* SEE EXAMPLE DIAGRAM BELOW
* concurrent Objectives are on the same row
* 
*         0 (start)
* 		/   \
* 	   1     2
* 		\   /
* 		  3
* 	    / | \
* 	   4  5  6
* 	    \ | /
* 		  7 (end)
* 
* 0 = [Talk to Professor Russell]
* 1 = [Find a pen]
* 2 = [Find a piece of paper]
* 3 = [Talk to Professor Russell]
* 4 = [Write your name down]
* 5 = [Write your G# down]
* 6 = [Write your Mason email address down]
* 7 = [Give the piece of paper to Professor Russell]
* 
*/

public abstract class Objective
{
	
	/**
	 * The unique ID number of this Objective
	 * 
	 * The unique ID number of this Objective, generated from the static variable
	 * "idGenerator" in the Quest class (tracking the total number of quests
	 * that have been instantiated)
	*/
	public final int OBJECTIVE_ID = -1;
	/**
	 * The type of this Objective
	 * 
	 * The type of this Objective, depending on the action necessary to complete the objective.
	 * EXAMPLES
	 * Type: Location	|	Objective Descripton: "Go to the Johnson Center."
	 * Type: Item		|	Objective Description: "Pick up a newspaper."
	 * Type: Action		|	Objective Description: "Talk to Professor Russell."
	*/
	public final String OBJECTIVE_TYPE = "";
	/**
	 * The description of this Objective
	*/
	public final String OBJECTIVE_DESCRIPTION = "";
	/**
	 * The unique ID number of the Quest this Objective belongs to
	*/
	public final Quest ASSOCIATED_QUEST = null;
	/**
	 * The boolean status of whether this Objective has been completed (false until updated)
	*/
	private boolean objectiveComplete = false;
	/**
	 * Array storing references to the next Objectives (there should never be more than 5 concurrent objectives)
	*/
	public final Objective NEXT_OBJECTIVES[] = new Objective[5];
	
	/**
	 * Constructor for an Objective
	 * 
	 * @param q the Quest associated with this Objective
	 * @param objScanner the scanner from the input file for a Quest
	*/
	public Objective() throws InputMismatchException, NoSuchElementException
	{
		
	}
	
	/**
	 * Returns whether this Objective has been completed
	*/
	public boolean getObjectiveComplete()
	{
		return this.objectiveComplete;
	}
	
	/**
	 * Changes the completion status of this Objective to "true" when the Objective has been completed
	*/
	public void updateObjectiveCompletion()
	{
		System.out.println("Implement updateObjectiveCompletion(Player p) method");
	}
	
	/**
	* Returns the Objective at the given position in the array NEXT_OBJECTIVES
	* 
	* Returns the Objective at the given position in the array NEXT_OBJECTIVES,
	* which can be an Objective or null.
	*/
	public Objective getNextObjective(int x)
	{
		return null;
	}
	
	/**
	 * Prints out an informative representation of this Objective
	 */
	public void printObjective()
	{
		System.out.println("Implement printObjective() method");
	}
	
	/**
	 * Returns a String representation of this Objective
	 * @return a String representation of this Objective
	*/
	@Override
	public String toString()
	{
		String status = "incomplete";
		if (objectiveComplete)
		{
			status = "complete";
		}
		return String.format("Objective: %s\nStatus: %s", this.OBJECTIVE_DESCRIPTION, status);
	}
	
}