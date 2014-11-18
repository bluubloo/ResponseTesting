package uni.apps.responsetesting.models;

import android.app.Activity;

public class EmailTaskData {

	public String[] strings;
	public Activity activity;
	
	public EmailTaskData(String[] data, Activity a){
		strings = data;
		activity = a;
	}
	
}
