package clicker.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import clicker.constants.Constants;

public class ClassModel 
{
    private static ClassModel _instance;
    private Map<String, ArrayList<String>> groupMap; // Name of Group, <Clients in Group>
    
    private ClassModel() 
    {
    	groupMap = Collections.synchronizedMap(new HashMap<String, ArrayList<String>>());
    }
    
    public static synchronized ClassModel getInstance() 
    {
        if(_instance == null) 
        {
            _instance = new ClassModel();
        }
        return _instance;
    }
    
    public ArrayList<String> getGroups()
    {
        ArrayList<String> groups = new ArrayList<String>();
        for (String group : groupMap.keySet()) 
        {
            groups.add(group);
        }
        return groups;
    }
    
    public ArrayList<String> getClientsFromGroup(String group)
    {
    	return groupMap.get(group);
    }
    
    public ArrayList<String> getClients()
    {
        ArrayList<String> clients = new ArrayList<String>();
        for (String group : groupMap.keySet()) 
        {
        	clients.addAll(groupMap.get(group));
        }
        return clients;
    }
    
    public String getGroupUpdate()
    {
        String groupOutput = Constants.UPDATE_CLIENT_LIST + Constants.GRAVE_SEPARATOR;
        for (String groupName : groupMap.keySet()) 
        {
            groupOutput += groupName + Constants.SEMI_COLON_SEPARATOR;
            ArrayList<String> members = groupMap.get(groupName);
            if (members.size() > 0) 
            {
                groupOutput += members.get(0);
                for (int j=1; j < members.size(); j++) 
                {
                    groupOutput += Constants.COMMA_SEPARATOR + members.get(j);
                }
            }
            groupOutput += Constants.AMPERSAND_SEPARATOR;
        }
        return groupOutput;
    }
    
    public void unGroupMembers()
    {
    	ArrayList<String> members = new ArrayList<String>();
    	
        for (String group : groupMap.keySet()) 
        {
            members.addAll(groupMap.get(group));
        }
    	groupMap.clear();
    	groupMap.put("Not grouped", members);

    }
    
    public void addGroup(String groupName)
    {
    	groupMap.put(groupName, new ArrayList<String>());
    }
    
    public void moveMember(String member, String oldGroup, String newGroup)
    {
    	
    	groupMap.get(oldGroup).remove(member);
    	groupMap.get(newGroup).add(member);
    }
    
    public void setClientsIntoGroups(String setString) 
    {
        String[] groupStrings = setString.split(Constants.AMPERSAND_SEPARATOR);
//        System.out.println("\nGroups and Members: ");
        for (int i=0; i < groupStrings.length; i++) 
        {
//        	System.out.println("\t" + groupStrings[i]);
            String[] groupParts = groupStrings[i].split(Constants.SEMI_COLON_SEPARATOR);

            ArrayList<String> newGroupListing = new ArrayList<String>();
            if (groupParts.length > 1) 
            {
                String[] newMembers = groupParts[1].split(Constants.COMMA_SEPARATOR);
                Collections.addAll(newGroupListing, newMembers);
            }
            groupMap.put(groupParts[0], newGroupListing);
        }
    }
}
