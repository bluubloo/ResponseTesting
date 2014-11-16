package uni.apps.responsetesting.utils;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.ResultsDisplayActivity;
import uni.apps.responsetesting.SettingsActivity;
import uni.apps.responsetesting.results.Email;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.MenuItem;

public class ActivityUtilities {

	//Does the action bar events for items common accross all activities
	public static boolean actionBarClicks(MenuItem item, Activity activity){
		switch(item.getItemId()){
		case R.id.action_settings:
			activity.startActivity(new Intent(activity, SettingsActivity.class));
			return true;
		case R.id.action_send_all:
			Email.sendResults(activity, true);
			return true;
		case R.id.action_send_recent:
			 Email.sendResults(activity, false);
			 return true;
		case R.id.action_results:
			 Intent i = new Intent(activity, ResultsDisplayActivity.class);
			 activity.startActivity(i);
			 return true;
		default:
			return false;
		}
	}

	//Gets the event info and displays it in a dialog
	public static void eventInfo(String eventName, Activity activity) {
		//gets info
		String eventInfo = getEventInfo(eventName, activity.getResources());
		new AlertDialog.Builder(activity)
		.setTitle(eventName)
		.setMessage(eventInfo)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
					}
				})
				.setIcon(android.R.drawable.ic_menu_info_details)
				.show();
	}

	//Gets the events info from application resources
	public static String getEventInfo(String eventName, Resources r) {
		String[] info = r.getStringArray(R.array.event_info_array);
		String[] names = r.getStringArray(R.array.event_name_array);
		for(int i = 0; i < names.length; i++)
			if(names[i].equals(eventName))
				return info[i];
		return "";
	}
	
	//displays results for all events that do not require extra actions on closing the dialog
	public static void displayResults(Activity activity, String eventName, String message){
		new AlertDialog.Builder(activity)
		.setTitle(eventName + " Complete")
		.setMessage(message)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 

					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}
	
}
