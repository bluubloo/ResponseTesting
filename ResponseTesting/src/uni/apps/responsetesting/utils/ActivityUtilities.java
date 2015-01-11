package uni.apps.responsetesting.utils;

import java.util.ArrayList;

import uni.apps.responsetesting.AboutActivity;
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
import android.util.Log;
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

	private static final String TAG = "ActivityUtilities";

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
			activity.startActivity(new Intent(activity, ResultsDisplayActivity.class));
			return true;
		case R.id.action_setup:
			passwordForSetupMode(activity);
			return true;
		case R.id.action_home:
			NavUtils.navigateUpFromSameTask(activity);
			return true;
		case R.id.action_about:
			activity.startActivity(new Intent(activity, AboutActivity.class));
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
		return times < 3 && db.checkRecent(eventName, userId, activity);
	}
	
	public static void changeTheme(Activity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = prefs.getString(activity.getResources().getString(R.string.pref_key_theme), 
				activity.getResources().getString(R.string.settings_theme_default));
		switch(theme){
		case "Default":
			activity.setTheme(R.style.DefaultTheme);
			break;
		case "Magic":
			activity.setTheme(R.style.MagicTheme);
			break;
		case "Forestry":
			activity.setTheme(R.style.ForestryTheme);
			break;
		default:
			activity.setTheme(R.style.DefaultTheme);
			break;
		}
	}
	
	public static void changeActionBarIcon(Activity activity){
		activity.getActionBar().setIcon(activity.getResources().getDrawable(getThemeIconId(activity)));
	}
	
	public static int getThemeIconId(Activity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = prefs.getString(activity.getResources().getString(R.string.pref_key_theme), 
				activity.getResources().getString(R.string.settings_theme_default));
		int imageId = 0;
		if(theme.equals("Default"))
			imageId = R.drawable.ic_launcher;
		else if(theme.equals("Magic"))
			imageId = R.drawable.ic_menu_magic;
		else if(theme.equals("Forestry"))
			imageId = R.drawable.ic_menu_forestry;
		else
			imageId = R.drawable.uni_logo;
		return imageId;
	}
	
	public static int getThemeQuestionsId(Activity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = prefs.getString(activity.getResources().getString(R.string.pref_key_theme), 
				activity.getResources().getString(R.string.settings_theme_default));
		int arrayId = 0;
		if(theme.equals("Magic"))
			arrayId = R.array.questionaire_array_magic;
		else
			arrayId = R.array.questionaire_array;
		return arrayId;
	}
	
	public static int getSplashIconId(Activity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = prefs.getString(activity.getResources().getString(R.string.pref_key_theme), 
				activity.getResources().getString(R.string.settings_theme_default));
		int imageId = 0;
		if(theme.equals("Default"))
			imageId = R.drawable.ic_launcher_512;
		else if(theme.equals("Magic"))
			imageId = R.drawable.ic_magic_512;
		else if(theme.equals("Forestry"))
			imageId = R.drawable.ic_forestry_512;
		else
			imageId = R.drawable.ic_launcher_512;
		return imageId;
	}
	
	public static String[] checkList(String[] stringArray, Activity activity, boolean graph) {
		//get user id
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String userId = prefs.getString(activity.getResources().getString(R.string.pref_key_user_id), "single");
		Resources r = activity.getResources();
		ArrayList<String> list = new ArrayList<String>();
		//gets data from database
		DatabaseHelper db = DatabaseHelper.getInstance(activity, r);
		for(String s: stringArray){
			boolean addable = true;
			
			//checks if event is addable
			if(s.equals(r.getString(R.string.event_name_questionaire)))
				addable = db.checkQuestionaire(s, userId);
			else
				if(graph)
					addable = checkPreferences(s, r, activity, userId);
				else
					addable = checkPreferences(s, r, activity, userId) && db.checkRecent(s, userId, activity);
			if(addable)
				list.add(s);
		}
		
		String[] tmp = new String[list.size()];
		for(int i = 0; i < tmp.length; i ++)
			tmp[i] = list.get(i);
		return tmp;
	}

	//checks preferencs
	private static boolean checkPreferences(String name, Resources r, Activity activity, String userId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		boolean single = prefs.getBoolean(r.getString(R.string.pref_key_user), true);
		//checks user mode
		if(single)
			return checkSinglePrefereneces(prefs, r, name);
		else
			return checkMultiUserPrferences(r, name, activity, userId);
	}

	//checks multi user mode settings
	private static boolean checkMultiUserPrferences(Resources resources,
			String name, Activity activity, String userId) {
		//gets settings string
		String multiUserSettings = DatabaseHelper.getInstance(activity, resources).
				getMultiSettingsString(userId);
				//gets setting value
			if(!multiUserSettings.equals("")){
				int index = getSettingIndex(name, resources);
				if(index != -1)
					return getPreferenceValue(index, multiUserSettings);
			}
			return true;
	}
	
	//gets settings value
	private static boolean getPreferenceValue(int index, String settings) {
		int i = index * 2;
		if(settings.length() > i && i >= 0)
			return settings.charAt(i) == '1';
		return true;
	}

	//gets setting index
	private static int getSettingIndex(String name, Resources r) {
		Log.d(TAG, name);
		String[] tmp = r.getStringArray(R.array.event_name_array_noq);
		for(int i = 0; i < tmp.length; i ++)
			if(tmp[i].equals(name))
				return i;
		return -1;
	}

	//checks single user mode preferences
	private static boolean checkSinglePrefereneces(SharedPreferences prefs,
			Resources r, String name) {
		switch(name){
		case "Appearing Object":
			return prefs.getBoolean(r.getString(R.string.pref_key_ao), true);
		case "Appearing Object - Fixed Point":
			return prefs.getBoolean(r.getString(R.string.pref_key_aof), true);
		case "Arrow Ignoring Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_ai), true);
		case "Changing Directions":
			return prefs.getBoolean(r.getString(R.string.pref_key_cd), true);
		case "Chase Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_ch), true);
		case "Even or Vowel":
			return prefs.getBoolean(r.getString(R.string.pref_key_eov), true);
		case "Finger Tap Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_ftt), true);
		case "Monkey Ladder":
			return prefs.getBoolean(r.getString(R.string.pref_key_ml), true);
		case "One Card Learning Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_oclt), true);
		case "Pattern Recreation":
			return prefs.getBoolean(r.getString(R.string.pref_key_pr), true);
		case "Stroop Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_s), true);
		default:
			return true;
		}
	}
	
	

}
