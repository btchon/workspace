package clicker.admin;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import clicker.constants.Constants;

public class QuestionBuilderGUI extends JPanel
{
	private CommunicationHub hub;
	private LectureModel lectureModel;
	
	// 1/3 Widget List
	private JPanel widgetListPanel;
	private JComboBox widgetComboBox;
	private JPanel parametersPanel;
	
	private String[] widgetNames = new String[]{"Textview", "Button", "Toggle", "Slider", "Textbox", "Rank C"};
	private Map<String, WidgetInterface> widgetMap;
	private WidgetInterface currentWidget;
	
	// 2/3 Question Preview
	private JPanel questionOptionsPanel;
	private JTextField nameQuestion;
	private JPanel previewPanel;
	private JComboBox consumerComboBox;
	private Map<String, String> consumerMap = new HashMap<String, String>();
	
	// ArrayList of the current widgets
	private ArrayList<WidgetInterface> currentWidgetList = new ArrayList<WidgetInterface>();
	
	// 3/3 Question Set
	private JPanel questionSetPanel;
	private JPanel setPanel;
	private JTextField nameQuestionSet;
	private JComboBox setsComboBox;
	
	// <Question Name, <Widgets>>
	private Map<String, ArrayList<WidgetInterface>> questionMap = new LinkedHashMap<String, ArrayList<WidgetInterface>>();
	
	// STATIC VALUES
	private static final int BOX_WIDTH = 236;
	private static final int BORDER = 10;
	private static final int TOP_BORDER = 20;
	private static final int DIVIDER_WIDTH = 5;
	
	
	public QuestionBuilderGUI()
	{
		super();
		hub = CommunicationHub.getInstance();
		lectureModel = LectureModel.getInstance();
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(new EmptyBorder(20, 20, 20, 20));
		
		widgetMap = new HashMap<String, WidgetInterface>();
		widgetMap.put("Button", new ButtonWidget());
		widgetMap.put("Textview", new TextviewWidget());
		widgetMap.put("Toggle", new ToggleWidget());
		widgetMap.put("Slider", new SliderWidget());
		widgetMap.put("Textbox", new TextboxWidget());
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
		addWidgetButton.setBounds(BORDER + 30 + widgetListPanel.getLocation().x, BORDER + parametersPanel.getLocation().y + parametersPanel.getSize().height, 
									BOX_WIDTH - 60 - BORDER, addWidgetButton.getPreferredSize().height);
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
			
			JButton editButton = new JButton("");
			editButton.setBounds(DIVIDER_WIDTH + item.getLocation().x + item.getSize().width, BORDER, 
					20, 20);
			editButton.addActionListener(new EditWidgetActionListener(widget));
			tempPanel.add(editButton);
			
			JButton removeButton = new JButton("");
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
			currentWidget = widgetToEdit;
			widgetComboBox.setSelectedItem(widgetToEdit.getType());
			updateParametersPanel();
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
			nameQuestion.setText("Question 1");
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
		
		nameQuestionSet = new JTextField("Set 1");
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
		
		JButton saveSetButton = new JButton("Save Set");
		saveSetButton.addActionListener(new SaveSetActionListener());
		saveSetButton.setBounds(BORDER + 30 + questionSetPanel.getLocation().x, BORDER + setPanel.getLocation().y + setPanel.getSize().height, 
										BOX_WIDTH - 60 - BORDER, saveSetButton.getPreferredSize().height);
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
			
			JLabel label = new JLabel(questionName);
			label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			label.setBounds(BORDER, BORDER, BOX_WIDTH - 90, 20);
			tempPanel.add(label);
			
			JButton editButton = new JButton("");
			editButton.setBounds(DIVIDER_WIDTH + label.getLocation().x + label.getSize().width, BORDER, 
					20, 20);
			editButton.addActionListener(new EditQuestionActionListener(questionName));
			tempPanel.add(editButton);
			
			JButton removeButton = new JButton("");
			removeButton.setBounds(DIVIDER_WIDTH + editButton.getLocation().x + editButton.getSize().width, BORDER, 
					20, 20);
			removeButton.addActionListener(new RemoveQuestionActionListener(questionName));
			tempPanel.add(removeButton);
			
			setPanel.add(tempPanel);
		}
		
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
			setPanel.removeAll();
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
			
