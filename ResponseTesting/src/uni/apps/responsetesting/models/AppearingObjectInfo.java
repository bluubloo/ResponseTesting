package uni.apps.responsetesting.models;

public class AppearingObjectInfo {

	private long start = 0;
	private long end = 0;
	private long duration = 0;
	
	public AppearingObjectInfo(long start){
		this.start = start;
	}
	
	public void addEndTime(long end){
		this.end = end;
		duration = end - start;
	}
	
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
