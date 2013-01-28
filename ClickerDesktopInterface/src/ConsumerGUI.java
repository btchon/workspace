
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import clicker.constants.Constants;
import clicker.logger.Logger;


public class ConsumerGUI extends JFrame
{
	private Map<String, Class<?>> availableDisplays;
	private Map<String, Class<?>> availableSummaries;
	private ClickerConsumerInterface activeDisplay;
	private ClickerConsumerInterface activeSummary;
	
	private String group;
	private String question;
	private Map<String, ArrayList< ArrayList<String>>> dataMap;
	private ArrayList<String> savedLabels;
	private ArrayList<String> intervalLabels;
	
	private JPanel displayPanel;
	private JPanel summaryPanel;
	
	private Double minimum = 0.0;
	private Double maximum = 0.0;
	private ButtonGroup seriesGroup = new ButtonGroup();
	private ButtonGroup limitGroup = new ButtonGroup();
	private JCheckBoxMenuItem displayCheckBox;
	private JCheckBoxMenuItem summaryCheckBox;
	private JPanel innerPanel;
	
	private Logger logger;
	
	private static int MAX_WIDTH = 1050;
	private static int MAX_HEIGHT = 760;
	
	private boolean control;
	
	public ConsumerGUI(String group, String question, String defaultDisplay, 
			Map<String, Class<?>> availableDisplays, Map<String, Class<?>> availableSummaries)
	{
		super();
		getContentPane().setLayout( new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		this.getContentPane().setBackground(Color.white);
		this.setResizable(true);
    	setTitle(group);
    	setSize(MAX_WIDTH, MAX_HEIGHT);
    	dataMap = Collections.synchronizedMap(new HashMap<String, ArrayList< ArrayList<String>>>());
    	savedLabels = new ArrayList<String>();
    	intervalLabels = new ArrayList<String>();
    	logger = new Logger();
    	
    	this.availableDisplays = availableDisplays;
    	this.availableSummaries = availableSummaries;
    	this.group = group;
    	this.question = question;
		
		String[] labels = question.split("`/&")[0].split("`/;")[3].split("`/,");
		for (String s : labels)
		{
			if (s.split("`/:")[0].equals("B") || s.split("`/:")[0].equals("SLIDER"))
			{
				savedLabels.add(s.split("`/:")[1]);
			}
		}
    	
    	buildGUI();
    	control = false;
    	setVisible(true);
	}
	
	private ClickerConsumerInterface activeControl;
	
	
	public ConsumerGUI(String group, String question, Map<String, Class<?>> availableControls, String consumer)
	{
		control = true;
		dataMap = Collections.synchronizedMap(new HashMap<String, ArrayList< ArrayList<String>>>());
		
		try 
		{
			// Creates a new instance of the consumer and starts it up
			activeControl = (ClickerConsumerInterface) availableControls.get(consumer).newInstance();
			activeControl.setID(group);
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	private void buildGUI()
	{
		// MENU
		JMenuBar menuBar = new JMenuBar();
		
		// DISPLAY MENU
		JMenu displayMenu = new JMenu("Display");
		
		for (String display : availableDisplays.keySet())
		{
			 JMenuItem item = new JMenuItem(display);
			 item.addActionListener(new OpenDisplayActionListener(display));
			 displayMenu.add(item);
		}
		displayMenu.addSeparator();
		displayCheckBox = new JCheckBoxMenuItem("Show");
		displayCheckBox.setSelected(true);
		displayCheckBox.addActionListener(new UpdatePanelsActionListener());
		displayMenu.add(displayCheckBox);
		
		menuBar.add(displayMenu);
		// END DISPLAY
		
		// SUMMARY MENU
		JMenu summaryMenu = new JMenu("Summary");
		
		for (String summary : availableSummaries.keySet())
		{
			 JMenuItem item = new JMenuItem(summary);
			 item.addActionListener(new OpenSummaryActionListener(summary));
			 summaryMenu.add(item);
		}
		summaryMenu.addSeparator();
		summaryCheckBox = new JCheckBoxMenuItem("Show");
		summaryCheckBox.setSelected(true);
		summaryCheckBox.addActionListener(new UpdatePanelsActionListener());
		summaryMenu.add(summaryCheckBox);
		
		menuBar.add(summaryMenu);
		// END SUMMARY
		
		// OPTIONS MENU
		JMenu optionsMenu = new JMenu("Options");
		
		JMenu limitData = new JMenu("Limit Data");
		
		JMenuItem limitOptions = new JMenuItem("Options");
		limitOptions.addActionListener(new LimitPopupActionListener());
		limitData.add(limitOptions);
		limitData.addSeparator();
		
		JRadioButtonMenuItem limitOn = new JRadioButtonMenuItem("On");
		limitOn.setActionCommand("On");
		limitOn.addActionListener(new UpdateActionListener());
		limitOn.addActionListener(new UpdatePanelsActionListener());
		limitGroup.add(limitOn);
		limitData.add(limitOn);
		
		JRadioButtonMenuItem limitOff = new JRadioButtonMenuItem("Off");
		limitOff.setSelected(true);
		limitOff.setActionCommand("Off");
		limitOff.addActionListener(new UpdateActionListener());
		limitOff.addActionListener(new UpdatePanelsActionListener());
		limitGroup.add(limitOff);
		limitData.add(limitOff);
		
		optionsMenu.add(limitData);
		optionsMenu.addSeparator();
		
		JMenu dataSeries = new JMenu ("Data Series");
		
		JRadioButtonMenuItem lastEntryItem = new JRadioButtonMenuItem("Last Entry");
		lastEntryItem.setSelected(true);
		lastEntryItem.setActionCommand("Last");
		lastEntryItem.addActionListener(new UpdateActionListener());
		seriesGroup.add(lastEntryItem);
		dataSeries.add(lastEntryItem);

		JRadioButtonMenuItem allDataItem = new JRadioButtonMenuItem("All Data");
		allDataItem.setActionCommand("All");
		allDataItem.addActionListener(new UpdateActionListener());
		seriesGroup.add(allDataItem);
		dataSeries.add(allDataItem);
		
		optionsMenu.add(dataSeries);
		optionsMenu.addSeparator();
		
		JMenuItem logOptions = new JMenuItem("Log");
		logOptions.addActionListener(new LogPopupActionListener());
		optionsMenu.add(logOptions);
		
		menuBar.add(optionsMenu);
		//END OPTIONS
		
		setJMenuBar(menuBar);
		
		// END MENU
		
		// INNER PANEL
		displayPanel = new JPanel();
		displayPanel.setBackground(Color.white);
		displayPanel.setMinimumSize(new Dimension(MAX_WIDTH - 320, 470));
		displayPanel.setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
		
		summaryPanel = new JPanel();
		summaryPanel.setBackground(Color.white);
		summaryPanel.setMinimumSize(new Dimension(320, 470));
		summaryPanel.setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
		
		innerPanel = new JPanel();
		innerPanel.setLayout( new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		
		activeDisplay = openPlugin("JFreeBarGraph", availableDisplays, displayPanel);
		innerPanel.add(displayPanel);
		
		activeSummary = openPlugin("Data Table", availableSummaries, summaryPanel);
		innerPanel.add(summaryPanel);
		
		add(innerPanel);
		
		// END INNER PANEL

		
	}
	
	private void buildPanels()
	{
		innerPanel.removeAll();
		
		if (displayCheckBox.isSelected())
		{
			innerPanel.add(displayPanel);
		}
		
		if (summaryCheckBox.isSelected())
		{
			innerPanel.add(summaryPanel);
		}
		
		if(!displayCheckBox.isSelected() && summaryCheckBox.isSelected())
			activeSummary.getPanel().setPreferredSize(new Dimension(800, MAX_HEIGHT - 100));
		else
			activeSummary.getPanel().setPreferredSize(new Dimension(800, 200));
		
		ArrayList<String> labelsList = new ArrayList<String>();
		labelsList.addAll(savedLabels);
		if (limitGroup.getSelection().getActionCommand().equals("On"))
		{
			labelsList.addAll(intervalLabels);
		}

		activeDisplay.setLabels(labelsList);
		activeSummary.setLabels(labelsList);
		
		innerPanel.validate();
		innerPanel.repaint();
		
	}
	
	private ClickerConsumerInterface openPlugin(String plugin, Map<String, Class<?>> pluginMap, JPanel panel)
	{
		ClickerConsumerInterface cci = null;
		try 
		{
			// Creates a new instance of the consumer and starts it up
			cci = (ClickerConsumerInterface) pluginMap.get(plugin).newInstance();
			cci.setID(group);
			
			ArrayList<String> labelsList = new ArrayList<String>();
			labelsList.addAll(savedLabels);
			if (limitGroup.getSelection().getActionCommand().equals("On"))
			{
				labelsList.addAll(intervalLabels);
			}

			cci.setLabels(labelsList);
			cci.inputData(dataMap);
			
			if(!displayCheckBox.isSelected() && summaryCheckBox.isSelected())
				cci.getPanel().setPreferredSize(new Dimension(800, MAX_HEIGHT - 100));
			
			panel.removeAll();
			panel.add(cci.getPanel());
			panel.validate();
			panel.repaint();
		} 
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return cci;
	}
	
	public void processNewInput(String clientName, String values)
	{
		String[] splitAnswers = values.split(Constants.COMMA_SEPARATOR);
		ArrayList<String> tempList = new ArrayList<String>();
        for (int i=0; i < splitAnswers.length; i++) 
        {
            tempList.add(splitAnswers[i]);
        }
        
        if (!dataMap.containsKey(clientName))
        {
        	dataMap.put(clientName, new ArrayList<ArrayList<String>>());
        }
        dataMap.get(clientName).add(tempList);
        
        if (!control)
        {
        	updateInputA();
        }
        else
        {
        	updateInputB();
        }
	}
	
	private void updateInputA()
	{
		Map<String, ArrayList< ArrayList<String>>> tempDataMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		
		if (seriesGroup.getSelection().getActionCommand().equals("Last"))
		{
			for (String user : dataMap.keySet())
			{
				ArrayList<String> latestSet = dataMap.get(user).get(dataMap.get(user).size() - 1);
				
				ArrayList<ArrayList<String>> history = new ArrayList<ArrayList<String>>();
				
				if (limitGroup.getSelection().getActionCommand().equals("On"))
				{
					history.add(checkData(latestSet));
				}
				else
				{
					history.add(latestSet);
				}
				
				tempDataMap.put(user, history);
			}
		}
		else if (seriesGroup.getSelection().getActionCommand().equals("All"))
		{
			for (String user : dataMap.keySet())
			{
				ArrayList<ArrayList<String>> history = new ArrayList<ArrayList<String>>();
				
				if (limitGroup.getSelection().getActionCommand().equals("On"))
				{
					for (ArrayList<String> set : dataMap.get(user))
					{
						history.add(checkData(set));
					}
				}
				else
				{
					history = dataMap.get(user);
				}
				
				tempDataMap.put(user, history);
			}
		}
		else
		{
			System.out.println("Error in finding available command.");
		}
		
        activeDisplay.inputData(tempDataMap);
        activeSummary.inputData(tempDataMap);
	}
	
	private ArrayList<String> checkData(ArrayList<String> set)
	{
		ArrayList<String> newSet = new ArrayList<String>();
		
		for (String answer : set)
		{
			try
			{
				if (Double.valueOf(answer) >= minimum && Double.valueOf(answer) <= maximum)
	        	{
					newSet.add(Double.toString(Double.valueOf((answer))));
	        	}
			} catch(NumberFormatException nfe) {}
		}
		return newSet;
	}
	
	private void updateInputB()
	{
		Map<String, ArrayList< ArrayList<String>>> tempDataMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		
		for (String user : dataMap.keySet())
		{
			ArrayList<String> latestSet = dataMap.get(user).get(dataMap.get(user).size() - 1);
			
			ArrayList<ArrayList<String>> history = new ArrayList<ArrayList<String>>();

			history.add(latestSet);
			
			tempDataMap.put(user, history);
		}
		
		activeControl.inputData(tempDataMap);
	}
	
	public void close()
	{
		setVisible(false);
	}
	
	private class OpenDisplayActionListener implements ActionListener
	{
		private String displayToOpen;
		
		public OpenDisplayActionListener(String display)
		{
			displayToOpen = display;
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			activeDisplay = openPlugin(displayToOpen, availableDisplays, displayPanel);
			updateInputA();
		}
	}	
	
	private class OpenSummaryActionListener implements ActionListener
	{
		private String summaryToOpen;
		
		public OpenSummaryActionListener(String summary)
		{
			summaryToOpen = summary;
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			activeSummary = openPlugin(summaryToOpen, availableSummaries, summaryPanel);
			updateInputA();
		}
	}	
	
	private class UpdateActionListener implements ActionListener
	{
		
		public UpdateActionListener() {}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			updateInputA();
		}
	}	
	
	private class UpdatePanelsActionListener implements ActionListener
	{
		
		public UpdatePanelsActionListener() {}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			buildPanels();
		}
	}	
	
	private class LogPopupActionListener implements ActionListener
	{
		
		public LogPopupActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new LogPopupFrame();
		}
	}	
	
	public class LogPopupFrame extends JFrame
    {
    	private JTextField file;
    	private JTextField section;
    	
    	public LogPopupFrame()
    	{
    		super();
        	setTitle("Log Data");
        	setSize(220, 125);
        	setLocation(300, 300);
        	setResizable(false);
        	
        	setLayout(null);
    		
        	JLabel fileLabel = new JLabel("File Name: ");
        	fileLabel.setBounds(10, 10, fileLabel.getPreferredSize().width, fileLabel.getPreferredSize().height);
    		add(fileLabel);
    		
    		file = new JTextField();
    		file.setColumns(11);
    		file.setBounds(5 + fileLabel.getLocation().x + fileLabel.getSize().width, 10, file.getPreferredSize().width, file.getPreferredSize().height);
    		add(file);
    		
    		JLabel sectionLabel = new JLabel("Section: ");
    		sectionLabel.setBounds(10, 10 + fileLabel.getLocation().y + fileLabel.getSize().height, sectionLabel.getPreferredSize().width, sectionLabel.getPreferredSize().height);
    		add(sectionLabel);
    		
    		section = new JTextField("None");
    		section.setColumns(11);
    		section.setBounds(file.getLocation().x, 10 + fileLabel.getLocation().y + fileLabel.getSize().height, section.getPreferredSize().width, section.getPreferredSize().height);
    		add(section);
    		
    		JButton saveLog = new JButton("Save Log");
    		saveLog.addActionListener(new SaveLogActionListener());
    		saveLog.setBounds(110 - (saveLog.getPreferredSize().width / 2), 10 + sectionLabel.getLocation().y + sectionLabel.getSize().height, saveLog.getPreferredSize().width, saveLog.getPreferredSize().height);
    		add(saveLog);
    		
        	setVisible(true);
    	}
    	
    	private class SaveLogActionListener implements ActionListener
    	{
    		public SaveLogActionListener() {}
    		
    		@Override
    		public void actionPerformed(ActionEvent e) 
    		{
    			logger.saveLog(section.getText(), file.getText(), dataMap);
    			setVisible(false);
    		}
    	}
    	
    }
	
	private class LimitPopupActionListener implements ActionListener
	{
		
		public LimitPopupActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new LimitPopupFrame();
		}
	}	
	
