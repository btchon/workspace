package org.csuni.PianoApp;


	import android.app.Activity;
	import android.content.Intent;
	import android.content.SharedPreferences;
import android.graphics.Color;
	import android.os.Bundle;
	import android.view.View;
	import android.util.Log;
	import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

	import java.io.PrintWriter;
import java.net.InetAddress;
	import java.net.Socket;

import org.csuni.PianoApp.R;


	public class Connect extends Activity implements OnClickListener 
	{
		String ip;
		private PSConnection psc;
		private  SharedPreferences mySharedPreferences;
		private EditText ipText;
		
		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			int bg;
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.connect);
			mySharedPreferences = getSharedPreferences("MY_PREFS", Activity.MODE_PRIVATE);
			
			bg = mySharedPreferences.getInt("bg", 0);
			View mlayout = findViewById(R.id.connectBG);
			Log.d("QuickSocket", "BG " + bg);
			int color = getResources().getColor(bg);
			mlayout.setBackgroundColor(color);	

			ip = mySharedPreferences.getString("ip", "0.0.0.0");
			
			ipText = (EditText)findViewById(R.id.edIP);
			ipText.setText(ip);
			
			View connectButton = findViewById(R.id.btnConnect);

			connectButton.setOnClickListener(this); 
		}

		public void onClick(View v) 
		{ 
			psc = new PSConnection(ip);	
			if (psc.connect(ipText.getText().toString())){
				Log.d("QuickSocket", "Connection Established");
				
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putString("ip",ipText.getText().toString());
				editor.commit();
				psc.disconnect();
				Intent i = new Intent(this, SelectStudent.class);
				startActivity(i);
			}
			else {
				Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
			}
		}
	}

