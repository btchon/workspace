package clicker.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JTextArea;

import javax.sql.ConnectionEvent;

import clicker.constants.Constants;



// TODO: Auto-generated Javadoc
/**
 * The Class PushServer.
 */
public class PushServer 
{
    /** Listens for incoming client connections. */
    private final ClientConnectionListener clientConnectionChecker;
    
    /** Handles new admin connections. */
    private final AdminListener adminListener;
    
    /** Handles new display connections. */
    private final DisplayListener displayListener;
    
    /** Map of the connected administrators. */
    private final Map<String, PushAdmin> admins;
    
    /** The question sets. 
     * Map of QuestionSetName to an ArrayList of Questions */
    private final Map<String, ArrayList<Question>> questionSets;
    
    /** The server socket. */
    private ServerSocket serverSocket;

    private JTextArea messageArea;
    
    /**
     * Instantiates a new push server.
     * 
     * @param clientPort
     *            the client port
     * @param adminPort
     *            the admin port
     * @param displayPort
     *            the display port
     */
    public PushServer(final int clientPort, final int adminPort, final int displayPort, JTextArea messageArea) 
    {
    	this.messageArea = messageArea;
    	writeMessage("The server has started...");
    	
        try 
        {
            serverSocket = new ServerSocket(clientPort);
            serverSocket.setSoTimeout(100);
        } 
        catch (final IOException e) 
        {
        	writeMessage("ERROR : Could not listen on port " + clientPort + ".");
        }
        
        admins = Collections.synchronizedMap(new HashMap<String, PushAdmin>(50));
        admins.put("frederis", new PushAdmin("frederis", this)); //STATIC
        
        adminListener = new AdminListener(adminPort);
        new Thread(adminListener).start();
        
        clientConnectionChecker = new ClientConnectionListener();
        new Thread(clientConnectionChecker).start();
        
        displayListener = new DisplayListener(displayPort);
        new Thread(displayListener).start();
   
        questionSets = Collections.synchronizedMap(new HashMap<String, ArrayList<Question>>());
        
        loadQuestionsFromFile();
    }
    
    /**
     * Load questions from the questions/questions.txt file one line at a time.
     */
    private void loadQuestionsFromFile() 
    {
        try 
        {
            final BufferedReader qIn = new BufferedReader(new FileReader("questions/questions.txt"));
            String str;
            while ((str = qIn.readLine()) != null) 
            {
            	if ((str = str.trim()).length() > 0)
            	{
            		parseQuestionSetFromFile(str);
            	}
            }
            qIn.close();
            writeMessage("Questions loaded!");
        } 
        catch (final IOException e) 
        {
        	writeMessage("ERROR : Could not open question file!");
        }
    }

    private void parseQuestionSetFromFile(final String setString) 
    {
    	
        final String[] setParts = setString.split(Constants.AT_SEPARATOR);
    	
    	if (!questionSets.containsKey(setParts[0]))
    	{
    		questionSets.put(setParts[0], new ArrayList<Question>());
    	}
    	
    	
    	writeMessage("Reading a question set...");
    	writeMessage("Name: " + setParts[0]);
    	writeMessage("Question: " + setParts[1]);
    	
        if (setParts.length > 1) 
        {
            Question previousQuestion = new Question(setParts[1]);
            questionSets.get(setParts[0]).add(previousQuestion);
        }
    }

    public void addQuestionSet(final String oldName, final String qSetString) 
    {
    	
        if (!oldName.equals("")) 
        {
            questionSets.remove(oldName);
        }
        parseQuestionSetFromMessage(qSetString);
        saveQuestionSetsToFile();
        writeMessage("Question set added.");
    }
    
    private void parseQuestionSetFromMessage(String setString)
    {
    	final String[] setParts = setString.split(Constants.AT_SEPARATOR);
    	final ArrayList<Question> questionList = new ArrayList<Question>();
    	
        if (setParts.length > 1) 
        {
            final String[] setQuestions = setParts[1].split(Constants.AMPERSAND_SEPARATOR);
            Question previousQuestion = new Question(setQuestions[0]);
            questionList.add(previousQuestion);
            
            Question newQuestion;
            
            if (setQuestions.length > 1) 
            {
                for (int i = 1; i < setQuestions.length; i++) 
                {
                    newQuestion = new Question(setQuestions[i]);
                    previousQuestion.setFollowUp(newQuestion.getQuestionId());
                    questionList.add(newQuestion);
                    previousQuestion = newQuestion;
                }
            }
            
        }
        questionSets.put(setParts[0], questionList);
    }
    
    public void deleteQuestionSet(final String questionSet) 
    {
        questionSets.remove(questionSet);
        saveQuestionSetsToFile();
        writeMessage("Question set removed.");
    }
    
