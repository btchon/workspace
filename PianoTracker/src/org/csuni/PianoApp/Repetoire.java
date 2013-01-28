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



public class Repetoire extends Activity implements OnClickListener {

	private SharedPreferences mySharedPreferences;
	private String ip;
	private PSConnection psc;
	private boolean isStudent;
	private int studentID;
	
	
	public void onCreate(Bundle savedInstanceState) {
		int bg;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repetoire);
		findViewById(R.id.btn_submit).setOnClickListener(this);
		
		mySharedPreferences = getSharedPreferences("MY_PREFS",
				Activity.MODE_PRIVATE);
		isStudent = mySharedPreferences.getBoolean("isStudent",false);
		studentID = mySharedPreferences.getInt("ActiveStudentID",0);
		
		bg = mySharedPreferences.getInt("bg", 0);
		View mlayout = findViewById(R.id.RepetoireBG);
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
			EditText pieceName = ((EditText)findViewById(R.id.ed_pname));
			int musicality = ((NumberPicker)findViewById(R.id.pick_musicality)).getCurrent();
			int memorization = ((NumberPicker)findViewById(R.id.pick_memorization)).getCurrent();
			int technique = ((NumberPicker)findViewById(R.id.pick_technique)).getCurrent();
			
			String pname = pieceName.getText().toString();
						
			if (psc.connect()){
				Log.d("QuickSocket", "Submit Repetoire");
				isStud = isStudent ? 1:0;
				psc.send("REPETOIRE:" + pname + "," + musicality + "," + memorization
					+ "," + technique + ","+ isStud + ","+ studentID); 
				psc.disconnect();
			}
			
			finish();
			break;
		}		
	}

}
