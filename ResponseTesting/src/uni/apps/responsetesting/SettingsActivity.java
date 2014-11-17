package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.SettingsFragment;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This activity class handles the settings of the application
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SettingsActivity extends Activity {

	//Variables
	private static final String TAG = "SettingsActivity";
	private static final String FRAG_TAG = "SettingsFragment";
	private SettingsFragment fragment;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//sets up settings fragment
		fragment = new SettingsFragment();		
		getFragmentManager().beginTransaction().replace(R.id.settings_container, fragment, 
				FRAG_TAG).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//calls action bar clicks
		if(ActivityUtilities.actionBarClicks(item, this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		//alters action bar
		menu.findItem(R.id.action_settings).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}
}
