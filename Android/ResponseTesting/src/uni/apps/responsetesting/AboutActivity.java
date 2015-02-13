package uni.apps.responsetesting;

import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtilities.changeTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		ActivityUtilities.changeActionBarIcon(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
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
		menu.findItem(R.id.action_about).setVisible(false);
		menu.findItem(R.id.action_home).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
}
