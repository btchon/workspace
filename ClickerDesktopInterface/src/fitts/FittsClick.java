
package fitts;

import java.awt.*;

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
