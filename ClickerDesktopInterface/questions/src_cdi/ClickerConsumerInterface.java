
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JPanel;

public interface ClickerConsumerInterface 
{
	public JPanel getPanel();
	public void setID(String id);
	public String declareConsumptions();
	public void inputData(Map<String, ArrayList< ArrayList <String>>> input);//  Person name, index#, value
	public void setQuestion(String question);
}