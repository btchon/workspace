package org.csuni.PianoApp;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;



public class Scales extends Activity implements OnClickListener {

	private SharedPreferences mySharedPreferences;
	private String ip;
	private PSConnection psc;
	private boolean isStudent;
	private int studentID;
	
	
	public void onCreate(Bundle savedInstanceState) {
		int bg;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scales);
		findViewById(R.id.btn_submit).setOnClickListener(this);
		
		mySharedPreferences = getSharedPreferences("MY_PREFS",
				Activity.MODE_PRIVATE);
		isStudent = mySharedPreferences.getBoolean("isStudent",false);
		studentID = mySharedPreferences.getInt("ActiveStudentID",0);
		bg = mySharedPreferences.getInt("bg", 0);
		View mlayout = findViewById(R.id.ScalesBG);
		Log.d("QuickSocket", "BG " + bg);
		int color = getResources().getColor(bg);
		mlayout.setBackgroundColor(color);

		ip = mySharedPreferences.getString("ip", "0.0.0.0");
		psc = new PSConnection(ip);
		
	}
	@Override
	public void onClick(View v) {
		int isStud;
		switch (v.getId()) {
		case R.id.btn_submit:
			int proficiency = ((NumberPicker)findViewById(R.id.pick_proficiency)).getCurrent();
			int tempo = ((NumberPicker)findViewById(R.id.pick_tempo)).getCurrent();
			int fingering = ((NumberPicker)findViewById(R.id.pick_fingering)).getCurrent();
								
			if (psc.connect()){
				Log.d("QuickSocket", "Submit Scales");
				isStud = isStudent ? 1:0;
				psc.send("SCALES:"+ proficiency + "," + tempo + "," + fingering
					+ ","+ isStud+ ","+ studentID);
				psc.disconnect();
			}
			
			finish();
			break;
		}		
	}

}
