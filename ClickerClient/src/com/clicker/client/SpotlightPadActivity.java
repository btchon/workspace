package com.clicker.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class ClickPadActivity.
 */
public class SpotlightPadActivity extends AbstractQuestionActivity
{
    private static final String COLOR = "color";
    private static final String FLAGS = "flags";
    private static final String QUESTION_NUMBER = "questionNumber";
    private static final String VALUES = "values";
    protected final static String EVERYONE_GROUP_FLAG = "e";
    protected final static String EVERYONE_GROUP_STRING = "Everyone";
    
    public static final int VIBRATE_MSG = 1000;
    public static final int LEFT_DOWN_MSG = 100;
    public static final int RIGHT_DOWN_MSG = 150;
    public static final int LEFT_UP_MSG = 200;
    public static final int RIGHT_UP_MSG = 250;
    public static final int LEFT_CLICK_MSG = 300;
    public static final int RIGHT_CLICK_MSG = 350;

    /** The ip. */
    private InetAddress ip;
    
    /** The port. */
    private int port;

    /** The app. */
    private ClickerClientApp app;
    
    /** The clicker client. */
    private ClickerClient clickerClient;
    
    /** The values. */
    private String values;
    
    /** The question number. */
    private String questionNumber;
    
    /** The flags. */
    private String flags;
    
    /** The color. */
    private String color;
    
    /** The everyone group flag set. */
    private boolean everyoneGroupFlagSet;

    private Handler activityHandler;
    
    private String dimensions;
    
    private Point previousPoint = new Point(0, 0);

    public SpotlightPadActivity() 
    { 

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        app = (ClickerClientApp)getApplication();
        clickerClient = app.getClickerClient(); 
        
        Bundle b = this.getIntent().getExtras();
        values = b.getString(VALUES);
        questionNumber = b.getString(QUESTION_NUMBER);
        flags = b.getString(FLAGS);
        color = b.getString(COLOR);
        
        processFlags();

        values= "";
        setHandler(); 
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spotlight_pad_layout);
        final TextView textView = (TextView)findViewById(R.id.textView);

        final View touchView = findViewById(R.id.touchView);
        // Subtract 50 from the width to account for the title bar
        dimensions = getWindowManager().getDefaultDisplay().getWidth() + "x" + (getWindowManager().getDefaultDisplay().getHeight() - 50); 
        

        touchView.setOnTouchListener(new View.OnTouchListener() 
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) 
            {
            	sendEvent((int)event.getX(), (int)event.getY());
                textView.setText("Touch coordinates : " +
                    String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                    return true;
            }
        });


    }

    protected void processValues()
    {
        String[] val = values.split(",");
        try 
        {
            ip = InetAddress.getByName(val[0]);
        } 
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        }
        port = Integer.parseInt(val[1]);
    }
    
    /**
     * Process flags.
     */
    protected void processFlags()
    {
        String[] flagTokens = flags.split(ClickerConstants.COMMA_SEPARATOR);
        for (String token : flagTokens)
        {
            if(token.equals(EVERYONE_GROUP_FLAG))
            {
                everyoneGroupFlagSet = true;
            }
        }
    }
    
    /**
     * Sets the flags.
     */
    protected void setFlags()
    {
        
    }
    
    /**
     * Switch flags.
     *
     * @param flags the flags
     */
    protected void switchFlags(int flags)
    {
        
    }

    public void removeView(int i)
    {
        
    }
    
    public void resendLast()
    {
        
    }
    

    protected void setView()
    {
        this.setContentView(R.layout.spotlight_pad_layout);
    }
 
    public void closeActivity()
    {
        finish();
    }
    
    /* (non-Javadoc)
     * @see com.clicker.client.AbstractQuestionActivity#setHandler()
     */
    protected void setHandler()
    {
        activityHandler = new Handler() 
        {
            public void handleMessage(android.os.Message msg) 
            {
                switch(msg.what)
                {
                    case ClickerConstants.CLOSE_DIALOG:
                        closeActivity();
                        break;
                }
            }
        };
    }

    protected void createViews() 
    {
        // do nothing
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        app.setSubHandler(activityHandler);
        app.setCurrentActivity(this);
    }

    private void sendEvent(int x, int y)
    {
    	if (previousPoint.x != x && previousPoint.y != y)
    	{
	        String group = "";
	        if(everyoneGroupFlagSet)
	        {
	            group = EVERYONE_GROUP_STRING;
	        } 
	        else 
	        {
	            group = app.getGroup();
	        }
	        
	        String val =  dimensions 
	        + ClickerConstants.COLON_SEPARATOR + x 
	        + ClickerConstants.COLON_SEPARATOR + y;
	        previousPoint = new Point(x, y);
	        
	        app.getPushPrintWriter().println(group + ClickerConstants.SEMI_COLON_SEPARATOR + questionNumber +
	        		ClickerConstants.SEMI_COLON_SEPARATOR + val);
	        app.getPushPrintWriter().flush();
	        
    	}
    	
//        Rect rectgle= new Rect();
//        Window window= getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
//        int StatusBarHeight= rectgle.top;
//        int contentViewTop= 
//            window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        int TitleBarHeight= contentViewTop - StatusBarHeight;
//
//        Log.i("Spotlight Clicker", "StatusBar Height= " + StatusBarHeight + " , TitleBar Height = " + TitleBarHeight);
           
    }
    
}

