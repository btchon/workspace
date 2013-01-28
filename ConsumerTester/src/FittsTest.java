import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class FittsTest extends JPanel implements ClickerConsumerInterface 
{
	protected final static String COLON_SEPARATOR      = "`/:";
	private int participantCount;

	private Robot r;
	private boolean open;
	
	private JFrame frame;
	
    private final int WIDTH = 800, HEIGHT = 550;
    private final int NUMTRIALS = 20;
    private Circle circle;
    private FittsClick[] theClicks;
    private int clickIndex;
 
    private boolean reset = false;
    private int trialNum = 0;

	public FittsTest() 
	{
		
		frame = new JFrame ("Fitts Test");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(this);

        addMouseListener(new FittsListener());
        theClicks = new FittsClick[1000];
        clickIndex = 0;
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        

        circle = newCircle();
		
		try 
		{
			r = new Robot();
			r.setAutoDelay(10);

		} 
		catch (AWTException e) 
		{
			e.printStackTrace();
		}
		open = true;
	}

	@Override
	public void setID(String id) 
	{
		frame.setVisible(true);
	}

	@Override
	public String declareConsumptions() 
	{
		return "FittsTest`/:Control";
	}

	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
	{
		participantCount = input.size();
		for (String user : input.keySet())
		{
			ArrayList<ArrayList<String>> history = input.get(user);
			ArrayList<String> latestSet = history.get(0);
				
			if (latestSet.get(0).equals("0")) //mouse move event
			{ 
				int xMove = Integer.parseInt(latestSet.get(1)) / participantCount;
				int yMove = Integer.parseInt(latestSet.get(2)) / participantCount;
				
				Point p = MouseInfo.getPointerInfo().getLocation();
				int x = p.x;
				int y = p.y;
				r.mouseMove(x + xMove, y + yMove);
			} else if (latestSet.get(0).equals("1")){ //mouse button event
				if (latestSet.get(2).equals("0")){ //left mouse click
					r.mousePress(InputEvent.BUTTON1_MASK);
					r.waitForIdle();
					r.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if(latestSet.get(2).equals("1")){//right mouse click
					r.mousePress(InputEvent.BUTTON3_MASK);
					r.waitForIdle();
					r.mouseRelease(InputEvent.BUTTON3_MASK);
				} else if(latestSet.get(2).equals("2")){//left mouse down
					r.mousePress(InputEvent.BUTTON1_MASK);
				} else if(latestSet.get(2).equals("3")){//left mouse up
					r.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if(latestSet.get(2).equals("4")){//right mouse down
					r.mousePress(InputEvent.BUTTON3_MASK);
				} else if(latestSet.get(2).equals("5")){//right mouse up
					r.mouseRelease(InputEvent.BUTTON3_MASK);
				}
			}
		}
	}



	@Override
	public JPanel getPanel() { return null; }


	@Override
	public void setLabels(ArrayList<String> labels) {}
	
    //-----------------------------------------------------------------
    //  Draws the current circle, if any.
    //-----------------------------------------------------------------
    public void paintComponent(Graphics page) 
    {
        super.paintComponent(page);

        if (circle != null) 
        {
            circle.draw(page);
        }
    }
    
    //-----------------------------------------------------------------
    //  Sets up this panel to listen for mouse events.
    //-----------------------------------------------------------------
    public Circle newCircle() 
    {
        int x = (int) (Math.random() * (WIDTH - 2 * Circle.MAX_RADIUS) + Circle.MAX_RADIUS);
        int y = (int) (Math.random() * (HEIGHT - 2 * Circle.MAX_RADIUS) + Circle.MAX_RADIUS);
        return new Circle(new Point(x, y), Color.BLUE);
    }

    //*****************************************************************
    //  Represents the listener for mouse events.
    //*****************************************************************
    private class FittsListener implements MouseListener 
    {
        //--------------------------------------------------------------
        // Creates a new circle at the current location whenever the
        // mouse button is clicked and repaints.
        //--------------------------------------------------------------

        public void mousePressed(MouseEvent event) 
        {

            if (circle.isInside(event.getPoint())) 
            {
                if (clickIndex < NUMTRIALS) 
                {
                    theClicks[clickIndex++] = new FittsClick(circle, event.getPoint());
                    circle = newCircle();
                } 
                else 
                {
                    setBackground(Color.red);
                    for (int i = 0; i < clickIndex; i++) 
                    {
                        System.out.println(theClicks[i]);
                    }
                    reset = true;
                    System.out.println("Trial#" + trialNum);
                }
                
            }

            repaint();
        }

        //-----------------------------------------------------------------
        //  Provide empty definitions for unused event methods.
        //-----------------------------------------------------------------
        public void mouseClicked(MouseEvent event) {}

        public void mouseReleased(MouseEvent event) {}

        public void mouseEntered(MouseEvent event) 
        {
            if (reset)
            {
                reset = false;
                trialNum++;
                setBackground(Color.LIGHT_GRAY);
                circle = newCircle();
                theClicks = new FittsClick[1000];
                clickIndex = 0;
               

                repaint();
            }
        }

        public void mouseExited(MouseEvent event) {}
    }
    
	public class Circle 
	{
		private int centerX, centerY;
	    private int radius;
	    private Color color;
	    public final static int MIN_RADIUS = 5;
	    public final static int RANGE_RADIUS = 20;
	    public final static int MAX_RADIUS = MIN_RADIUS + RANGE_RADIUS;

	    Random generator = new Random();

	    //---------------------------------------------------------
	    // Creates a circle with center at point given, random radius and color
	    //   -- radius 5..24
	    //   -- color RGB value 0..16777215 (24-bit)
	    //---------------------------------------------------------
	    public Circle(Point point)
	    {
			radius = Math.abs(generator.nextInt())%RANGE_RADIUS + MIN_RADIUS;
			color = new Color(Math.abs(generator.nextInt())% 16777216);
			centerX = point.x;
			centerY = point.y;
	    }

	    public Circle(Point point, Color c)
	    {

			radius = Math.abs(generator.nextInt())%RANGE_RADIUS + MIN_RADIUS;
			color = c;
			centerX = point.x;
			centerY = point.y;

	        System.out.println(this);
	    }

	    public boolean isInside(Point point)
	    {
	        double dist;

	        dist = Math.sqrt((point.x - centerX)* (point.x - centerX) +
	                (point.y - centerY) * (point.y - centerY));
	        return dist < radius;
	    }

	    //---------------------------------------------------------
	    // Draws circle on the graphics object given
	    //---------------------------------------------------------
	    public void draw(Graphics page)
	    {
			page.setColor(color);
			page.fillOval(centerX-radius,centerY-radius,radius*2,radius*2);
	    }

	    public int getCenterX() 
	    {
	        return centerX;
	    }

	    public void setCenterX(int centerX) 
	    {
	        this.centerX = centerX;
	    }

	    public int getCenterY() 
	    {
	        return centerY;
	    }

	    public void setCenterY(int centerY) 
	    {
	        this.centerY = centerY;
	    }

	    public int getRadius() 
	    {
	        return radius;
	    }

	    public void setRadius(int radius) 
	    {
	        this.radius = radius;
	    }

	    public String toString()
	    {
	        return centerX + "," + centerY + "," + radius*2;
	    }

	}
	
	public class FittsClick 
	{

	    private Circle cir;
	    private long timestamp;
	    private Point clickSpot;

	    public FittsClick(Circle c, Point p) 
	    {
	        cir = c;
	        timestamp = System.currentTimeMillis();
	        clickSpot = p;
	    }

	    public Circle getCir() 
	    {
	        return cir;
	    }

	    public void setCir(Circle cir) 
	    {
	        this.cir = cir;
	    }

	    public long getTimestamp() 
	    {
	        return timestamp;
	    }

	    public void setTimestamp(long timestamp) 
	    {
	        this.timestamp = timestamp;
	    }

	    public String toString() 
	    {
	        String output = "";
	        output += cir + "," + timestamp;
	        output += "," + clickSpot.x + "," + clickSpot.y;

	        return output;
	    }
	}

}
