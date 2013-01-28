import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;


public class MouseControl implements ClickerConsumerInterface 
{
	protected final static String COLON_SEPARATOR      = "`/:";
	private int participantCount;

	private Robot r;
	private boolean open;

	public MouseControl() 
	{
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
	public void setID(String id) {}

	@Override
	public String declareConsumptions() 
	{
		return "MouseControl`/:Control";
	}

	@Override
	public void inputData(Map<String, ArrayList<ArrayList<String>>> input) 
	{
		System.out.println(input.toString());
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

}
