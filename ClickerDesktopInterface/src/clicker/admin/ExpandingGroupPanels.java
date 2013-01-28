package clicker.admin;

import java.awt.*;  
import java.awt.event.*;  
import java.awt.font.*;  
import java.awt.image.BufferedImage;  
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;  
   
public class ExpandingGroupPanels extends MouseAdapter 
{  
    private ClassModel classModel;
    
    private JPanel topPanel;
    private Map<GroupPanel, MemberPanel> panelsMap;
    private Map<String, GroupPanel> groupsMap;
    private GridBagConstraints gbc;
   
    public ExpandingGroupPanels()  
    {  
    	classModel = ClassModel.getInstance();
        topPanel = new JPanel(new GridBagLayout());  
        panelsMap = new HashMap<GroupPanel, MemberPanel>();
        groupsMap = new HashMap<String, GroupPanel>();
        
        gbc = new GridBagConstraints();  
        gbc.insets = new Insets(1,3,0,3);  
        gbc.weightx = 1.0; 
        gbc.fill = gbc.HORIZONTAL;  
        gbc.gridwidth = gbc.REMAINDER; 
    }  
   
    public void mousePressed(MouseEvent e)  
    {  
        GroupPanel ap = (GroupPanel)e.getSource();  
        if(ap.target.contains(e.getPoint()))  
        {  
            ap.toggleSelection();  
            togglePanelVisibility(ap);  
        }  
    }  
   
    private void togglePanelVisibility(GroupPanel ap)  
    {  
        JPanel panel = panelsMap.get(ap);
        if (panel.isShowing())  
            panel.setVisible(false);  
        else  
            panel.setVisible(true);  
        ap.getParent().validate();  
    }  
   
    public void refreshPanelsMap()  
    {  
        ArrayList<String> ids = classModel.getGroups();  
        for (String group : ids)
        {
        	if (!groupsMap.containsKey(group))
        	{
        		GroupPanel groupPanel = new GroupPanel(group, this);
        		groupsMap.put(group, groupPanel);
        		
	        	MemberPanel memberPanel = new MemberPanel(group);
	        	panelsMap.put(groupPanel, memberPanel);
	        	
	        	topPanel.add(groupPanel, gbc);
	        	topPanel.add(panelsMap.get(groupPanel), gbc);
	        	panelsMap.get(groupPanel).setVisible(false);
        	}
        	
        	for (String member : classModel.getClientsFromGroup(group))
        	{
        		panelsMap.get(groupsMap.get(group)).addMember(member);
        	}
        }
        topPanel.validate();

    }  
    
    public JPanel getComponent()
    {
    	refreshPanelsMap();
    	return topPanel;
    }

}  
   
class GroupPanel extends JPanel  
{  
    String text;  
    Font font;  
    private boolean selected;  
    boolean bad = false; 
    BufferedImage open, closed;  
    Rectangle target;  
    final int  
        OFFSET = 30,  
        PAD    =  5;  
   
    public GroupPanel(String text, MouseListener ml)  
    {  
        this.text = text;  
        addMouseListener(ml);  
        font = new Font("sans-serif", Font.PLAIN, 12);  
        selected = false;  
        setBackground(new Color(200,200,220));  
        setPreferredSize(new Dimension(200,20));  
        setBorder(BorderFactory.createRaisedBevelBorder());  
        setPreferredSize(new Dimension(200,20));  
        createImages();  
        setRequestFocusEnabled(true);  
    }  
   
    public void toggleSelection()  
    {  
        selected = !selected;  
        repaint();  
    }  
    
    public boolean getSelection()  
    {  
        return selected; 
    }  
    
    public String getText()
    {
    	return text;
    }
   
