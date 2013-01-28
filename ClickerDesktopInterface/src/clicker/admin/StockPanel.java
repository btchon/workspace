package clicker.admin;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
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
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(15, 30, 10, 30));
		
		JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        
        JButton multipleChoiceOption = new JButton("Multiple Choice");
        multipleChoiceOption.addActionListener(new QuestionActionListener("Open`/;34`/;`/;B`/:A`/:0`/,B`/:B`/:0`/,B`/:C`/:0`/,B`/:D`/:0"));
        
        JButton trueFalseOption = new JButton("True/False");
        trueFalseOption.addActionListener(new QuestionActionListener("Open`/;40`/;`/;B`/:True`/:0`/,B`/:False`/:0"));
        
        JButton toggleOption = new JButton("Toggle");
        toggleOption.addActionListener(new QuestionActionListener("Open`/;35`/;`/;TOG`/:A`/:0`/,TOG`/:B`/:0`/,TOG`/:C`/:0`/,TOG`/:D`/:0"));
        
        JButton singleComboOption = new JButton("Single Combobox");
        singleComboOption.addActionListener(new QuestionActionListener("Open`/;37`/;A`/;COMBO`/:Combo 1`/:a`/~b`/~c`/~d`/:0"));
        
        JButton singleSliderOption = new JButton("Single Slider");
        singleSliderOption.addActionListener(new QuestionActionListener("Open`/;36`/;`/;SLIDE`/:Slider`/:0`/:10`/:0"));
        
        JButton textboxOption = new JButton("Textbox");
        textboxOption.addActionListener(new QuestionActionListener("Open`/;38`/;`/;TEXTBOX`/:Enter text:`/;TEXT"));
        
        JButton numericTextboxOption = new JButton("Numeric Textbox");
        numericTextboxOption.addActionListener(new QuestionActionListener("Open`/;38`/;`/;NUMERICTEXTBOX`/:Enter number: `/:UNBOUNDED`/:0`/:0"));
        
        JButton mouseControlOption = new JButton("Mouse Control");
        mouseControlOption.addActionListener(new QuestionActionListener("OpenClickPad`/;39`/;MouseControl`/;TEXTBOX`/:Enter text`/:TEXT"));
        
        JButton spotlightControlOption = new JButton("Spotlight Control");
        spotlightControlOption.addActionListener(new QuestionActionListener("OpenSpotlight`/;41`/;Spotlight`/;TEXTBOX`/:Enter text`/:TEXT"));
        
        JButton weightedObjectsOption = new JButton("Weighted Objects");
        weightedObjectsOption.addActionListener(new QuestionActionListener("OpenClickPad`/;42`/;WeightedObject`/;TEXTBOX`/:Enter text`/:TEXT"));
//        
//        JButton fittsTestOption = new JButton("Fitts Test");
//        fittsTestOption.addActionListener(new QuestionActionListener("OpenClickPad`/;43`/;FittsTest`/;TEXTBOX`/:Enter text`/:TEXT"));
        
		JButton closeQuestionOption = new JButton("Close Question");
		closeQuestionOption.addActionListener(new QuestionActionListener("Close"));
		
		panel.add(multipleChoiceOption);
		panel.add(trueFalseOption);
		panel.add(toggleOption);
		panel.add(singleComboOption);
		panel.add(singleSliderOption);
		panel.add(textboxOption);
		panel.add(numericTextboxOption);
		panel.add(mouseControlOption);
		panel.add(spotlightControlOption);
		panel.add(weightedObjectsOption);
//		panel.add(fittsTestOption);
		
		add(panel);
		JPanel closePanel = new JPanel();
		closePanel.add(closeQuestionOption);
		add(closePanel);
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
