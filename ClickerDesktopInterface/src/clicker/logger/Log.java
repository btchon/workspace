package clicker.logger;

import java.util.ArrayList;

public class Log 
{

	private ArrayList<String> seriesLog = new ArrayList<String>();
	private String sectionID;
	private String questionID;
	
	public Log(String section, String questionID)
	{
		this.sectionID = section;
		this.questionID = questionID;
	}
	
	public void addSeriesLog(String series)
	{
		seriesLog.add(series);
	}
	
	public ArrayList<String> getSeriesLog()
	{
		return seriesLog;
	}
	
	public String getQuestionID()
	{
		return questionID;
	}
	
	public String getSectionID()
	{
		return sectionID;
	}
}
