import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.date.SerialDate;

public class TheoreticalXYOverlay extends JPanel implements ClickerConsumerInterface
{
	private static final long serialVersionUID = 1L;
	private boolean running;
	private String groupID;
	
	private ChartPanel chartPanel;
	private JTextField mean;
	private JTextField sd;
	private JTextField minLabel;
	private JTextField maxLabel;

	private int inputCount;
	private int graphHeight;
	
	private Double min;
	private Double max;

	private Map<String, String> currentValueMap;

	private ArrayList<String> savedLabel;
	public TheoreticalXYOverlay()
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		inputCount = 0;

		currentValueMap = Collections.synchronizedMap(new HashMap<String, String>());
		
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(3, 2));
//        optionPanel.add(new JLabel("Min: "));
//        minLabel = new JTextField("2");
//        optionPanel.add(minLabel);
//        
//        optionPanel.add(new JLabel("Max"));
//        maxLabel = new JTextField("12");
//        optionPanel.add(maxLabel);
        
        optionPanel.add(new JLabel("Mean: "));
        mean = new JTextField("7");
        optionPanel.add(mean);
        
        optionPanel.add(new JLabel("Std: "));
        sd = new JTextField("2.412");
        optionPanel.add(sd);
        
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ApplyActionListener());
        optionPanel.add(applyButton);
        
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
        add(chartPanel);
        
        add(optionPanel);
	}
	
	private class ApplyActionListener implements ActionListener
	{

		public ApplyActionListener() {}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			chartPanel.setChart(createChart());
			validate();
		}
	}
	 
	@Override
	public JPanel getPanel() 
	{
		return this;
	}
	
	@Override
	public String declareConsumptions() 
	{
		return "TheoreticalXYOverlay`/:Display";
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
		
		for (String l : savedLabel)
		{
			currentValueMap.put(l, "0");
		}
		inputCount = 0;
		for (String user : input.keySet())
		{
			ArrayList<ArrayList<String>> history = input.get(user);
			
			for (ArrayList<String> set : history)
			{
				for (String label : set)
				{
					try {
						Double check = Double.valueOf(label);

						inputCount += 1;
						if (currentValueMap.containsKey(label))
						{
							currentValueMap.put(label, "" + (Integer.parseInt(currentValueMap.get(label)) + 1));
							if (Integer.parseInt(currentValueMap.get(label)) > graphHeight)
								graphHeight = Integer.parseInt(currentValueMap.get(label));
						}
						else if(!label.equals(" "))
						{
							currentValueMap.put(label, "1");
						}
					}
					catch (NumberFormatException nfe) {}
					
				}
			}
		}
	}
	
	private JFreeChart createChart() 
	{
        final IntervalXYDataset data1 = createDataset();
        final XYItemRenderer renderer1 = new XYBarRenderer(0.20);
        final NumberAxis domainAxis = new NumberAxis("Label");
        final ValueAxis rangeAxis = new NumberAxis("Value");
        setMinMax();
        domainAxis.setRange(min - min /10, max + min /10);
        
        final XYPlot plot = new XYPlot(data1, domainAxis, rangeAxis, renderer1);

        // add a second dataset and renderer...
        final XYDataset data2 = createOverlayDataset();
        final XYItemRenderer renderer2 = new StandardXYItemRenderer();
        plot.setDataset(1, data2);
        plot.setRenderer(1, renderer2);
        
        
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // return a new chart containing the overlaid plot...
        return new JFreeChart("Theoretical XY Overlay", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
    }
	
	private IntervalXYDataset createDataset() 
	{
		TreeSet<String> answersSorted = new TreeSet<String>(currentValueMap.keySet());
		
        // create the dataset...
		XYSeries dataset = new XYSeries(0);
        
        for (String key : answersSorted)
        {
        	try {
	        dataset.add(Double.valueOf(key), (Double)(Double.valueOf(currentValueMap.get(key)) / inputCount));
        }
        	catch (NumberFormatException nfe) {}
        }
        
        return new XYSeriesCollection(dataset);
    }
	
	private IntervalXYDataset createOverlayDataset()
	{
		setMinMax();
		XYSeries dataset = new XYSeries(0);
        for (double i = min; i < max; i += .1)
        {
        	dataset.add(i, getY(i));
        }
        
        return new XYSeriesCollection(dataset);
	}
	
    public double getY(double x) 
    { 
    	Double m = 10.0;
    	Double s = 2.0;
    	
    	try
    	{
    		m = Double.parseDouble(mean.getText());
    		s = Double.parseDouble(sd.getText());
    	}
    	catch (NumberFormatException nfe) {}
    	
    	Double variance = Math.pow(s, 2);
    	return (1 / (Math.sqrt(2 * Math.PI * variance))) * (Math.pow(Math.E, - (Math.pow( (x - m) , 2) / (2 * variance))));
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
	
	public void setMinMax(){
		min = Double.valueOf(mean.getText()) - Double.valueOf(sd.getText()) * 3;
		max = Double.valueOf(mean.getText()) + Double.valueOf(sd.getText()) * 3;
	}
}
