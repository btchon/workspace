package com.clicker.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class ClickPadActivity.
 */
public class ClickPadActivity extends AbstractQuestionActivity
{
    
    private static final String COLOR = "color";

    private static final String FLAGS = "flags";

    private static final String QUESTION_NUMBER = "questionNumber";

    private static final String VALUES = "values";

    /** The Constant EVERYONE_GROUP_FLAG. */
    protected final static String EVERYONE_GROUP_FLAG = "e";
    
    /** The Constant EVERYONE_GROUP_STRING. */
    protected final static String EVERYONE_GROUP_STRING = "Everyone";
    
    /** The Constant SEMI_COLON_SEPARATOR. */
    protected final static String SEMI_COLON_SEPARATOR = "`/;";
    
    /** The Constant COMMA_SEPARATOR. */
    protected final static String COMMA_SEPARATOR      = "`/,";
    
    /** The Constant COLON_SEPARATOR. */
    protected final static String COLON_SEPARATOR      = "`/:";
    
    /** The Constant TILDE_SEPARATOR. */
    protected final static String TILDE_SEPARATOR      = "`/~";
    
    /** The Constant CARET_SEPARATOR. */
    protected final static String CARET_SEPARATOR      = "`/^";
    
    /** The Constant VIBRATE_MSG. */
    public static final int VIBRATE_MSG = 1000;
    
    /** The Constant LEFT_DOWN_MSG. */
    public static final int LEFT_DOWN_MSG = 100;
    
    /** The Constant RIGHT_DOWN_MSG. */
    public static final int RIGHT_DOWN_MSG = 150;
    
    /** The Constant LEFT_UP_MSG. */
    public static final int LEFT_UP_MSG = 200;
    
    /** The Constant RIGHT_UP_MSG. */
    public static final int RIGHT_UP_MSG = 250;
    
    /** The Constant LEFT_CLICK_MSG. */
    public static final int LEFT_CLICK_MSG = 300;
    
    /** The Constant RIGHT_CLICK_MSG. */
    public static final int RIGHT_CLICK_MSG = 350;
    
    /** The MOUS e_ movemen t_ event. */
    private final byte MOUSE_MOVEMENT_EVENT = 0;
    
    /** The MOUS e_ butto n_ event. */
    private final byte MOUSE_BUTTON_EVENT = 1;
    
    /** The KEYBOAR d_ event. */
    private final byte KEYBOARD_EVENT = 2;
    
    /** The LEF t_ mous e_ click. */
    private final byte LEFT_MOUSE_CLICK = 0;
    
    /** The RIGH t_ mous e_ click. */
    private final byte RIGHT_MOUSE_CLICK = 1;
    
    /** The LEF t_ mous e_ down. */
    private final byte LEFT_MOUSE_DOWN = 2;
    
    /** The LEF t_ mous e_ up. */
    private final byte LEFT_MOUSE_UP = 3;
    
    /** The RIGH t_ mous e_ down. */
    private final byte RIGHT_MOUSE_DOWN = 4;
    
    /** The RIGH t_ mous e_ up. */
    private final byte RIGHT_MOUSE_UP = 5;
    
    /** The UNUSED. */
    private final byte UNUSED = 0;
    
    /** The SENSITIVITY. */
    private final float SENSITIVITY = 1.6f;
    
    /** The et advanced text. */
    private EditText etAdvancedText;
    
    /** The changed. */
    private String changed;
    
    /** The x coord. */
    private float xCoord;
    
    /** The y coord. */
    private float yCoord;

    /** The c. */
    private ClickerClient c;
    
    /** The message handler. */
    private Handler messageHandler;
    
    /** The left down. */
    private boolean leftDown;
    
    /** The right down. */
    private boolean rightDown;
    
    /** The m gesture detector. */
    private GestureDetector mGestureDetector;

    /** The back. */
    private RelativeLayout back;
    
    /** The ip. */
    private InetAddress ip;
    
    /** The port. */
    private int port;
    //TODO restore clickpad to working order, last working copy: revision 84
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
    
