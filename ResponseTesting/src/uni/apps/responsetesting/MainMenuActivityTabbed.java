package uni.apps.responsetesting;

import java.util.Calendar;
import java.util.Locale;

import uni.apps.responsetesting.adapter.MultiUserNameListAdapter;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.fragment.MainMenuFragmentTabbed;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import uni.apps.responsetesting.models.MultiUserInfo;
import uni.apps.responsetesting.reminders.AlertClient;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainMenuActivityTabbed extends Activity implements ActionBar.TabListener, MainMenuListener {

	private static final String TAG = "TestActivity";


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;


	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
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
		setContentView(R.layout.activity_test);

		PreferenceManager.setDefaultValues(this, R.xml.all_settings, true);
		//frag_manager = this.getFragmentManager();
		//checks the user mode
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
		if(!single)
			//if multi user mode
			startSession();
		else{
			//if single user mode
			//sets user id to single
			Editor editor = prefs.edit();
			editor.putString(getResources().getString(R.string.pref_key_user_id), "single");
			editor.commit();
			setUpPage();
		}
		//checks if reminders are on
		if(prefs.getBoolean(getResources().getString(R.string.pref_key_remind), false)){
			//binds service to activty
			bindClient();
			//sets timer
			timerHandler.postDelayed(timerRunnable, 2000);
		}
	}

	private void setUpPage(){
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
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
	public void onResume() {
		super.onResume();
		//resets action bar
		invalidateOptionsMenu();
		//gets id and user mode
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
		String id = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		if(single && !id.equals("single")){
			//updates user id
			Editor editor = prefs.edit();
			editor.putString(getResources().getString(R.string.pref_key_user_id), "single");
			editor.commit();
		} else if(!single && id.equals("single"))
			startSession();

			mViewPager.getAdapter().notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		ActionBar a = getActionBar();
		a.setSubtitle(getResources().getString(R.string.main_menu));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_switch_user:
			startSession();
			return true;
		default:
			return ActivityUtilities.actionBarClicks(item, this);
		}
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

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			return MainMenuFragmentTabbed.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
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

	}

	private void startSession() {
		final MultiUserNameListAdapter adapter = new MultiUserNameListAdapter(setUpList(), this);

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
		builderSingle.setIcon(R.drawable.uni_logo);
		builderSingle.setTitle("Select One Name:-");

		builderSingle.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builderSingle.setAdapter(adapter,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MultiUserInfo info = (MultiUserInfo) adapter.getItem(which);
				OnMultiUserClick(info.getId());
				if(mViewPager == null)
					setUpPage();
				else
					mViewPager.getAdapter().notifyDataSetChanged();
			}
		});
		builderSingle.show();


	}

	private MultiUserInfo[] setUpList() {
		//gets data and creates adapter
		return formatData(DatabaseHelper.getInstance(this, getResources()).getMultiUsers());
	}

	private MultiUserInfo[] formatData(Cursor cursor) {
		//formats data into list from cursor
		MultiUserInfo[] tmp =  new MultiUserInfo[cursor.getCount()];
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				tmp[i] = new MultiUserInfo(cursor.getString(0), cursor.getString(1),
						cursor.getString(2));
				i++;
			} while(cursor.moveToNext());
		}
		return tmp;
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
