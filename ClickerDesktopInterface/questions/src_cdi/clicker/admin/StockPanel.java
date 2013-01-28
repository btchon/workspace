package clicker.admin;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;



public class StockPanel extends JPanel
{

	private CommunicationHub hub;
	
	public StockPanel()
	{
		super();
		hub = CommunicationHub.getInstance();
		setBorder(new EmptyBorder(15, 30, 10, 30));
        setLayout(new GridLayout(10, 1, 5, 5));
        
        JButton multipleChoiceOption = new JButton("Multiple Choice");
        multipleChoiceOption.addActionListener(new QuestionActionListener("Open`/;34`/;`/;B`/:A`/:0`/,B`/:B`/:0`/,B`/:C`/:0`/,B`/:D`/:0"));
        
        JButton trueFalseOption = new JButton("True/False");
        trueFalseOption.addActionListener(new QuestionActionListener("Open`/;40`/;`/;B`/:True`/:0`/,B`/:False`/:0"));
        
        JButton toggleOption = new JButton("Toggle");
        toggleOption.addActionListener(new QuestionActionListener("Open`/;35`/;`/;TOG`/:A`/:0`/,TOG`/:B`/:0`/,TOG`/:C`/:0`/,TOG`/:D`/:0"));
        
        JButton singleComboOption = new JButton("Single Combobox");
        singleComboOption.addActionListener(new QuestionActionListener("Open`/;37`/;A`/;COMBO`/:Combo 1`/:a`/~b`/~c`/~d`/:0"));
        
        JButton singleSliderOption = new JButton("Single Slider");
        singleSliderOption.addActionListener(new QuestionActionListener("Open`/;36`/;P`/;SLIDE`/:Slider 1`/:0`/:50`/:25"));
        
        JButton singleFreeText = new JButton("Single Free Text");
        singleFreeText.addActionListener(new QuestionActionListener("Open`/;38`/;`/;TEXTBOX`/:Enter text`/: "));
        
        JButton mouseControlOption = new JButton("Mouse Control");
        mouseControlOption.addActionListener(new QuestionActionListener("OpenClickPad`/;39`/;`/;TEXTBOX`/:Enter text`/: "));
        
        JButton spotlightControlOption = new JButton("Spotlight Control");
        spotlightControlOption.addActionListener(new QuestionActionListener("OpenSpotlight`/;41`/;`/;TEXTBOX`/:Enter text`/: "));
        
		JButton closeQuestionOption = new JButton("Close Question");
		closeQuestionOption.addActionListener(new QuestionActionListener("Close"));
		
		

		add(multipleChoiceOption);
		add(trueFalseOption);
		add(toggleOption);
		add(singleComboOption);
		add(singleSliderOption);
		add(singleFreeText);
		add(mouseControlOption);
		add(spotlightControlOption);
		add(new JLabel(" "));
		add(closeQuestionOption);
	}
	
	private class QuestionActionListener implements ActionListener
	{
		private String commandString;
		public QuestionActionListener(String s)
		{
			commandString = s;
		}
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (commandString.equals("Close"))
			{
				hub.closeQuestion();
			}
			else
				hub.sendQuestion(commandString);
		}
	}
}
