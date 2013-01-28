package org.csuni.PianoApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.io.PrintWriter;
import java.net.Socket;

import org.csuni.PianoApp.R;


public class AddStudent  extends Activity implements OnClickListener {
	
	private SharedPreferences mySharedPreferences;
	private String ip;
	private PSConnection psc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int bg;	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addstudent);
		
		mySharedPreferences = getSharedPreferences("MY_PREFS", Activity.MODE_PRIVATE);	
		bg = mySharedPreferences.getInt("bg", 0);
		View mlayout = findViewById(R.id.addStudentBG);
		Log.d("QuickSocket", "BG " + bg);
		int color = getResources().getColor(bg);
		mlayout.setBackgroundColor(color);	
		
		ip = mySharedPreferences.getString("ip", "0.0.0.0");
		psc = new PSConnection(ip);	
			
		View addStudentButton = findViewById(R.id.btn_addStudent);
		addStudentButton.setOnClickListener(this); 
	}
	
	public void onClick(View v) { 
		
		String fName, lName;
		
		if (psc.connect()){
			Log.d("QuickSocket", "Connection Established");
			fName = ((EditText)findViewById(R.id.edFName)).getText().toString();
			lName = ((EditText)findViewById(R.id.edLName)).getText().toString();			
			psc.send("ADD_STUDENT:"+ fName + "," + lName);
			psc.disconnect();
		}
		else
		{
			Log.d("QuickSocket", "Needs Connection");
			Intent i = new Intent(this, Repetoire.class);
		}
	}

}

