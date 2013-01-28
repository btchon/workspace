package QBWidgets;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clicker.constants.Constants;

public class RankCWidget implements WidgetInterface
{
	private JPanel panel;
	
	private JTextArea widgetLabel = new JTextArea();
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
		
		JPanel candidatePanel = new JPanel();
		candidatePanel.setBorder(BorderFactory.createTitledBorder("Candidates"));
		candidatePanel.setLayout(new BoxLayout(candidatePanel, BoxLayout.Y_AXIS));
		
		comboBox = new JComboBox(values);
		comboBox.addActionListener(new ComboActionListener());
		candidatePanel.add(comboBox);
		candidatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		candidatePanel.add(enterField);
		
		JPanel buttonPanel = new JPanel();
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new AddActionListener());
		buttonPanel.add(addButton);
		
		JButton deleteButton = new JButton("Del");
		deleteButton.addActionListener(new DeleteActionListener());
		buttonPanel.add(deleteButton);
		
		candidatePanel.add(buttonPanel);
		panel.add(candidatePanel);
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