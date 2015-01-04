package uni.apps.responsetesting;

import java.util.Calendar;

import uni.apps.responsetesting.fragment.MainMenuFragment;
import uni.apps.responsetesting.fragment.MultiUserNameFragment;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import uni.apps.responsetesting.reminders.AlertClient;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The application's entry point will be a small menu  
 * 
 * 
 * @author Mathew Andela
 *
 */
public class MainMenuActivity extends Activity implements MainMenuListener {

	//Needed Variables
	private FragmentManager frag_manager;
	private MainMenuFragment main_menu_frag;
	private MultiUserNameFragment multi_name_frag ;
	private static final String MAIN_MENU_TAG = "MainMenuFragment";
	private static final String MULTI_NAME_TAG = "MultiNameFragment";
	private static final String TAG = "MainMenuActivity";
	private AlertClient alertClient;
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable(){

		@Override
		public void run() {
			startNotify();
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set layout, fragment manager and fragment
		setContentView(R.layout.activity_main_menu);
		PreferenceManager.setDefaultValues(this, R.xml.all_settings, true);
		frag_manager = this.getFragmentManager();
		//checks the user mode
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
		if(!single)
			//if multi user mode
			startSession(false);
		else{
			//if single user mode
			//sets user id to single
			Editor editor = prefs.edit();
			editor.putString(getResources().getString(R.string.pref_key_user_id), "single");
			editor.commit();
			//add fragment to activity
			addFragments();
		}
		//checks if reminders are on
		if(prefs.getBoolean(getResources().getString(R.string.pref_key_remind), false)){
			//binds service to activty
			bindClient();
			//sets timer
			timerHandler.postDelayed(timerRunnable, 2000);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//resets action bar
		invalidateOptionsMenu();
		//if first time since changing out of multi user mode
		boolean done = false;
		//gets id and user mode
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
		String id = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		if(single && !id.equals("single")){
			//updates user id
			Editor editor = prefs.edit();
			editor.putString(getResources().getString(R.string.pref_key_user_id), "single");
			editor.commit();
			//refreshes page
			addFragments();
			done = true;
		}
		//checks if fragment needs to be updated
		if(main_menu_frag != null && !done)
			main_menu_frag.update();
	}

	@Override
	protected void onStop(){
		//unbinds service
		if(alertClient != null)
			alertClient.doUnbindService();
		//remove timers
		timerHandler.removeCallbacks(timerRunnable);
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//calls the action bar methods
		switch(item.getItemId()){
		case R.id.action_switch_user:
			startSession(true);
			return true;
		default:
			return ActivityUtilities.actionBarClicks(item, this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar, menu);
		//sets action bar subtitle
		ActionBar a = getActionBar();
		a.setSubtitle(getResources().getString(R.string.main_menu));
		return true;
	}

	@Override
	public void OnMenuItemClick(String value) {
		//switches to event activity on menu item click
		Intent i = new Intent(this, EventActivity.class);
		i.putExtra(getResources().getString(R.string.event), value);
		startActivity(i);
	}

	@Override
	public void OnMultiUserClick(String id) {
		//updates user id
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = prefs.edit();
		editor.putString(getResources().getString(R.string.pref_key_user_id), id);
		editor.commit();
		//replaces fragment
		addFragments();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//alters action bar
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean results = prefs.getBoolean(getResources().getString(R.string.pref_key_results), true);
		menu.findItem(R.id.action_results).setVisible(results);
		boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
		menu.findItem(R.id.action_switch_user).setVisible(!single);
		return super.onPrepareOptionsMenu(menu);
	}

	//adds fragment to activity
	private void addFragments() {
			//begins transaction
			FragmentTransaction ft = frag_manager.beginTransaction();

			//creates a new fragment and adds it to the activity 
			//	if(main_menu_frag == null){
			main_menu_frag = new MainMenuFragment();
			ft.replace(R.id.main_menu_container, main_menu_frag, MAIN_MENU_TAG);
			//	}
			//commits the transaction
			ft.commit();
			frag_manager.executePendingTransactions();
	}

	private void startSession(boolean show) {
		//get existing fragment
		multi_name_frag = (MultiUserNameFragment) frag_manager.findFragmentByTag(MULTI_NAME_TAG);
		//checks if needs to be shown
		if(multi_name_frag == null || show){
			//adds fragment to activity container
			multi_name_frag = new MultiUserNameFragment();
		}
		frag_manager.beginTransaction().replace(R.id.main_menu_container, multi_name_frag, MULTI_NAME_TAG).commit();
	}

	private void startNotify() {
		//gets alert time
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String time = prefs.getString(getResources().getString(R.string.pref_key_alert),
				getResources().getString(R.string.settings_time_default));
		//sets alert time
		int hour = getHour(time);
		int min = getMin(time);
		Calendar c = Calendar.getInstance();
		//c.setTimeZone(TimeZone.getTimeZone("GTM+12"));
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		//gets next alert time
		c.setTimeInMillis(c.getTimeInMillis() + (24*60*60*1000));
		long tmp = prefs.getLong(getResources().getString(R.string.pref_key_alert_next), 0);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(tmp);
		//checks if alert isn't set already
		if(c2.get(Calendar.DATE) != c.get(Calendar.DATE)){
			Log.d(TAG, "set Alarm");
			//set up alert
			alertClient.setAlarmForNotification(c);
			Editor editor = prefs.edit();
			//updates next alert time
			editor.putLong(getResources().getString(R.string.pref_key_alert_next),
					c.getTimeInMillis());
			editor.commit();
		}else{
			Log.d(TAG, "Alarm already set");
		}
	}

	//gets minutes from string
	private int getMin(String time) {
		int index = time.indexOf(':');
		if(index == -1)
			return 0;
		else{
			return Integer.parseInt(time.substring(index + 1));
		}
	}

	//gets hour from string
	private int getHour(String time) {
		int index = time.indexOf(':');
		if(index == -1)
			return 9;
		else{
			return Integer.parseInt(time.substring(0, index));
		}
	}

	//binds alert service to activity
	private void bindClient() {
		alertClient = new AlertClient(this);
		alertClient.doBindService();
	}

}
