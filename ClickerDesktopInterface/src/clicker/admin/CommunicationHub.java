package clicker.admin;


import clicker.logger.Logger;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import clicker.constants.Constants;


/* 			~ Notes ~
 * 
 * 	The Reconnector is critical to this class. It gets everything started
 * 	and keeps it going. The AdminPanel is basically where the CommunicationHub
 * 	is created and everything passed that is an instance of it.
 */


public class CommunicationHub 
{
    private boolean waitingForHeartbeat = false;
    private boolean isConnected = false;
    private Socket socket;
    
    private PrintWriter writer;
    private BufferedReader reader;
    private Logger logger;
    
    // These are static values for now
    private String username = "frederis";
    private String password = "testpw";
    private String ip;
    
    private Timer heartbeatTimer;
    private int heartbeatSeconds = 15;
    private InputManagementThread inputManager;
    
    private static CommunicationHub _instance = null;
    private ClassModel classModel;
    
    private Set<String> consumerSet = new HashSet<String>();
    private ArrayList<String> openQuestionGroups = new ArrayList<String>();
    
    private boolean everyoneOnQuestion = false;
    
    private GroupOptionsFrame groupOptionsFrame;
    private CloseQuestionGroupSelectionFrame closeGroupSelectFrame;
    private ExistingQuestionFrame existingQuestionFrame;
    
    
    private CommunicationHub() 
    {
    	classModel = ClassModel.getInstance();
    	logger = new Logger();
    }
    
    public static synchronized CommunicationHub getInstance() 
    {
        if(_instance == null) 
        {
            _instance = new CommunicationHub();
        }
        return _instance;
    }
    
    public void setIp(String ip, JTabbedPane pane) 
    {
    	this.ip = ip;
        new Thread(new Reconnecter(ip)).start();
    }
    
    public String getConnectionString()
    {
    	return ip + Constants.COLON_SEPARATOR + "4321" + Constants.COLON_SEPARATOR + username;
    }
    
    public Logger getLogger()
    {
    	return logger;
    }
    
    public String[] getConsumerList()
    {
    	Set<String> modifiedSet = new LinkedHashSet<String>();
    	modifiedSet.add("None");
    	modifiedSet.addAll(consumerSet);
    	String[] strings = new String[modifiedSet.size()];  
    	return (String[]) modifiedSet.toArray(strings);
    }
    
    public void sendMessage(String text) 
    {
        writer.println(text);
        writer.flush();
    }
    
    public String readMessage() throws IOException 
    {
        return reader.readLine();
    }
    
    public void gotDisconnected() 
    {
        inputManager.stop();
        waitingForHeartbeat = false;
        heartbeatTimer.cancel();      
    }
    
    public void receivedHeartbeat() 
    {
        waitingForHeartbeat = false;
    }
    
