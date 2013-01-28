
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import clicker.constants.Constants;
import clicker.logger.Logger;
import javax.swing.JPanel;

public class ClickerIMP extends JPanel
{

	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private Logger logger;
	
	private static ClickerConsumerInterface consumerInstance;
	
	private Map<String, Class<?>> availableDisplays;
	private Map<String, Class<?>> availableSummaries;
	private Map<String, ConsumerGUI> openGroups;        // GroupName, GUI for Group
	
	public ClickerIMP()
	{
		availableDisplays = Collections.synchronizedMap(new HashMap<String, Class<?>>());
		availableSummaries = Collections.synchronizedMap(new HashMap<String, Class<?>>());
		openGroups = Collections.synchronizedMap(new HashMap<String, ConsumerGUI>());
		
		doLogin("127.0.0.1", "7171");
	}
	
	private void doLogin(String ip, String port)
	{
		try 
		{
			loadConsumersFromSubdirectory();
			socket = new Socket(ip, Integer.parseInt(port));
			socket.setKeepAlive(true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			
			writer.println("frederis`/,default");
			writer.flush();
			
			writer.println(getConsumptionString());
			writer.flush();
			
			Thread thread = new Thread(new HandlingRunnable());
			thread.start();
			System.out.println("Login successful.");
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			System.out.println("Login unsuccessful.");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Login unsuccessful.");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Login unsuccessful.");
		}
	}
	
	/*
	 * Load up all the available consumers from the consumers directory
	 */
	private void loadConsumersFromSubdirectory()
	{
		Class<?> consumerClass;
		
		// Navigate to the consumber directory
		File consumerDirectory = new File("./consumers/");
		// Get a list of all the consumers in that directory
		String[] files = consumerDirectory.list();
		URLClassLoader urlcl = null;
		
		try 
		{
			urlcl = new URLClassLoader(new URL[] {(consumerDirectory.toURI().toURL())});
		} 
		catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
		}

		// Go to each file
		for (String s: files)
		{
			try 
			{
				// Check to see if it has .class
				if(s.length() < 6)
				{
					System.out.println("Filename: "+ s + "is too short to be an appropriate java class file. Skipping.");
					continue;
				}
				
				// Load up the class
				consumerClass = urlcl.loadClass(s.substring(0,s.length()-6));
				// Check to see if the class is compatible with the CCI
				boolean works = ClickerConsumerInterface.class.isAssignableFrom(consumerClass);
				
				// If it is compatible make the ClickerIMP its parent, get its consumption type 
				// and add it in as an available consumer
				if (works)
				{
					consumerInstance = (ClickerConsumerInterface)consumerClass.newInstance();
					String temporaryConsumption = consumerInstance.declareConsumptions();
					String[] parts = temporaryConsumption.split(Constants.COLON_SEPARATOR);
					if (parts.length > 1 && parts[1].equals("Display"))
					{
						availableDisplays.put(parts[0], consumerClass);
						System.out.println(temporaryConsumption);
					}
					else if (parts.length > 1 && parts[1].equals("Summary"))
					{
						availableSummaries.put(parts[0], consumerClass);
						System.out.println(temporaryConsumption);
					}
					
				} 
				else 
				{
					System.out.println(s + " does not properly fit the necessary interface. Skipping.");
				}
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println(s + " was not an appropriately formed java class file. Skipping.");
				continue;
			} 
			catch (InstantiationException e) 
			{
				e.printStackTrace();
			} 
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			} 
			catch (NoClassDefFoundError e)
			{
				System.out.println("Invalid class file " + s + " found. Skipping.");
				continue;
			}
		}
	}
	
	private String getConsumptionString()
	{
		String tempConsumptionString = Constants.I_CONSUME + Constants.SEMI_COLON_SEPARATOR;
		tempConsumptionString += buildConsumptionNotificationString();
		return tempConsumptionString;
	}
	
	private String buildConsumptionNotificationString()
	{
		//Each consumption will be a Title,expected value:type;expected value:type
		//no ev:t will imply they take everything
		String tempConsumptionString = "";
		Iterator<String> i = availableDisplays.keySet().iterator();
		while (i.hasNext())
		{
			tempConsumptionString += i.next() + Constants.COMMA_SEPARATOR;
		}
		return tempConsumptionString.substring(0, tempConsumptionString.length()-3);
	}
	
	private class HandlingRunnable implements Runnable
	{
		private String str;
		private String[] strParts;
		private boolean run;
		
		public void run()
		{
			run = true;
			while(run)
			{
				try 
				{
					str = reader.readLine();
					strParts = str.split(Constants.SEMI_COLON_SEPARATOR);
					
					// Check the first part of the array for a valid open command
					// Expecting: Open`/;34`/;`/;B`/:A`/:0`/,B`/:B`/:0`/,B`/:C`/:0`/,B`/:D`/:0`/&JFreeBarGraph`/:Count`/&Ungrouped`/:1`/,
					if (strParts[0].equalsIgnoreCase(Constants.OPEN) 
							|| strParts[0].equalsIgnoreCase(Constants.OPEN_CLICK_PAD) 
							|| strParts[0].equalsIgnoreCase(Constants.OPEN_SPOTLIGHT_PAD))
					{
//						System.out.println("Open question: " + str);
						// Split the str into 3 parts: question&consumer&group
						String[] questionArray = str.split(Constants.AMPERSAND_SEPARATOR);
						
						// Loop through all the groups in the str
						for(String groupName : questionArray[2].split(Constants.COMMA_SEPARATOR))
						{
							// Split it to group name : number of students
							String[] groupNameParts = groupName.split(Constants.COLON_SEPARATOR);
							openGroups.put(groupNameParts[0], new ConsumerGUI(groupNameParts[0], str, "", availableDisplays, availableSummaries));
						}
					} 
					// Expecting: Close`/;Group`/,
					else if (strParts[0].equals(Constants.CLOSE))
					{
//						System.out.println("Close question: " + str);
						for(String groupName : strParts[1].split(Constants.COMMA_SEPARATOR))
						{
							closeConsumerPlugin(groupName);
						}
					} 
					// Expecting: ClientID`/;Group`/;QuestionID`/;A`/, `/, `/, 
					else if (str.length() > 0)
					{
//						System.out.println("Recieved: " + str);
						if (openGroups.containsKey(strParts[1]))
						{
							openGroups.get(strParts[1]).processNewInput(strParts[0], strParts[3]);
						}
					}
				} 
				catch(SocketException e)
				{
					run = false;
				} 
				catch(Exception e)
				{
					run = false;
					e.printStackTrace();
				}
			}
		}
	}
	
	private void closeConsumerPlugin(String groupName) 
	{
		System.out.println("Close called on: " + groupName);
	}

}
