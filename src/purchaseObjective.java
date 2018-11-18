import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

public class purchaseObjective extends Objective
{
	
	/**
	 * The unique ID number of this Objective
	 * 
	 * The unique ID number of this Objective, generated from the static variable
	 * "idGenerator" in the Quest class (tracking the total number of quests
	 * that have been instantiated)
	*/
	public final int OBJECTIVE_ID;
	/**
	 * The type of this Objective
	 * 
	 * The type of this Objective, depending on the action necessary to complete the objective.
	 * EXAMPLES
	 * Type: Location	|	Objective Descripton: "Go to the Johnson Center."
	 * Type: Item		|	Objective Description: "Pick up a newspaper."
	 * Type: Action		|	Objective Description: "Talk to Professor Russell."
	*/
	public final String OBJECTIVE_TYPE = "Purchase";
	/**
	 * The description of this Objective
	*/
	public final String OBJECTIVE_DESCRIPTION;
	/**
	 * The unique ID number of the Quest this Objective belongs to
	*/
	public final Quest ASSOCIATED_QUEST;
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
	public purchaseObjective(Quest q, Scanner objScanner) throws InputMismatchException, NoSuchElementException
	{
		if(objScanner == null){
			throw new NullPointerException("NullPointerException: scanner passed to purchaseObjective constructor was null.");
		}
		this.OBJECTIVE_ID = Quest.generateID();
		this.ASSOCIATED_QUEST = q;
		this.OBJECTIVE_DESCRIPTION = objScanner.next();
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