    public void startListening() 
    {
        heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new HeartbeatTask(), 15000, heartbeatSeconds * 1000);
        inputManager = new InputManagementThread();
    }
    
    public void updateGroupsInClassModel(String string) 
    {
        ClassModel.getInstance().setClientsIntoGroups(string);
    }
    
    public void questionSetReceived(String string)
    {
        LectureModel.getInstance().receiveQuestionSet(string);
    }
    
    public void allSetsReceived(String string)
    {
        LectureModel.getInstance().receiveAllSets(string);
    }
    
    public void updateDisplayInHub(String displaySet) 
    {
        String[] consumptionTypes = displaySet.split(Constants.COMMA_SEPARATOR);
        for (int i=0; i < consumptionTypes.length; i++) 
        {
            consumerSet.add(consumptionTypes[i]);
            System.out.println("Consumer Types: " + consumptionTypes[i]);
        }
    }
    
    public void sendQuestion(String question) 
    {
    	System.out.println(question);
    	if (classModel.getGroups().size() == 1)
    	{
    		if (!everyoneOnQuestion)
    		{
	    		sendMessage(Constants.CLIENT_COMMAND + Constants.GRAVE_SEPARATOR + question + 
						Constants.AMPERSAND_SEPARATOR + question.split(Constants.SEMI_COLON_SEPARATOR)[2] +
						Constants.AMPERSAND_SEPARATOR + "Ungrouped");
	    		everyoneOnQuestion = true;
    		}
    		else
    		{
    			existingQuestionFrame = new ExistingQuestionFrame(Constants.CLIENT_COMMAND + Constants.GRAVE_SEPARATOR + question + 
						Constants.AMPERSAND_SEPARATOR + question.split(Constants.SEMI_COLON_SEPARATOR)[2] +
						Constants.AMPERSAND_SEPARATOR + "Ungrouped");
    		}
    	}
    	else
    	{
    		groupOptionsFrame = new GroupOptionsFrame(question);
    	}
    }
    
    
    public String cleanQuestionGroups(String groups)
    {
		ArrayList<String> groupList = 
				new ArrayList<String>(Arrays.asList(groups.split(Constants.COMMA_SEPARATOR)));

		everyoneOnQuestion = groupList.contains("Ungrouped"); // Ungrouped should be Everyone
		
		if (everyoneOnQuestion)
		{
			groups = "Ungrouped"; // Ungrouped should be Everyone
		}
		else
		{
			openQuestionGroups.addAll(groupList);
		}
		
		return groups;
    }
    
    public void closeQuestion() 
    {
    	if (everyoneOnQuestion)
    	{
    		sendMessage(Constants.CLIENT_COMMAND + Constants. GRAVE_SEPARATOR + Constants.CLOSE + 
    				Constants.SEMI_COLON_SEPARATOR + "Ungrouped" + 
    				Constants.COMMA_SEPARATOR); // Ungrouped should be Everyone
    		everyoneOnQuestion = false;
    	}
    	else
    	{
    		if (openQuestionGroups.size() > 0)
    		{
    			closeGroupSelectFrame = new CloseQuestionGroupSelectionFrame();
    		}
    	}
    }
    
    // CLASS : GroupSelectionFrame
    public class GroupOptionsFrame extends JFrame
    {
    	private ArrayList<JCheckBox> groupsCheckList;
    	private static final int MAX_WIDTH = 220;
    	private static final int MAX_HEIGHT = 400;
    	public GroupOptionsFrame(String question)
    	{
    		super();
        	setTitle("Groups");
        	setSize(MAX_WIDTH, MAX_HEIGHT);
        	
        	setLayout(null);
        	
        	JPanel groupsPanel = new JPanel();
        	groupsPanel.setLayout(new BoxLayout(groupsPanel, BoxLayout.Y_AXIS));
        	groupsCheckList = new ArrayList<JCheckBox>();
        	for ( String groupName : classModel.getGroups())
        	{
        		if (!openQuestionGroups.contains(groupName))
        		{
        			groupsCheckList.add(new JCheckBox(groupName));
        		}
        	}
        	if (openQuestionGroups.size() == 0)
        	{
        		groupsCheckList.add(new JCheckBox("Everyone"));
        	}
        	groupsPanel.setLayout(new GridLayout(groupsCheckList.size() + 1, 1));
        	for (int i=0; i < groupsCheckList.size(); i++) 
        	{
        		groupsPanel.add(groupsCheckList.get(i));
        	}
        	
        	String defaultConsumer = question.split(Constants.SEMI_COLON_SEPARATOR)[2];
        	
        	groupsPanel.setBounds(0, 0, MAX_WIDTH, MAX_HEIGHT - 100);
        	add(groupsPanel);
        	
        	JButton send = new JButton("Send");
        	send.addActionListener(new SendActionListener(question, defaultConsumer));
        	send.setBounds(100 - (send.getPreferredSize().width / 2), groupsPanel.getSize().height + 20 - (send.getPreferredSize().height / 2), 
        						send.getPreferredSize().width, send.getPreferredSize().height);
        	add(send);
        	
        	setVisible(true);
    	}
    	
    	private class SendActionListener implements ActionListener
		{
			private String question;
			private String defaultConsumer;
			
			public SendActionListener(String q, String c)
			{
				question = q;
				defaultConsumer = c;
			}
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					String groups = "";
					
                    for (JCheckBox box : groupsCheckList) 
                    {
                        if (box.isSelected()) 
                        {
                        	groups += box.getText() + Constants.COMMA_SEPARATOR;
                        }
                    }
                    groups = groups.substring(0, groups.length() - 3);
    				
    				groups = cleanQuestionGroups(groups);
    				sendMessage(Constants.CLIENT_COMMAND + Constants.GRAVE_SEPARATOR + question + 
							Constants.AMPERSAND_SEPARATOR + defaultConsumer +
							Constants.AMPERSAND_SEPARATOR + groups);
    				
    				groupOptionsFrame.setVisible(false);

				}
				catch (Exception f) 
				{
					System.out.println("Group Selection - Must pick a group.");
				}
			}
		}	
    }
    	
    // CLASS : CloseQuestionGroupSelectionFrame
    public class CloseQuestionGroupSelectionFrame extends JFrame
    {
		private ArrayList<JCheckBox> checkList;
    	public CloseQuestionGroupSelectionFrame()
    	{
    		super();
        	setTitle("Select Groups to Close Questions");
        	setSize(300, 400);
        	
        	JPanel panel = new JPanel();
        	checkList = new ArrayList<JCheckBox>();
        	for ( String groupName : openQuestionGroups)
        	{
        		checkList.add(new JCheckBox(groupName));
        	}
        	
        	panel.setLayout(new GridLayout(checkList.size() + 1, 1));
        	for (int i=0; i < checkList.size(); i++) 
        	{
        		panel.add(checkList.get(i));
        	}

        	JButton okay = new JButton("Ok");
        	okay.addActionListener(new OkayActionListener());
        	panel.add(okay);
        	
        	add(panel);
        	setVisible(true);
    	}
    	
    	private class OkayActionListener implements ActionListener
		{
			public OkayActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					String groupsToSend = "";
					
                    for (int i=0; i < checkList.size(); i++) 
                    {
                        if (checkList.get(i).isSelected()) 
                        {
                            groupsToSend += checkList.get(i).getText() + Constants.COMMA_SEPARATOR;
                            openQuestionGroups.remove(checkList.get(i).getText());
                        }
                    }
                    sendMessage(Constants.CLIENT_COMMAND + Constants.GRAVE_SEPARATOR + Constants.CLOSE + 
                    		Constants.SEMI_COLON_SEPARATOR + groupsToSend);
					closeGroupSelectFrame.setVisible(false);
				}
				catch (Exception f) 
				{
					System.out.println("Group Selection - Must pick a group.");
				}
			}
		}	
    }
    
    // CLASS : GroupSelectionFrame
    public class ExistingQuestionFrame extends JFrame
    {
    	
	   	public ExistingQuestionFrame(String question)
    	{
    		super();
        	setTitle("Existing question!");
        	setSize(300, 100);
        	setLocation(300, 300);
        	setResizable(false);

        	JPanel panel = new JPanel();
        	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        	
        	// Label
        	JPanel labelPanel = new JPanel();
        	labelPanel.add(new JLabel("Do you want to close the previous question?"));
        	
        	panel.add(labelPanel);
        	//END
        	
        	// Button Panel
        	JPanel buttonPanel = new JPanel();
        	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        	
        	
        	JButton yes = new JButton("Yes");
        	yes.addActionListener(new YesActionListener(question));
        	buttonPanel.add(yes);
        	
        	buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        	
        	JButton no = new JButton("No");
        	no.addActionListener(new NoActionListener());
        	buttonPanel.add(no);
        	
        	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        	panel.add(buttonPanel);
        	// END
        	
        	panel.add(Box.createRigidArea(new Dimension(0, 10)));
        	add(panel);
        	
        	setVisible(true);
    	}
    	
    	
    	private class YesActionListener implements ActionListener
		{
    		String question;
			public YesActionListener(String q) 
			{
				question = q;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				closeQuestion();
				sendMessage(question);
				everyoneOnQuestion = true;
				existingQuestionFrame.setVisible(false);
			}
		}	
    	
    	private class NoActionListener implements ActionListener
		{
			public NoActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				existingQuestionFrame.setVisible(false);
			}
		}	
    }
    
    // CLASS : HeartbeatTask
    private class HeartbeatTask extends TimerTask 
    {
        public void run() 
        {
            if (waitingForHeartbeat) 
            {
                try 
                {
                    socket.close();
                } 
                catch (IOException e) {}
            } 
            else 
            {
                waitingForHeartbeat = true;
                sendMessage(Constants.CLIENT_HEARTBEAT_REQUEST);
            }
        }
    }
    
    // CLASS : Reconnecter
    private class Reconnecter implements Runnable 
    {
        String ip;
        int adminPort = 7700;
        int timeout = 5000;
        
        public Reconnecter(String ip) 
        {
            this.ip = ip;
        }
        
        public void run() 
        {
            int retryCount = 0;
            while (!isConnected && retryCount < 2) 
            {
                try 
                {               
                    SocketAddress sockaddr = new InetSocketAddress(ip, adminPort);
                    Socket newSocket = new Socket();
                    newSocket.connect(sockaddr, timeout);
                    newSocket.setKeepAlive(true);
                    socket = newSocket;
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new PrintWriter(socket.getOutputStream(), false);
                    sendMessage(username);
                    sendMessage(password);
                    isConnected = true;
                    startListening();
                } 
                catch (Exception e) 
                {
                    try
                    {
                        System.out.println("ERROR: Failed to connect, waiting and trying again");
                        retryCount++;
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {}
                }
            }
            
            if (isConnected) 
            {
                System.out.println("Hub is connected...");
            } 
            else 
            {
                System.out.println("Hub is not connected...");
            }
        }
        
    }
}
