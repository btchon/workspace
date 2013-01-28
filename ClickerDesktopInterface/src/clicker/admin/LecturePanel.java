package clicker.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import clicker.constants.Constants;

public class LecturePanel extends JPanel
{
	private CommunicationHub hub;
	private LectureModel lectureModel;
	
	private static int LEFT_BOUND = 80;
	private static int PADDING = 15;
	
	private JComboBox lectureList;
	
	private JPanel loadPanel;
	private JLabel indexLabel;
	private QuestionSetPanel qSetPanel;
	
	public LecturePanel()
	{
		super();
		hub = CommunicationHub.getInstance();
		lectureModel = LectureModel.getInstance();
		
		hub.sendMessage(Constants.GET_QUESTION_SETS + Constants.GRAVE_SEPARATOR);
		try { Thread.sleep(1000); } catch(Exception e) {}
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		buildLoadPanel();
//		switchView(loadPanel);
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
		loadPanel.setLayout(null);
		
		// LABEL
		JLabel loadLabel = new JLabel("Select a question set to load:");
		loadLabel.setBounds(loadPanel.getLocation().x + LEFT_BOUND, loadPanel.getLocation().y + 40, loadLabel.getPreferredSize().width, loadLabel.getPreferredSize().height);
		loadPanel.add(loadLabel);
		// END

		JButton loadButton = new JButton("Load");
		
		// LIST
		if  (lectureModel.getAllSets() != null)
		{
			System.out.println("asdasdad");
			lectureList = new JComboBox(lectureModel.getAllSets());
			lectureList.setSelectedIndex(0);
		}
		else
		{
			lectureList = new JComboBox();
			loadButton.setEnabled(false);
		}
		lectureList.setBounds(loadPanel.getLocation().x + LEFT_BOUND, loadLabel.getLocation().y + 20, 250, lectureList.getPreferredSize().height);
		loadPanel.add(lectureList);
		// END
		
		// LOAD BUTTON
		// Load button created above to make sure it can be enabled.
		loadButton.addActionListener(new LoadActionListener());
		loadButton.setBounds(loadPanel.getLocation().x + LEFT_BOUND, lectureList.getLocation().y + 30, loadButton.getPreferredSize().width, loadButton.getPreferredSize().height);
		loadPanel.add(loadButton);
		
		add(loadPanel);
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
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		qSetPanel = new QuestionSetPanel();
		qSetPanel.buildPanels(lectureModel.getQuestionSet());
		qSetPanel.setBorder(BorderFactory.createTitledBorder(lectureModel.getAllSets()[lectureList.getSelectedIndex()]));
		panel.add(qSetPanel);
		
		panel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel navigationPanel = new JPanel();
		navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
		
        JButton buttonFirst = new JButton("|<");
        buttonFirst.addActionListener(new FirstActionListener());
        navigationPanel.add(buttonFirst);
        
        navigationPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        
        JButton buttonPrev = new JButton("<");
        buttonPrev.addActionListener(new PrevActionListener());
        navigationPanel.add(buttonPrev);
        
        navigationPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        
        indexLabel = new JLabel(qSetPanel.getIndex() + 1 + "");
        indexLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        indexLabel.setMinimumSize(new Dimension(30, 20));
        indexLabel.setMaximumSize(new Dimension(30, 20));
        indexLabel.setHorizontalAlignment( SwingConstants.CENTER );
        navigationPanel.add(indexLabel);
        
        navigationPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		
        JButton buttonNext = new JButton(">");
        buttonNext.addActionListener(new NextActionListener());
        navigationPanel.add(buttonNext);
        
        navigationPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        
        JButton buttonLast = new JButton(">|");
        buttonLast.addActionListener(new LastActionListener());
        navigationPanel.add(buttonLast);
        
        panel.add(navigationPanel);
		
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
        JButton buttonOpen = new JButton("Open");
        buttonOpen.addActionListener(new OpenActionListener());
		buttonPanel.add(buttonOpen);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		
		JButton buttonBack = new JButton("Back");
        buttonBack.addActionListener(new BackActionListener());
		buttonPanel.add(buttonBack);
		
		buttonPanel.add(Box.createRigidArea(new Dimension(30, 0)));
		
        JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(new CloseActionListener());
		buttonPanel.add(buttonClose);
		
        
		panel.add(buttonPanel);
		return panel;
	}
	
	private void refreshIndex()
	{
		indexLabel.setText(qSetPanel.getIndex() + 1 + "");
	}
	
	private class FirstActionListener implements ActionListener
	{

		public FirstActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			qSetPanel.first();
			refreshIndex();
		}
	}
	
	private class PrevActionListener implements ActionListener
	{

		public PrevActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			qSetPanel.prev();
			refreshIndex();
		}
	}
	
	private class NextActionListener implements ActionListener
	{

		public NextActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			qSetPanel.next();
			refreshIndex();
		}
	}
	
	private class LastActionListener implements ActionListener
	{

		public LastActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			qSetPanel.last();
			refreshIndex();
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
	
}