	public class LimitPopupFrame extends JFrame
    {
    	private JTextField minEntry;
    	private JTextField maxEntry;
    	private JTextField intervalEntry;
    	private JCheckBox intervalBox;
    	
    	public LimitPopupFrame()
    	{
    		super();
        	setTitle("Limit Data");
        	setSize(220, 320);
        	setLocation(300, 300);
        	setResizable(false);

        	JPanel panel = new JPanel();
        	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        	
        	// Label
        	JPanel labelPanel = new JPanel();
        	labelPanel.setLayout(new GridLayout(3,2));
        	
        	labelPanel.add(new JLabel("Minimum: "));
        	minEntry = new JTextField("0");
        	minEntry.setColumns(6);
        	labelPanel.add(minEntry);
        	
        	labelPanel.add(new JLabel("Maximum: "));
        	maxEntry = new JTextField("10");
        	maxEntry.setColumns(6);
        	labelPanel.add(maxEntry);
        	panel.add(labelPanel);
        	//END
        	
        	// INTERVAL
        	JPanel intervalPanel = new JPanel();
        	intervalPanel.setLayout(new GridLayout(1,3));
        	
        	intervalBox = new JCheckBox();
        	intervalBox.setSelected(true);
        	intervalPanel.add(intervalBox);
        	
        	intervalPanel.add(new JLabel("Interval: "));
        	intervalEntry = new JTextField("1");
        	intervalEntry.setColumns(3);
        	intervalPanel.add(intervalEntry);
        	
        	panel.add(intervalPanel);
        	
        	// END
        	panel.add(Box.createRigidArea(new Dimension(0, 10)));
        	// Button Panel
        	JPanel buttonPanel = new JPanel();
        	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        	
        	
        	JButton okay = new JButton("Apply");
        	okay.addActionListener(new OkayActionListener());
        	buttonPanel.add(okay);

        	panel.add(buttonPanel);
        	// END
        	add(panel);
        	
        	setVisible(true);
    	}
    	
    	private class OkayActionListener implements ActionListener
		{
			
			public OkayActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				intervalLabels.clear();
				minimum = Double.valueOf(minEntry.getText());
				maximum = Double.valueOf(maxEntry.getText());
				
				if (intervalBox.isSelected())
				{
					Double x = minimum;
					Double y = maximum;
					int interval;
					
					try
					{
						interval = Integer.parseInt(intervalEntry.getText());
						if (interval == 0) interval = 1;
					}
					catch (NumberFormatException nfe) 
					{ 
						interval = 1;
					}
					
					while (x < y)
					{
						intervalLabels.add(x + "");
						x += interval;
					}
					intervalLabels.add(y + "");
				}
				
				updateInputA();
				setVisible(false);
			}
		}
    	
    }
}
