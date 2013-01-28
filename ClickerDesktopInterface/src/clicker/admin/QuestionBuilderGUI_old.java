package clicker.admin;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import QBWidgets.*;


import clicker.constants.Constants;

public class QuestionBuilderGUI_old extends JPanel
{
	private CommunicationHub hub;
	private LectureModel lectureModel;
	
	// 1/3 Widget List
	private JPanel widgetListPanel;
	private JComboBox widgetComboBox;
	private JPanel parametersPanel;
	private JButton updateWidgetButton;
	
	private String[] widgetNames = new String[]{"Label", "Button", "Toggle", "Slider", "Textbox", "Numeric Textbox", "Rank C"};
	private Map<String, WidgetInterface> widgetMap;
	private WidgetInterface currentWidget;
	
	// 2/3 Question Preview
	private JPanel questionOptionsPanel;
	private JTextField nameQuestion;
	private JPanel previewPanel;
	private JComboBox consumerComboBox;
	private Map<String, String> consumerMap;
	
	// ArrayList of the current widgets
	private ArrayList<WidgetInterface> currentWidgetList = new ArrayList<WidgetInterface>();
	
	// 3/3 Question Set
	private JPanel questionSetPanel;
	private JPanel setPanel;
	private JTextField nameQuestionSet;
	private JComboBox setsComboBox;
	
	// <Question Name, <Widgets>>
	private Map<String, ArrayList<WidgetInterface>> questionMap;
	
	// STATIC VALUES
	private static final int BOX_WIDTH = 236;
	private static final int BORDER = 10;
	private static final int TOP_BORDER = 20;
	private static final int DIVIDER_WIDTH = 5;
	
	
	public QuestionBuilderGUI_old()
	{
		super();
		hub = CommunicationHub.getInstance();
		lectureModel = LectureModel.getInstance();
		
		consumerMap = Collections.synchronizedMap(new HashMap<String, String>());
		questionMap = Collections.synchronizedMap(new LinkedHashMap<String, ArrayList<WidgetInterface>>());
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new EmptyBorder(20, 20, 20, 20));
		
		widgetMap = new HashMap<String, WidgetInterface>();
		widgetMap.put("Button", new ButtonWidget());
		widgetMap.put("Label", new LabelWidget());
		widgetMap.put("Toggle", new ToggleWidget());
		widgetMap.put("Slider", new SliderWidget());
		widgetMap.put("Textbox", new TextboxWidget());
		widgetMap.put("Numeric Textbox", new NumericTextboxWidget());
		widgetMap.put("Rank C", new RankCWidget());
		
		currentWidget = widgetMap.get(widgetNames[0]);
		
		buildWidgetPanel();
		add(widgetListPanel);
		
		buildQuestionPreviewPanel();
		add(questionOptionsPanel);
		
