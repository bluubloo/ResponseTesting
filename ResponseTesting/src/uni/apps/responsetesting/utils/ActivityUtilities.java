package uni.apps.responsetesting.utils;

import uni.apps.responsetesting.MultiUserSetupModeActivity;
import uni.apps.responsetesting.R;
import uni.apps.responsetesting.ResultsDisplayActivity;
import uni.apps.responsetesting.SettingsActivity;
import uni.apps.responsetesting.SetupModeActivity;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.mail.Email;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.widget.EditText;


/**
 * This class handles common app methods
 * 
 * 
 * @author Mathew Andela
 *
 */
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
		case R.id.action_setup:
			passwordForSetupMode(activity);
			return true;
		case R.id.action_home:
			NavUtils.navigateUpFromSameTask(activity);
			return true;
		default:
			return false;
		}
	}

	//runs setup mode
	private static void setupMode(Activity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		boolean single = prefs.getBoolean(activity.getResources().getString(R.string.pref_key_user), true);
		Intent intent;
		if(single)
			intent = new Intent(activity, SetupModeActivity.class);
		else
			intent = new Intent(activity, MultiUserSetupModeActivity.class);
		intent.putExtra("user", single);
		activity.startActivity(intent);
	}

	//checks password for setup mode
	private static void passwordForSetupMode(final Activity activity){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Enter Password");

		final EditText text = new EditText(activity);
		text.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		text.setTransformationMethod(PasswordTransformationMethod.getInstance());
		builder.setView(text);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//updates the notes
				if(text.getText().toString().equals(
						activity.getResources().getString(R.string.setup_mode_password)))
					setupMode(activity);
				else
					displayMessage(activity, "Incorrect Password", "The entered password was incorrect");
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//closes dialog
				dialog.cancel();

			}
		});
		builder.show();
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
	public static void displayResults(final Activity activity, String eventName, String message){
		new AlertDialog.Builder(activity)
		.setTitle(eventName + " Complete")
		.setMessage(message)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				activity.onBackPressed();
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}
	
	//displays message
	public static void displayMessage(Activity activity, String title, String message){
		new AlertDialog.Builder(activity)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 

			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}

	//checks if the test is replayable
	public static boolean checkPlayable(String eventName, int times, Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String userId = prefs.getString(activity.getResources().getString(R.string.pref_key_user_id), "single");
		return times < 3 && db.checkRecent(eventName, userId);
	}

}
