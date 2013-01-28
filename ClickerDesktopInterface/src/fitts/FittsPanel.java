
package fitts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class FittsPanel extends JPanel 
{

    private final int WIDTH = 800, HEIGHT = 550;
    private final int NUMTRIALS = 20;
    private Circle circle;
    private FittsClick[] theClicks;
    private int clickIndex;
 
    private boolean reset = false;
    private int trialNum = 0;

    //-----------------------------------------------------------------
    //  Sets up this panel to listen for mouse events.
    //-----------------------------------------------------------------
    public Circle newCircle() 
    {
        int x = (int) (Math.random() * (WIDTH - 2 * Circle.MAX_RADIUS) + Circle.MAX_RADIUS);
        int y = (int) (Math.random() * (HEIGHT - 2 * Circle.MAX_RADIUS) + Circle.MAX_RADIUS);
        return new Circle(new Point(x, y), Color.BLUE);
    }

    public FittsPanel() 
    {
        addMouseListener(new FittsListener());
        theClicks = new FittsClick[1000];
        clickIndex = 0;
      

        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        circle = newCircle();
    }

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
}


