package clicker.logger;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Logger 
{
	private Map<String, Log> logMap;
	
	public Logger()
	{
		logMap = new HashMap<String, Log>();
		
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
	
	public void logAttendance(String sectionName, ArrayList<String> students)
	{
		
		File currentLog = createLogFile(sectionName, "attendanceLog");
		
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		
		try 
		{
			PrintWriter pw = new PrintWriter(currentLog);
			pw.println("     Attendance Log - " + dateFormat.format(date));
			pw.println("Section: " + sectionName);
			pw.println("Count: " + students.size());
			pw.println("");
			for(String s : students)
			{
				pw.println("  " + s);
			}
			pw.flush();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void openLog(String group, String section, String questionID)
	{
		logMap.put(group, new Log(section, questionID));
	}
	
	public void logSeries(String group, String series)
	{
		if (logMap.containsKey(group))
			logMap.get(group).addSeriesLog(series);
	}
	
	public void closeLog(String group)
	{
		if (logMap.containsKey(group))
		{
			Log heldLog = logMap.get(group);
			File currentLog = createLogFile(heldLog.getSectionID(), 
					heldLog.getQuestionID());
//			+ "_" + System.currentTimeMillis()
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			
			try 
			{
				PrintWriter pw = new PrintWriter(currentLog);
				pw.println("     " + heldLog.getQuestionID() + " : " +dateFormat.format(date));
				pw.println("Section: " + heldLog.getSectionID());
				pw.println("");
				
				int count = 0;
				for(String s : heldLog.getSeriesLog())
				{
					count++;
					pw.println(count + " : " + s);
				}
				pw.flush();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
