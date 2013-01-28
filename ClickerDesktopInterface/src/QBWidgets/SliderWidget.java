package QBWidgets;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clicker.constants.Constants;

public class SliderWidget implements WidgetInterface
{
	private JPanel panel;
	
	private JTextArea widgetLabel = new JTextArea();
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
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createTitledBorder("Slider Label"));
		widgetLabel = new JTextArea(label, 4, 16);
		widgetLabel.setLineWrap(true);
		widgetLabel.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(widgetLabel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		labelPanel.add(scrollPane);
		panel.add(labelPanel);
		
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new GridLayout(3, 2));
		valuesPanel.setBorder(BorderFactory.createTitledBorder("Values"));
		
		valuesPanel.add(new JLabel("  Min:"));
		minField.setColumns(8);
		minField.setText("" + min);
		valuesPanel.add(minField);
		
		valuesPanel.add(new JLabel("  Max:"));
		maxField.setColumns(8);
		maxField.setText("" + max);
		valuesPanel.add(maxField);
		
		valuesPanel.add(new JLabel("  Inital:"));
		initField.setColumns(8);
		initField.setText("" + current);
		valuesPanel.add(initField);
		
		panel.add(valuesPanel);
		
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
		if (initField.getText().equals(""))
			initField.setText(minField.getText());
		
		if (Integer.parseInt(minField.getText()) > Integer.parseInt(maxField.getText()))
		{
			String temp = minField.getText();
			minField.setText(maxField.getText());
			maxField.setText(temp);
		}
		if (Integer.parseInt(initField.getText()) < Integer.parseInt(minField.getText()))
			initField.setText(minField.getText());
		if (Integer.parseInt(initField.getText()) > Integer.parseInt(maxField.getText()))
			initField.setText(maxField.getText());
		
		return "SLIDE" + Constants.COLON_SEPARATOR + 
		widgetLabel.getText() + Constants.COLON_SEPARATOR + 
		minField.getText() + Constants.COLON_SEPARATOR + 
		maxField.getText() + Constants.COLON_SEPARATOR + 
		initField.getText();
	}

	@Override
	public JComponent getComponent() 
	{
		if (Integer.parseInt(initField.getText()) < Integer.parseInt(minField.getText()))
			initField.setText(minField.getText());
		if (Integer.parseInt(initField.getText()) > Integer.parseInt(maxField.getText()))
			initField.setText(maxField.getText());
			
		JComponent component = new JSlider(JSlider.HORIZONTAL, Integer.parseInt(minField.getText()), 
				Integer.parseInt(maxField.getText()), Integer.parseInt(initField.getText()));
		String text = "<html>Type: Slider <br>Text: " + widgetLabel.getText() +
			"<br>Min: " + minField.getText() + 
			"<br>Max: " + maxField.getText() + 
			"<br>Initial: " + initField.getText() + "</html>";  
		component.setToolTipText(text);
		return component;
	}

	@Override
	public String getType() 
	{
		return "Slider";
	}
}