package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.results.ResultsFragment;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ResultsDisplayActivity extends Activity {

	//Needed Variables
	private FragmentManager frag_manager;
	private ResultsFragment fragment;
	//private ResultsDisplayFragment fragment;
	private static final String RESULTS_FRAG_TAG = "ResultsDisplayFragment";
	private static final String TAG = "ResultsDisplayActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set layout, fragment manager and fragment
		setContentView(R.layout.activity_results_display);
		frag_manager = this.getFragmentManager();
        addFragments();
	}

	//adds fragment to activity
	private void addFragments() {
		//checks if fragment exists
		fragment = (ResultsFragment) frag_manager.findFragmentByTag(RESULTS_FRAG_TAG);
		//fragment = (ResultsDisplayFragment) frag_manager.findFragmentByTag(RESULTS_FRAG_TAG);
		//begins transaction
		FragmentTransaction ft = frag_manager.beginTransaction();
		//creates a new fragment and adds it to the activity 
		if(fragment == null){
			fragment = new ResultsFragment();
			//fragment = new ResultsDisplayFragment();
			ft.add(R.id.results_container, fragment, RESULTS_FRAG_TAG);
		}
		//commits the transaction
		ft.commit();
		frag_manager.executePendingTransactions();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//checks if delete all is clicked
		if(item.getItemId() == R.id.action_delete_all){
			deleteAll();
			fragment.update();
			return true;
		}
		//calls the action bar defaults
		if(ActivityUtilities.actionBarClicks(item, this)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		//alters action bar
		menu.findItem(R.id.action_delete_all).setVisible(true);
		menu.findItem(R.id.action_home).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
	
	//brings up a confirmation message dialog
	private void deleteAll() {
		new AlertDialog.Builder(this)
		.setTitle("Delete All")
		.setMessage("Are you sure you wish to delete all event information?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				//deletes all results
				Results.deleteAllResults(ResultsDisplayActivity.this);
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
            // do nothing
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
		
	}
}
