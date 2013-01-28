import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class WeightedObject extends JPanel implements ClickerConsumerInterface 
{

	protected final static String COLON_SEPARATOR      = "`/:";
	
	private JFrame frame;
	
	private ArrayList<Weight> objects = new ArrayList<Weight>();
	
	private Map<String, Pointer> pointers;
	private int participantCount;
	
	private int setWeight = 1;
	
	
	public WeightedObject() 
	{
        frame = new JFrame();
        frame.setLocation(50, 50);
        frame.setSize(800, 800);
        frame.getContentPane().add(this);
        frame.addMouseListener(new DotsListener());
        frame.addMouseWheelListener(new DotsListener());
        
        objects.add(new Weight(new Point(200, 200), 1));
        
        Thread thread = new Thread(new PaintRunnable());
        thread.start();
        
	}
	
	private class DotsListener implements MouseListener, MouseWheelListener
	{

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			objects.add(new Weight(e.getPoint(), setWeight));
//			repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseWheelMoved(MouseWheelEvent o) 
		{
			
			int notches = -o.getWheelRotation();
			
			if (setWeight + notches >= 1)
			{
				setWeight += notches;
			}
//			repaint();
		}
		
		
	}
	
	@Override
	public void setID(String id) 
	{
		pointers = Collections.synchronizedMap(new HashMap<String, Pointer>());
		
		frame.setVisible(true);
	}

	@Override
	public String declareConsumptions() 
	{
		return "WeightedObject`/:Control";
	}

	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
	{
		
		for (String user : input.keySet())
		{
			ArrayList<ArrayList<String>> history = input.get(user);
			ArrayList<String> data = history.get(0);
			
			if (!pointers.containsKey(user))
			{
				pointers.put(user, new Pointer(user, frame.getLocation()));
			}
			
			if (data.get(0).equals("0")) //mouse move event
			{ 
				int xMove = Integer.parseInt(data.get(1));
				int yMove = Integer.parseInt(data.get(2));
				
				pointers.get(user).move(xMove, yMove);
			} 
			else if (data.get(0).equals("1")) //mouse button event
			{ 
				if (data.get(2).equals("0")) //left mouse click
				{ 
					System.out.println("Left mouse clicked!");
				} 
				else if(data.get(2).equals("1")) //right mouse click
				{
					System.out.println("Right mouse clicked!");
				}
				
				else if(data.get(2).equals("2")) //left mouse down
				{
					pointers.get(user).pointerDown();
					selectionCheck(pointers.get(user));
				} 
				else if(data.get(2).equals("3")) //left mouse up
				{
					pointers.get(user).pointerUp();
					pointers.get(user).dropWeight();
				} 
				
				else if(data.get(2).equals("4")) //right mouse down
				{
					System.out.println("Right mouse down!");
				} 
				else if(data.get(2).equals("5")) //right mouse up
				{
					System.out.println("Right mouse up!");
				}
			}	
			
		}
		
//		repaint();
	}
	
	public void selectionCheck(Pointer pointer)
	{
		for (Weight weight : objects)
		{
			if (pointer.getBounds().intersects(weight.getBounds()))
			{
				pointer.pickupWeight(weight);
				break;
			}
		}
	}
	
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(Color.black);
		String weightString = "Weight: " + setWeight;
		g2.drawChars(weightString.toCharArray(), 0, weightString.toCharArray().length, (int)frame.getLocation().x + 10, (int)frame.getLocation().y + 10);
		
		for (Weight weight : objects)
		{
			weight.draw(g2);
		}
		
		for (String name : pointers.keySet())
		{
			pointers.get(name).draw(g2);
		}
	}
	
	private class Pointer
	{
		private Point point;
		private Weight weight = null;
		private boolean isDown = false;
		private String name;
		private int RADIUS = 5;
		
		public Pointer(String name, Point location)
		{
			point = location;
			this.name = name;
		}
		
		public void move(int x, int y)
		{
			
			if (weight != null)
			{
				if ( weight.movable())
				{
					weight.move(x, y);
					point = new Point(point.x + x, point.y + y);
				}
				else
				{
					// Otherwise dont move
				}
			}
			else
			{
				point = new Point(point.x + x, point.y + y);
			}
		}
		
		public void pickupWeight(Weight weight)
		{
			this.weight = weight;
			weight.grabbed();
		}
		
		public void dropWeight()
		{
			if (weight != null)
			{
				weight.dropped();
				weight = null;
			}
		}
		
		public Ellipse2D.Double getBounds()
		{
			return new Ellipse2D.Double(point.x - RADIUS, point.y - RADIUS, RADIUS * 2, RADIUS * 2);
		}
		
		public void pointerUp()   { isDown = false; }
		public void pointerDown() { isDown = true; }
		
		public void draw(Graphics2D g2)
		{
			if (isDown)
			{
				g2.setColor(Color.RED);
			}
			else
			{
				g2.setColor(Color.BLACK);
			}
			g2.fill(new Ellipse2D.Double(point.x - RADIUS, point.y - RADIUS, RADIUS * 2, RADIUS * 2));
			g2.drawChars(name.toCharArray(), 0, name.toCharArray().length, (int)point.x + 10, (int)point.y + 10);
		}
	}
	
	private class Weight
	{
		private Point point;
		private int selected = 0;
		private final int weight;
		private boolean canMove;
		
		private static final int WIDTH = 200;
		private static final int HEIGHT = 100;
		
		public Weight(Point location, int weight)
		{
			point = location;
			this.weight = weight;
		}
		
		public void move(int x, int y) 
		{
			if (canMove)
			{
				point = new Point(point.x + x, point.y + y);
			}
		}
		
		public void grabbed()
		{
			selected += 1;
			canMove = selected >= weight;
		}
		
		public void dropped()
		{
			selected -= 1;
			canMove = selected >= weight;
		}
		
		public boolean movable()
		{
			return canMove;
		}
		
		public Rectangle2D.Double getBounds()
		{
			return new Rectangle2D.Double(point.x - (WIDTH / 2), point.y - (HEIGHT / 2), WIDTH, HEIGHT);
		}
		
		public void draw(Graphics2D g2)
		{
			g2.setColor(Color.lightGray);
			g2.fill(new Rectangle2D.Double(point.x - (WIDTH / 2), point.y - (HEIGHT / 2), WIDTH, HEIGHT));
			g2.setColor(Color.BLACK);
			g2.draw(new Rectangle2D.Double(point.x - (WIDTH / 2), point.y - (HEIGHT / 2), WIDTH, HEIGHT));
			
			String label = selected + " / " + weight;
			g2.drawChars(label.toCharArray(), 0, label.toCharArray().length, (int)point.x, (int)point.y - (HEIGHT / 2) - 10);
		}
	}
	
	private class PaintRunnable implements Runnable 
	{

		
		public PaintRunnable() {}
		
		@Override
		public void run() 
		{
			while (true)
			{
				repaint();
				try { Thread.sleep(250); } catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
	}

	@Override
	public JPanel getPanel() {return null;}

	@Override
	public void setLabels(ArrayList<String> labels) {}


}
