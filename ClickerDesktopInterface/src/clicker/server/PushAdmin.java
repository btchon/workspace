package clicker.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import clicker.constants.Constants;


// TODO: Auto-generated Javadoc
/**
 * The Class PushAdmin.
 */
public class PushAdmin 
{
    /** The Constant heartbeatTime. 
     * <br>This is the maximum amount of time that the server will 
     * wait for an admin to have sent a heartbeat request. If this time 
     * has elapsed without having recieved a request, the admin is 
     * considered to have disconnected and the connection will be closed.*/
    private static final int heartbeatTimeSeconds = 32;
    
    /** The map of clients. <br>Maps(Name:String, Client:client)*/
    private final Map<String, Client> clientMap;
    
    /** The map of groups.<br>
     * Maps group name to an arraylist of the client names in the group */
    private final Map<String, ArrayList<String>> groupMap;
    
    /** The question for everyone. */
    private String questionForEveryone;
    
    /** The group questions. */
    private final Map<String, String> groupQuestions;
    
    /** The ID. */
    private final String adminId;
    
    /** The admin socket. */
    private Socket adminSocket;
    
    /** The in. */
    private BufferedReader in;
    
    /** The out. */
    private PrintWriter out;
    
    /** The command handler. */
    private CommandHandler commandHandler;
    
    /** The displays. */
    private final ArrayList<Display> displays;
    
    /** The server. */
    private final PushServer server;
    
    /** The timer. */
    private Timer timer;
    
    /**
     * Instantiates a new push admin.
     * 
     * @param ID
     *            the iD
     * @param server
     *            the server
     */
    public PushAdmin(final String ID, final PushServer server) 
    {
        // public PushAdmin(String ID, Socket adminSocket, BufferedReader in,
        // PrintWriter out, PushServer server) {
        this.adminId = ID;
        // this.adminSocket = adminSocket;
        // this.in = in;
        // this.out = out;
        this.server = server;
        clientMap = Collections.synchronizedMap(new HashMap<String, Client>(50));
        groupMap = Collections.synchronizedMap(new HashMap<String, ArrayList<String>>(50));
        groupMap.put(Constants.UNGROUPED, new ArrayList<String>());
        questionForEveryone = "";
        groupQuestions = Collections.synchronizedMap(new HashMap<String, String>(50));
        displays = new ArrayList<Display>();
        // commandHandler = new CommandHandler();
        // timer = new Timer();
        // new Thread(commandHandler).start();
        // answerUpdater = new AnswerUpdater();
        // new Thread(answerUpdater).start();
        
        // timer.schedule(new PauseTask(), heartbeatTime * 1000);
    }
    
    /**
     * Send message.
     * 
     * @param message
     *            the message
     */
    private void sendMessage(final String message) 
    {
        if (out != null) 
        {
            out.println(message);
            out.flush();
        }
    }
    
    /**
     * Handle response.
     * 
     * @param clientID
     *            the client id
     * @param response
     *            the response
     */
    public void handleResponse(final String clientID, final String response) 
    {
        server.writeMessage("Client " + clientID + " responded with " + response + ".");
    }
    
    /**
     * Client died.
     * 
     * @param clientID
     *            the client id
     */
    public void clientDied(final String clientID) 
    {
    	server.writeMessage("Client " + clientID + " has died.");
    }
    
    /**
     * Display died.
     * 
     * @param displayID
     *            the display id
     */
    public void displayDied(final int displayID) 
    {
    	server.writeMessage("Display " + displayID + " has died.");
    }
    
    /**
     * Admin connected.
     * 
     * @param newSocket
     *            the new socket
     * @param newIn
     *            the new in
     * @param newOut
     *            the new out
     */
    public void connectAdmin(final Socket newSocket,
            final BufferedReader newIn,
            final PrintWriter newOut) 
    {
        reconnectAdmin(newSocket, newIn, newOut);
    }
    
    /**
     * Admin reconnected.
     * 
     * @param newSocket
     *            the new socket
     * @param newIn
     *            the new in
     * @param newOut
     *            the new out
     */
    public void reconnectAdmin(final Socket newSocket,
            final BufferedReader newIn,
            final PrintWriter newOut) 
    {
        if (commandHandler != null) 
        {
            commandHandler.pause();
        }
        adminSocket = newSocket;
        in = newIn;
        out = newOut;
        for (int i = 0; i < displays.size(); i++) 
        {
            sendMessage(Constants.DISPLAY_CONNECTED + Constants.GRAVE_SEPARATOR + 
            		displays.get(i).getIConsume());
        }
        commandHandler = new CommandHandler();
        timer = new Timer();
        new Thread(commandHandler).start();
        timer.schedule(new PauseTask(), heartbeatTimeSeconds * 1000);
    }
    