			questionMap = new HashMap<String, ArrayList<WidgetInterface>>();
			System.out.println(tempString);
			hub.sendMessage(tempString);
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
		questionMap = new HashMap<String, ArrayList<WidgetInterface>>();
		
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
		updateSetPanel();
	}
	
	// END
	
	// WIDGETS
	
	public class ButtonWidget implements WidgetInterface
	{
		private JPanel panel;
		
		private JTextField widgetLabel = new JTextField();
		private ButtonGroup buttonGroup = new ButtonGroup();
		
		public ButtonWidget()
		{
			buildPanel("", false);
		}
		
		public ButtonWidget(String label, boolean tog)
		{
			buildPanel(label, tog);
		}
		
		private void buildPanel(String label, boolean tog)
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(2,2));
			
			panel.add(new JLabel("Label:"));
			widgetLabel.setColumns(9);
			widgetLabel.setText(label);
			panel.add(widgetLabel);
			
			JRadioButton on = new JRadioButton("On");
			on.setActionCommand("1");
			buttonGroup.add(on);
			panel.add(on);
			
			JRadioButton off = new JRadioButton("Off");
			off.setActionCommand("0");
			buttonGroup.add(off);
			panel.add(off);
			
			if (tog)
			{
				on.setSelected(true);
			}
			else
			{
				off.setSelected(true);
			}
		}
		
		@Override
		public JPanel getPanel() 
		{
			return panel;
		}

		@Override
		public String getValue() 
		{
			return "B" + Constants.COLON_SEPARATOR + 
			widgetLabel.getText() + Constants.COLON_SEPARATOR + 
			buttonGroup.getSelection().getActionCommand();
		}

		@Override
		public JComponent getComponent() 
		{
			return new JButton(widgetLabel.getText());
		}

		@Override
		public String getType() 
		{
			return "Button";
		}
	}
	
	public class TextboxWidget implements WidgetInterface
	{
		private JPanel panel;
		
		private JTextField widgetLabel = new JTextField();
		private JTextField widgetText = new JTextField();
		private ButtonGroup buttonGroup = new ButtonGroup();
		
		public TextboxWidget()
		{
			buildPanel("Enter text: ", "", "TEXT");
		}
		
		public TextboxWidget(String label, String text, String type)
		{
			buildPanel(label, text, type);
		}
		
		private void buildPanel(String label, String text, String type)
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(3,2));
			
			panel.add(new JLabel("Label:"));
			widgetLabel.setColumns(9);
			widgetLabel.setText(label);
			panel.add(widgetLabel);
			
			panel.add(new JLabel("Text:"));
			widgetText.setColumns(9);
			widgetText.setText(text);
			panel.add(widgetText);
			
			JRadioButton all = new JRadioButton("All");
			all.setActionCommand("TEXT");
			buttonGroup.add(all);
			panel.add(all);
			
			JRadioButton numeric = new JRadioButton("Numeric Only");
			numeric.setActionCommand("NUMERIC");
			buttonGroup.add(numeric);
			panel.add(numeric);
			
			if (type.equals("NUMERIC"))
			{
				numeric.setSelected(true);
			}
			else
			{
				all.setSelected(true);
			}
			
		}
		
		@Override
		public JPanel getPanel() 
		{
			return panel;
		}

		@Override
		public String getValue() 
		{
			return "TEXTBOX" + Constants.COLON_SEPARATOR + 
			widgetLabel.getText() + Constants.COLON_SEPARATOR + 
			widgetText.getText() + Constants.COLON_SEPARATOR + 
			buttonGroup.getSelection().getActionCommand();
		}

		@Override
		public JComponent getComponent() 
		{
			JPanel temp = new JPanel();
			temp.add(new JLabel(widgetLabel.getText()));
			JTextField field = new JTextField(widgetText.getText());
			field.setEditable(false);
			field.setColumns(6);
			temp.add(field);
			return temp;
		}

		@Override
		public String getType() 
		{
			return "Textbox";
		}
	}
	
	public class TextviewWidget implements WidgetInterface
	{
		private JPanel panel;
		private JTextField widgetLabel = new JTextField();
		
		public TextviewWidget()
		{
			buildPanel("");
		}
		
		public TextviewWidget(String label)
		{
			buildPanel(label);
		}
		
		private void buildPanel(String label)
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(1,2));
			
			panel.add(new JLabel("Label:"));
			widgetLabel.setColumns(9);
			widgetLabel.setText(label);
			panel.add(widgetLabel);
		}
		
		@Override
		public JPanel getPanel() 
		{
			return panel;
		}

		@Override
		public String getValue() 
		{
			String s;
			if (widgetLabel.getText().equals(""))
				s = " ";
			else
				s = widgetLabel.getText();
			
			return "TEXTVIEW" + Constants.COLON_SEPARATOR + s;
		}

		@Override
		public JComponent getComponent() 
		{
			return new JLabel(widgetLabel.getText());
		}

		@Override
		public String getType() 
		{
			return "Textview";
		}
	}
	
	public class ToggleWidget implements WidgetInterface
	{
		private JPanel panel;
		
		private JTextField widgetLabel = new JTextField();
		private ButtonGroup buttonGroup = new ButtonGroup();
		
		public ToggleWidget()
		{
			buildPanel("", false);
		}
		
		public ToggleWidget(String label, boolean tog)
		{
			buildPanel(label, tog);
		}
		
		private void buildPanel(String label, boolean tog)
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(2,2));
			
			panel.add(new JLabel("Label:"));
			widgetLabel.setColumns(9);
			widgetLabel.setText(label);
			panel.add(widgetLabel);
			
			JRadioButton on = new JRadioButton("On");
			on.setActionCommand("1");
			buttonGroup.add(on);
			panel.add(on);
			
			JRadioButton off = new JRadioButton("Off");
			off.setActionCommand("0");
			buttonGroup.add(off);
			panel.add(off);
			
			if (tog)
			{
				on.setSelected(true);
			}
			else
			{
				off.setSelected(true);
			}
		}
		
		@Override
		public JPanel getPanel() 
		{
			return panel;
		}

		@Override
		public String getValue() 
		{
			return "TOG" + Constants.COLON_SEPARATOR + 
			widgetLabel.getText() + Constants.COLON_SEPARATOR + 
			buttonGroup.getSelection().getActionCommand();
		}

		@Override
		public JComponent getComponent() 
		{
			return new JButton(widgetLabel.getText());
		}

		@Override
		public String getType() 
		{
			return "Toggle";
		}
	}
	
	public class SliderWidget implements WidgetInterface
	{
		private JPanel panel;
		
		private JTextField widgetLabel = new JTextField();
		private JTextField minField = new JTextField();
		private JTextField maxField = new JTextField();
		private JTextField initField = new JTextField();
		
		public SliderWidget()
		{
			buildPanel("", 0, 10, 0);
		}
		
		public SliderWidget(String label, int min, int max, int current)
		{
			buildPanel(label, min, max, current);
		}
		
		private void buildPanel(String label, int min, int max, int current)
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(4,2));
			
			panel.add(new JLabel("Label:"));
			widgetLabel.setColumns(9);
			widgetLabel.setText(label);
			panel.add(widgetLabel);
			
			panel.add(new JLabel("Min:"));
			minField.setColumns(9);
			minField.setText("" + min);
			panel.add(minField);
			
			panel.add(new JLabel("Max:"));
			maxField.setColumns(9);
			maxField.setText("" + max);
			panel.add(maxField);
			
			panel.add(new JLabel("Inital:"));
			initField.setColumns(9);
			initField.setText("" + current);
			panel.add(initField);
			
		}
		
		@Override
		public JPanel getPanel() 
		{
			return panel;
		}

		@Override
		public String getValue() 
		{
			String s;
			if (initField.getText().equals(""))
				s = minField.getText();
			else
				s = initField.getText();
			
			return "SLIDE" + Constants.COLON_SEPARATOR + 
			widgetLabel.getText() + Constants.COLON_SEPARATOR + 
			minField.getText() + Constants.COLON_SEPARATOR + 
			maxField.getText() + Constants.COLON_SEPARATOR + 
			initField.getText();
		}

		@Override
		public JComponent getComponent() 
		{
			return new JSlider(JSlider.HORIZONTAL, Integer.parseInt(minField.getText()), 
					Integer.parseInt(maxField.getText()), Integer.parseInt(initField.getText()));
		}

		@Override
		public String getType() 
		{
			return "Slider";
		}
	}
	
	public class RankCWidget implements WidgetInterface
	{
		private JPanel panel;
		
		private JTextField widgetLabel = new JTextField();
		private JComboBox comboBox;
		private JTextField enterField = new JTextField();
		
		public RankCWidget()
		{
			buildPanel("", new String[]{});
		}
		
		public RankCWidget(String label, String[] values)
		{
			buildPanel(label, values);
		}
		
		private void buildPanel(String label, String[] values)
		{
			panel = new JPanel();
			panel.setLayout(new GridLayout(4,2));
			
			panel.add(new JLabel("Label:"));
			widgetLabel.setColumns(9);
			widgetLabel.setText(label);
			panel.add(widgetLabel);
			
			panel.add(new JLabel("Candidates:"));
			comboBox = new JComboBox(values);
			comboBox.addActionListener(new ComboActionListener());
			panel.add(comboBox);
			
			panel.add(enterField);
			
			JButton addButton = new JButton("Add");
			addButton.addActionListener(new AddActionListener());
			panel.add(addButton);
			
			JButton deleteButton = new JButton("Del");
			deleteButton.addActionListener(new DeleteActionListener());
			panel.add(deleteButton);
		}
		
		private class ComboActionListener implements ActionListener
		{

			public ComboActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				enterField.setText((String)comboBox.getSelectedItem());
			}
		}
		
		private class AddActionListener implements ActionListener
		{

			public AddActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				comboBox.addItem(enterField.getText());
				enterField.setText("");
			}
		}
		
		private class DeleteActionListener implements ActionListener
		{

			public DeleteActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				comboBox.removeItem(enterField.getText());
				enterField.setText("");
			}
		}
		
		@Override
		public JPanel getPanel() 
		{
			return panel;
		}

		@Override
		public String getValue() 
		{
			String s = "RANKC" + Constants.COLON_SEPARATOR + widgetLabel.getText() + Constants.COLON_SEPARATOR;
			for (int i = 0; i < comboBox.getItemCount(); i++) 
			{
				if (i == 0)
					s += comboBox.getItemAt(i);
				else
					s += Constants.TILDE_SEPARATOR + comboBox.getItemAt(i);
			}
			return s;
		}

		@Override
		public JComponent getComponent() 
		{
			return comboBox;
		}

		@Override
		public String getType() 
		{
			return "Rank C";
		}
	}
	
	private WidgetInterface copyWidget(String value)
	{
		System.out.println(value);
		String[] valueArray = value.split(Constants.COLON_SEPARATOR);
		
		if (valueArray[0].equals("TEXTVIEW"))
		{
			return new TextviewWidget(valueArray[1]);
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
			return new TextboxWidget(valueArray[1], valueArray[2], valueArray[3]);
		}
		else if (valueArray[0].equals("RANKC"))
		{
			return new RankCWidget(valueArray[1], valueArray[2].split(Constants.TILDE_SEPARATOR));
		}
		else
		{
			return new TextviewWidget("Unknown Widget");
		}
	}
	
	// END

}
