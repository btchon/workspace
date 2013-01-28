package clicker.admin;

import javax.swing.JTabbedPane;
import java.net.*;
import java.io.*;

public class AdminPanel extends JTabbedPane
{
    private CommunicationHub hub;

    public AdminPanel() 
    {
    	super();
        hub = CommunicationHub.getInstance();

        try 
        {
        	hub.setIp(InetAddress.getLocalHost().getHostAddress(), this);
        }
        catch(Exception e) 
        {
        	e.printStackTrace();
        }
    	
    	try { Thread.sleep(100); } catch (Exception e) {}
    	addTab("Questions", new QuestionGUI());
    	addTab("Question Builder", new QuestionBuilderGUI());
    	addTab("QR Code", new QRCodeGUI());
    	
    }
}