    /**
     * Save question sets.
     */
    public void saveQuestionSetsToFile() 
    {
        try 
        {
        	File questionSetFile = new File("./questions/questions.txt");
        	PrintWriter pw = new PrintWriter(questionSetFile);
            
            for (String setName : questionSets.keySet())
            {
                final ArrayList<Question> setQuestions = questionSets.get(setName);
                for (int i = 0; i < setQuestions.size(); i++) 
                {
                	pw.println(setName + Constants.AT_SEPARATOR + setQuestions.get(i).getQuestionString());
                }
            }
            pw.flush();
            writeMessage("Questions saved!");
        } 
        catch (final IOException e) 
        {
        	writeMessage("ERROR : Could not save questions!");
        }
    }
    
    /*
     * private void connectToSQLDatabase(){ try {
     * Class.forName("com.mysql.jdbc.Driver"); connect =
     * DriverManager.getConnection("jdbc:mysql://localhost/clicker?" +
     * "autoReconnect=true&user=clickeruser&password=clickerpassword"); } catch
     * (Exception e) { e.printStackTrace(); } }
     */
    
    /*
     * public synchronized ResultSet runQuery(String query) { try { Statement
     * qStatement = connect.createStatement(); return
     * qStatement.executeQuery(query); } catch (Exception e)
     * {e.printStackTrace();} return null; }
     */
    
    /**
     * Gets the push server.
     * 
     * @return the push server
     */
    public PushServer getPushServer() 
    {
        return this;
    }
    
    /**
     * Gets the question sets.
     * 
     * @return the question sets
     */
    public String getQuestionSets() 
    {
        final Iterator<String> questionSetItererator = questionSets.keySet().iterator();
        final String[] resultArray = new String[questionSets.size()];
        String finalResult = "";
        int index = 0;
        
        while (questionSetItererator.hasNext()) 
        {
            resultArray[index] = questionSetItererator.next();
            index++;
        }
        
        java.util.Arrays.sort(resultArray);
        for (final String questionString : resultArray) 
        {
            finalResult = finalResult + questionString + Constants.AMPERSAND_SEPARATOR;
        }
        return Constants.ALL_SETS + Constants.GRAVE_SEPARATOR + finalResult;
    }
    
    /**
     * Gets the questions in set.
     * 
     * @param questionSetKey
     *            the q set key
     * @return the questions in set
     */
    public String getQuestionsInSet(final String questionSetKey) 
    {
        System.out.println("Getting questions for set: " + questionSetKey);
        final ArrayList<Question> setQuestions = questionSets.get(questionSetKey);
        String finalResult = "";
        for (int i = 0; i < setQuestions.size(); i++) 
        {
            final Question nextQuestion = setQuestions.get(i);
            finalResult = finalResult + nextQuestion.getQuestionId() + Constants.SEMI_COLON_SEPARATOR + 
                    nextQuestion.getQuestionFlags() + Constants.SEMI_COLON_SEPARATOR + nextQuestion.getWidgets() + 
                    Constants.SEMI_COLON_SEPARATOR + nextQuestion.getBackgroundColor() + Constants.AMPERSAND_SEPARATOR;
        }
        writeMessage("Final Result : " + finalResult);
        return Constants.QUESTION_SET + Constants.GRAVE_SEPARATOR + finalResult;
    }
    
    public void writeMessage(String message)
    {
    	messageArea.append(message + "\n");
    }
    
    /**
     *  This class listens for incoming client connections.
     *  It runs as a thread and accepts and processes new connections, 
     *  waiting for a new connection as soon as the old connection has been processed.
     * 
     * @see ConnectionEvent
     */
    private class ClientConnectionListener implements Runnable 
    {
        
        @Override
        public void run() 
        {
        	writeMessage("Server listening for clients...");
            while (true) 
            {
                try 
                {
                    final Socket client = serverSocket.accept();
                    writeMessage("Accepted a client connection!");
                    client.setKeepAlive(true);
                    client.setSoTimeout(100);
                    final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    final PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    String idString = "";
                    boolean isIdSet = false;
                    while (!isIdSet) 
                    {
                        try 
                        {
                            idString = in.readLine();
                            writeMessage("ID has been set!");
                            isIdSet = true;
                        } 
                        catch (final InterruptedIOException e) {}
                    }
                    final String[] idparts = idString.split(Constants.SEMI_COLON_SEPARATOR);
                    if (idparts.length == 3) 
                    {
                        final String clientId = idparts[0];
                        final String clientMac = idparts[1];
                        final String[] adminParts = idparts[2].split(Constants.COMMA_SEPARATOR);
                        final String adminID = adminParts[0];
                        final String groupID = adminParts[1];
                        writeMessage("Client is wanting to connect to admin : " + adminID);
                        if (admins.containsKey(adminID)) 
                        {
                        	writeMessage("Calling processClient...");
                            admins.get(adminID).processClient(client, clientId, clientMac, in, out, groupID);
                        } 
                        else 
                        {
                            out.println(Constants.INVALID_ADMIN);
                            client.close();
                        }
                    } 
                    else if (idparts.length == 4) 
                    {
                    	System.out.println(idString);
//                        final Question requested = questions.get(idparts[3]);
//                        out.println(Constants.OPEN + Constants.SEMI_COLON_SEPARATOR + idparts[3] + Constants.SEMI_COLON_SEPARATOR + 
//                                requested.getQuestionFlags() + Constants.SEMI_COLON_SEPARATOR + requested.getWidgets() + 
//                                Constants.SEMI_COLON_SEPARATOR + requested.getBackgroundColor());
                    } 
                    else if (idparts.length == 5) 
                    {
                    	System.out.println(idString);
                    	
//                        final Question previous = questions.get(idparts[3]);
//                        
//                        if (previous.hasFollowUp()) 
//                        {
//                            final Question next = questions.get(previous.getFollowUp());
//                            out.println(Constants.OPEN + Constants.SEMI_COLON_SEPARATOR + next.getQuestionId() + 
//                            		Constants.SEMI_COLON_SEPARATOR + next.getQuestionFlags() + Constants.SEMI_COLON_SEPARATOR + 
//                                    next.getWidgets() + Constants.SEMI_COLON_SEPARATOR + next.getBackgroundColor());
//                            
//                        } 
//                        else 
//                        {
//                            out.println(Constants.CLOSE + Constants.SEMI_COLON_SEPARATOR);
//                        }
                    } 
                    else 
                    {
                        // invalid
                    }
                } 
                catch (final Exception e) {}
            }
        }
    }
    