    /**
     * Instantiates a new click pad activity.
     */
    public ClickPadActivity() 
    {

        leftDown = false;
        rightDown = false;
        messageHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg) 
            {
                switch (msg.what) 
                {
                    case VIBRATE_MSG:
                        feedback();
                        break;
                    case LEFT_DOWN_MSG:
                        leftButtonDown();
                        break;
                    case RIGHT_DOWN_MSG:
                        rightButtonDown();
                        break;
                    case LEFT_UP_MSG:
                        leftButtonUp();
                        break;
                    case RIGHT_UP_MSG:
                        rightButtonUp();
                        break;
                    case LEFT_CLICK_MSG:
                        leftClick();
                        break;
                    case RIGHT_CLICK_MSG:
                        rightClick();
                        break;
                }
            }
        };
        mGestureDetector = new GestureDetector( new ClickGestureListener(messageHandler));
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        app = (ClickerClientApp)getApplication();
        clickerClient =app.getClickerClient(); 
        
        Bundle b = this.getIntent().getExtras();
        values = b.getString(VALUES);
        questionNumber = b.getString(QUESTION_NUMBER);
        flags = b.getString(FLAGS);
        color = b.getString(COLOR);
        
        processFlags();	
        values= "";
        setHandler();
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
        String[] flagTokens = flags.split(COMMA_SEPARATOR);
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
    protected void setFlags(){}
    
    /**
     * Switch flags.
     *
     * @param flags the flags
     */
    protected void switchFlags(int flags){}
    
    public void removeView(int i){}
    
    public void resendLast(){}
    
    protected void setView()
    {
        this.setContentView(R.layout.click_pad_layout);
        back = (RelativeLayout)findViewById(R.id.click_pad_relative_layout);
    }
    
    public void closeActivity()
    {
        finish();
    }
    
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
    
    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
        if(mGestureDetector.onTouchEvent(event))
        {
            return true;
        }
        
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) 
        {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                rightClick();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                rightButtonUp();
                break;
        }
        return true;
    }	
    
    protected void createViews() {}
    
    /**
     * Touch_start.
     *
     * @param x the x
     * @param y the y
     */
    private void touch_start(float x, float y) 
    {
        xCoord = x;
        yCoord = y;
    }
    
    /**
     * Touch_move.
     *
     * @param x the x
     * @param y the y
     */
    private void touch_move(float x, float y) 
    {
        sendEvent(MOUSE_MOVEMENT_EVENT, (byte)((x - xCoord) * SENSITIVITY), (byte)((y - yCoord) * SENSITIVITY));
        xCoord = x;
        yCoord = y;
    }
    
    /**
     * Touch_up.
     */
    private void touch_up() 
    {
        xCoord = 0.0f;
        yCoord = 0.0f;
        if (leftDown)
        {
            leftButtonUp();
        }
        if (rightDown)
        {
            rightButtonUp();
        }
    }
    /**
     * Feedback.
     */
    private void feedback(){}
    
    /**
     * Left click.
     */
    private void leftClick()
    {
        sendEvent(MOUSE_BUTTON_EVENT, UNUSED, LEFT_MOUSE_CLICK);
    }
    
    /**
     * Right click.
     */
    private void rightClick()
    {
        sendEvent(MOUSE_BUTTON_EVENT, UNUSED, RIGHT_MOUSE_CLICK);
    }
    
    /**
     * Left button down.
     */
    private void leftButtonDown()
    {
        sendEvent(MOUSE_BUTTON_EVENT, UNUSED, LEFT_MOUSE_DOWN);
        leftDown = true;
    }
    
    /**
     * Left button up.
     */
    private void leftButtonUp()
    {
        sendEvent(MOUSE_BUTTON_EVENT, UNUSED, LEFT_MOUSE_UP);
        leftDown = false;
    }
    
    /**
     * Right button down.
     */
    private void rightButtonDown()
    {
        sendEvent(MOUSE_BUTTON_EVENT, UNUSED, RIGHT_MOUSE_DOWN);
        rightDown = true;
    }
    
    /**
     * Right button up.
     */
    private void rightButtonUp()
    {
        sendEvent(MOUSE_BUTTON_EVENT, UNUSED, RIGHT_MOUSE_UP);
        rightDown = false;
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        app.setSubHandler(activityHandler);
        app.setCurrentActivity(this);
    }
    
    /**
     * Send event.
     *
     * @param deviceCode the device code
     * @param x the x
     * @param y the y
     */
    private void sendEvent(byte deviceCode, byte x, byte y)
    {
        String group = "";
        if(everyoneGroupFlagSet)
        {
            group = EVERYONE_GROUP_STRING;
        } else 
        {
            group = app.getGroup();
        }

        String val = deviceCode + COLON_SEPARATOR + x + COLON_SEPARATOR + y;

        app.getPushPrintWriter().println(group+SEMI_COLON_SEPARATOR+questionNumber +
                SEMI_COLON_SEPARATOR + val);
        app.getPushPrintWriter().flush();
    }
    
}

