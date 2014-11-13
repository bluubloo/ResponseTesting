package uni.apps.responsetesting.utils;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.ResultsDisplayActivity;
import uni.apps.responsetesting.results.Email;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.MenuItem;

public class ActivityUtilities {

	public static boolean actionBarClicks(MenuItem item, Activity activity){
		switch(item.getItemId()){
		case R.id.action_settings:
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

	public static void eventInfo(String eventName, Activity activity) {
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

	private static String getEventInfo(String eventName, Resources r) {
		//TODO add event info strings here
		if(eventName.equals(r.getString(R.string.event_name_appear_obj)))
			return r.getString(R.string.appear_obj_info);
		if(eventName.equals(r.getString(R.string.event_name_finger_tap)))
			return r.getString(R.string.tap_info);
		if(eventName.equals(r.getString(R.string.event_name_questionaire)))
			return r.getString(R.string.questionaire_info);
		if(eventName.equals(r.getString(R.string.event_name_stroop)))
			return r.getString(R.string.stroop_info);
		return "";
	}
	
}
