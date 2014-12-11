package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.settings.MultiUserGroupFragment;
import uni.apps.responsetesting.fragment.settings.MultiUserSelectionFragment;
import uni.apps.responsetesting.interfaces.listener.MultiUserSettingsListener;
import uni.apps.responsetesting.models.MultiUserInfo;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MultiUserSetupModeActivity extends Activity implements MultiUserSettingsListener {

	//Needed variables
	private static final String TAG = "MultiUserSetupModeActivity";
	private static final String FRAG_TAG = "MultiUserSetupMode";
	private FragmentManager manager;
	private MultiUserSelectionFragment selectFragment;
	private MultiUserGroupFragment groupFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_user_setup_mode);
		//set variable
		manager = this.getFragmentManager();
		addFragments();
	}

	private void addFragments() {
		//add fragment to activity
		groupFragment = new MultiUserGroupFragment();
		manager.beginTransaction().add(R.id.settings_container, groupFragment, FRAG_TAG).commit();
	}
	
	private void switchFragments(boolean b, MultiUserInfo user) {
		//switches fragments
		if(b){
			selectFragment = MultiUserSelectionFragment.getInstance(user);
			manager.beginTransaction().replace(R.id.settings_container, selectFragment, FRAG_TAG).commit();
		} else {
			manager.beginTransaction().replace(R.id.settings_container, groupFragment, FRAG_TAG).commit();
		}
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
		if(item.getItemId() == R.id.action_add_group){
			if(groupFragment.isVisible() && groupFragment.isResumed() && !groupFragment.isRemoving())
				groupFragment.addNewGroup();
			return true;
		} else if(ActivityUtilities.actionBarClicks(item, this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		//alters action bar
		menu.findItem(R.id.action_setup).setVisible(false);
		menu.findItem(R.id.action_add_group).setVisible(true);
		menu.findItem(R.id.action_home).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onBackPressed(){
		//switches fragments or goes back
		if(groupFragment.isVisible() && groupFragment.isResumed() && !groupFragment.isRemoving())
			super.onBackPressed();
		else
			switchFragments(false, null);
	}

	@Override
	public void onGroupChildClick(MultiUserInfo user) {
		//switches fragments
		switchFragments(true, user);
	}
}
