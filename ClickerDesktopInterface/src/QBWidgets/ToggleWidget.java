package QBWidgets;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import clicker.constants.Constants;

public class ToggleWidget implements WidgetInterface
{
	private JPanel panel;
	
	private JTextArea widgetLabel = new JTextArea();
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
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createTitledBorder("Toggle Label"));
		widgetLabel = new JTextArea(label, 4, 16);
		widgetLabel.setLineWrap(true);
		widgetLabel.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(widgetLabel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		labelPanel.add(scrollPane);
		panel.add(labelPanel);
		
		JPanel togglePanel = new JPanel();
		togglePanel.setLayout(new GridLayout(1, 2));
		togglePanel.setBorder(BorderFactory.createTitledBorder("Toggle"));
		JRadioButton on = new JRadioButton("On");
		on.setActionCommand("1");
		buttonGroup.add(on);
		togglePanel.add(on);
		
		JRadioButton off = new JRadioButton("Off");
		off.setActionCommand("0");
		buttonGroup.add(off);
		togglePanel.add(off);
		panel.add(togglePanel);
		
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
		String tog;
		if (buttonGroup.getSelection().getActionCommand().equals("0"))
		{
			tog = "Off";
		}
		else
		{
			tog = "On";
		}
		
		JComponent component  = new JToggleButton(widgetLabel.getText());
		String text = "<html>Type: Toggle <br>Text: " + widgetLabel.getText() +
			"<br>Toggle: " + tog + "</html>";  
		component.setToolTipText(text);
		return component;
	}

	@Override
	public String getType() 
	{
		return "Toggle";
	}
}