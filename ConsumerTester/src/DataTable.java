import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jfree.data.category.DefaultCategoryDataset;



public class DataTable extends JPanel implements ClickerConsumerInterface
{

	private static final long serialVersionUID = 1L;
	private boolean running;
	private String groupID;
	
	private String[] columnNames = {"List of Answers",
			"# of times selected","% of choices"};
	
	private JTable table;
	private DefaultTableModel tableModel;
	
	private int participantCount;
	private int inputCount = 0;
	private DecimalFormat twoDec = new DecimalFormat("#.##");
	
	private JLabel inputCountTotal;
	private JPanel panel = new JPanel();

	private LinkedHashMap<String, Integer> currentValueMap;
	private ArrayList<String> savedLabel;
	 
	public DataTable()
	{
		inputCountTotal = new JLabel("Number of Responders: 0");
	
		panel.add(inputCountTotal);
		panel.setBackground(Color.white);
		
		participantCount = 0;
		setLayout(new BorderLayout());
		table = new JTable();
		table.setFont(new Font("Tahoma", Font.PLAIN, 24));
		table.setRowHeight(30);
		
		tableModel = (DefaultTableModel) table.getModel();
		tableModel.setColumnIdentifiers(columnNames);
		
		table.setModel(tableModel);
        table.setBackground(Color.white);
        table.setEnabled(false);
        
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(600);
        tcm.getColumn(1).setPreferredWidth(100);
        tcm.getColumn(2).setPreferredWidth(100);
              
        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setPreferredSize(new Dimension(800, 200));
        
        add(panel, BorderLayout.NORTH);
		add(scrollPane);
		
		currentValueMap = new LinkedHashMap<String, Integer>();
	}
	
	@Override
	public void setID(String id) 
	{
		groupID = id;
	}

	@Override
	public String declareConsumptions() 
	{
		return "Data Table`/:Summary";
	}
	
	@Override
	public JPanel getPanel() 
	{
		return this;
	}
	
	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
	{
		processData(input);
        validate();
	}
	
	public void processData(Map<String, ArrayList< ArrayList <String>>> input)
	{
		currentValueMap.clear();
		inputCount = 0;
		
		for (String l : savedLabel)
		{
			currentValueMap.put(l, 0);
		}
		
		for (String user : input.keySet())
		{
			ArrayList<ArrayList<String>> history = input.get(user);
			
			for (ArrayList<String> set : history)
			{
				for (String label : set)
				{
					

					if (currentValueMap.containsKey(label))
					{
						currentValueMap.put(label, currentValueMap.get(label) + 1);
						inputCount += 1;
					}

					else if(!label.equals(" "))
					{
						currentValueMap.put(label, 1);
						inputCount += 1;
					}
					inputCountTotal.setText("Number of Responders: " + inputCount);
				}
			}
		}
		
		tableModel.setRowCount(currentValueMap.size());
		

		int i = 0;
		
		Map<Double, Integer> numericSorted = new HashMap<Double, Integer>();
		TreeSet<String> stringSorted = new TreeSet<String>();
        Double check;
        
		for (String label : currentValueMap.keySet())
		{
			try
	    	{
	    		check = Double.parseDouble(label);
	    		numericSorted.put(check, currentValueMap.get(label));
	    	}
	    	catch (NumberFormatException nfe) 
	    	{
	    		stringSorted.add(label);
	    	}
	    }
		
        for (String string : savedLabel)
        {
        	try
        	{
        		check = Double.parseDouble(string);
        		numericSorted.put(check, currentValueMap.get(string));
        	}
        	catch (NumberFormatException nfe) 
        	{
            	double inputs = (double) Double.valueOf(currentValueMap.get(string)) / inputCount * 100;
    			tableModel.setValueAt(string, i, 0);
            	tableModel.setValueAt((currentValueMap.get(string)), i, 1);
    			tableModel.setValueAt(twoDec.format(inputs),i,2);
    			i += 1;
        	}
        }
        
	
	    for (Double number : new TreeSet<Double>(numericSorted.keySet()))
	    {
	    	double inputs = Double.valueOf(numericSorted.get(number)) / inputCount * 100;
			tableModel.setValueAt(number, i, 0);
	    	tableModel.setValueAt((numericSorted.get(number)), i, 1);
			tableModel.setValueAt(twoDec.format(inputs),i,2);
			i += 1;
	    }
	    
	    for (String string : stringSorted)
	    {
	    	if (!savedLabel.contains(string))
	    	{
	    		double inputs = (double) Double.valueOf(currentValueMap.get(string)) / inputCount * 100;
	    		tableModel.setValueAt(string, i, 0);
	    		tableModel.setValueAt((currentValueMap.get(string)), i, 1);
	    		tableModel.setValueAt(twoDec.format(inputs),i,2);
	    		i += 1;
	    	}
	    }
		
		
		
		
		//System.out.println(i);

		tableModel.fireTableDataChanged();
	}

	@Override
	public void setLabels(ArrayList<String> labels)
	{
		savedLabel = labels;
	}
	
}
