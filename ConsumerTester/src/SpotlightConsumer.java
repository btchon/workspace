
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SpotlightConsumer extends JPanel implements ClickerConsumerInterface 
{
	private JFrame frame;
	private boolean status;
	
	private Map<String, Point> clientPoints;
	private int participantCount;
	private int RADIUS = 60;
	
	private double xScale = 1;
	private double yScale = 1;
	
	private final Dimension screenSize;
	private Thread thread;
	protected final static String COLON_SEPARATOR = "`/:";

	@SuppressWarnings("restriction")
	public SpotlightConsumer() 
	{
        frame = new JFrame();
        frame.setLocation(0, 0);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        frame.setUndecorated(true);
        com.sun.awt.AWTUtilities.setWindowOpacity(frame, 0.5f);

        screenSize = getToolkit().getScreenSize();
        
        setBackground(Color.black);
        
        frame.getContentPane().add(this);
        
        status = false;
        
        thread = new Thread(new MotionCheckRunnable());
        thread.start();
        
    
	}

	@Override
	public void setID(String id) 
	{
		participantCount = 1;
		clientPoints = Collections.synchronizedMap(new HashMap<String, Point>());
		
		status = true;
		frame.setVisible(true); 
	}

	@Override
	public String declareConsumptions() { return "Spotlight`/:Control"; }
	


	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
	{
		participantCount = input.size();
		for (String user : input.keySet())
		{
			ArrayList<ArrayList<String>> history = input.get(user);
			ArrayList<String> latestSet = history.get(0);
			String[] screenDim = latestSet.get(0).split("x");
			
			xScale = (double)screenSize.width / (double)Integer.valueOf(screenDim[0]);
			yScale = (double)screenSize.height / (double)Integer.valueOf(screenDim[1]);
			
			int xMove = Integer.parseInt(latestSet.get(1));
			int yMove = Integer.parseInt(latestSet.get(2));
			
			clientPoints.put(user, new Point((int)(xMove * xScale), (int)(yMove * yScale)));
		}
		
		repaint();
	}
	
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(new Color(255, 255, 255, (255 / participantCount)));
		
		for (String name : clientPoints.keySet())
		{
			g2.fill(new Ellipse2D.Double(clientPoints.get(name).x - RADIUS, 
					clientPoints.get(name).y - RADIUS,RADIUS * 2, RADIUS * 2));
		}
	}
	
	private class MotionCheckRunnable implements Runnable 
	{
		private Point previousPoint;
		private final static int WAKEUP = 8; // In Seconds
		private long time;
		
		public MotionCheckRunnable()
		{
			previousPoint = MouseInfo.getPointerInfo().getLocation();
		}
		
		@Override
		public void run() 
		{
			while (status)
			{
				Point currentPoint = MouseInfo.getPointerInfo().getLocation();
				
				if (!currentPoint.equals(previousPoint))
				{
					clientPoints = Collections.synchronizedMap(new HashMap<String, Point>());
					frame.setVisible(false);
					time = System.currentTimeMillis() / 1000;
				}
				else
				{
					if ((System.currentTimeMillis() / 1000) - time > WAKEUP)
					{
						frame.setVisible(true);
					}
				}
				previousPoint = currentPoint;
			}
		}
	}

	@Override
	public JPanel getPanel() 
	{
		return null;
	}

	@Override
	public void setLabels(ArrayList<String> labels) {}

}