    /**
     * Update group questions.
     */
    public void updateGroupQuestions() 
    {
        final Iterator<String> gcIter = groupQuestions.keySet().iterator();
        while (gcIter.hasNext()) {
            final String gcNext = gcIter.next();
            if (!groupMap.containsKey(gcNext)) 
            {
                gcIter.remove();
            }
        }
        
        final Iterator<String> gIter = groupMap.keySet().iterator();
        while (gIter.hasNext()) 
        {
            final String gNext = gIter.next();
            if (!groupQuestions.containsKey(gNext)) 
            {
                groupQuestions.put(gNext, "");
            }
        }
    }
    
    /**
     * Process client.
     * 
     * @param clientSocket
     *            the client socket
     * @param clientId
     *            the client id
     * @param clientMac
     *            the client mac
     * @param clientIn
     *            the client in
     * @param clientOut
     *            the client out
     * @param groupName
     *            the group name
     */
    public void processClient(final Socket clientSocket,
            final String clientId,
            final String clientMac,
            final BufferedReader clientIn,
            final PrintWriter clientOut,
            final String groupName) 
    {
    	server.writeMessage("Processing client...");
        if (clientMap.containsKey(clientId)) 
        {
            handleExistingClientConnectingToServer(clientSocket,
                    clientId,
                    clientMac,
                    clientIn,
                    clientOut);
        } 
        else 
        {
            handleNewClientConnection(clientSocket,
                    clientId,
                    clientMac,
                    clientIn,
                    clientOut,
                    groupName);
        }
        server.writeMessage("Client connected to admin " + adminId + " with an id of " + clientId + ".");
        // out.println("ClientConnected;" + clientID);
    }

    private void handleNewClientConnection(final Socket clientSocket,
            final String clientId,
            final String clientMac,
            final BufferedReader clientIn,
            final PrintWriter clientOut,
            final String groupName) 
    {
        clientMap.put(clientId, new Client(clientId,
                clientMac,
                clientSocket,
                clientIn,
                clientOut,
                this));
//        if (!groupName.equals(Constants.UNGROUPED)) // OLD CODE
//        {
            if (groupMap.containsKey(groupName))
            {
                groupMap.get(groupName).add(clientId);
                server.writeMessage("Added person to existing group : " + groupName);
            } 
            else 
            {
                final ArrayList<String> newGroupList = new ArrayList<String>();
                newGroupList.add(clientId);
                groupMap.put(groupName, newGroupList);
                groupQuestions.put(groupName, "");
                server.writeMessage("Added person to new group : " + groupName);
            }
            clientMap.get(clientId).setGroup(groupName);
            final Iterator<String> groupIter = groupMap.keySet().iterator();
            String groupUpdate = Constants.GROUP_LIST + Constants.GRAVE_SEPARATOR;
            while (groupIter.hasNext()) 
            {
                groupUpdate += groupIter.next() + Constants.SEMI_COLON_SEPARATOR;
            }
            out.println(groupUpdate);
//        } // END
    }

    private void handleExistingClientConnectingToServer(final Socket clientSocket,
            final String clientId,
            final String clientMac,
            final BufferedReader clientIn,
            final PrintWriter clientOut) 
    {
        final Client oldClient = clientMap.get(clientId);
        if (oldClient.getMacAddress().equals(clientMac)) 
        {
            handleExistingClientWithMatchingMac(clientSocket, clientIn, clientOut, oldClient);
        } 
        else 
        {
            sendMessage(Constants.DUPLICATE_ID);
            try 
            {
                clientSocket.close();
            } 
            catch (final IOException e) {}
        }
    }

    private void handleExistingClientWithMatchingMac(final Socket clientSocket,
            final BufferedReader clientIn,
            final PrintWriter clientOut,
            final Client oldClient) 
    {
        try 
        {
            oldClient.getSocket().close();
        } 
        catch (final IOException e) {}
        
        oldClient.clientReconnected(clientSocket, clientIn, clientOut);
        final String oldGroupName = oldClient.getGroup();
        if (oldGroupName.equals("")) 
        {
            if (!questionForEveryone.equals("")) 
            {
                oldClient.startQuestion(questionForEveryone);
            }
        } 
        else 
        {
            oldClient.setGroup(oldGroupName);
            if (!groupQuestions.get(oldGroupName).equals("")) 
            {
                oldClient.startQuestion(groupQuestions.get(oldGroupName));
            } 
            else if (!questionForEveryone.equals("")) 
            {
                oldClient.startQuestion(questionForEveryone);
            }
        }
    }
    
