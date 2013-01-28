package clicker.admin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import clicker.constants.Constants;

public class LecturePanel extends JPanel
{
	private CommunicationHub hub;
	private LectureModel lectureModel;
	
	private static int LEFT_BOUND = 80;
	private static int PADDING = 15;
	
	private JComboBox lectureList;
	
	private JPanel loadPanel;
	private QuestionSetPanel qSetPanel;
	
	public LecturePanel()
	{
		super();
		hub = CommunicationHub.getInstance();
		lectureModel = LectureModel.getInstance();
		
		hub.sendMessage(Constants.GET_QUESTION_SETS + Constants.GRAVE_SEPARATOR);
		try { Thread.sleep(1000); } catch(Exception e) {}
		
		buildLoadPanel();
		switchView(loadPanel);
	}
	
	public void switchView(JPanel panel)
	{
		removeAll();
		repaint();
		add(panel);
	}
	
	// LOAD PANEL
	public void buildLoadPanel()
	{
		loadPanel = new JPanel();
		loadPanel.setLayout(new BoxLayout(loadPanel, BoxLayout.Y_AXIS));
		
		loadPanel.add(Box.createRigidArea(new Dimension(0, PADDING + 10)));
		
		// LABEL
		JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		textPanel.add(Box.createRigidArea(new Dimension(LEFT_BOUND,0)));
		textPanel.add(new JLabel("Select a question set to load:"));
		
		loadPanel.add(textPanel);
		loadPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
		// END


		// LIST
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
		listPanel.add(Box.createRigidArea(new Dimension(LEFT_BOUND,0)));
		
		
		//Create the combo box, select item at index 4.
		//Indices start at 0, so 4 specifies the pig.
		if  (lectureModel.getAllSets() != null)
		{
			lectureList = new JComboBox(lectureModel.getAllSets());
			lectureList.setSelectedIndex(0);
		}
		else
		{
			lectureList = new JComboBox();
		}
	
		listPanel.add(lectureList);
		listPanel.add(Box.createRigidArea(new Dimension(300,0)));
		
		loadPanel.add(listPanel);
		loadPanel.add(Box.createRigidArea(new Dimension(0, PADDING)));
		// END
		
		// LOAD BUTTON
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		buttonPanel.add(Box.createRigidArea(new Dimension(LEFT_BOUND,0)));
		
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new LoadActionListener());
		buttonPanel.add(loadButton);
		
		loadPanel.add(buttonPanel);
		// END
		
		loadPanel.add(Box.createRigidArea(new Dimension(0,500)));
	}
	
	private class LoadActionListener implements ActionListener
	{

		public LoadActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			hub.sendMessage(Constants.GET_ALL_QUESTIONS + Constants.GRAVE_SEPARATOR + lectureModel.getAllSets()[lectureList.getSelectedIndex()]);
			try { Thread.sleep(1000); } catch(Exception j) {}
			switchView(buildQuestionSetPanel());
		}
	}
	// END
	
	// QUESTION SET PANEL
	public JPanel buildQuestionSetPanel()
	{
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new BorderLayout());
		qSetPanel = new QuestionSetPanel();
		qSetPanel.buildPanels(lectureModel.getQuestionSet());
		aPanel.add(qSetPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		
        JButton buttonPrev = new JButton("<");
        buttonPrev.addActionListener(new PrevActionListener());
		buttonPanel.add(buttonPrev);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        JButton buttonOpen = new JButton("Open");
        buttonOpen.addActionListener(new OpenActionListener());
		buttonPanel.add(buttonOpen);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(new CloseActionListener());
		buttonPanel.add(buttonClose);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        JButton buttonNext = new JButton(">");
        buttonNext.addActionListener(new NextActionListener());
		buttonPanel.add(buttonNext);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        JButton buttonBack = new JButton("Back");
        buttonBack.addActionListener(new BackActionListener());
		buttonPanel.add(buttonBack);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		
		aPanel.add(buttonPanel, BorderLayout.SOUTH);
		return aPanel;
	}
	
	private class PrevActionListener implements ActionListener
	{

		public PrevActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			qSetPanel.prev();
			validate();
		}
	}
	
	private class NextActionListener implements ActionListener
	{

		public NextActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			qSetPanel.next();
			validate();
		}
	}
	
	private class OpenActionListener implements ActionListener
	{

		public OpenActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			hub.sendQuestion(Constants.OPEN + Constants.SEMI_COLON_SEPARATOR + lectureModel.getQuestionSet()[qSetPanel.getIndex()]);
		}
	}
	
	private class CloseActionListener implements ActionListener
	{

		public CloseActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			hub.closeQuestion();
		}
	}
	
	private class BackActionListener implements ActionListener
	{

		public BackActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			buildLoadPanel();
			switchView(loadPanel);
		}
	}
	// END
	
}