    protected void paintComponent(Graphics g)  
    {  
        super.paintComponent(g);  
        Graphics2D g2 = (Graphics2D)g;  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                            RenderingHints.VALUE_ANTIALIAS_ON);  
        int w = getWidth();  
        int h = getHeight();  
        if (selected)  
            g2.drawImage(open, PAD, 0, this);  
        else  
            g2.drawImage(closed, PAD, 0, this);  
        g2.setFont(font);  
        FontRenderContext frc = g2.getFontRenderContext();  
        LineMetrics lm = font.getLineMetrics(text, frc);  
        float height = lm.getAscent() + lm.getDescent();  
        float x = OFFSET;  
        float y = (h + height)/2 - lm.getDescent();  
        g2.drawString(text, x, y);  
    }  
   
    private void createImages()  
    {  
        int w = 20;  
        int h = getPreferredSize().height;  
        target = new Rectangle(2, 0, 20, 18);  
        open = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);  
        Graphics2D g2 = open.createGraphics();  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                            RenderingHints.VALUE_ANTIALIAS_ON);  
        g2.setPaint(getBackground());  
        g2.fillRect(0,0,w,h);  
        int[] x = { 2, w/2, 18 };  
        int[] y = { 4, 15,   4 };  
        Polygon p = new Polygon(x, y, 3);  
        g2.setPaint(Color.green.brighter());  
        g2.fill(p);  
        g2.setPaint(Color.blue.brighter());  
        g2.draw(p);  
        g2.dispose();  
        closed = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);  
        g2 = closed.createGraphics();  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                            RenderingHints.VALUE_ANTIALIAS_ON);  
        g2.setPaint(getBackground());  
        g2.fillRect(0,0,w,h);  
        x = new int[] { 3, 13,   3 };  
        y = new int[] { 4, h/2, 16 };  
        p = new Polygon(x, y, 3);  
        g2.setPaint(Color.red);  
        g2.fill(p);  
        g2.setPaint(Color.blue.brighter());  
        g2.draw(p);  
        g2.dispose();  
    }  
} 

class MemberPanel extends JPanel
{
	private String groupName;
	private Map<String, JPanel> memberMap;
	private ClassModel classModel;
    
	public MemberPanel(String group)
	{
		groupName = group;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		memberMap = new HashMap<String, JPanel>();
		classModel = ClassModel.getInstance();
	}
	
	public void addMember(String name)
	{
		if (!memberMap.containsKey(name))
		{
			JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			memberPanel.add(Box.createRigidArea(new Dimension(80, 0)));
	    	JButton button = createSimpleButton(name);
	    	button.addActionListener(new SelectMemberActionListener(name));
	    	memberPanel.add(button);
			add(memberPanel);
			memberMap.put(name, memberPanel);
		}
	}
	
	public void removeMember(String name)
	{
		this.remove(memberMap.remove(name));
	}
	
	private static JButton createSimpleButton(String text) 
	{
		  JButton button = new JButton(text);
		  button.setForeground(Color.BLACK);
		  button.setBackground(new Color(207, 197, 191));
		  return button;
	}
	
	private class SelectMemberActionListener implements ActionListener
	{
		private String member;
		public SelectMemberActionListener(String member) 
		{
			this.member = member;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			MoveFrame frame = new MoveFrame(groupName, member);
		}
	}
	
	public class MoveFrame extends JFrame
    {
    	private String name;
    	private String currentGroup;
    	public MoveFrame(String currentGroup, String name)
    	{
    		super();
    		this.name = name;
    		this.currentGroup = currentGroup;
    		
        	setTitle("Move " + name + " to: ");
        	setSize(250, 200);
        	setLocation(300, 300);
        	setResizable(false);

        	JPanel panel = new JPanel();
        	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        	
        	// Group Panel
        	ArrayList<String> temp = new ArrayList<String>();
        	for (String group : classModel.getGroups())
        	{
        		if (!group.equals(currentGroup))
        		{
        			temp.add(group);
        		}
        	}
        	Collections.sort(temp);
        	
        	JPanel aPanel = new JPanel();
//        	aPanel.setLayout(new BoxLayout(aPanel, BoxLayout.Y_AXIS));
        	if (temp.size() == 0)
        	{
        		aPanel.add(new JLabel("No available groups."));
        	}
        	else
        	{
	        	for (String group : temp)
	        	{
	        		
		        	JButton button = createSimpleButton(group);
		        	button.addActionListener(new MoveToActionListener(group));
					aPanel.add(button);
	        	}
        	}
        	panel.add(aPanel);
        	
        	//END
        	
        	// Button Panel
        	JPanel buttonPanel = new JPanel();
        	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        	
        	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        	JButton close = new JButton("Close");
        	close.addActionListener(new CloseActionListener());
        	buttonPanel.add(close);
        	
        	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        	
        	panel.add(buttonPanel);
        	// END
        	
        	panel.add(Box.createRigidArea(new Dimension(0, 10)));
            
        	add(panel);
        	
        	setVisible(true);
        	
    	}
    	
    	private class MoveToActionListener implements ActionListener
		{
			String newGroup;
			public MoveToActionListener(String groupName) 
			{
				this.newGroup = groupName;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				removeMember(name);
				classModel.moveMember(name, currentGroup, newGroup);
				setVisible(false);
			}
		}	
    	
    	private class CloseActionListener implements ActionListener
		{
			
			public CloseActionListener() {}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}
		}	
    }
}