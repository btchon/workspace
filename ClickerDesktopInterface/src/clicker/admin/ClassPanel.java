package clicker.admin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import clicker.constants.Constants;

public class ClassPanel extends JPanel
{
	private CommunicationHub hub;
	private ClassModel classModel;
	Thread thread;
	private boolean runThread;
	
	private JPanel bPanel;
	private ExpandingGroupPanels groupPanels;  
	
	public ClassPanel()
	{
		super();
		hub = CommunicationHub.getInstance();
		classModel = ClassModel.getInstance();
		hub.sendMessage(Constants.GET_CLIENT_LIST + Constants.GRAVE_SEPARATOR);
		groupPanels = new ExpandingGroupPanels();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		// BUTTON PANEL
        bPanel = new JPanel();
        bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));
        
        bPanel.add(Box.createRigidArea(new Dimension(30, 0)));

        JButton addGroupButton = new JButton("Add Group");
        addGroupButton.addActionListener(new AddGroupActionListener());
        bPanel.add(addGroupButton);
        // CD - Taken out because not functional
//        bPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//        
//        JButton ungroupButton = new JButton("Ungroup All");
//        ungroupButton.addActionListener(new UngroupActionListener());
//        bPanel.add(ungroupButton);
        
        bPanel.add(Box.createRigidArea(new Dimension(30, 0)));

        JButton logButton = new JButton("Take Attendance");
        logButton.addActionListener(new LogActionListener());
        bPanel.add(logButton);
        
        bPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        // END
        
        // CLASS INTERFACE
        add(new JScrollPane(groupPanels.getComponent()));
        
        add(Box.createRigidArea(new Dimension(0, 20)));
        
        add(bPanel);
        
        add(Box.createRigidArea(new Dimension(0, 20)));
        // END
        
        runThread = true;
        thread = new Thread( new RefreshThread() );
        thread.start();
        System.out.println("Refresh Thread Starting...");
	}
	
	private void refresh()
	{
		groupPanels.refreshPanelsMap();
	}
	
	private class UngroupActionListener implements ActionListener
	{

		public UngroupActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			classModel.unGroupMembers();
			refresh();
		}
	}
	
	private class AddGroupActionListener implements ActionListener
	{

		public AddGroupActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new AddGroupFrame();
		}
	}
	
	private class LogActionListener implements ActionListener
	{

		public LogActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			hub.getLogger().logAttendance("Test Class", classModel.getClients());
		}
	}
	
	public class AddGroupFrame extends JFrame
    {
    	private JTextField nameEntry;
    	
    	public AddGroupFrame()
    	{
    		super();
        	setTitle("Add Group");
        	setSize(250, 100);
        	setLocation(300, 300);
        	setResizable(false);

        	JPanel panel = new JPanel();
        	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        	
        	// Label
        	JPanel labelPanel = new JPanel();
        	labelPanel.add(new JLabel("Group Name: "));
        	
        	String setName = "Group 0";
        	int n = 1;
        	
        	while (true)
        	{
        		if (classModel.getGroups().contains("Group " + n))
        		{
        			n++;
        		}
        		else
        		{
        			setName = "Group " + n;
        			break;
        		}
        	}
        	
        	nameEntry = new JTextField(setName);
        	nameEntry.setColumns(8);
        	nameEntry.setMaximumSize(new Dimension(100, 20));
        	nameEntry.setMinimumSize(new Dimension(100, 20));
        	nameEntry.revalidate();
        	labelPanel.add(nameEntry);
        	panel.add(labelPanel);
        	
        	//END
        	
        	// Button Panel
        	JPanel buttonPanel = new JPanel();
        	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        	
        	
        	JButton okay = new JButton("Ok");
        	okay.addActionListener(new OkayActionListener());
        	buttonPanel.add(okay);
        	
        	buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        	
        	JButton close = new JButton("Close");
        	close.addActionListener(new CloseActionListener());
        	buttonPanel.add(close);
        	
        	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        	panel.add(buttonPanel);
        	// END
        	panel.add(Box.createRigidArea(new Dimension(0, 10)));
        	add(panel);
        	
        	setVisible(true);
    	}
    	
    	private class OkayActionListener implements ActionListener
		{
			
			public OkayActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (nameEntry.getText() != null)
				{
					classModel.addGroup(nameEntry.getText());
				}
				refresh();
				setVisible(false);
			}
		}	
    	
    	private class CloseActionListener implements ActionListener
		{
			
			public CloseActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}
		}	
    }
	
	private class RefreshThread implements Runnable 
    {

        @Override
        public void run() 
        {
        	while (runThread)
        	{
        		try 
        		{
					String output = classModel.getGroupUpdate();
					hub.sendMessage(output);
					hub.sendMessage(Constants.GET_CLIENT_LIST + Constants.GRAVE_SEPARATOR);
					refresh();
					
					Thread.sleep(1000);
				} 
        		catch(Exception i) {}
        	}
        }
        
        public void stop() 
        {
            runThread = false;
        }
    }
}

