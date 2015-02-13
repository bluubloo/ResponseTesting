package uni.apps.responsetesting.models;

import android.app.Activity;

/**
 * This is the data structure for the emailtask
 * 
 * @author Mathew Andea
 *
 */
public class EmailTaskData {

	public String[] strings;
	public Activity activity;
	public boolean sendViaIntent;
	
	public EmailTaskData(String[] data, Activity a, boolean s){
		strings = data;
		activity = a;
		sendViaIntent = s;
	}
	
}
