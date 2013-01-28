import java.awt.Color;
import java.awt.Dimension;
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
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class PieChart extends JPanel implements ClickerConsumerInterface
{
	private static final long serialVersionUID = 1L;
	private boolean running;
	private String currentQuestion;
	private String groupID;
	
	private ChartPanel chartPanel;

	private int participantCount;
	private ArrayList<String> savedLabel;
	private int graphHeight;
	private Map<String, Integer> currentValueMap;

	
	public PieChart()
	{
		currentQuestion = "";

		participantCount = 0;

		currentValueMap = Collections.synchronizedMap(new HashMap<String, Integer>());
		
        chartPanel = new ChartPanel(
        		ChartFactory.createPieChart(
	                "Pie Chart",              // chart title
	                null,                     // data
	                false,                    // include legend
	                true,                     // tooltips?
	                false                     // URLs?
        				), false);
		
        add(chartPanel);
	}
	
	
	@Override
	public String declareConsumptions() 
	{
		return "PieChart`/:Display";
	}
	
	@Override
	public JPanel getPanel() 
	{
		return this;
	}

	@Override
	public void inputData(Map<String, ArrayList< ArrayList <String>>> input)
	{
		System.out.println(input.toString());

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
        JFreeChart chart = ChartFactory.createPieChart
        (
            "Pie Graph",              // chart title
            (PieDataset) createDataset(),          // data
            false,                    // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMIZATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;
    }
	
	private PieDataset createDataset() 
	{
		TreeSet<String> answersSorted = new TreeSet<String>(currentValueMap.keySet());
		
        // create the dataset...
        DefaultPieDataset dataset = new DefaultPieDataset();
        

        for (String key : answersSorted)
        {
	        //dataset.setValue(Integer.parseInt(currentValueMap.get(key)), "", key);
        	dataset.setValue(key, currentValueMap.get(key));
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
}
