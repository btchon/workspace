package com.clicker.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Auto-generated Javadoc
/**
 * The Class ClickerClient.
 */
public class ClickerClient extends Activity 
{
    
    private static final String FLAGS = "flags";

    private static final String VALUES = "values";

    private static final String UNGROUPED = "Ungrouped";

    //TODO: keep version updated
    /** The Constant VERSION. */
    protected static final String VERSION = "v0.13013";
    
    /** The Constant TAG. */
    protected static final String TAG = "SOCKET";
    
    // static constants for messages are in ClickerConstants
    
    // activity result codes
    /** The Constant PULL_MODE_QR_SCAN. */
    protected static final int PULL_MODE_QR_SCAN = 2;
    
    /** The Constant NAME_AND_PREFERENCES_UPDATE. */
    protected static final int NAME_AND_PREFERENCES_UPDATE = 1;
    
    /** The Constant PUSH_MODE_QR_SCAN. */
    protected static final int PUSH_MODE_QR_SCAN = 0;
    
    /** The Constant SEMI_COLON_SEPARATOR. */
    protected static final String SEMI_COLON_SEPARATOR = "`/;";
    
    /** The Constant COLON_SEPARATOR. */
    protected static final String COLON_SEPARATOR = "`/:";
    
    /** The Constant COMMA_SEPARATOR. */
    protected static final String COMMA_SEPARATOR = "`/,";
    
    /** The app. */
    protected ClickerClientApp app;
    
    /** The port. */
    protected int port;
    
    /** The ip. */
    protected InetAddress ip;
    
    /** The ip string. */
    protected String ipString;
    
    /** The ip port. */
    protected int ipPort;
    
    /** The question room. */
    protected String questionRoom;
    
    /** The id. */
    protected String id;
    
    /** The MAC address. */
    protected String MACAddress;
    
    /** The inr. */
    protected BufferedReader inr;
    
    /** The text view. */
    protected TextView textView;
    
    /** The activity handler. */
    protected Handler activityHandler;
    
    /** The shared pref. */
    protected SharedPreferences sharedPref;
    
    /** The inflater. */
    protected LayoutInflater inflater;
    
    /** The connection string. */
    private String connectionString;
    
    /** The dialog. */
    private Dialog dialog;
    
    /**
     * Instantiates a new clicker client.
     */
    public ClickerClient() {}
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //		LinearLayout mainLinearLayout = (LinearLayout)findViewById(R.id.main_linear_layout);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        app = (ClickerClientApp)getApplication();
        app.setClickerClient(this);
        
        //Button bPushScan = (Button) findViewById(R.id.scan_button);
        Button bConnect = (Button) findViewById(R.id.connect_button);
        Button bExit = (Button) findViewById(R.id.exit_button);
        
        //ImageView image = (ImageView) findViewById(R.id.imageview);
        
        TextView versionLabel = (TextView) findViewById(R.id.clicker_version_label);
        versionLabel.setText("ClickerClient "+VERSION);
        
        textView = (TextView) findViewById(R.id.textview);
        
        //editText = (EditText) findViewById(R.id.edit_text);
        //editText.setText(sharedPref.getString(NamePreferences.LAST_CONNECTION,""));
        connectionString = sharedPref.getString(NamePreferences.LAST_CONNECTION, "");
        
