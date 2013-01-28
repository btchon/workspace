package fitts;

import java.awt.*;
import java.util.Random;

public class Circle 
{
	private int centerX, centerY;
    private int radius;
    private Color color;
    public final static int MIN_RADIUS = 5;
    public final static int RANGE_RADIUS = 20;
    public final static int MAX_RADIUS = MIN_RADIUS + RANGE_RADIUS;

    static Random generator = new Random();

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
