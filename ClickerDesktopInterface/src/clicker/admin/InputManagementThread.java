package clicker.admin;

import java.io.IOException;
import clicker.constants.Constants;

public class InputManagementThread 
{
    
    Thread thread;
    protected boolean runThread;
    private CommunicationHub hub;
    
    public InputManagementThread() 
    {
        runThread = true;
        hub = CommunicationHub.getInstance();
        thread = new Thread( new InputThread() );
        thread.start();
        System.out.println("IMT Starting...");
    }
    
    public void stop() 
    {
        runThread = false;
    }
    
    private class InputThread implements Runnable 
    {

        @Override
        public void run() 
        {
            String str = "";
            while (runThread) 
            {
                try 
                {
                    str = hub.readMessage();
                    if (str == null) 
                    {
                    	System.out.println("Hub - IMT was disconnected");
                        hub.gotDisconnected();
                        break;
                    } 
                    else if (str.equals(Constants.SERVER_HEARTBEAT_RESPONSE))
                    {
                        hub.receivedHeartbeat();
                    } 
                    else 
                    {
                        String[] parts = str.split(Constants.GRAVE_SEPARATOR);
                        if (parts[0].equals(Constants.ALL_SETS)) 
                        {
                            if (parts.length > 1) 
                            {
                                hub.allSetsReceived(parts[1]);
                            }
                            
                        } 
                        else if (parts[0].equals(Constants.QUESTION_SET)) 
                        {
                            String setData = "";
                            if (parts.length > 1) 
                            {
                                setData = parts[1];
                            }
                            hub.questionSetReceived(setData);
                        } 
                        else if (parts[0].equals(Constants.CLIENT_LIST)) 
                        {
                            hub.updateGroupsInClassModel(parts[1]);
                        } 
                        else if (parts[0].equals(Constants.GROUP_LIST)) 
                        {
                            String[] groupNames = parts[1].split(Constants.SEMI_COLON_SEPARATOR);
                            // DOESNT DO ANYTHING RIGHT NOW
                        }
                        else if (parts[0].equals(Constants.DISPLAY_CONNECTED)) 
                        {
                        	hub.updateDisplayInHub(parts[1]);
                        }
                    }
                    Thread.sleep(100);
                } 
                catch (IOException e) {} 
                catch (InterruptedException e) {}
            }
        } 
    }




}
