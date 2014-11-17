package uni.apps.responsetesting.models;

/**
 * Data structure for the Appearing object clicks
 * 
 * 
 * @author Mathew Andela
 *
 */
public class DurationInfo {

	//variables for start, end and duration time in milliseconds
	private long start = 0;
	private long end = 0;
	private long duration = 0;
	
	//Constructor
	//only sets start time
	public DurationInfo(long start){
		this.start = start;
	}
	
	//adds end time and calculates duration
	public void addEndTime(long end){
		this.end = end;
		duration = end - start;
	}
	
	//------------------------------------------------------------------------------
	//GET METHODS
	
	public long getStart(){
		return start;
	}
	
	public long getEnd(){
		return end;
	}
	
	public long getDuration(){
		return duration;
	}
	
}
