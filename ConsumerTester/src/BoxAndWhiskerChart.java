import java.awt.Color;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;

 
public class BoxAndWhiskerChart extends JPanel implements ClickerConsumerInterface{
	
	private static final long serialVersionUID = 1L;
	private boolean running;
	private String groupID;
	
	private Double max;
	private Double min;
	
	private ChartPanel chartPanel;

	private ArrayList<Double> currentValueList;

	private ArrayList<String> savedLabel;
	
	public BoxAndWhiskerChart() 
	{

		currentValueList = new ArrayList<Double>();
		
		
		BoxAndWhiskerXYDataset dataset = null;
		DateAxis dateaxis = new DateAxis("");
		NumberAxis numberaxis = new NumberAxis("Values");
		XYBoxAndWhiskerRenderer xyboxandwhiskerrenderer = new XYBoxAndWhiskerRenderer();
		XYPlot xyplot = new XYPlot(dataset, dateaxis, numberaxis, xyboxandwhiskerrenderer);
		xyplot.setOrientation(PlotOrientation.HORIZONTAL);
		JFreeChart jfreechart = new JFreeChart("Box-and-Whisker Chart", xyplot);
		jfreechart.setBackgroundPaint(Color.white);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setDomainGridlinesVisible(true);
		xyplot.setRangeGridlinePaint(Color.white);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		chartPanel = new ChartPanel(jfreechart);
	}

	@Override
	public void setID(String id) 
	{
		groupID = id;
	}

	@Override
	public String declareConsumptions() 
	{
		return "Box and Whisker Chart`/:Display";
	}

	
	private JFreeChart createChart() 
	{

		DateAxis dateaxis = new DateAxis("");
		NumberAxis numberaxis = new NumberAxis("Values");
		XYBoxAndWhiskerRenderer xyboxandwhiskerrenderer = new XYBoxAndWhiskerRenderer();
		XYPlot xyplot = new XYPlot(createDataset(), dateaxis, numberaxis, xyboxandwhiskerrenderer);
		xyplot.setOrientation(PlotOrientation.HORIZONTAL);
		JFreeChart jfreechart = new JFreeChart("Box-and-Whisker Chart", xyplot);
		jfreechart.setBackgroundPaint(Color.white);
		if (min != 0) {numberaxis.setRange(min - min/10, max + min / 10);}
		else numberaxis.setRange(min - 1, max + 1);
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.white);
		xyplot.setDomainGridlinesVisible(true);
		xyplot.setRangeGridlinePaint(Color.white);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		return jfreechart;
	}

	
	 private BoxAndWhiskerXYDataset createDataset() 
	 {
		 DefaultBoxAndWhiskerXYDataset dataset = new DefaultBoxAndWhiskerXYDataset("Box and Whisker");
	        Date date = new Date();
	        dataset.add(date, BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(currentValueList));
		 
		 return dataset;
	 }

	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) {
		processData(input);
		
        chartPanel.setChart(createChart());
        validate();
	}
	
	public void processData(Map<String, ArrayList< ArrayList <String>>> input)
	{
		currentValueList.clear();
		
		for (String user : input.keySet())
		{
			ArrayList<ArrayList<String>> history = input.get(user);
			
			for (ArrayList<String> set : history)
			{
				for (String label : set)
				{
					try {
						Double temp = Double.valueOf(label);
						currentValueList.add(Double.valueOf(temp));
					}
					catch (NumberFormatException nfe) {}
						
				}
			}
		}
		
		if (currentValueList.isEmpty())
			currentValueList.add(0.0);
		
		max = currentValueList.get(0);
		for (int i = 0; i < currentValueList.size(); i++)
		{
			if (currentValueList.get(i) > max)
			{
				max = currentValueList.get(i);
			}
		}
		
		min = currentValueList.get(0);
		for (int i = 0; i < currentValueList.size(); i++)
		{
			if (currentValueList.get(i) < min)
			{
				min = currentValueList.get(i);
			}
		}
		
	}

	@Override
	public void setLabels(ArrayList<String> labels) 
	{
		savedLabel = labels;
	}

	@Override
	public JPanel getPanel() 
	{
		return chartPanel;
	}

}
