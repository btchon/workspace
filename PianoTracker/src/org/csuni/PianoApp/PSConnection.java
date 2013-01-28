package org.csuni.PianoApp;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import android.util.Log;


public class PSConnection 
{
	private Socket sock;
	private PrintWriter pw;
	private String ip;
	public static final int port = 400;
	private boolean isConnected;
	private Scanner scan;

	/**
	 * Sets the connection flag;
	 */
	public PSConnection() {
		isConnected = false;
		ip = "";
	}

	public PSConnection(String ip){
		isConnected = false;
		this.ip = ip;
	}
	
	public void setIP(String ip) {
		this.ip = ip;
	}

	public String getIP() {
		return ip;
	}

	public boolean connect() {
		try {
			if (!ip.equals("")) {
				sock = new Socket(ip, 400);
				pw = new PrintWriter(sock.getOutputStream(), true);
				scan = new Scanner(sock.getInputStream());
				isConnected = true;
				return true;
			} else {
				return false;
			}
		} catch (Exception exp) {
			isConnected = false;
			Log.d("QuickSocket", "Bad Connect");
			return false;
		}
	}
	
	public boolean connect(String addr) {
		ip = addr;
		return connect();
		
	}
	
	public void disconnect() {
		try {
			sock.close();
			isConnected=false;
		} catch (Exception exp) {

			Log.d("QuickSocket", "Bad Close");
		}
	}
	
	public void send(String msg){
		pw.println(msg);
	}
	public boolean hasNext(){
		return scan.hasNext();
	}
	
	public String get(){
		return scan.nextLine();
	}

}