    /**
     * Process display.
     * 
     * @param displaySocket
     *            the display
     * @param displayIn
     *            the display in
     * @param displayOut
     *            the display out
     * @param displayName
     *            the display name
     */
    public void processDisplay(final Socket displaySocket,
            final BufferedReader displayIn,
            final PrintWriter displayOut,
            final String displayName) 
    {
        final Display newDisplay = new Display(displays.size() - 1,
                displaySocket,
                displayIn,
                displayOut,
                this,
                displayName);
        displays.add(newDisplay);
        sendMessage(Constants.DISPLAY_CONNECTED + Constants.GRAVE_SEPARATOR + newDisplay.getIConsume());
    }
    
    /**
     * Gets the admin id.
     * 
     * @return the admin id
     */
    public String getAdminId() 
    {
        return adminId;
    }
    
    /**
     * Update answer.
     * 
     * @param clientAndAnswer
     *            the client and answer
     */
    public synchronized void updateAnswer(final String clientAndAnswer) 
    {
        for (int i = 0; i < displays.size(); i++) 
        {
            displays.get(i).sendMessage(clientAndAnswer);
        }
        server.writeMessage("Sending: " + clientAndAnswer + " to " + displays.size() + " displays.");
        sendMessage(clientAndAnswer);
    }
    
    /**
     * Checks if is paused.
     * 
     * @return true, if is paused
     */
    public boolean isPaused() 
    {
        return commandHandler.isPaused();
    }
    
    /**
     * The Class PauseTask.
     */
    private class PauseTask extends TimerTask 
    {
        
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() 
        {
            commandHandler.pause();
            server.writeMessage("No heartbeat from admin, pausing now...");
        }
    }
    
