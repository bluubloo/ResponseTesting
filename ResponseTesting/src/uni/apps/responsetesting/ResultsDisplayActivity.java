package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.results.ResultsFragment;
import uni.apps.responsetesting.interfaces.listener.ResultsListener;
import uni.apps.responsetesting.results.GraphListFragment;
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

public class ResultsDisplayActivity extends Activity implements ResultsListener{

	//Needed Variables
	private FragmentManager frag_manager;
	private ResultsFragment fragment;
	private GraphListFragment listFragment;
	//private ResultsDisplayFragment fragment;
	private static final String RESULTS_FRAG_TAG = "ResultsDisplayFragment";
	private static final String GRAPH_LIST_TAG = "GraphListFragment";
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
		//fragment = (ResultsFragment) frag_manager.findFragmentByTag(RESULTS_FRAG_TAG);
		listFragment = (GraphListFragment) frag_manager.findFragmentByTag(GRAPH_LIST_TAG);
		//begins transaction
		FragmentTransaction ft = frag_manager.beginTransaction();
		//creates a new fragment and adds it to the activity 
		if(listFragment == null){
			listFragment = new GraphListFragment();
			//fragment = new ResultsFragment();
		}
		ft.replace(R.id.results_container, listFragment, GRAPH_LIST_TAG);
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
			if(fragment != null)
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

	@Override
	public void switchToGraph(String value) {
		fragment = ResultsFragment.getInstance(value);
		frag_manager.beginTransaction().replace(R.id.results_container, fragment, RESULTS_FRAG_TAG).commit();
	}
	
	@Override
	public void onBackPressed(){
		if(fragment != null && fragment.isVisible() && fragment.isResumed() && !fragment.isRemoving()){
			listFragment = (GraphListFragment) frag_manager.findFragmentByTag(GRAPH_LIST_TAG);
			if(listFragment == null)
				listFragment = new GraphListFragment();
			frag_manager.beginTransaction().replace(R.id.results_container, listFragment, GRAPH_LIST_TAG).commit();
		} else
			super.onBackPressed();
	}
}
