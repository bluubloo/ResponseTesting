package uni.apps.responsetesting;

import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


/**
 * This class displays the splash screen and handles events related to it
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SplashScreenActivity extends Activity {

//	private static final String TAG = "SplashScreenActivity";
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable(){

		@Override
		public void run() {
			//Changes to main menu
			Intent i = new Intent(SplashScreenActivity.this, MainMenuActivityTabbed.class);
			startActivity(i);
		}
	};

	//-----------------------------------------------------------------------------


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//changes theme
		ActivityUtilities.changeTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//sets splash screen image
		FrameLayout view = (FrameLayout) findViewById(R.id.container);
		view.setBackground(getResources().getDrawable(ActivityUtilities.getSplashIconId(this)));
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//set image info and starts timer
		setUpImage();
		timerHandler.postDelayed(timerRunnable, 3000);
	}

	private void setUpImage() {
		//sets image info
		FrameLayout view = (FrameLayout) findViewById(R.id.container);
		Point p = new Point(0,0);
		this.getWindowManager().getDefaultDisplay().getSize(p);
		int dim = p.x;
		if(dim > 2000)
			dim = 2000;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dim, dim);
		params.setMargins(10, 10, 10, 10);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		view.setLayoutParams(params);
	}

	@Override
	public void onStop(){
		super.onStop();
		//cancels timers
		timerHandler.removeCallbacks(timerRunnable);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		ActivityUtilities.changeActionBarIcon(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		default:
			return ActivityUtilities.actionBarClicks(item, this);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//alters action bar
		menu.findItem(R.id.action_send_all).setVisible(false);
		menu.findItem(R.id.action_send_recent).setVisible(false);
		menu.findItem(R.id.action_setup).setVisible(false);
		menu.findItem(R.id.action_settings).setVisible(false);
		menu.findItem(R.id.action_about).setVisible(false);
		menu.findItem(R.id.action_email_intent).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}

}
