package org.csuni.PianoApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.Socket;

import org.csuni.PianoApp.R;
import org.csuni.PianoData.*;


/**
 * Needs to connect to db to get a list of available students. Has the option to
 * add a new student to the db via an Option Menu
 * 
 * When selected - pass the student name (id?) into the task selection.
 * 
 * @author sthughes
 * 
 */

public class SelectStudent extends Activity implements OnClickListener, OnItemSelectedListener
{
	/** Called when the activity is first created. */

	private SharedPreferences mySharedPreferences;
	private String ip;
	private PSConnection psc;
	private Student[] students;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		int bg;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_select);

		mySharedPreferences = getSharedPreferences("MY_PREFS",
				Activity.MODE_PRIVATE);
		bg = mySharedPreferences.getInt("bg", 0);
		View mlayout = findViewById(R.id.StudentSelectBG);
		Log.d("QuickSocket", "BG " + bg);
		int color = getResources().getColor(bg);
		mlayout.setBackgroundColor(color);

		ip = mySharedPreferences.getString("ip", "0.0.0.0");
		psc = new PSConnection(ip);
		
		View continueButton = findViewById(R.id.btn_continue);
		continueButton.setOnClickListener(this); 

	}

	public void onResume() {
		super.onResume();
		populateSpinner();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.createmenu, menu);
		return true;
	}

	public void populateSpinner() {
		int numNames = 0;
   
		//unpleasant hack
		String[] temp = new String[100];

		if (psc.connect()) {
			Log.d("QuickSocket", "Connection Established;  Populate Spinner");
			psc.send("GET_STUDENT:" + " ");
			while (psc.hasNext())
				temp[numNames++] = psc.get(); 

			//copy into a properly sized array so it works with the adapter.
			students = new Student[numNames];
			for (int i = 0; i < numNames; i++){
				students[i] = new Student(temp[i]);
			}
			
			Spinner spin = (Spinner) findViewById(R.id.spinner1);
			spin.setOnItemSelectedListener(this);
			ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, students);
			
			aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin.setAdapter(aa);
			
			psc.disconnect();
		}
	}

	public void onClick(View v) {
		Intent i = new Intent(this, PianoMetrics.class);
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addStudentMenu:
			startActivity(new Intent(this, AddStudent.class));
			return true;
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putInt("ActiveStudentID",students[position].getId());
		editor.putString("ActiveStudentName",students[position].toString());
		editor.commit();
		
		Toast.makeText(this, "Student ID: " + students[position].getId(), Toast.LENGTH_LONG).show();
		//selection.setText(names[position]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
