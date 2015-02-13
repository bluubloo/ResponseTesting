package uni.apps.responsetesting.models;

/**
 * This is the data structure for the results gathering
 * for tests with correct values
 * 
 * @author Mathew Andea
 *
 */
public class CorrectDurationInfo extends DurationInfo {

	private boolean correct;
	
	public CorrectDurationInfo(long start) {
		super(start);
	}
	
	public void addResult(boolean result){
		correct = result;
	}
	
	public boolean getResult(){
		return correct;
	}
	

}
