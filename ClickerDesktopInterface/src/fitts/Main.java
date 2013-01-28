/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fitts;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author sthughes
 */
public class Main 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
      JFrame circlesFrame = new JFrame ("FittsDots");
      circlesFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

      circlesFrame.getContentPane().add (new FittsPanel());

      circlesFrame.pack();
      circlesFrame.setVisible(true);
    }

}
