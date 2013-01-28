package org.csuni.PianoApp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;

import org.csuni.PianoApp.R;

public class PianoMetrics extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private SharedPreferences mySharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		int bg;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metrics);

		View scalesButton = findViewById(R.id.btn_Scales);
		View sightButton = findViewById(R.id.btn_SightReading);
		View repetoireButton = findViewById(R.id.btn_Repetoire);

		scalesButton.setOnClickListener(this);
		sightButton.setOnClickListener(this);
		repetoireButton.setOnClickListener(this);
		
		mySharedPreferences = getSharedPreferences("MY_PREFS",
				Activity.MODE_PRIVATE);
		bg = mySharedPreferences.getInt("bg", 0);
		View mlayout = findViewById(R.id.metricsBG);
		Log.d("QuickSocket", "BG " + bg);
		int color = getResources().getColor(bg);
		mlayout.setBackgroundColor(color);

		TextView studentLbl = (TextView) findViewById(R.id.studentName);
		studentLbl.setText(mySharedPreferences.getString("ActiveStudentName", ""));
	}

	public void onClick(View v) { 
		Intent i;
		switch (v.getId()) {
		case R.id.btn_Repetoire:
			Log.d("QuickSocket", "Repetoire");
			i = new Intent(this, Repetoire.class);
			startActivity(i);
			break;

		case R.id.btn_Scales:
			Log.d("QuickSocket", "Scales");
			i = new Intent(this, Scales.class);
			startActivity(i);
			break;
			
		case R.id.btn_SightReading:
			Log.d("QuickSocket", "SightReading");
			i = new Intent(this, Sight.class);
			startActivity(i);
			break;

		}
	}


/*	public void onActivityResult(int requestCode, int resultCode, Intent r) {
		super.onActivityResult(requestCode, resultCode, r);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case SHOW_REPETOIRE:
				String pname = r.getStringExtra("Name");
				int musicality = r.getIntExtra("Mus", 0);
				int memorization = r.getIntExtra("Mem", 0);
				int technique = r.getIntExtra("Tec", 0);

				connect();
				Log.d("QuickSocket", "Send Clicked");
				print.println("REPETOIRE:"+ pname + "," + musicality + "," + memorization
						+ "," + technique);
				disconnect();
				break;
			}
		}


	}
		*/
}