		buildQuestionSetPanel();
		add(questionSetPanel);
		
	}
	
	// BUILDING THE WIDGET LIST PANEL
	
	private void buildWidgetPanel()
	{
		widgetListPanel = new JPanel();
		widgetListPanel.setLayout(null);
		widgetListPanel.setBorder(BorderFactory.createTitledBorder("Widget Options"));
		
		JLabel widgetLabel = new JLabel("Select a widget: ");
		widgetLabel.setBounds(BORDER + widgetListPanel.getLocation().x, TOP_BORDER + 5 + widgetListPanel.getLocation().y, 
								widgetLabel.getPreferredSize().width, widgetLabel.getPreferredSize().height);
		widgetListPanel.add(widgetLabel);
		
		widgetComboBox = new JComboBox(widgetNames);
		widgetComboBox.addActionListener(new SelectActionListener());
		widgetComboBox.setBounds(DIVIDER_WIDTH + widgetLabel.getLocation().x + widgetLabel.getSize().width, widgetListPanel.getLocation().y + TOP_BORDER,
									BOX_WIDTH - BORDER - DIVIDER_WIDTH - widgetLabel.getSize().width, widgetComboBox.getPreferredSize().height);
		widgetListPanel.add(widgetComboBox);
		
		parametersPanel = new JPanel();
		parametersPanel.setBounds(BORDER + widgetListPanel.getLocation().x, BORDER + widgetLabel.getLocation().y + widgetLabel.getSize().height, 
									BOX_WIDTH - BORDER + 3, 370);
		parametersPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		updateParametersPanel();
		widgetListPanel.add(parametersPanel);
		
		JButton addWidgetButton = new JButton("Add Widget");
		addWidgetButton.addActionListener(new AddWidgetActionListener());
		addWidgetButton.setBounds(BORDER + widgetListPanel.getLocation().x, BORDER + parametersPanel.getLocation().y + parametersPanel.getSize().height, 
				(BOX_WIDTH / 2) - BORDER, addWidgetButton.getPreferredSize().height);
		
		updateWidgetButton = new JButton("Update");
		updateWidgetButton.addActionListener(new UpdateWidgetActionListener());
		updateWidgetButton.setBounds(DIVIDER_WIDTH + addWidgetButton.getLocation().x + addWidgetButton.getSize().width, BORDER + parametersPanel.getLocation().y + parametersPanel.getSize().height, 
				BOX_WIDTH - BORDER - DIVIDER_WIDTH - addWidgetButton.getSize().width, updateWidgetButton.getPreferredSize().height);
		updateWidgetButton.setEnabled(false);
		widgetListPanel.add(updateWidgetButton);
		
		widgetListPanel.add(addWidgetButton);
	}
	
	private void updateParametersPanel()
	{
		parametersPanel.removeAll();
		parametersPanel.add(currentWidget.getPanel());
		widgetListPanel.validate();
		parametersPanel.repaint();
	}
	
	private class SelectActionListener implements ActionListener
	{

		public SelectActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			currentWidget = widgetMap.get((String) widgetComboBox.getSelectedItem());
			updateParametersPanel();
		}
	}
	
	private class AddWidgetActionListener implements ActionListener
	{

		public AddWidgetActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			currentWidgetList.add(copyWidget(currentWidget.getValue()));
			updatePreviewPanel();
		}
	}
	
	private class UpdateWidgetActionListener implements ActionListener
	{

		public UpdateWidgetActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			updatePreviewPanel();
			updateWidgetButton.setEnabled(false);
		}
	}
	// END
	
	// BUILDING THE QUESTION PREVIEW PANEL
	
	private void buildQuestionPreviewPanel()
	{
		
		questionOptionsPanel = new JPanel();
		questionOptionsPanel.setLayout(null);
		questionOptionsPanel.setBorder(BorderFactory.createTitledBorder("Question Options"));
		
		JLabel nameLabel = new JLabel("ID:       ");
		nameLabel.setBounds(BORDER + questionOptionsPanel.getLocation().x, TOP_BORDER + questionOptionsPanel.getLocation().y, 
								nameLabel.getPreferredSize().width, nameLabel.getPreferredSize().height);
		questionOptionsPanel.add(nameLabel);
		
		nameQuestion = new JTextField("Question 1");
		nameQuestion.setColumns(10);
		nameQuestion.setBounds(DIVIDER_WIDTH + nameLabel.getLocation().x + nameLabel.getSize().width, TOP_BORDER + questionOptionsPanel.getLocation().y, 
								BOX_WIDTH - BORDER - DIVIDER_WIDTH - nameLabel.getSize().width, nameQuestion.getPreferredSize().height);
		questionOptionsPanel.add(nameQuestion);
		
		JLabel consumerLabel = new JLabel("Default Consumer:  ");
		consumerLabel.setBounds(BORDER + questionOptionsPanel.getLocation().x, BORDER + 5 + nameLabel.getLocation().y + nameLabel.getSize().height, 
									consumerLabel.getPreferredSize().width, consumerLabel.getPreferredSize().height);
		questionOptionsPanel.add(consumerLabel);
		
		
		if  (hub.getConsumerList() != null)
		{
			consumerComboBox = new JComboBox(hub.getConsumerList());
			consumerComboBox.setSelectedIndex(0);
		}
		else
		{
			consumerComboBox = new JComboBox();
		}
		consumerComboBox.setBounds(DIVIDER_WIDTH + consumerLabel.getLocation().x + consumerLabel.getSize().width, BORDER + nameLabel.getLocation().y + nameLabel.getSize().height,
									BOX_WIDTH - BORDER - DIVIDER_WIDTH - consumerLabel.getSize().width, consumerComboBox.getPreferredSize().height);
		questionOptionsPanel.add(consumerComboBox);
		
		previewPanel = new JPanel();
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
		previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
		previewPanel.setBounds(BORDER + questionOptionsPanel.getLocation().x, BORDER + consumerLabel.getLocation().y + consumerLabel.getSize().height,
									BOX_WIDTH - BORDER + 3, 343);
		questionOptionsPanel.add(previewPanel);
		
		JButton addQuestionButton = new JButton("Add Question");
		addQuestionButton.addActionListener(new AddQuestionActionListener());
		addQuestionButton.setBounds(BORDER + 30 + questionOptionsPanel.getLocation().x, BORDER + previewPanel.getLocation().y + previewPanel.getSize().height, 
										BOX_WIDTH - 60 - BORDER, addQuestionButton.getPreferredSize().height);
		questionOptionsPanel.add(addQuestionButton);
	}
	
	private void updatePreviewPanel()
	{
    	
		boolean textCheck = false;
		previewPanel.removeAll();
		for (WidgetInterface widget : currentWidgetList)
		{
			JPanel tempPanel = new JPanel();
			tempPanel.setLayout(null);
			
			JComponent item = widget.getComponent();
			item.setBounds(BORDER, BORDER, BOX_WIDTH - 90, 20);
			tempPanel.add(item);
			
			JButton editButton = new JButton("E");
			editButton.setFont(new Font("Serif", Font.PLAIN, 10));
			editButton.setMargin(new Insets(0, 0, 0, 0));
			editButton.setBounds(DIVIDER_WIDTH + item.getLocation().x + item.getSize().width, BORDER, 
					20, 20);
			editButton.addActionListener(new EditWidgetActionListener(widget));
			tempPanel.add(editButton);
			
			JButton removeButton = new JButton("X");
			removeButton.setFont(new Font("Serif", Font.PLAIN, 10));
			removeButton.setMargin(new Insets(0, 0, 0, 0));
			removeButton.setBounds(DIVIDER_WIDTH + editButton.getLocation().x + editButton.getSize().width, BORDER, 
					20, 20);
			removeButton.addActionListener(new RemoveWidgetActionListener(widget));
			tempPanel.add(removeButton);
			
			if (widget.getType().equals("Textbox"))
				textCheck = true;
			
			previewPanel.add(tempPanel);
		}
		if (textCheck)
		{
			previewPanel.add(new JButton("Submit"));
		}
		questionOptionsPanel.validate();
		questionOptionsPanel.repaint();
	}
	
	private class EditWidgetActionListener implements ActionListener
	{
		WidgetInterface widgetToEdit;

		public EditWidgetActionListener(WidgetInterface widget) 
		{
			widgetToEdit = widget;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			widgetComboBox.setSelectedItem(widgetToEdit.getType());
			currentWidget = widgetToEdit;
			updateParametersPanel();
			updateWidgetButton.setEnabled(true);
		}
	}
	
	private class RemoveWidgetActionListener implements ActionListener
	{
		WidgetInterface widgetToDelete;

		public RemoveWidgetActionListener(WidgetInterface widget) 
		{
			widgetToDelete = widget;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			currentWidgetList.remove(widgetToDelete);
			updatePreviewPanel();
		}
	}
	
	private class AddQuestionActionListener implements ActionListener
	{

		public AddQuestionActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			
			questionMap.put(nameQuestion.getText(), currentWidgetList);
			consumerMap.put(nameQuestion.getText(), (String) consumerComboBox.getSelectedItem());
			updateSetPanel();
			
			currentWidgetList = new ArrayList<WidgetInterface>();
			updatePreviewPanel();
		}
	}
	
	private void nameCheck()
	{
		nameQuestion.setText("Question 1");
    	int n = 1;
    	
    	while (true)
    	{
    		if (questionMap.keySet().contains("Question " + n))
    		{
    			n++;
    		}
    		else
    		{
    			nameQuestion.setText("Question " + n);
    			break;
    		}
    	}
	}
	
	// END
	
	// BUILDING THE QUESTION SET PANEL
	
	private void buildQuestionSetPanel()
	{
		questionSetPanel = new JPanel();
		questionSetPanel.setLayout(null);
		questionSetPanel.setBorder(BorderFactory.createTitledBorder("Set Options"));
		
		JLabel setLabel = new JLabel("Name: ");
		setLabel.setBounds(BORDER + questionSetPanel.getLocation().x, TOP_BORDER + questionSetPanel.getLocation().y, 
							setLabel.getPreferredSize().width, setLabel.getPreferredSize().height);
		questionSetPanel.add(setLabel);
		
		nameQuestionSet = new JTextField("");
		nameQuestionSet.setColumns(10);
		nameQuestionSet.setBounds(DIVIDER_WIDTH + setLabel.getLocation().x + setLabel.getSize().width, TOP_BORDER + questionSetPanel.getLocation().y, 
								BOX_WIDTH - BORDER - DIVIDER_WIDTH - setLabel.getSize().width, nameQuestionSet.getPreferredSize().height);
		questionSetPanel.add(nameQuestionSet);
		
		setPanel = new JPanel();
		setPanel.setLayout(new BoxLayout(setPanel, BoxLayout.Y_AXIS));
		setPanel.setBorder(BorderFactory.createTitledBorder("Set"));
		setPanel.setBounds(BORDER + questionSetPanel.getLocation().x, BORDER + setLabel.getLocation().y + setLabel.getSize().height,
									BOX_WIDTH - BORDER + 3, 300);
		questionSetPanel.add(setPanel);
		
		JButton newSetButton = new JButton("New");
		newSetButton.addActionListener(new NewSetActionListener());
		newSetButton.setBounds(BORDER + questionSetPanel.getLocation().x, BORDER + setPanel.getLocation().y + setPanel.getSize().height, 
										(BOX_WIDTH / 2) - BORDER, newSetButton.getPreferredSize().height);
		questionSetPanel.add(newSetButton);
		
		JButton saveSetButton = new JButton("Save");
		saveSetButton.addActionListener(new SaveSetActionListener());
		saveSetButton.setBounds(DIVIDER_WIDTH + newSetButton.getLocation().x + newSetButton.getSize().width, BORDER + setPanel.getLocation().y + setPanel.getSize().height, 
										BOX_WIDTH - BORDER - DIVIDER_WIDTH - newSetButton.getSize().width, saveSetButton.getPreferredSize().height);
		questionSetPanel.add(saveSetButton);
		

		
		updateComboBox();
		setsComboBox.setBounds(BORDER + questionSetPanel.getLocation().x, BORDER + saveSetButton.getLocation().y + saveSetButton.getSize().height,
									BOX_WIDTH - BORDER - 70, setsComboBox.getPreferredSize().height);
		questionSetPanel.add(setsComboBox);
		
		JButton loadSetButton = new JButton("Load");
		loadSetButton.addActionListener(new LoadSetActionListener());
		loadSetButton.setBounds(DIVIDER_WIDTH + setsComboBox.getLocation().x + setsComboBox.getSize().width, BORDER + saveSetButton.getLocation().y + saveSetButton.getSize().height,
								BOX_WIDTH - BORDER - DIVIDER_WIDTH - setsComboBox.getSize().width, loadSetButton.getPreferredSize().height);
		questionSetPanel.add(loadSetButton);
	}

	private void updateSetPanel()
	{
		setPanel.removeAll();
		for (String questionName : questionMap.keySet())
		{
			JPanel tempPanel = new JPanel();
			tempPanel.setLayout(null);
			
			JLabel label = new JLabel(" " + questionName);
			label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			label.setBounds(BORDER, BORDER, BOX_WIDTH - 90, 20);
			tempPanel.add(label);
			
			JButton editButton = new JButton("E");
			editButton.setFont(new Font("Serif", Font.PLAIN, 10));
			editButton.setMargin(new Insets(0, 0, 0, 0));
			editButton.setBounds(DIVIDER_WIDTH + label.getLocation().x + label.getSize().width, BORDER, 
					20, 20);
			editButton.addActionListener(new EditQuestionActionListener(questionName));
			tempPanel.add(editButton);
			
			JButton removeButton = new JButton("X");
			removeButton.setFont(new Font("Serif", Font.PLAIN, 10));
			removeButton.setMargin(new Insets(0, 0, 0, 0));
			removeButton.setBounds(DIVIDER_WIDTH + editButton.getLocation().x + editButton.getSize().width, BORDER, 
					20, 20);
			removeButton.addActionListener(new RemoveQuestionActionListener(questionName));
			tempPanel.add(removeButton);
			
			setPanel.add(tempPanel);
		}
		nameCheck();
		questionSetPanel.validate();
		questionSetPanel.repaint();
	}
	
	private void updateComboBox()
	{
		if (lectureModel.getAllSets() != null)
		{
			setsComboBox = new JComboBox(lectureModel.getAllSets());
			setsComboBox.setSelectedIndex(0);
		}
		else
		{
			setsComboBox = new JComboBox();
		}
	}
	
	private class EditQuestionActionListener implements ActionListener
	{
		String questionToEdit;

		public EditQuestionActionListener(String question) 
		{
			questionToEdit = question;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			currentWidgetList = questionMap.get(questionToEdit);
			nameQuestion.setText(questionToEdit);
			
			consumerComboBox.setSelectedItem(consumerMap.get(questionToEdit));
			
			updatePreviewPanel();

		}
	}
	
	private class RemoveQuestionActionListener implements ActionListener
	{
		String questionToDelete;

		public RemoveQuestionActionListener(String question) 
		{
			questionToDelete = question;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			questionMap.remove(questionToDelete);
			updateSetPanel();
		}
	}
	
	private class SaveSetActionListener implements ActionListener
	{

		public SaveSetActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (!nameQuestionSet.getText().trim().equals(""))
			{
				String questionSetString = "";
				int qCount = 0;
				for (String questionID : questionMap.keySet())
				{
					String consumer;
					if (consumerMap.get(questionID).equals("None"))
						consumer = "";
					else
						consumer = consumerMap.get(questionID);
					
					if (qCount == 0)
					{
						questionSetString += questionID + Constants.SEMI_COLON_SEPARATOR + 
												consumer + Constants.SEMI_COLON_SEPARATOR;
						qCount = 1;
					}
					else
					{
						questionSetString += Constants.SEMI_COLON_SEPARATOR + Constants.AMPERSAND_SEPARATOR +
												questionID + Constants.SEMI_COLON_SEPARATOR + 
												consumer + Constants.SEMI_COLON_SEPARATOR;
					}
					
					int wCount = 0;
					for (WidgetInterface widget : questionMap.get(questionID))
					{
						if (wCount == 0)
						{
							questionSetString += widget.getValue();
							wCount = 1;
						}
						else
						{
							questionSetString += Constants.COMMA_SEPARATOR + widget.getValue();
						}
					}
				}
				
				String tempString = Constants.ADD_QUESTION_SET + Constants.GRAVE_SEPARATOR + Constants.GRAVE_SEPARATOR + 
									nameQuestionSet.getText() + Constants.AT_SEPARATOR + 
									questionSetString;
				System.out.println(tempString);
				hub.sendMessage(tempString);
			}
		}
	}
	
	private class NewSetActionListener implements ActionListener
	{

		public NewSetActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			nameQuestionSet.setText("");
			questionMap.clear();
			currentWidgetList = new ArrayList<WidgetInterface>();
			updateSetPanel();
			updatePreviewPanel();
		}
	}
	
	private class LoadSetActionListener implements ActionListener
	{

		public LoadSetActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			hub.sendMessage(Constants.GET_ALL_QUESTIONS + Constants.GRAVE_SEPARATOR + lectureModel.getAllSets()[setsComboBox.getSelectedIndex()]);
			try { Thread.sleep(100); } catch(Exception j) {}
			loadSet(lectureModel.getQuestionSet());
		}
	}
	
	private void loadSet(String[] allQArray)
	{
		nameQuestionSet.setText(lectureModel.getAllSets()[setsComboBox.getSelectedIndex()]);
		questionMap.clear();
		
		for (int i=0; i < allQArray.length; i++) 
        {
			currentWidgetList = new ArrayList<WidgetInterface>();
            String[] parts = allQArray[i].split(Constants.SEMI_COLON_SEPARATOR);
            String questionName = parts[0];
            String consumer = parts[1];
            String[] widgets = parts[2].split(Constants.COMMA_SEPARATOR);

            for (String widget : widgets) 
            {
            	currentWidgetList.add(copyWidget(widget));
            }
            
            questionMap.put(questionName, currentWidgetList);
            if (consumer.equals(""))
            	consumerMap.put(questionName, "None");
            else
            	consumerMap.put(questionName, consumer);
        }
		currentWidgetList = new ArrayList<WidgetInterface>();
		updateSetPanel();
		
	}
	
	private WidgetInterface copyWidget(String value)
	{
		System.out.println(value);
		String[] valueArray = value.split(Constants.COLON_SEPARATOR);
		
		if (valueArray[0].equals("TEXTVIEW"))
		{
			return new LabelWidget(valueArray[1]);
		}
		else if (valueArray[0].equals("B"))
		{
			boolean tog;
			if(valueArray[2].equals("0"))
				tog = false;
			else
				tog = true;
			return new ButtonWidget(valueArray[1], tog);
		}
		else if (valueArray[0].equals("TOG"))
		{
			boolean tog;
			if(valueArray[2].equals("0"))
				tog = false;
			else
				tog = true;
			return new ToggleWidget(valueArray[1], tog);
		}
		else if (valueArray[0].equals("SLIDE"))
		{
			return new SliderWidget(valueArray[1], Integer.parseInt(valueArray[2]), 
					Integer.parseInt(valueArray[3]), Integer.parseInt(valueArray[4]));
		}
		else if (valueArray[0].equals("TEXTBOX"))
		{
			return new TextboxWidget(valueArray[1]);
		}
		else if (valueArray[0].equals("NUMERICTEXTBOX"))
		{
			return new NumericTextboxWidget(valueArray[1], valueArray[2], Integer.parseInt(valueArray[3]), Integer.parseInt(valueArray[4]));
		}
		else if (valueArray[0].equals("RANKC"))
		{
			return new RankCWidget(valueArray[1], valueArray[2].split(Constants.TILDE_SEPARATOR));
		}
		else
		{
			return new LabelWidget("Unknown Widget");
		}
	}
	
	// END

}