        Button.OnClickListener mConnectionButtonListener = new Button.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                runConnectionDialogPrompt();
                
            }};
            
            Button.OnClickListener mExit = new Button.OnClickListener() {
                public void onClick(View v) {
                    // Log.d(TAG, "starting send");
                    try {
                        if (app.getPushSocket().isConnected()) {
                            app.disconnectServer();
                        }
                        finish();
                    } catch (Exception e) {
                        Log.d(TAG, "error on exit: " + e.getMessage());
                    }
                }
            };
            
            //bPushScan.setOnClickListener(mPushScan);
            bConnect.setOnClickListener(mConnectionButtonListener);
            bExit.setOnClickListener(mExit);
            
            
            
            // Messages are generated in the clickerclientapp
            activityHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    switch(msg.what){
                        case 0:
                            //Log.d(TAG,"message received inside clickerClient");
                    }
                }
            };
            
            //MAC address retrieved to use as a unique identifier on the server
            WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = manager.getConnectionInfo();
            MACAddress = wifiInfo.getMacAddress();
            //
            updateNameFields();
            if (id.startsWith("`")) 
            {//separator is now `/;
                Toast.makeText(this,
                        "Remember to set your name\nin the menu options.",
                        Toast.LENGTH_LONG).show();
            }
    }
    
    /**
     * Run connection dialog prompt.
     */
    protected void runConnectionDialogPrompt() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect_dialog_layout);
        dialog.setTitle("Select how you wish to connect:");
        dialog.show();
        Button qrscan = (Button) dialog.findViewById(R.id.connection_dialog_qrbutton);
        Button manual = (Button) dialog.findViewById(R.id.connection_dialog_manualbutton);
        Button cancel = (Button) dialog.findViewById(R.id.connection_dialog_cancelbutton);
        
        Button.OnClickListener mPushScan = new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
                // Scans a QR code, expecting an ip address:port:admin like
                // "1.2.3.4`/:4321`/:SEAN,group"
                startQRScanActivity(PUSH_MODE_QR_SCAN);
                dismissDialog();
            }
        };
        qrscan.setOnClickListener(mPushScan);
        
        Button.OnClickListener mConnectPrompt = new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
                //connectToServer(connectionString);
                runManualConnectionDialogPrompt();
            }
        };
        manual.setOnClickListener(mConnectPrompt);
        
        Button.OnClickListener mCancel = new Button.OnClickListener() 
        {
            
            @Override
            public void onClick(View arg0) 
            {
                dismissDialog();
            }};
            cancel.setOnClickListener(mCancel);
    }
    
    /**
     * Run manual connection dialog prompt.
     */
    protected void runManualConnectionDialogPrompt() {
        dialog.dismiss();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.manual_connection_dialog_layout);
        dialog.setTitle("Connection fields:");
        dialog.show();
        Button connect = (Button) dialog.findViewById(R.id.manual_connections_connect_button);
        Button cancel  = (Button) dialog.findViewById(R.id.manual_connections_cancel_button);
        EditText ipEditText = (EditText) dialog.findViewById(R.id.manual_connections_ipfield);
        EditText portEditText = (EditText) dialog.findViewById(R.id.manual_connections_portfield);
        EditText adminEditText = (EditText) dialog.findViewById(R.id.manual_connections_admin_namefield);
        EditText groupEditText = (EditText) dialog.findViewById(R.id.manual_connections_group_namefield);
        
        String[] connectionStringParts = connectionString.split(COLON_SEPARATOR);
        if(connectionStringParts.length >= 3)
        {
            ipEditText.setText(connectionStringParts[0]);
            portEditText.setText(connectionStringParts[1]);
            
            String[] adminAndGroup = connectionStringParts[2].split(COMMA_SEPARATOR);
            if(adminAndGroup.length > 1)
            {
                adminEditText.setText(adminAndGroup[0]);
                groupEditText.setText(adminAndGroup[1]);
            } 
            else 
            {
                adminEditText.setText(connectionStringParts[2]);
            }
        }
        
        Button.OnClickListener mConnect = new Button.OnClickListener() {
            public void onClick(View v) {
                EditText ipEditText = (EditText) dialog.findViewById(R.id.manual_connections_ipfield);
                EditText portEditText = (EditText) dialog.findViewById(R.id.manual_connections_portfield);
                EditText adminEditText = (EditText) dialog.findViewById(R.id.manual_connections_admin_namefield);
                EditText groupEditText = (EditText) dialog.findViewById(R.id.manual_connections_group_namefield);
                String str = ipEditText.getText().toString() + COLON_SEPARATOR;
                str = str + portEditText.getText().toString() + COLON_SEPARATOR;
                str = str + adminEditText.getText().toString();
                if(groupEditText.getText().toString().length() > 0){
                    str = str + COMMA_SEPARATOR + groupEditText.getText().toString();
                }
                connectionString = str;
                
                connectToServer(connectionString);
                dismissDialog();
            }
        };
        connect.setOnClickListener(mConnect);
        
        Button.OnClickListener mCancel = new Button.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                dismissDialog();
                
            }};
            cancel.setOnClickListener(mCancel);
            
    }
    
    /**
     * Dismiss dialog.
     */
    protected void dismissDialog()
    {
        if(dialog.isShowing())
        {
            dialog.dismiss();
        }
    }
    
    /**
     * Start qr scan activity.
     *
     * @param mode the mode
     */
    private void startQRScanActivity(int mode)
    {
        try
        {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra(
                    "com.google.zxing.client.android.SCAN.SCAN_MODE",
                    "QR_CODE_MODE");
            // QR Droid set as Default
            intent.setPackage("la.droid.qr");
            startActivityForResult(intent, mode);
        } 
        catch (ActivityNotFoundException e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to download \nQR Droid from the \nAndroid Market!")
            .setCancelable(false)
            .setPositiveButton("Ok",new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int id) 
                {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // startActivity(new Intent(this, NamePreferences.class));
                startActivityForResult(new Intent(this, NamePreferences.class),
                        NAME_AND_PREFERENCES_UPDATE);
                return true;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Currently, the name change and QR scanning are the only external
        // intents called
        if (requestCode == PUSH_MODE_QR_SCAN) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                // Handle successful scan
                //editText.setText(contents);
                connectionString = contents;
                String[] connectionStringParts = connectionString.split(COLON_SEPARATOR);
                if(connectionStringParts.length == 3){
                    connectToServer(contents);
                } else if (connectionStringParts.length == 4){
                    connectPullServer(contents);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Toast.makeText(this, "Scan failed. Action cancelled.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PULL_MODE_QR_SCAN){
            if(resultCode == RESULT_OK){
                String contents = intent.getStringExtra("SCAN_RESULT");
                connectPullServer(contents);
            }  else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Scan failed. Action cancelled.",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == NAME_AND_PREFERENCES_UPDATE) {
            // NAME AND PREFERENCE UPDATE is on returning from the name changing
            // menu
            updateNameFields();
            connectionString = sharedPref.getString("listPref", "");
        }
    }
    
    /**
     * Update name fields.
     */
    private void updateNameFields() 
    {
        // Retrieves the stored user name and creates a unique identifier to
        // submit to the server
        String first = sharedPref.getString(NamePreferences.FIRST_KEY_MY_PREFERENCE, "");
        app.setUserName(first);
        app.setMACAddress(MACAddress);
        id = first + SEMI_COLON_SEPARATOR + MACAddress;
        textView.setText("Welcome " + first +".");
    }
    
    /**
     * Connect pull server.
     *
     * @param contents the contents
     */
    private void connectPullServer(String contents){
        String[] pullServerConnectionInfo = contents.split(COLON_SEPARATOR);
        if(pullServerConnectionInfo.length >= 4){
            try {
                app.setPullServerIP(InetAddress.getByName(pullServerConnectionInfo[0]));
            } catch (UnknownHostException e) {
                Log.d(TAG,"unknown host while trying to find pull server inetAddress");
                e.printStackTrace();
            }
            app.setPullServerPort(Integer.parseInt(pullServerConnectionInfo[1]));
            
            String[] adminAndGroup = pullServerConnectionInfo[2].split(COMMA_SEPARATOR);
            
            
            app.setAdminName(adminAndGroup[0]);
            try{
                app.setGroup(adminAndGroup[1]);
            } catch (ArrayIndexOutOfBoundsException e){
                app.setGroup(UNGROUPED);
            } catch (Exception e){
                Log.d(TAG,"error while parsing group during pull server connect: "+e.getMessage());
                e.printStackTrace();
            }
            
            app.connectToPullServer();
            
            //send name;mac;room;questionNumber
            try {
                app.getPullServerPrintWriter().println(app.getUserName()+SEMI_COLON_SEPARATOR
                        + app.getMACAddress() + SEMI_COLON_SEPARATOR + pullServerConnectionInfo[2]
                                + SEMI_COLON_SEPARATOR + pullServerConnectionInfo[3]);
                app.runTask(app.getPullServerBufferedReader());
                
                app.getPullSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Error while trying to close pull socket in clickerclient");
            } catch (NullPointerException e){
                e.printStackTrace();
                Toast.makeText(this, "Unable to connect.", Toast.LENGTH_LONG).show();
            }
        } else {
            insufficientParametersToast();
        }
    }
    
    /**
     * Insufficient parameters toast.
     */
    private void insufficientParametersToast(){
        Toast.makeText(this, "Insufficient parameters in the request.\nEnsure the QR code is correct and try again.", Toast.LENGTH_LONG).show();
    }
    
    /**
     * Connect to server.
     *
     * @param contents the contents
     */
    private void connectToServer(String contents) 
    {
    	String first = sharedPref.getString(NamePreferences.FIRST_KEY_MY_PREFERENCE, "");
    	if (first.length() == 0)
    	{
    		Toast.makeText(this, "You must have a name.\n You can set it in the menu.", Toast.LENGTH_LONG)
            .show();
    	}
    	else
    	{
        String[] ipPort = contents.toString().split(COLON_SEPARATOR);
        if(ipPort.length >= 3)
        {
            checkStoredConnections(contents);
            ipString = ipPort[0];
            port = Integer.parseInt(ipPort[1]);
            
            String[] adminAndGroup = ipPort[2].split(COMMA_SEPARATOR);
            
            app.setAdminName(adminAndGroup[0]);
            try
            {
                app.setGroup(adminAndGroup[1]);
            } 
            catch (ArrayIndexOutOfBoundsException e)
            {
                app.setGroup(UNGROUPED);
            } 
            catch (Exception e)
            {
                Log.d(TAG,"error while parsing group during push server connect: "+e.getMessage());
                e.printStackTrace();
            }
            questionRoom = app.getAdminName();
            //app.setQuestionRoom(questionRoom);
            try 
            {
                ip = InetAddress.getByName(ipString);
            } 
            catch (Exception e)
            {
                Log.d(TAG, "inetAddress creation exception");
            }
            try 
            {
                app.setConnectionInfo(ip, port);
                app.setPushSocket( new Socket(ip, port) );
                app.getPushSocket().setSoTimeout(90);
                app.getPushSocket().setKeepAlive(true);
            } catch (SocketException e) {
                Log.d(TAG, "timeout exception");
                e.printStackTrace();
            } catch (InterruptedIOException e) {
                Log.d(TAG, "interrupted io exception: " + e.getMessage());
            } catch (Exception e) {
                Log.d(TAG, "exception during connnect: " + e.getMessage());
                e.printStackTrace();
            }
            if (app.getPushSocket() != null) 
            {
                if (app.getPushSocket().isConnected())
                {
                    Toast.makeText(this, "Connected to server", Toast.LENGTH_SHORT)
                    .show();
                    
                    try {
                        OutputStream out = app.getPushSocket().getOutputStream();
                        PrintWriter outw = new PrintWriter(out, true);
                        app.setPushPrintWriter( outw );
                        InputStream in = app.getPushSocket().getInputStream();
                        inr = new BufferedReader(new InputStreamReader(in));
                        
                        app.setConnectString(id + SEMI_COLON_SEPARATOR + app.getAdminName() + COMMA_SEPARATOR + app.getGroup());
                        outw.println(app.getConnectString()); 
                        //identify self: name;mac;room to use
                        
                    } catch (Exception e) {
                        Log.d(TAG, "connect: error creating output/input streams: " + e.getMessage());
                    }
                    
                    app.setTaskThread( new TaskThread(this, app.getHandler(), inr) );
                    
                    app.setQuestionInPushMode(true);
                    SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();
                    sharedPrefEdit.putString(NamePreferences.LAST_CONNECTION, contents);
                    sharedPrefEdit.commit();
                    
                    app.resetHeartbeatWaiting();
                    app.setCurrentlyDisconnected(false);
                    //start new activity to wait for questions
                    startPushModeWaiting();
                    
                } else {
                    // Log.d(TAG, "not connected ://");
                    Toast.makeText(this, "Not connected to server",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "socket is null");
                Toast.makeText( this,
                        "Socket could not be connected.\nIs the server up and the IP correct?",
                        Toast.LENGTH_LONG).show();
            }
            
        } else {
            insufficientParametersToast();
        }
    	}
    }
    
    /**
     * Start push mode waiting.
     */
    protected void startPushModeWaiting(){
        Intent intent = new Intent(this, PushServerWaitingActivity.class); 
        startActivity(intent);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        app.setQuestionActive(false);
        app.setSubHandler(activityHandler);
        app.setCurrentActivity(this);
    }
    // Doesn't do anything
//    /**
//     * Open click pad.
//     *
//     * @param flags the flags
//     * @param port the port
//     */
//    protected void openClickPad(int flags, int port) 
//    {
//        Intent intent = new Intent(ClickerClient.this, ClickPadActivity.class); 
//        intent.putExtra(VALUES, ip.toString().substring(1)+","+port);
//        intent.putExtra(FLAGS, flags);
//        startActivity(intent);
//    }
    
    /**
     * Gets the clicker client.
     *
     * @return the clicker client
     */
    protected ClickerClient getClickerClient() {
        return this;
    }
    
    /**
     * Check stored connections.
     *
     * @param str the str
     */
    protected void checkStoredConnections(String str){
        boolean contains = false;
        
        String[] entries = new String[] {"", "", "", "", ""};
        entries[0] = sharedPref.getString(NamePreferences.FIRST_CONNECTION, "");
        entries[1] = sharedPref.getString(NamePreferences.SECOND_CONNECTION, "");
        entries[2] = sharedPref.getString(NamePreferences.THIRD_CONNECTION, "");
        entries[3] = sharedPref.getString(NamePreferences.FOURTH_CONNECTION, "");
        entries[4] = sharedPref.getString(NamePreferences.FIFTH_CONNECTION, "");
        
        for(String s: entries){
            if(s.equals(str)){
                contains = true;
            }
        }
        if(!contains){
            entries[4] = entries[3];
            entries[3] = entries[2];
            entries[2] = entries[1];
            entries[1] = entries[0];
            entries[0] = str;
        }	
        
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();
        sharedPrefEdit.putString(NamePreferences.FIRST_CONNECTION, entries[0]);
        sharedPrefEdit.putString(NamePreferences.SECOND_CONNECTION, entries[1]);
        sharedPrefEdit.putString(NamePreferences.THIRD_CONNECTION, entries[2]);
        sharedPrefEdit.putString(NamePreferences.FOURTH_CONNECTION, entries[3]);
        sharedPrefEdit.putString(NamePreferences.FIFTH_CONNECTION, entries[4]);
        sharedPrefEdit.commit();
    }
    
}
