import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class LoggerPlugin implements ClickerConsumerInterface
{

	private PrintWriter pw;
	private ConsumerFrame parent;
	private File currentLog;
	private String currentQuestion;
	private Map<String, ArrayList<ArrayList<String>>> logData;
	
	public LoggerPlugin()
	{
		currentQuestion = "";
		
		logData = new HashMap<String, ArrayList<ArrayList<String>>>();
		
		File logDirectory = new File("./logs");
		if(!logDirectory.exists())
		{
			if(logDirectory.mkdir())
			{
				System.out.println("Made a directory at: " + logDirectory.toString());
			} 
			else 
			{
				System.out.println("Log directory is: " + logDirectory.toString());
			}
		}
		}
	
	
	@Override
	public void setParent(ConsumerFrame parent) 
	{
		this.parent = parent;
	}

	@Override
	public String declareConsumptions() 
	{
		return "Logging`/:All";
	}

	@Override
	public void setActiveStatus(boolean status) 
	{
		if (!status)
		{
			File currentLog = createLogFile("Test Class", "Test Log");
			
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			
			try 
			{
				PrintWriter pw = new PrintWriter(currentLog);
				pw.println("     Test Log - " + dateFormat.format(date));
				pw.println("");
				for(String userName : logData.keySet())
				{
					pw.println("  " + userName);
					
					for (ArrayList<String> history : logData.get(userName))
					{
						for (String label : history)
						{
							pw.println("    " + label);
						}
						
					}
				}
				pw.flush();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
	}
		
	private File createLogFile(String section, String filename)
	{
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
		Date date = new Date();

		File directory = new File("./logs/" + section + "/" + 
				dateFormat.format(date));
		
		if(!directory.exists())
		{
			directory.mkdir();
		}
		
		File currentLog = new File("./logs/" + section + "/" + 
									dateFormat.format(date) + "/" + 
									filename + ".txt");
		try
		{
			int count = 0;
			while (currentLog.exists())
			{
				currentLog = new File("./logs/" + section + "/" + 
										dateFormat.format(date) + "/" + 
										filename + " (" + count + ")" + ".txt");
				count += 1;
			}
			
			currentLog.createNewFile();
		} 
		catch (IOException e) 
		{
			System.out.println("Error creating new log file");
		}
		
		return currentLog;
	}
	
	@Override
	public boolean getActiveStatus() 
	{
		return false;
	}
	
	
	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
	{

		logData = input;

	}

	@Override
	public void setID(String id) {}

	@Override
	public void setQuestion(String question) 
	{
		currentQuestion = question;
	}

}
