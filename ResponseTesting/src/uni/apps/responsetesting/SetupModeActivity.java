package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.settings.SingleUserSettingsFragment;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SetupModeActivity extends Activity {

	private static final String TAG = "SetupModeActivity";
	private static final String FRAG_TAG = "SetupModeFragment";
	private PreferenceFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_mode);
		boolean single = this.getIntent().getBooleanExtra("user", true);
		if(single)
			fragment = new SingleUserSettingsFragment();
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
		menu.findItem(R.id.action_setup).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}
}