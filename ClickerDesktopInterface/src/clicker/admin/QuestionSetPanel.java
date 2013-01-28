package clicker.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import clicker.constants.Constants;

public class QuestionSetPanel extends JPanel
{
	private ArrayList<JPanel> panelList;

	private int panelIndex = 0;
	
	public QuestionSetPanel()
	{
		super();
		panelList = new ArrayList<JPanel>();
		
	}
	
	public void buildPanels(String[] allQArray)
	{
		
		for (int i=0; i < allQArray.length; i++) 
        {
        	JPanel outerPanel = new JPanel();
        	outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        	
            String[] qTextParts = allQArray[i].split(Constants.SEMI_COLON_SEPARATOR);
            String[] qWidgets = qTextParts[2].split(Constants.COMMA_SEPARATOR);

            outerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            for (int j=0; j<qWidgets.length; j++) 
            {
            	JPanel innerPanel = new JPanel();
            	innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
            	
                String[] widgetParts = qWidgets[j].split(Constants.COLON_SEPARATOR);

                if (widgetParts[0].equals("B") || widgetParts[0].equals("JEO")) 
                {
                    JButton tb = new JButton();
                    tb.setMinimumSize(new Dimension(150, 20));
                    tb.setMaximumSize(new Dimension(500, 20));

                    tb.setText(widgetParts[1]);
                    innerPanel.add(tb);
                } 
                else if (widgetParts[0].equals("TOG")) 
                {
                    JToggleButton ttb = new JToggleButton();
                    
                    ttb.setMinimumSize(new Dimension(150, 20));
                    ttb.setMaximumSize(new Dimension(500, 20));
                    
                    ttb.setText(widgetParts[1]);
                    innerPanel.add(ttb);
                } 
                else if (widgetParts[0].equals("TEXTVIEW") || widgetParts[0].equals("TVBUTTON")) 
                {
                    JLabel ttv = new JLabel();

                    ttv.setText(widgetParts[1]);
                    innerPanel.add(ttv);
                    if (widgetParts[0].equals("TVBUTTON")) 
                    {
                        System.out.println("TV Button not available.");
                    }
                } 
                else if (widgetParts[0].equals("SLIDE")) 
                {
                    JSlider tsb = new JSlider();
		
                    tsb.setMaximum(Integer.parseInt(widgetParts[3]) - Integer.parseInt(widgetParts[2]));
                    JLabel stv = new JLabel();

                    stv.setText(widgetParts[1]);
                    innerPanel.add(stv);
                    innerPanel.add(tsb);
                } 
                else if (widgetParts[0].equals("COMBO")) 
                {
                    JLabel ctv = new JLabel();

                    ctv.setText(widgetParts[1]);
                    String[] copts = widgetParts[2].split(Constants.TILDE_SEPARATOR);
                    JComboBox tcs = new JComboBox(copts);


                    innerPanel.add(ctv);
                    innerPanel.add(tcs);
                } 
                else if (widgetParts[0].equals("TEXTBOX") || widgetParts[0].equals("NUMERICTEXTBOX")) 
                {
                    JTextArea tet = new JTextArea(widgetParts[1], 4, 20);
                    tet.setEditable(false);
                    tet.setLineWrap(true);
                    tet.setWrapStyleWord(true);
                    innerPanel.add(tet);
                    
                    JTextField tf = new JTextField();
                    tf.setEditable(false);
                    innerPanel.add(tf);
                } 
                else if (widgetParts[0].equals("TEXTQ") || widgetParts[0].equals("QRTEXT")) 
                {
                	JTextField tet = new JTextField();
   
                    JButton sbut = new JButton();
                    if (widgetParts[0].equals("QRTEXT")) 
                    {
                        sbut.setText("Scan");
                    } 
                    else 
                    {
                        sbut.setText("Submit");
                    }
                    innerPanel.add(tet);
                    innerPanel.add(sbut);
                } 
                else 
                {
                	System.out.println("Something else created.");
                	JLabel tv = new JLabel();

                    tv.setText("Some other widget type");
                    innerPanel.add(tv);
                }
                outerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                outerPanel.add(innerPanel);
            }
            
            panelList.add(outerPanel);
        }
        if (panelList.size() != 0)
        {
        	panelIndex = 0;
        	updatePanel();
        }
	}
	
	public void first()
	{
		panelIndex = 0;
		updatePanel();
	}
	
	public void next()
	{
		panelIndex += 1;
		if (panelIndex > (panelList.size() - 1))
		{
			panelIndex = 0;
		}
		updatePanel();
	}
	
	public void prev()
	{
		panelIndex -= 1;
		if (panelIndex < 0)
		{
			panelIndex = (panelList.size() - 1);
		}
		updatePanel();
	}
	
	public void last()
	{
		panelIndex = panelList.size() - 1;
		updatePanel();
	}
	
	public int getIndex()
	{
		return panelIndex;
	}
	
	private void updatePanel()
	{
		removeAll();
		add(panelList.get(panelIndex));
		validate();
		repaint();
	}

}