    /**
     * The Class CommandHandler.
     */
    private class CommandHandler implements Runnable 
    {
        
        
        /** The paused. */
        private boolean paused = false;
        
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
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
                        paused = true;
                        server.writeMessage("Read null command, pausing now...");
                    } 
                    else if (command.equals(Constants.CLIENT_HEARTBEAT_REQUEST)) 
                    {
                        handleHeartbeatRequest();
                    } 
                    else 
                    {
                        final String[] directiveParts = command.split(Constants.GRAVE_SEPARATOR);
                        if (directiveParts[0].equals(Constants.CLIENT_COMMAND)) 
                        {
                        	System.out.println(command);
                            handleAdminCommand(directiveParts);
                        } 
                        else if (directiveParts[0].equals(Constants.GET_QUESTION_SETS)) 
                        {
                            handleGetQuestionSet();
                        } 
                        else if (directiveParts[0].equals(Constants.ADD_QUESTION_SET)) 
                        {
                            handleAddQuestionSet(directiveParts);
                        } 
                        else if (directiveParts[0].equals(Constants.DELETE_QUESTION_SET)) 
                        {
                            handleDeleteQuestionSet(directiveParts);
                        } 
                        else if (directiveParts[0].equals(Constants.GET_ALL_QUESTIONS)) 
                        {
                            handleGetAllQuestions(directiveParts);
                        } 
                        else if (directiveParts[0].equals(Constants.GET_CLIENT_LIST)) 
                        {
                            handleGetClientList();
                        } 
                        else if (directiveParts[0].equals(Constants.UPDATE_CLIENT_LIST)) 
                        {
                        	if (directiveParts.length > 1)
                        		handleUpdateClientList(directiveParts);
                        } 
                        else 
                        {
                        	server.writeMessage("ERROR : Invalid command" + command + ".");
                        }
                    }
                } 
                catch (final SocketTimeoutException e) {} 
                catch (final IOException e) 
                {
                    paused = true;
                    server.writeMessage(e.getMessage());
                    server.writeMessage("Admin is paused!");
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
            timer.cancel();
            adminSocket = null;
        }

        private void handleAdminCommand(final String[] directiveParts) 
        {
            final String[] clientCommandParts = directiveParts[1].split(Constants.AMPERSAND_SEPARATOR);
            final String[] commandParts = clientCommandParts[0].split(Constants.SEMI_COLON_SEPARATOR);
            if (commandParts[0].equals(Constants.OPEN) || commandParts[0].equals(Constants.OPEN_CLICK_PAD) || commandParts[0].equals(Constants.OPEN_SPOTLIGHT_PAD)) 
            {
                handleOpenCommand(clientCommandParts);
            } 
            else if (commandParts[0].equals(Constants.CLOSE)) 
            {
                handleCloseCommand(directiveParts, commandParts);
            }
        }
        
        private void handleOpenCommand(final String[] clientCommandParts) 
        {
        	server.writeMessage("Got the command to open a question!");
            String displayGroupString = "";
            final String[] groupsToOpen = clientCommandParts[2].split(Constants.COMMA_SEPARATOR);
            
            if ((groupsToOpen.length == 1) && groupsToOpen[0].equals(Constants.EVERYONE)) 
            {
            	System.out.println("Opening a question for everyone!"); // !
                final Iterator<Map.Entry<String, Client>> clientMapIterator = clientMap.entrySet().iterator();
                while (clientMapIterator.hasNext()) 
                {
                    clientMapIterator.next().getValue().startQuestion(clientCommandParts[0]);
                }
                displayGroupString = Constants.EVERYONE + Constants.COLON_SEPARATOR + clientMap.size() + Constants.COMMA_SEPARATOR;
                questionForEveryone = clientCommandParts[0];
            } 
            else 
            {
            	System.out.print("Opening a question for: "); // !
                for (final String gName : groupsToOpen) 
                {
                	System.out.print(gName + " "); // !
                    final ArrayList<String> groupList = groupMap.get(gName);
                    groupQuestions.put(gName, clientCommandParts[0]);
                    displayGroupString += gName + Constants.COLON_SEPARATOR + groupList.size() + Constants.COMMA_SEPARATOR;
                    for (int j = 0; j < groupList.size(); j++) 
                    {
                        clientMap.get(groupList.get(j)).startQuestion(clientCommandParts[0]);
                    }
                }
                System.out.println(); // !
            }
            
         
            for (int i = 0; i < displays.size(); i++) 
            {
                displays.get(i).sendMessage(clientCommandParts[0] + Constants.AMPERSAND_SEPARATOR + clientCommandParts[1] + Constants.AMPERSAND_SEPARATOR + displayGroupString);
            }
        }

        private void handleCloseCommand(final String[] directiveParts, final String[] commandParts) 
        {
            for (int i = 0; i < displays.size(); i++) 
            {
                displays.get(i).sendMessage(directiveParts[1]);
            }
            
            final String[] groupsToClose = commandParts[1].split(Constants.COMMA_SEPARATOR);
            if ((groupsToClose.length == 1) && groupsToClose[0].equals(Constants.EVERYONE)) 
            {
                questionForEveryone = "";
                final Iterator<Map.Entry<String, Client>> clientMapIterator = clientMap.entrySet().iterator();
                while (clientMapIterator.hasNext()) 
                {
                    clientMapIterator.next().getValue().stopQuestion();
                }
            } 
            else 
            {
                for (final String groupName : groupsToClose) 
                {
                    final ArrayList<String> groupList = groupMap.get(groupName);
                    groupQuestions.put(groupName, "");
                    // check 304
                    for (int j = 0; j < groupList.size(); j++) 
                    {
                        clientMap.get(groupList.get(j)).stopQuestion();
                    }
                }
            }
        }

        private void handleHeartbeatRequest() 
        {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new PauseTask(), heartbeatTimeSeconds * 1000);
            sendMessage(Constants.SERVER_HEARTBEAT_RESPONSE);
            server.writeMessage("Received admin heartbeat and reset timer...");
        }

        private void handleGetQuestionSet() 
        {
        	server.writeMessage("Got request for sets.");
            out.println(server.getQuestionSets());
        }

        private void handleAddQuestionSet(final String[] directiveParts) 
        {
            server.addQuestionSet(directiveParts[1], directiveParts[2]);
        }

        private void handleDeleteQuestionSet(final String[] directiveParts) 
        {
            if (directiveParts.length > 1) 
            {
                server.deleteQuestionSet(directiveParts[1]);
            }
        }

        private void handleGetAllQuestions(final String[] directiveParts) 
        {
        	server.writeMessage("Got request for all from set.");
            out.println(server.getQuestionsInSet(directiveParts[1]));
        }

        private void handleGetClientList() 
        {
        	// OLD CODE
//        	String output = "";
//        	
//        	// Clients that are in a group
//            final Iterator<String> groupMapIter = groupMap.keySet().iterator();
//            while (groupMapIter.hasNext()) 
//            {
//                final String groupName = groupMapIter.next();
//                
//                output = output + groupName + PushServer.SEMI_COLON_SEPARATOR;
//                
//                final ArrayList<String> groupMembers = groupMap.get(groupName);
//                for (int i = 0; i < groupMembers.size(); i++) 
//                {
//                    if (i != 0) 
//                    {
//                        output = output + PushServer.COMMA_SEPARATOR;
//                    }
//                    output = output + groupMembers.get(i);
//                }
//                output = output + PushServer.AMPERSAND_SEPARATOR;
//            }
//            
//            // Clients not in a group
//            final Iterator<Map.Entry<String, Client>> clientIterator = clientMap.entrySet().iterator();
//            String notGrouped = Constants.NOT_GROUPED + PushServer.SEMI_COLON_SEPARATOR;
//            while (clientIterator.hasNext()) 
//            {
//                final Map.Entry<String, Client> next = clientIterator.next();
//                if (next.getValue().getGroup().equals("")) 
//                {
//                    notGrouped += next.getKey() + PushServer.COMMA_SEPARATOR;
//                }
//            }
//            notGrouped += PushServer.AMPERSAND_SEPARATOR;
//            System.out.println("Not Grouped: " + notGrouped);
//            
//            output = Constants.CLIENT_LIST + PushServer.GRAVE_SEPARATOR + notGrouped + output;
//            output = output.substring(0, output.length() - 3);
//            server.sendMessage("Returning : " + output);
//            sendMessage(output);
        	// END
        	
        	// NEW CODE
        	String output = "";
        	
            final Iterator<String> groupMapIter = groupMap.keySet().iterator();
            while (groupMapIter.hasNext()) 
            {
                final String groupName = groupMapIter.next();
                
                output = output + groupName + Constants.SEMI_COLON_SEPARATOR;
                
                final ArrayList<String> groupMembers = groupMap.get(groupName);
                for (int i = 0; i < groupMembers.size(); i++) 
                {
                    if (i != 0) 
                    {
                        output += Constants.COMMA_SEPARATOR;
                    }
                    output += groupMembers.get(i);
                }
                output = output + Constants.AMPERSAND_SEPARATOR;
            }
            
            output = Constants.CLIENT_LIST + Constants.GRAVE_SEPARATOR + output;
            output = output.substring(0, output.length() - 3);
            sendMessage(output);
            // END
        }

        private void handleUpdateClientList(final String[] directiveParts) 
        {
        	// OLD CODE
//            groupMap.clear();
//            final String[] groupStrings = directiveParts[1].split(PushServer.AMPERSAND_SEPARATOR);
//            for (final String groupString : groupStrings) 
//            {
//                final String[] groupParts = groupString.split(PushServer.SEMI_COLON_SEPARATOR);
//                if (groupParts[0].equals(Constants.NOT_GROUPED)) 
//                {
//                    if (groupParts.length > 1) 
//                    {
//                        final String[] nogroupClients = groupParts[1].split(PushServer.COMMA_SEPARATOR);
//                        for (final String nogroupClient : nogroupClients) 
//                        {
//                            clientMap.get(nogroupClient).unsetGroup();
//                        }
//                    }
//                } 
//                else 
//                {
//                    final ArrayList<String> newGroupListing = new ArrayList<String>();
//                    if (groupParts.length > 1) 
//                    {
//                        final String[] newMembers = groupParts[1].split(PushServer.COMMA_SEPARATOR);
//                        Collections.addAll(newGroupListing, newMembers);
//                        for (final String newMember : newMembers) 
//                        {
//                            clientMap.get(newMember).setGroup(groupParts[0]);
//                        }
//                    }
//                    groupMap.put(groupParts[0], newGroupListing);
//                }
//            }
        	// END
        	
        	// NEW CODE
        	
            final String[] groupStrings = directiveParts[1].split(Constants.AMPERSAND_SEPARATOR);
            for (final String groupString : groupStrings) 
            {
            	// groupParts[0] = Group Name - groupParts[1] = Members
                final String[] groupParts = groupString.split(Constants.SEMI_COLON_SEPARATOR);
                
                groupMap.put(groupParts[0], new ArrayList<String>());
                
                if (groupParts.length > 1)
                {
                	for (String member : groupParts[1].split(Constants.COMMA_SEPARATOR))
                	{
                		clientMap.get(member).setGroup(groupParts[0]);
                	}

                }
            }
            
            for (String clientID : clientMap.keySet())
            {
            	groupMap.get(clientMap.get(clientID).getGroup()).add(clientID);
            }
            // END
            updateGroupQuestions();
        }
        
        /**
         * Unpause.
         */
        public void unpause() 
        {
            paused = false;
            server.writeMessage("Admin is no longer paused at " + System.currentTimeMillis() + ".");
        }
        
        /**
         * Pause.
         */
        public void pause() 
        {
            paused = true;
            timer.cancel();
            server.writeMessage("Admin is now paused at " + System.currentTimeMillis() + ".");
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
