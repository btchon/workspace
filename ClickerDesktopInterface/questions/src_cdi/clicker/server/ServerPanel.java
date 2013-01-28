package clicker.server;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ServerPanel extends JPanel
{
    
    private static final int CLIENT_PORT = 4321;
    private static final int ADMIN_PORT = 7700;
    private static final int DISPLAY_PORT = 7171;
    
    private static PushServer pushServer;
    
    private JTextArea messageArea;
    private JScrollPane scroll;
    
    public ServerPanel() 
    {
    	super();
    	setLayout(new GridLayout(1,1));
    	
    	messageArea = new JTextArea("", 5, 50);
    	messageArea.setLineWrap(true);
    	scroll = new JScrollPane(messageArea);
    	scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    
    	add(scroll);
    	
    	this.setVisible(true);
    	
        pushServer = new PushServer(CLIENT_PORT, ADMIN_PORT, DISPLAY_PORT, messageArea);
    }
}
