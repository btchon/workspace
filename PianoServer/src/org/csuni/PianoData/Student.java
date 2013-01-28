/**
 * 
 */
package org.csuni.PianoData;

/**
 * @author sthughes
 *
 */
public class Student {

	private int id;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the lname
	 */
	public String getLname() {
		return lname;
	}

	/**
	 * @param lname the lname to set
	 */
	public void setLname(String lname) {
		this.lname = lname;
	}

	/**
	 * @return the fname
	 */
	public String getFname() {
		return fname;
	}

	/**
	 * @param fname the fname to set
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return lname + ", " + fname;
	}

	private String lname;
	private String fname;
	
	public Student(String data){
		// Expecting format:
		// id+ "," + lname +"," + fname +"\n"
		
		String[] s = data.split(",");
		
		id = Integer.parseInt(s[0]);
		lname = s[1];
		fname = s[2];
	}
}
