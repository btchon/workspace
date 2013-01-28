

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import clicker.server.ServerPanel;
import clicker.admin.AdminPanel;

public class DesktopGUI extends JFrame
{
	private ServerPanel server;
    private JTextField loginField = new JTextField("frederis");
    private JPasswordField passField = new JPasswordField("testpw");
	
	public DesktopGUI()
	{
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setTitle("Desktop Clicker");
		setSize(800, 600);
		this.setResizable(false);
		
		server = new ServerPanel();
		ClickerIMP consumerIMP = new ClickerIMP();
		JTabbedPane interfacePane = new JTabbedPane();
		
		interfacePane.addTab("Admin", new AdminPanel());
		interfacePane.addTab("Consumer", consumerIMP);
		interfacePane.addTab("Server", server);
		
		add(interfacePane);
		
//		getContentPane().add(buildLogin());

		setVisible(true);
	}
	
	private void login()
	{
		getContentPane().removeAll();
		
		server = new ServerPanel();
		JTabbedPane interfacePane = new JTabbedPane();
		
		interfacePane.addTab("Admin", new AdminPanel());
		interfacePane.addTab("Consumer", new ClickerIMP());
		interfacePane.addTab("Server", server);
		
		add(interfacePane);
		
		getContentPane().validate();
	}
	
    private JPanel buildLogin()
    {
    	JPanel panel = new JPanel();
    	panel.setLayout(new GridLayout(4, 1));
    	panel.add(new JLabel("Welcome to Desktop Clicker!"));
    	
    	JPanel loginPanel = new JPanel();
    	loginPanel.setLayout(new GridLayout(1,2));
    	
    	loginPanel.add(new JLabel("Login Name: "));
    	loginPanel.add(loginField);
    	panel.add(loginPanel);

    	JPanel passwordPanel = new JPanel();
    	passwordPanel.setLayout(new GridLayout(1,2));
    	passwordPanel.add(new JLabel("Password: "));
    	passwordPanel.add(passField);
    	passField.setEchoChar('*');
    	panel.add(passwordPanel);

    	JButton loginButton = new JButton("Login");
    	loginButton.addActionListener(new LoginActionListener());
    	panel.add(loginButton);
    	return panel;
    }
    
	private class LoginActionListener implements ActionListener
	{

		public LoginActionListener(){}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			login();
		}
	}
}
