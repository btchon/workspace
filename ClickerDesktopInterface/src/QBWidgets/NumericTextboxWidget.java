package QBWidgets;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class NumericTextboxWidget implements WidgetInterface
{
	private JPanel panel;
	
	private JTextArea widgetLabel = new JTextArea();
	private ButtonGroup buttonGroup = new ButtonGroup();
	
	private JTextField minField = new JTextField();
	private JTextField maxField = new JTextField();
	
	public NumericTextboxWidget()
	{
		buildPanel("", "UNBOUNDED", 0, 0);
	}
	
	public NumericTextboxWidget(String label, String type, int min, int max)
	{
		buildPanel(label, type, min, max);
	}
	
	private void buildPanel(String label, String type, int min, int max)
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
		
		JPanel typePanel = new JPanel();
		typePanel.setLayout(new GridLayout(1, 2));
		typePanel.setBorder(BorderFactory.createTitledBorder("Data Type"));
		
		JRadioButton unbound = new JRadioButton("Unbounded");
		unbound.setActionCommand("UNBOUNDED");
		buttonGroup.add(unbound);
		typePanel.add(unbound);
		
		JRadioButton bound = new JRadioButton("Bounded");
		bound.setActionCommand("BOUNDED");
		buttonGroup.add(bound);
		typePanel.add(bound);
		
		panel.add(typePanel);
		
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new GridLayout(3, 2));
		valuesPanel.setBorder(BorderFactory.createTitledBorder("Bound"));
		
		valuesPanel.add(new JLabel("  Lower:"));
		minField.setColumns(8);
		minField.setText("" + min);
		valuesPanel.add(minField);
		
		valuesPanel.add(new JLabel("  Upper:"));
		maxField.setColumns(8);
		maxField.setText("" + max);
		valuesPanel.add(maxField);
		
		panel.add(valuesPanel);
		
		if (type.equals("BOUNDED"))
		{
			bound.setSelected(true);
		}
		else
		{
			unbound.setSelected(true);
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
		if (minField.getText().equals(""))
			minField.setText("0");
		if (maxField.getText().equals(""))
			maxField.setText("10");
		
		if (Integer.parseInt(minField.getText()) > Integer.parseInt(maxField.getText()))
		{
			String temp = minField.getText();
			minField.setText(maxField.getText());
			maxField.setText(temp);
		}
		
		return "NUMERICTEXTBOX" + Constants.COLON_SEPARATOR + 
		widgetLabel.getText() + Constants.COLON_SEPARATOR +
		buttonGroup.getSelection().getActionCommand() + Constants.COLON_SEPARATOR +
		minField.getText() + Constants.COLON_SEPARATOR + 
		maxField.getText() + Constants.COLON_SEPARATOR;
	}

	@Override
	public JComponent getComponent() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		JLabel label = new JLabel(widgetLabel.getText());
		panel.add(label);
		String text = "<html>Type: Textbox " +
			"<br>Text: " + widgetLabel.getText() +
			"<br>Data: " + buttonGroup.getSelection().getActionCommand() +
			"<br>Min: " + minField.getText() + 
			"<br>Max: " + maxField.getText() +  "</html>"; 
		panel.setToolTipText(text);
		
		JTextField field = new JTextField();
		field.setEditable(false);
		panel.add(field);
		return panel;
	}

	@Override
	public String getType() 
	{
		return "Numeric Textbox";
	}
}