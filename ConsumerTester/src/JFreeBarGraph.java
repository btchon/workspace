import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class JFreeBarGraph extends JPanel implements ClickerConsumerInterface
{
	private static final long serialVersionUID = 1L;
	private boolean running;
	private String groupID;
	
	private ChartPanel chartPanel;

	private int participantCount = 2;
	private int graphHeight;

	private Map<String, Integer> currentValueMap;

	private ArrayList<String> savedLabel;
	public JFreeBarGraph()
	{

		participantCount = 0;

		currentValueMap = Collections.synchronizedMap(new HashMap<String, Integer>());
		
        chartPanel = new ChartPanel(
        		ChartFactory.createBarChart(
	                "Bar Graph",              // chart title
	                "Category",               // domain axis label
	                "Value",                  // range axis label
	                null,                     // data
	                PlotOrientation.VERTICAL, // orientation
	                false,                    // include legend
	                true,                     // tooltips?
	                false                     // URLs?
        				), false);
	}
	
	@Override
	public JPanel getPanel() 
	{
		return chartPanel;
	}
	
	@Override
	public String declareConsumptions() 
	{
		return "JFreeBarGraph`/:Display";
	}

	@Override
	public void inputData(Map<String, ArrayList< ArrayList <String>>> input)
	{
		processData(input);
		
        chartPanel.setChart(createChart());
        validate();
	}
	
	public void processData(Map<String, ArrayList< ArrayList <String>>> input)
	{
		currentValueMap.clear();
		
		for (String s : savedLabel)
		{
			currentValueMap.put(s, 0);
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
						if (currentValueMap.get(label) > graphHeight)
							graphHeight = currentValueMap.get(label);
					}
					else if(!label.equals(" "))
					{
						currentValueMap.put(label, 1);
					}
				}
			}
		}
	}
	
	private JFreeChart createChart() 
	{
        
        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart
        (
            "Bar Graph",              // chart title
            "Category",               // domain axis label
            "Value",                  // range axis label
            createDataset(),          // data
            PlotOrientation.VERTICAL, // orientation
            false,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMIZATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);
        

        // get a reference to the plot for further customization...
        CategoryPlot plot = chart.getCategoryPlot();

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        rangeAxis.setRange(0.0, graphHeight + (int)Math.max((double)(graphHeight * .1), 1.0));
        graphHeight = participantCount;
        
        rangeAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 16));
        
        // Domain axis manipulation
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 16));
        
        // set series color
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        renderer.setSeriesPaint(0, new Color(255,0,0));
        
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        
        return chart;    
    }
	
	private CategoryDataset createDataset() 
	{
		Map<Double, Integer> numericSorted = new HashMap<Double, Integer>();
		TreeSet<String> stringSorted = new TreeSet<String>();
		
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Double check;
        for (String key : currentValueMap.keySet())
        {
        	try
        	{
        		check = Double.parseDouble(key);
        		numericSorted.put(check, currentValueMap.get(key));
        	}
        	catch (NumberFormatException nfe) 
        	{
        		stringSorted.add(key);
        	}
        }

        for (String string : savedLabel)
        {
        	dataset.addValue(currentValueMap.get(string), "", string);
        }

        for (Double number : new TreeSet<Double>(numericSorted.keySet()))
        {
        	if (!savedLabel.contains(number + ""))
        		dataset.addValue(numericSorted.get(number), "", number);
        }
        
        for (String string : stringSorted)
        {
        	if (!savedLabel.contains(string))
        		dataset.addValue(currentValueMap.get(string), "", string);
        }

        return dataset;
	}
	
	@Override
	public void setID(String id) 
	{
		groupID = id;
	}

	@Override
	public void setLabels(ArrayList<String> labels) 
	{
		savedLabel = labels;
		
	}
	
	public void printParts(String[] array)
	{
		
		for(String c : array)
		{
			System.out.println(c);
		}
	}

	@Override
	public void resize() {
		// TODO Auto-generated method stub
		
	}
}
