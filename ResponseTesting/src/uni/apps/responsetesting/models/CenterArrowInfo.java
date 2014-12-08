package uni.apps.responsetesting.models;

/**
 * This is the data structure for the CenterArrowfragment
 * 
 * @author Mathew Andea
 *
 */
public class CenterArrowInfo {

	private int drawableId = 0;
	private String direction = "";
	
	public CenterArrowInfo(int id, String d){
		drawableId = id;
		direction = d;
	}
	
	public int getId(){
		return drawableId;
	}
	
	public String getDirection(){
		return direction;
	}
	
	public boolean checkCorrect(String result){
		return direction.equals(result);
	}
	
}
