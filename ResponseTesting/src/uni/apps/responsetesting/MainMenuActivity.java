package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.MainMenuFragment;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private MainMenuFragment main_menu_frag = null;
	private static final String MAIN_MENU_TAG = "MainMenuFragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set layout, fragment manager and fragment
		setContentView(R.layout.activity_main_menu);
		PreferenceManager.setDefaultValues(this, R.xml.all_settings, true);
		frag_manager = this.getFragmentManager();
		addFragments();
	}


	//adds fragment to activity
	private void addFragments() {
		//checks if fragment exists
		main_menu_frag = (MainMenuFragment) frag_manager.findFragmentByTag(MAIN_MENU_TAG);

		//begins transaction
		FragmentTransaction ft = frag_manager.beginTransaction();

		//creates a new fragment and adds it to the activity 
		if(main_menu_frag == null){
			main_menu_frag = new MainMenuFragment();
			ft.add(R.id.main_menu_container, main_menu_frag, MAIN_MENU_TAG);
		}
		//commits the transaction
		ft.commit();
		frag_manager.executePendingTransactions();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//calls the action bar defaults
		if(ActivityUtilities.actionBarClicks(item, this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
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
}
