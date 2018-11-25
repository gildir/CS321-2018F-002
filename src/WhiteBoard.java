import java.util.*;
import java.io.PrintWriter;

public class WhiteBoard
{
	private String message;
	private int roomId;

	public WhiteBoard(int id)
	{
		this.message = "";
		this.roomId = id;
	}
	public WhiteBoard(String text, int id)
	{
		this.message = text;
		this.roomId = id;
	}

	/**
	*	Formatted accessor for the whiteboard message variable that limits it to a maximum of 120 characters
	*	@param text the string that will be checked and saved to the message variable if it is within the character limit
	*	@return a string informing the user whether their message was invalid, too many characters, or whether it was set
	*/
	public String write(String text)
	{
		if(text == null)
		{
			return "Please include a message to write.";
		}
		if(text.length() > 120)
		{
			return "Message character limit exceeded. Messages cannot be more than 120 characters long.";
		}
		else
		{
			this.message = text;
			return "Your message has been saved to the white board.";
		}
	}

	/**
	*	A restricted mutator for the whiteboard's message variable which sets it to the empty string.
	*	@return a string informing the user that the whiteboard's message was erased
	*/
	public String erase()
	{
		this.message = "";
		return "The white board's message has been erased.";
	}

	/**
	*	A fucntion that creates a formated version of the message variable to give it the appearance of being on a whiteboard and returns it.
	*	@return the formatted version of the message variable
	*/
	public String display()
	{
		String formatedMessage = "";
		for(int i = 0; i < 80; i++)
		{
			formatedMessage += "=";
		}
		formatedMessage += "\n";
		formatedMessage += "| ";
		if(this.message.length() > 80){
			formatedMessage += this.message.substring(0, 76);
			formatedMessage += " |";
			formatedMessage += "\n";
			formatedMessage += "| ";
			formatedMessage += this.message.substring(76);
		}
		else{formatedMessage += this.message;}
		if(this.message.length() < 120)
		{
			for(int i = this.message.length(); i < 152; i++)
			{
				if(i == 76 && this.message.length() < 76){break;}
				formatedMessage += " ";
			}
		}
		formatedMessage += " |";
		formatedMessage += "\n";
		for(int i = 0; i < 80; i++)
		{
			formatedMessage += "=";
		}
		return formatedMessage;
	}

	/**
	*	A standard accessor for the message variable.
	*	@return the string currently contained in the message variable
	*/
	public String getMessage()
	{
		return this.message;
	}

	/**
	*	A standard mutator for the message variable.
	*	@param msg the message that will be saved to the message variable
	*/
	public void setMessage(String msg)
	{
		this.message = msg;
	}
}






