package org.csuni.PianoServer;

import javax.swing.*;

public class PianoServer 
{

	public PianoServer() {}

	/**
	 * @param args
	 * 
	 *            the command line arguments
	 */
	public static void main(String[] args) 
	{
		// TODO code application logic here

		JFrame s = new JFrame("Piano Server");

		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		PianoServerPanel sp = new PianoServerPanel();

		s.getContentPane().add(sp);
		s.pack();
		s.setVisible(true);
		while(true)
			sp.listen();
	}
}
