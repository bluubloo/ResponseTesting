package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.settings.SingleUserSettingsFragment;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SetupModeActivity extends Activity {

	//Needed variables
	private static final String TAG = "SetupModeActivity";
	private static final String FRAG_TAG = "SetupModeFragment";
	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtilities.changeTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_mode);
		//adds fragment
		fragment = new SingleUserSettingsFragment();
		getFragmentManager().beginTransaction().replace(R.id.settings_container, fragment, 
				FRAG_TAG).commit();
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
		menu.findItem(R.id.action_setup).setVisible(false);
		menu.findItem(R.id.action_home).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
}
