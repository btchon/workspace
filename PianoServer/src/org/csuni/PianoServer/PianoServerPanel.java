package org.csuni.PianoServer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.csuni.PianoGraphs.RepetoireChart;

public class PianoServerPanel extends javax.swing.JPanel 
{

	private String framework = "embedded";
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String protocol = "jdbc:derby:";

	private String message, newmsg;
	private BufferedReader in = null;
	private Connection con;
	
	

	/** Creates new form ServerPanel */
	public PianoServerPanel() {
		initComponents();
		message = "";
		con = getDBConnection();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		Text = new javax.swing.JLabel();
		lbl_IP = new javax.swing.JLabel();

		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();

		// Text.setText("a");

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														391, Short.MAX_VALUE)
												.addComponent(
														Text,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														391, Short.MAX_VALUE)
												.addComponent(
														lbl_IP,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														391, Short.MAX_VALUE))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addGap(29, 29, 29)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										202, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(Text,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										36,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(27, 27, 27)
								.addComponent(lbl_IP,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										36,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(27, 27, 27)));
	}// </editor-fold>//GEN-END:initComponents

	private Connection getDBConnection() {

		Connection con = null;
		Properties props = new Properties();
		props.put("user", "CSUNI");
		props.put("password", "csuni");

		String dbName = "PIANODB"; // the name of the database

		try {
			// Class.forName("com.mysql.jdbc.Driver");
			Class.forName(driver).newInstance();
			System.out.println("Loaded the appropriate driver");
			con = DriverManager.getConnection(protocol + dbName, props);

			System.out.println("Connected to and created database " + dbName);

		} catch (InstantiationException ie) {
			System.err.println("\nUnable to instantiate the JDBC driver "
					+ driver);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err.println("\nNot allowed to access the JDBC driver "
					+ driver);
			iae.printStackTrace(System.err);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public void listen() {
		Scanner scan;
		PrintWriter pw;
		String response = "";

		try {
			ServerSocket serve = new ServerSocket(400);
			Text.setText(serve.getInetAddress().toString());
			// System.out.println(serve);
			System.out.println(serve.getInetAddress().getLocalHost());
			Text.setText("waiting");
			System.out.println("Waiting");
			lbl_IP.setText(serve.getInetAddress().getLocalHost().toString());

			Socket s = serve.accept();
			Text.setText("Accepted");

			System.out.println("Accepted");

			// try{
			// in = new BufferedReader(new
			// InputStreamReader(s.getInputStream()));
			// } catch (IOException e) {
			// }

			scan = new Scanner(s.getInputStream());
			pw = new PrintWriter(s.getOutputStream(), true);

			// for (;;) {
			if (scan.hasNext()) {
				newmsg = scan.nextLine();
				response = process(newmsg);

				if (response.length() > 0) {
					pw.println(response);
				}
				message = message + newmsg + "\n";
				Text.setText(newmsg);

				message = message + newmsg;
				jTextArea1.setText(message);
				Text.revalidate();
				jTextArea1.revalidate();
				System.out.println(newmsg);
			}
			// }
			s.close();
			serve.close();
			System.out.println("Closed");

		} 
		catch (Exception e) {}
	}

	public String process(String msg) {
		String response = "";

		// Separate the command from the data
		String[] cmdMsg = msg.split(":");
		String[] data = cmdMsg[1].split(",");

		if (cmdMsg[0].equals("REPETOIRE")) {
			processRepetoire(data);
		}

		if (cmdMsg[0].equals("SIGHT")) {
			processSight(data);
		}

		if (cmdMsg[0].equals("SCALES")) {
			processScales(data);
		}

		if (cmdMsg[0].equals("ADD_STUDENT")) {
			processAddStudent(data);
		}

		if (cmdMsg[0].equals("GET_STUDENT")) {
			response = processGetStudent();
		}

		return response;
	}

	private void processRepetoire(String[] data) {
		PreparedStatement ps;
		ResultSet student;
		String name ="";
		
		System.out.println("Processing: Repetoire\n");

		for (int i = 0; i < data.length; i++) {
			System.out.println(i + ":" + data[i] + "\n");
		}

		try {

			ps = con.prepareStatement("INSERT INTO CSUNI.REPETOIRE"
					+ "(PIECENAME, MUSICALITY, MEMORIZATION, TECHNIQUE, IS_STUDENT, STUDENT_IDSTUDENT, REC_DATE )"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			try {
				for (int i = 0; i < data.length; i++)
					ps.setString(i + 1, data[i]);
				java.sql.Date javaSqlDate = new java.sql.Date(System.currentTimeMillis());
				ps.setDate(data.length+1, javaSqlDate);

				ps.executeUpdate();
				con.commit();
			} catch (SQLException e) {
				System.out.println(e);
				System.out.println("Duplicate Entry");
			}

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// now Display the chart
		//demo.dispose();
/*
try {

			ps = con.prepareStatement("SELECT * FROM CSUNI.STUDENT");
				//	+ " WHERE IDSTUDENT = 4");
			
			student = ps.executeQuery();
		    name = student.getString(1) + " " + student.getString(2);
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Bad Student");
		}
*/
		RepetoireChart demo = new RepetoireChart("Repetoire", data[0]+ ":" + name );
        demo.pack();
        demo.setVisible(true);
	}

		
	private void processSight(String[] data) {
		PreparedStatement ps;

		System.out.println("Processing: SIGHT\n");

		for (int i = 0; i < data.length; i++) {
			System.out.println(i + ":" + data[i] + "\n");
		}

		try {

			ps = con.prepareStatement("INSERT INTO CSUNI.SIGHT_READING"
					+ "(EYES_ON_MUSIC, LOOKING_AHEAD, ANALYSIS, TEMPO, PULSE, TONE, FINGERING, OTHER, IS_STUDENT, STUDENT_IDSTUDENT, REC_DATE )"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			try {
				for (int i = 0; i < data.length; i++)
					ps.setString(i + 1, data[i]);
				java.sql.Date javaSqlDate = new java.sql.Date(System.currentTimeMillis());
				ps.setDate(data.length+1, javaSqlDate);

				ps.executeUpdate();
				con.commit();
			} catch (SQLException e) {
				System.out.println(e);
				System.out.println("Duplicate Entry");
			}

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void processScales(String[] data) {
		PreparedStatement ps;

		System.out.println("Processing: Scales\n");

		for (int i = 0; i < data.length; i++) {
			System.out.println(i + ":" + data[i] + "\n");
		}

		try {

			ps = con.prepareStatement("INSERT INTO CSUNI.SCALES"
					+ "(PROFICIENCY, TEMPO, FINGERING, IS_STUDENT, STUDENT_IDSTUDENT, REC_DATE )"
					+ " VALUES (?, ?, ?, ?, ?, ?)");

			try {
				for (int i = 0; i < data.length; i++)
					ps.setString(i + 1, data[i]);
				java.sql.Date javaSqlDate = new java.sql.Date(System.currentTimeMillis());
				ps.setDate(data.length+1, javaSqlDate);

				ps.executeUpdate();
				con.commit();
			} catch (SQLException e) {
				System.out.println(e);
				System.out.println("Duplicate Entry");
			}

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void processAddStudent(String[] data) {
		PreparedStatement ps;

		System.out.println("Processing: Add Students\n");

		for (int i = 0; i < data.length; i++) {
			System.out.println(i + ":" + data[i] + "\n");
		}

		try {

			ps = con.prepareStatement("INSERT INTO CSUNI.STUDENT"
					+ "(FName, LName) VALUES (?, ?)");

			try {
				ps.setString(1, data[0]);
				ps.setString(2, data[1]);

				ps.executeUpdate();
				con.commit();
			} catch (SQLException e) {
				System.out.println("Duplicate Entry");
			}

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String processGetStudent() {

		String response = "";

		System.out.println("Processing: Get Students\n");

		String query = "SELECT * FROM CSUNI.STUDENT ORDER BY LNAME";
		try {
			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				String id = rs.getString(1);
				String fname = rs.getString(2);
				String lname = rs.getString(3);
				String year = rs.getString(4);

				System.out
						.println(id + "," + lname + ", " + fname + "," + year);

				response += id + "," + lname + "," + fname + "\n";

			} // end while
		} // end try

		catch (SQLException e) {
			e.printStackTrace();
		}

		return response;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel Text;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextArea1;
	private javax.swing.JLabel lbl_IP;
	// End of variables declaration//GEN-END:variables
}