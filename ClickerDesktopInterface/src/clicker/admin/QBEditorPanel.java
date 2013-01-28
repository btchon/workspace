package clicker.admin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


import QBWidgets.ButtonWidget;
import QBWidgets.LabelWidget;
import QBWidgets.NumericTextboxWidget;
import QBWidgets.RankCWidget;
import QBWidgets.SliderWidget;
import QBWidgets.TextboxWidget;
import QBWidgets.ToggleWidget;
import QBWidgets.WidgetInterface;

public class QBEditorPanel extends JPanel
{
	private static final int BOX_WIDTH = 360;
	private static final int BORDER = 10;
	private static final int TOP_BORDER = 20;
	private static final int DIVIDER_WIDTH = 5;
	
	private String[] widgetNames = new String[]{"Label", "Button", "Toggle", "Slider", "Textbox", "Numeric Textbox", "Rank C"};
	private Map<String, WidgetInterface> widgetMap;
	
	private JPanel setPanel;
	private JTextField setName;
	
	
	public QBEditorPanel()
	{
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		widgetMap = new HashMap<String, WidgetInterface>();
		widgetMap.put("Button", new ButtonWidget());
		widgetMap.put("Label", new LabelWidget());
		widgetMap.put("Toggle", new ToggleWidget());
		widgetMap.put("Slider", new SliderWidget());
		widgetMap.put("Textbox", new TextboxWidget());
		widgetMap.put("Numeric Textbox", new NumericTextboxWidget());
		widgetMap.put("Rank C", new RankCWidget());
		
		add(buildSetPanel());
	}
	
	private JPanel buildSetPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JLabel setLabel = new JLabel("Set Name: ");
		setLabel.setBounds((2 * BORDER) + panel.getLocation().x, TOP_BORDER + panel.getLocation().y, 
							setLabel.getPreferredSize().width, setLabel.getPreferredSize().height);
		panel.add(setLabel);
		
		setName = new JTextField("");
		setName.setColumns(20);
		setName.setBounds(DIVIDER_WIDTH + setLabel.getLocation().x + setLabel.getSize().width, TOP_BORDER + panel.getLocation().y, 
								setName.getPreferredSize().width, setName.getPreferredSize().height);
		panel.add(setName);
		
		setPanel = new JPanel();
		setPanel.setLayout(new BoxLayout(setPanel, BoxLayout.Y_AXIS));
		  
		for(int i = 0; i < 3; i++) {  
		setPanel.add(new JButton("Button " + i));  
		}  
		JScrollPane scrollPane = new JScrollPane(setPanel);
		scrollPane.setBounds(BORDER + panel.getLocation().x, BORDER + setLabel.getLocation().y + setLabel.getSize().height,
				BOX_WIDTH - BORDER + 3, 400);
		panel.add(scrollPane);
		
		return panel;
	}
	
	public void switchView(JPanel panel)
	{
		removeAll();
		repaint();
		add(panel);
	}
	
	
	
	
	
	
	
	
	
	
	
}
