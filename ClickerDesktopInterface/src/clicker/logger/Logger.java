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
	
	public Logger()
	{
		
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
		
		File directory = new File("./logs/" + section);
		
		if(!directory.exists())
		{
			directory.mkdir();
		}

		directory = new File("./logs/" + section + "/" + 
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

	
	public void saveLog(String section, String fileName, Map<String, ArrayList< ArrayList<String>>> dataMap)
	{
		File currentLog = createLogFile(section, fileName);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		
		try 
		{
			PrintWriter pw = new PrintWriter(currentLog);
			pw.println("     " + fileName + " : " + dateFormat.format(date));
			pw.println("Section: " + section);
			pw.println("");
			
			
			for (String user : dataMap.keySet())
			{
				pw.println(user);
				int count = 0;
				for (ArrayList<String> set : dataMap.get(user))
				{
					count++;
					pw.print("  " + count + " : ");
					for (String s : set)
					{
						pw.print(s + " ");
					}
					pw.println();
					pw.println();
					
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
