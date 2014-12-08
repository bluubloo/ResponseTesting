package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * This fragment performs the Finger Tap Test
 * Tests - CNS
 * 
 * @author Mathew Andela
 *
 */
public class FingerTapTestFragment extends Fragment {

	//variables
	private static final String TAG = "FingerTapTestFragment";
	private static final String eventName = "Finger Tap Test";
	private static final int seconds = 5; 
	private Button startButton;
	private TextView infoTextView;
	private TextView clickCountTextView;
	private TextView timeLeftTextView;
	private boolean running = true;
	private int clickCount = 0;
	private int playTimes = 0;
	private long startTime = 0;	
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable() {

		@Override
		public void run(){
			//gets time
			long millis = Calendar.getInstance().getTimeInMillis();
			double seconds = (double) 5 - (double) (millis - startTime) / 1000;
			String value = Double.toString(seconds).substring(0, 3);
			timeLeftTextView.setText("Time Left: " + value + "s");
			//checks if time left
			if(running && (millis - startTime) < 5000)
				timerHandler.postDelayed(this, 100);
			else{
				endTest();
			}
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
			playTimes = savedInstanceState.getInt("playTime");
		setRetainInstance(true);
		clickCount = 0;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("playTime", playTimes);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.finger_tap_test_fragment, container, false);
		setupTest(view);
		return view;
	}

	@Override
	public void onStop(){
		super.onStop();
		timerHandler.removeCallbacks(timerRunnable);
	}

	private void setupTest(View view) {
		//sets views
		RelativeLayout clickable = (RelativeLayout) view.findViewById(R.id.tap_container);
		startButton = (Button) view.findViewById(R.id.tap_click_start_button);
		infoTextView = (TextView) clickable.findViewById(R.id.tap_click_info);
		clickCountTextView = (TextView) clickable.findViewById(R.id.tap_click_count);
		timeLeftTextView = (TextView) clickable.findViewById(R.id.tap_click_time);
		//sets click events
		clickable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!running){
					//initialises test variables
					running = !running;
					clickCount = 0;
					startTime = Calendar.getInstance().getTimeInMillis();
					startButton.setText(getResources().getString(R.string.tap_click_info_3));
					infoTextView.setText(getResources().getString(R.string.tap_click_info_3));
					timerHandler.postDelayed(timerRunnable, 0);
					clickCount ++;
					clickCountTextView.setText("Tap Count: " + Integer.toString(clickCount));
				} else if (running && (Calendar.getInstance().getTimeInMillis() - startTime) > (seconds * 1000)){
					//do nothing
				} else if (running && startTime != 0) {
					//increase tap count
					clickCount ++;
					clickCountTextView.setText("Tap Count: " + Integer.toString(clickCount));
				}
			}

		});

		//checks startable
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(running && startTime == 0){
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
						running = !running;
						infoTextView.setVisibility(View.VISIBLE);
						playTimes++;
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
				}
			}

		});
	}

	//ends test
	private void endTest() {		
		if(running){
			//resets views
			timerHandler.removeCallbacks(timerRunnable);
			startButton.setText(getResources().getString(R.string.restart));
			infoTextView.setVisibility(View.INVISIBLE);
			infoTextView.setText(getResources().getString(R.string.start_square));
			clickCountTextView.setText(getResources().getString(R.string.tap_click_info_count));
			startTime = 0;
			//gets user id
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
			//inserts and displays results
			Results.insertResult(eventName, Integer.toString(clickCount),
					Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
			ActivityUtilities.displayResults(getActivity(), eventName, 
					"Your score was " + Integer.toString(clickCount) + " taps in 5s. With an average of " +
							Double.toString((double) clickCount / (double) seconds) + " taps per second.");
		}
	}
}
