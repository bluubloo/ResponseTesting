package uni.apps.responsetesting;

import uni.apps.responsetesting.results.Email;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * The super class activity which will handle all events and actions across all the activities
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SuperActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_settings:
			return true;
		case R.id.action_send_all:
			Email.sendResults(this, true);
			return true;
		case R.id.action_send_recent:
			 Email.sendResults(this, false);
			 return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
}
