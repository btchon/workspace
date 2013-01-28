
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import clicker.constants.Constants;


public class ConsumerGUI extends JFrame
{
	private Map<String, Class<?>> availableDisplays;
	private Map<String, Class<?>> availableSummaries;
	private ClickerConsumerInterface activeDisplay;
	private ClickerConsumerInterface activeSummary;
	
	private String group;
	private String question;
	private Map<String, ArrayList< ArrayList<String>>> dataMap;
	
	private JPanel displayPanel;
	private JPanel summaryPanel;
	
	private static int MAX_WIDTH = 750;
	private static int MAX_HEIGHT = 660;
	
	public ConsumerGUI(String group, String question, String defaultDisplay, 
			Map<String, Class<?>> availableDisplays, Map<String, Class<?>> availableSummaries)
	{
		
		super();
		setLayout(null);
		this.getContentPane().setBackground(Color.white);
    	setTitle(group);
    	setSize(MAX_WIDTH, MAX_HEIGHT);
    	dataMap = Collections.synchronizedMap(new HashMap<String, ArrayList< ArrayList<String>>>());
    	
    	this.availableDisplays = availableDisplays;
    	this.availableSummaries = availableSummaries;
    	this.group = group;
    	this.question = question;
    	
    	buildGUI();
    	setVisible(true);
	}
	
	private void buildGUI()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu displayMenu = new JMenu("Display");
		menuBar.add(displayMenu);
		
		for (String display : availableDisplays.keySet())
		{
			 JMenuItem item = new JMenuItem(display);
			 item.addActionListener(new OpenDisplayActionListener(display));
			 displayMenu.add(item);
		}
		
		JMenu summaryMenu = new JMenu("Summary");
		menuBar.add(summaryMenu);
		
		for (String summary : availableSummaries.keySet())
		{
			 JMenuItem item = new JMenuItem(summary);
			 item.addActionListener(new OpenSummaryActionListener(summary));
			 summaryMenu.add(item);
		}
		
		
		setJMenuBar(menuBar);
		
		displayPanel = new JPanel();
		displayPanel.setBackground(Color.white);
		displayPanel.setBounds(0, 0, MAX_WIDTH - 20, 470);
		displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
		openDisplayPlugin("JFreeBarGraph");
		add(displayPanel);
		
		summaryPanel = new JPanel();
		summaryPanel.setBackground(Color.white);
		summaryPanel.setBounds(displayPanel.getLocation().x, 
							   displayPanel.getLocation().y + displayPanel.getSize().height, 
							   MAX_WIDTH - 20, MAX_HEIGHT - displayPanel.getSize().height - 62);

		summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
		openSummaryPlugin("Data Table");
		add(summaryPanel);
		
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
			openDisplayPlugin(displayToOpen);
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
			openSummaryPlugin(summaryToOpen);
		}
	}	
	
	private void openDisplayPlugin(String display)
	{
		ClickerConsumerInterface cci = null;
		try 
		{
			// Creates a new instance of the consumer and starts it up
			cci = (ClickerConsumerInterface) availableDisplays.get(display).newInstance();
			cci.setID(group);
			cci.setQuestion(question);
			activeDisplay = cci;
			activeDisplay.inputData(dataMap);
			
			displayPanel.removeAll();
			displayPanel.add(cci.getPanel());
			displayPanel.validate();
			displayPanel.repaint();
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
	
	private void openSummaryPlugin(String summary)
	{
		ClickerConsumerInterface cci = null;
		try 
		{
			// Creates a new instance of the consumer and starts it up
			cci = (ClickerConsumerInterface) availableSummaries.get(summary).newInstance();
			cci.setID(group);
			cci.setQuestion(question);
			activeSummary = cci;
			activeSummary.inputData(dataMap);
			
			summaryPanel.removeAll();
			summaryPanel.add(cci.getPanel());
			summaryPanel.validate();
			summaryPanel.repaint();
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
        
        activeDisplay.inputData(dataMap);
        activeSummary.inputData(dataMap);
	}
	
}
