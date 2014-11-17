package uni.apps.responsetesting.models;

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
