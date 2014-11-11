package uni.apps.responsetesting.utils;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.ResultsDisplayActivity;
import uni.apps.responsetesting.results.Email;
import android.app.Activity;
import android.content.Intent;
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
	
}
