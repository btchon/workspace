package QBWidgets;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clicker.constants.Constants;

public class TextboxWidget implements WidgetInterface
{
	private JPanel panel;
	
	private JTextArea widgetLabel = new JTextArea();
	
	public TextboxWidget()
	{
		buildPanel("");
	}
	
	public TextboxWidget(String label)
	{
		buildPanel(label);
	}
	
	private void buildPanel(String label)
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createTitledBorder("Textbox Label"));
		widgetLabel = new JTextArea(label, 4, 16);
		widgetLabel.setLineWrap(true);
		widgetLabel.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(widgetLabel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		labelPanel.add(scrollPane);
		panel.add(labelPanel);
		
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
		"TEXT";
	}

	@Override
	public JComponent getComponent() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		JLabel label = new JLabel(widgetLabel.getText());
		panel.add(label);
		String text = "<html>Type: Textbox " +
			"<br>Text: " + widgetLabel.getText() + "</html>";  
		panel.setToolTipText(text);
		
		JTextField field = new JTextField();
		field.setEditable(false);
		panel.add(field);
		return panel;
	}

	@Override
	public String getType() 
	{
		return "Textbox";
	}
}