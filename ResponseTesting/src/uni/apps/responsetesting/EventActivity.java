package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.events.AppearingObjectFragment;
import uni.apps.responsetesting.fragment.events.CenterArrowFragment;
import uni.apps.responsetesting.fragment.events.ChangingDirectionsFragment;
import uni.apps.responsetesting.fragment.events.ChaseTestFragment;
import uni.apps.responsetesting.fragment.events.EvenVowelFragment;
import uni.apps.responsetesting.fragment.events.EventInstructionsFragment;
import uni.apps.responsetesting.fragment.events.FingerTapTestFragment;
import uni.apps.responsetesting.fragment.events.MonkeyLadderFragment;
import uni.apps.responsetesting.fragment.events.OneCardLearningFragment;
import uni.apps.responsetesting.fragment.events.PatternRecreationFragment;
import uni.apps.responsetesting.fragment.events.QuestionaireFragment;
import uni.apps.responsetesting.fragment.events.QuestionaireQuestionFragment;
import uni.apps.responsetesting.fragment.events.StroopTest2Fragment;
import uni.apps.responsetesting.interfaces.listener.EventInstructionsListener;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
public class EventActivity extends Activity implements EventInstructionsListener  {

	//Needed Variables
	private FragmentManager frag_manager;
	private static final String EVENT_TAG = "EventFragment";
	private static final String TAG = "EventActivity";
	private String eventName = "";
	private Fragment fragment;
	private EventInstructionsFragment instructFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityUtilities.changeTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		//gets fragment manager
		frag_manager = this.getFragmentManager();
		//gets eventname from intent extras
		//then adds fragment to activity
		eventName = getIntent().getExtras().getString(
				this.getResources().getString(R.string.event));
		addFragments();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}

	//adds a fragment to the activity
	private void addFragments() {
		//transaction and resources
		FragmentTransaction ft = frag_manager.beginTransaction();
		//gets preference for showing instruction page or not
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean instruct = prefs.getBoolean(getResources().getString(R.string.pref_key_instruct), true);

		instructFragment = EventInstructionsFragment.getInstance(eventName, getResources());
		setFragment();
		//adds fragment to activity
		if(instruct)
			ft.replace(R.id.event_container, instructFragment, EVENT_TAG);
		else
			ft.replace(R.id.event_container, fragment, EVENT_TAG);
		ft.commit();
		frag_manager.executePendingTransactions();
	}

	public void setFragment(){
		Resources r = getResources();
		if(eventName.equals(r.getString(R.string.event_name_questionaire)) && checkTheme()){
			fragment = new QuestionaireQuestionFragment();
			return;
		}
		
		//TODO add new Event fragments here
		if(eventName.equals(r.getString(R.string.event_name_finger_tap)))
			fragment = new FingerTapTestFragment();
		else if(eventName.equals(r.getString(R.string.event_name_questionaire)))
			fragment = new QuestionaireFragment();
		else if(eventName.equals(r.getString(R.string.event_name_appear_obj)))
			fragment = AppearingObjectFragment.getInstance(false);
		else if(eventName.equals(r.getString(R.string.event_name_appear_obj_fixed)))
			fragment = AppearingObjectFragment.getInstance(true);
		else if(eventName.equals(r.getString(R.string.event_name_stroop)))
			fragment = new StroopTest2Fragment();
		else if(eventName.equals(r.getString(R.string.event_name_one_card)))
			fragment = new OneCardLearningFragment();
		else if(eventName.equals(r.getString(R.string.event_name_change_direct)))
			fragment = new ChangingDirectionsFragment();
		else if(eventName.equals(r.getString(R.string.event_name_pattern_recreate)))
			fragment = new PatternRecreationFragment();
		else if(eventName.equals(r.getString(R.string.event_name_even_vowel)))
			fragment = new EvenVowelFragment();
		else if(eventName.equals(r.getString(R.string.event_name_chase)))
			fragment = new ChaseTestFragment();
		else if(eventName.equals(r.getString(R.string.event_name_center_arrow)))
			fragment = new CenterArrowFragment();
		else if(eventName.equals(r.getString(R.string.event_name_monkey)))
			fragment = new MonkeyLadderFragment();
	}

	private boolean checkTheme() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String theme = prefs.getString(getResources().getString(R.string.pref_key_theme), 
				getResources().getString(R.string.settings_theme_default));
		return theme.equals("Default");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//checks if info button on action bar is clicked
		if(item.getItemId() == R.id.action_info){
			ActivityUtilities.eventInfo(eventName, this);
			return true;
		}
		//else calls the defaults
		else if(ActivityUtilities.actionBarClicks(item, this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		//adds event name to actionbar subtitle
		ActionBar a = getActionBar();
		a.setSubtitle(eventName);
		ActivityUtilities.changeActionBarIcon(this);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		//alters action bar
		menu.findItem(R.id.action_info).setVisible(true);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean results = prefs.getBoolean(getResources().getString(R.string.pref_key_results), true);
		menu.findItem(R.id.action_results).setVisible(results);
		menu.findItem(R.id.action_home).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void switchFragments() {
		//switches from instruction fragment to event fragment
		if(instructFragment.isVisible() && instructFragment.isResumed() &&
				!instructFragment.isRemoving()){
			if(fragment == null)
				setFragment();
			FragmentTransaction ft = frag_manager.beginTransaction();
			ft.replace(R.id.event_container, fragment, EVENT_TAG);
			ft.commit();
			frag_manager.executePendingTransactions();
		}		
	}

	@Override
	public void goBack() {
		//goes back to main menu
		onBackPressed();
	}

	@Override
	public void onNextClick(String total, String light, String sound, String hr) {
		QuestionaireQuestionFragment frag = QuestionaireQuestionFragment.getInstance(total, light, sound, hr);
		frag_manager.beginTransaction().replace(R.id.event_container, frag, EVENT_TAG).commit();
	}
}
