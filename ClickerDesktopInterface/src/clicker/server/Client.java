package clicker.server;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import clicker.constants.Constants;


// TODO: Auto-generated Javadoc
/**
 * The Class Client.
 * 
 * @author Aaron
 */
public class Client 
{
    /** The Constant heartbeatTime. */
    private static final int heartbeatTime = 32;
    
    /** The ID. */
    private final String clientId;
    
    /** The mac address. */
    private final String macAddress;
    
    /** The socket. */
    private Socket socket;
    
    /** The in. */
    private BufferedReader in;
    
    /** The out. */
    private PrintWriter out;
    
    /** The current answer. */
    private final String currentAnswer;
    
    /** The admin. */
    private final PushAdmin admin;
    
    /** The command handler. */
    private CommandHandler commandHandler;
    
    /** The group. */
    private String group;
    
    /** The timer. */
    private Timer timer;
    
    public Client(final String ID,
            final String macAddress,
            final Socket socket,
            final BufferedReader in,
            final PrintWriter out,
            final PushAdmin admin) 
    {
        this.clientId = ID;
        this.macAddress = macAddress;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.admin = admin;
        currentAnswer = " ";
        group = "";
        commandHandler = new CommandHandler();
        timer = new Timer();
        new Thread(commandHandler).start();
        timer.schedule(new PauseTask(), heartbeatTime * 1000);
        
    }
    
    public String getID() 
    {
        return clientId;
    }
    
    public String getMacAddress() 
    {
        return macAddress;
    }
    
    public Socket getSocket() 
    {
        return socket;
    }
    
    public BufferedReader getReader() 
    {
        return in;
    }
    
    public PrintWriter getWriter() 
    {
        return out;
    }
    
    public String getAnswer() 
    {
        return currentAnswer;
    }
    
    public String getGroup() 
    {
        return group;
    }
    
    public void clientReconnected(final Socket newSocket,
            final BufferedReader newIn,
            final PrintWriter newOut)
    {
        socket = newSocket;
        in = newIn;
        out = newOut;
        commandHandler = new CommandHandler();
        timer = new Timer();
        new Thread(commandHandler).start();
        timer.schedule(new PauseTask(), heartbeatTime * 1000);
    }
    
    public void setSocket(final Socket socket) 
    {
        this.socket = socket;
    }
    
    public void setGroup(final String groupName) 
    {
        group = groupName;
        sendMessage(Constants.SYSTEM + Constants.SEMI_COLON_SEPARATOR + 
                Constants.GROUP + Constants.SEMI_COLON_SEPARATOR + groupName);
    }
    
    public void unsetGroup() 
    {
        group = "";
        sendMessage(Constants.SYSTEM + Constants.SEMI_COLON_SEPARATOR + 
                Constants.GROUP + Constants.SEMI_COLON_SEPARATOR + Constants.UNGROUPED);
    }
    
    public boolean isPaused() 
    {
        return commandHandler.isPaused();
    }
    
    public void startQuestion(final String openMessage) 
    {
        sendMessage(openMessage);
    }
    
    public void stopQuestion() 
    {
        sendMessage(Constants.CLOSE);
    }
    
    private void sendMessage(final String message) 
    {
        out.println(message);
        out.flush();
    }
    
    private class PauseTask extends TimerTask 
    {

        @Override
        public void run() 
        {
            commandHandler.pause();
            System.out.println("No heartbeat from client " + clientId + ", pausing now");
        }
    }
    
    private class CommandHandler implements Runnable 
    {
        
        /** The paused. */
        private boolean paused = false;
        
        @Override
        public void run() 
        {
            while (!paused) 
            {
            	
                try 
                {
                    final String command = in.readLine();
                    if (command == null) 
                    {
                        pause();
                        // start a paused timer and when it expires call client
                        // died?
                        System.out.println("Client " + clientId + " is paused");
                    } 
                    else 
                    {
                        if (command.equals(Constants.CLIENT_HEARTBEAT_REQUEST)) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(new PauseTask(), heartbeatTime * 1000);
                            sendMessage(Constants.SERVER_HEARTBEAT_RESPONSE);
                            System.out.println("Received heartbeat from " + clientId + " and reset timer");
                        } 
                        else
                        {
                            admin.updateAnswer(clientId + Constants.SEMI_COLON_SEPARATOR + command);
                        }
                    }
                } 
                catch (final SocketTimeoutException e) {} 
                catch (final IOException e) 
                {
                    pause();
                    System.out.println(e.getMessage());
                    System.out.println("Client " + clientId + " is paused");
                }
                try 
                {
                    Thread.sleep(100);
                } 
                catch (final InterruptedException e) {}
            }
            try 
            {
                Thread.sleep(100);
            } 
            catch (final InterruptedException e) {}
        }
        
        /**
         * Unpause.
         */
        public void unpause() 
        {
            paused = false;
            System.out.println("Client " + clientId + " just got unpaused");
        }
        
        /**
         * Pause.
         */
        public void pause() 
        {
            paused = true;
            timer.cancel();
            System.out.println("Just paused client " + clientId);
        }
        
        /**
         * Checks if is paused.
         * 
         * @return true, if is paused
         */
        public boolean isPaused() 
        {
            return paused;
        }
    }
}
