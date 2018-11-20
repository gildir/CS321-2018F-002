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

	public String erase()
	{
		this.message = "";
		return "The white board's message has been erased.";
	}

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

	public String getMessage()
	{
		return this.message;
	}

	public void setMessage(String msg)
	{
		this.message = msg;
	}
}






