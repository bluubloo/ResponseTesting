package uni.apps.responsetesting;

import uni.apps.responsetesting.fragment.results.ResultsDisplayFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ResultsDisplayActivity extends Activity {

	private FragmentManager frag_manager;
	private ResultsDisplayFragment fragment;
	private static final String RESULTS_FRAG_TAG = "ResultsDisplayFragment";
	
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()){
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