    /**
     * This class listens for incoming administrator connections.
     * If there is no instance of an administrator that has previously connected, 
     * it will create a new administrator.
     * If the admin is reconnecting, it simply updates the old PushAdmin object 
     * with the new connection.
     * 
     * @see AdminEvent
     */
    private class AdminListener implements Runnable 
    {
        
        /** The connection socket. */
        private ServerSocket connectionSocket;
        
        /**
         * Instantiates a new admin listener.
         * 
         * @param port
         *            the port
         */
        public AdminListener(final int port) 
        {
            try 
            {
                connectionSocket = new ServerSocket(port);
            } 
            catch (final IOException e) 
            {
            	writeMessage("ERROR : Could not listen on port " + port + ".");
            }
        }
        
        @Override
        public void run() 
        {
            while (true) 
            {
                String userName = "";
                String password = "";
                Socket adminSocket = null;
                BufferedReader adminIn = null;
                PrintWriter adminOut = null;
                
                try 
                {
                	writeMessage("Waiting for next admin to connect on server...");
                    adminSocket = connectionSocket.accept();
                    adminSocket.setKeepAlive(true);
                    writeMessage("Admin connected, authenticating now...");
                    adminIn = new BufferedReader(new InputStreamReader(adminSocket.getInputStream()));
                    adminOut = new PrintWriter(adminSocket.getOutputStream(), true);
                    userName = adminIn.readLine();
                    password = adminIn.readLine();
                    writeMessage("Admin authenticated!");
                } 
                catch (final IOException e) {}
                if (userName.equals("frederis") && password.equals("testpw")) 
                {
                    if (admins.containsKey(userName)) 
                    {
                    	writeMessage("Admin already exists!");
                        final PushAdmin oldAdmin = admins.get(userName);
                        writeMessage("Calling admin reconnected...");
                        oldAdmin.reconnectAdmin(adminSocket, adminIn, adminOut);
                    } 
                    else 
                    {
                        admins.put(userName, new PushAdmin(userName, getPushServer()));
                        admins.get(userName).connectAdmin(adminSocket, adminIn, adminOut);
                    }
                }
            }
        }
    }
    
    
    /**
     * This class listens for new connections coming in on the display's port.
     * 
     * @see DisplayEvent
     */
    private class DisplayListener implements Runnable 
    {
        
        /** The display socket. */
        private ServerSocket displaySocket;
        
        public DisplayListener(final int port) 
        {
            try 
            {
                displaySocket = new ServerSocket(port);
            } 
            catch (final IOException e) 
            {
            	writeMessage("ERROR : Could not listen on port " + port + ".");
            }
        }
        
        @Override
        public void run() 
        {
            while (true) 
            {
                try 
                {
                	writeMessage("Waiting for display on server...");
                    final Socket display = displaySocket.accept();
                    display.setKeepAlive(true);
                    display.setSoTimeout(100);
                    final BufferedReader displayIn = new BufferedReader(new InputStreamReader(display.getInputStream()));
                    final PrintWriter displayOut = new PrintWriter(display.getOutputStream(), true);
                    final String adminAndName = displayIn.readLine();
                    // final String requestedAdmin = displayIn.readLine();
                    final String[] adminAndNameParts = adminAndName.split(Constants.COMMA_SEPARATOR);
                    final String requestedAdmin = adminAndNameParts[0];
                    final String displayName = adminAndNameParts[1];
                    if (admins.containsKey(requestedAdmin)) 
                    {
                        admins.get(requestedAdmin).processDisplay(display,
                                displayIn,
                                displayOut,
                                displayName);
                    } 
                    else 
                    {
                        admins.put(requestedAdmin, new PushAdmin(requestedAdmin, getPushServer()));
                        writeMessage("Display connected and waiting for admin on " + requestedAdmin + ".");
                    }
                } 
                catch (final IOException e) {}
            }
        }
    }
    
}
