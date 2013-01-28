package clicker.admin;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class QuestionGUI extends JTabbedPane
{
	
	public QuestionGUI()
	{
		super();
		
		addTab("Stock Questions", new StockPanel());
		addTab("Lecture Style Questions", new LecturePanel());
		addTab("Classroom Managment", new ClassPanel());
		
	}

}
