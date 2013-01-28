package QBWidgets;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clicker.constants.Constants;

public class LabelWidget implements WidgetInterface
{
	private JPanel panel;
	private JTextArea widgetLabel = new JTextArea();
	
	public LabelWidget()
	{
		buildPanel("");
	}
	
	public LabelWidget(String label)
	{
		buildPanel(label);
	}
	
	private void buildPanel(String label)
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createTitledBorder("Text Label"));
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
		JComponent component = new JLabel(widgetLabel.getText());
		component.setBorder(BorderFactory.createLineBorder(Color.black));
		String text = "<html>Type: Label <br>Text: " + widgetLabel.getText()+ "</html>";  
		component.setToolTipText(text);
		return component;
	}

	@Override
	public String getType() 
	{
		return "Label";
	}
}