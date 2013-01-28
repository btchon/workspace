package clicker.admin;

import clicker.constants.Constants;

public class LectureModel 
{
    private static LectureModel _instance;
    
    private String[] allSets;
    private String[] allQArray;
    
    private LectureModel() {}
    
    public static synchronized LectureModel getInstance() 
    {
        if(_instance == null) 
        {
            _instance = new LectureModel();
        }
        return _instance;
    }
    
    public String[] getAllSets()
    {
    	return allSets;
    }
 
    public String[] getQuestionSet()
    {
    	return allQArray;
    }
    
    public void receiveAllSets(String setString)
    {
    	allSets = setString.split(Constants.AMPERSAND_SEPARATOR);
    }
    
    public void receiveQuestionSet(String setString)
    {
    	System.out.println("Set String: " + setString);
    	allQArray = setString.split(Constants.AMPERSAND_SEPARATOR);
	}
}
    

