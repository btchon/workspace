import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;


public class MeanAndSD extends JPanel implements ClickerConsumerInterface{

	private static final long serialVersionUID = 1L;
	private boolean running;
	private String groupID;
	
	private double mean;
	private double sd;
	
	private JLabel meanLabel;
	private JLabel sdLabel;
	private JPanel panel = new JPanel();
	private DecimalFormat threeDec = new DecimalFormat("#.###");

	private int participantCount;

	private ArrayList<Double> currentValueList;

	private ArrayList<String> savedLabel;
	
	public MeanAndSD()
	{
		participantCount = 0;


		currentValueList = new ArrayList<Double>();
		
		meanLabel = new JLabel("Mean = 0.0");
		sdLabel = new JLabel("Standard Deviation = 0.0");
		
		panel.add(meanLabel);
		panel.add(sdLabel);
	}
	
	@Override
	public void setID(String id) 
	{
		groupID = id;
	}

	@Override
	public String declareConsumptions() 
	{
		return "Mean and Standard Deviation`/:Summary";
	}

	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
		{
			currentValueList.clear();
			

			for (String user : input.keySet())
			{
				ArrayList<ArrayList<String>> history = input.get(user);
				
				for (ArrayList<String> set : history)
				{
					for (String label : set)
					{
//
						try
						{
							Double check = Double.valueOf(label);
							currentValueList.add(check);
						}
						catch (NumberFormatException nfe){}
							
//						String temp = "";
//						for (int i = 0; i < label.length(); i++){
//	
//							if (Character.isDigit(label.charAt(i)) || Character.toString(label.charAt(i)).equals("."))
//								temp += label.charAt(i);
//						}
//	
//						if (temp.contains(".")){
//							int k = temp.indexOf(".");
//							temp = temp.substring(0, k+1) + temp.substring(k).replaceAll("\\.+", "").trim();
//						}
//						
//						if (!temp.isEmpty())
							
					}
				}
			}
			
		calculateMean(currentValueList);
		calculateSD(currentValueList);
		meanLabel.setText("Mean = " + threeDec.format(mean));
		sdLabel.setText("Standard Deviation = " + threeDec.format(sd));
	}
	 
	public Double calculateMean(ArrayList<Double> values){
		int i = 0;
		Double j = 0.0;
		Double k = 0.0;
		
		for (Double input : values){
			j += input;
			i++;
		}
		
		mean = j/i;
		
		for (Double input : values){
			Double temp = input - mean;
			k += temp*temp;
		}
		
		sd = Math.sqrt(k/(i-1));
		return mean;
		}
	
	public Double calculateSD(ArrayList<Double> values){
		int i = 0;
		Double j = 0.0;

		for (Double input : values){
			Double temp = input - mean;
			j += temp*temp;
			i++;
		}

		sd = Math.sqrt(j/(i-1));
		return sd;
	}


	public void setLabels(ArrayList<String> labels) 
	{
		savedLabel = labels;
	}


	@Override
	public JPanel getPanel() 
	{
		return panel;
	}


}
