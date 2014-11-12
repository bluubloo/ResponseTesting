package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.events.AppearingObjectFragment;
import uni.apps.responsetesting.fragment.events.FingerTapTestFragment;
import uni.apps.responsetesting.fragment.events.QuestionaireFragment;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This activity will be the placeholder activity for all or the majority of the events/tests
 * 
 * 
 * @author Mathew Andela
 *
 */
public class EventActivity extends Activity {

	private FragmentManager frag_manager;
	private static final String EVENT_TAG = "EventFragment";
	private static final String TAG = null;
	private String eventName = "";
	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		frag_manager = this.getFragmentManager();
		eventName = getIntent().getExtras().getString(
				this.getResources().getString(R.string.event));
		addFragments();
	}

	private void addFragments() {
		FragmentTransaction ft = frag_manager.beginTransaction();
		Resources r = getResources();

		//TODO add new Event fragments here
		if(eventName.equals(r.getString(R.string.event_name_finger_tap)))
			fragment = new FingerTapTestFragment();
		else if(eventName.equals(r.getString(R.string.event_name_questionaire)))
			fragment = new QuestionaireFragment();
		else if(eventName.equals(r.getString(R.string.event_name_appear_obj)))
			fragment = new AppearingObjectFragment();

		ft.replace(R.id.event_container, fragment, EVENT_TAG);
		ft.commit();
		frag_manager.executePendingTransactions();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_info){
			ActivityUtilities.eventInfo(eventName, this);
			return true;
		}
		else if(ActivityUtilities.actionBarClicks(item, this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		//alters action bar
		menu.findItem(R.id.action_info).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
}
