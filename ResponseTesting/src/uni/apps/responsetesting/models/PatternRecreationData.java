package uni.apps.responsetesting.models;


/**
 * This is the data structure for the patternrecreationfragment
 * 
 * @author Mathew Andea
 *
 */
public class PatternRecreationData {

	private boolean pattern = false;
	private boolean uncovered = false;
	private boolean error = false;	
	
	public PatternRecreationData(){}
	
	public PatternRecreationData(boolean p){
		pattern = p;
	}
	
	public PatternRecreationData(boolean p, boolean u){
		pattern = p;
		uncovered = u;
	}
	
	public void errorMade(){
		error = !error;
	}
	
	public void switchPattern(){
		pattern = !pattern;
	}
	
	public void switchCover(){
		uncovered = !uncovered;
	}
	
	public boolean isUncovered(){
		return uncovered;
	}
	
	public boolean isPartOfPattern(){
		return pattern;
	}
	
	public boolean wasErrorMade(){
		return error;
	}
}
