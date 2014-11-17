package uni.apps.responsetesting.models;

public class StroopDurationInfo extends DurationInfo {

	private boolean correct;
	
	public StroopDurationInfo(long start) {
		super(start);
		// TODO Auto-generated constructor stub
	}
	
	public void addResult(boolean result){
		correct = result;
	}
	
	public boolean getResult(){
		return correct;
	}
	

}
