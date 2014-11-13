package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.results.ResultsDisplayFragment;
import uni.apps.responsetesting.results.Results;
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

	private FragmentManager frag_manager;
	private ResultsDisplayFragment fragment;
	private static final String RESULTS_FRAG_TAG = "ResultsDisplayFragment";
	private static final String TAG = "ResultsDisplayActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results_display);
		frag_manager = this.getFragmentManager();
        addFragments();
	}

	private void addFragments() {
		fragment = (ResultsDisplayFragment) frag_manager.findFragmentByTag(RESULTS_FRAG_TAG);
		
		FragmentTransaction ft = frag_manager.beginTransaction();
		
		if(fragment == null){
			fragment = new ResultsDisplayFragment();
			ft.add(R.id.results_container, fragment, RESULTS_FRAG_TAG);
		}
		
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
		if(item.getItemId() == R.id.action_delete_all){
			deleteAll();
			fragment.update();
			return true;
		}
		switch(item.getItemId()){
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		//alters action bar
		menu.findItem(R.id.action_delete_all).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void deleteAll() {
		new AlertDialog.Builder(this)
		.setTitle("Delete All")
		.setMessage("Are you sure you wish to delete all event information?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
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
