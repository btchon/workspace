package org.csuni.PianoApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import java.io.PrintWriter;
import java.net.Socket;

import org.csuni.PianoApp.R;

public class PianoTracker extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	//Socket sock;
	SharedPreferences mySharedPreferences;
	PSConnection psc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mySharedPreferences = getSharedPreferences("MY_PREFS", Activity.MODE_PRIVATE);
		psc = new PSConnection();
		
		View studentButton = findViewById(R.id.btnStudent);
		View teacherButton = findViewById(R.id.btnTeacher);

		studentButton.setOnClickListener(this);
		teacherButton.setOnClickListener(this);
		
	}

	public void onClick(View v) { 
		
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		Intent i;
		String ip = "";
		
		switch (v.getId()) {
		case R.id.btnStudent:
			Log.d("QuickSocket", "Student");
			editor.putInt("bg",R.color.Gold);
			editor.putBoolean("isStudent", true);
			editor.commit();
			break;

		case R.id.btnTeacher:
			Log.d("QuickSocket", "Teacher");
			editor.putInt("bg",R.color.Purple);
			editor.putBoolean("isStudent",false);
			editor.commit();
			break;	
		}
						
		if (psc.connect(mySharedPreferences.getString("ip",""))){
			Log.d("QuickSocket", "Connection Established");
			psc.disconnect();
			i = new Intent(this, SelectStudent.class);
		}
		else
		{
			Log.d("QuickSocket", "Needs Connection");
			i = new Intent(this, Connect.class);
		}
		startActivity(i);	
	}
}