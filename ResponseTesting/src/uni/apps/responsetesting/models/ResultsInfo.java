package uni.apps.responsetesting.models;

import java.util.Calendar;
import java.util.HashMap;

import android.util.Log;

public class ResultsInfo {

	private static final String TAG = "ResultsInfo";
	String username;
	String date;
	Calendar time;
	HashMap<String, String> scores = new HashMap<String, String>();
	String[] ratings = new String[] {"", "", "", ""};
	String totalSleep = "";
	String restinghr = "";
	String[] eventNames;
	
	public ResultsInfo(String u, long t, String eventName, String score, String[] eventNames){
		setCommon(u, t, eventNames);
		scores.put(eventName, score);
	}
	
	public ResultsInfo(String u, long t, String s, String hr, String[] r, String[] eventNames){
		setCommon(u, t, eventNames);
		totalSleep = s;
		restinghr = hr;
		ratings = r;
	}
	
	private void setCommon(String u, long t, String[] eventNames){
		username = u;
		time = Calendar.getInstance();
		time.setTimeInMillis(t);
		date = time.getTime().toString();
		this.eventNames = eventNames;
	}
	
	public void addScore(String eventName, String score){
		scores.put(eventName, score);
	}
	
	public void addRating(String[] rating){
		ratings = rating;
	}
	
	public void addTotalSleep(String sleep){
		totalSleep = sleep;
	}
	
	public void addRestingHR(String hr){
		restinghr = hr;
	}
	
	public Calendar getTime(){
		return time;
	}
	
	public boolean checkUserName(String u){
		return username.equals(u);
	}
	
	public String getString(){
		String tmp = username + "," + date + ",";
		for(String s: eventNames){
			if(scores.containsKey(s))
				tmp += scores.get(s) + ",";
			else
				tmp += ",";
		}
		tmp += totalSleep + "," + restinghr + ",";
		
		for(String s: ratings){
			if(s.equals(null))
				tmp += ",";
			else
				tmp += s + ",";
		}
		Log.d(TAG, tmp);
		return tmp;
	}
}
