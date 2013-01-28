package org.csuni.PianoGraphs;

import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.Rotation;


public class RepetoireChart extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public RepetoireChart(String applicationTitle, String chartTitle) {
        super(applicationTitle);
        // This will create the dataset 
       TimeSeries dataset = createDataset();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, chartTitle);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);

    }
    
    
/**
     * Creates a sample dataset 
     */

    private  TimeSeries createDataset() {
//    	XYSeries series = new XYSeries("XYGraph");
//    	series.add(1, 1);
//    	series.add(1, 2);
//    	series.add(2, 1);
//    	series.add(3, 9);s
//    	series.add(4, 10);
    	
    	TimeSeries pop = new TimeSeries("Pop");
    	pop.add(new Day(20, 1, 2004), 200);
    	pop.add(new Day(20, 2, 2004), 250);
    	pop.add(new Day(20, 3, 2004), 450);
    	pop.add(new Day(20, 4, 2004), 475);
    	pop.add(new Day(20, 5, 2004), 125);
    	
    	return pop;
        
    }
    
    
/**
     * Creates a chart
     */

    private JFreeChart createChart(TimeSeries series, String title) {
        
    	TimeSeriesCollection dataset = new TimeSeriesCollection();
    	dataset.addSeries(series);
    	
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,  	  // chart title
            "x-axis",     // x-axis Label
            "y-axis",     // y-axis Label
            dataset,                // data
            true, // Show Legend
            true, // Use tooltips
            false // Configure chart to generate URLs?
            );

        XYPlot plot =(XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));

        return chart;
        
    }

}
