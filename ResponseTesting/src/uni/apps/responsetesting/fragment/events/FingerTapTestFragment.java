package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.Results;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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
 * 
 * 
 * @author Mathew Andela
 *
 */
public class FingerTapTestFragment extends Fragment {

	private static final String TAG = "FingerTapTestFragment";
	private static final String eventName = "Finger Tap Test";
	private static final int seconds = 5; 
	private Button startButton;
	private TextView infoTextView;
	private TextView clickCountTextView;
	private TextView timeLeftTextView;
	private boolean running = true;
	private int clickCount = 0;
	private long startTime = 0;	
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable() {

		@Override
		public void run(){
			long millis = Calendar.getInstance().getTimeInMillis();
			double seconds = (double) 5 - (double) (millis - startTime) / 1000;
			String value = Double.toString(seconds).substring(0, 3);
			timeLeftTextView.setText("Time Left: " + value + "s");
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
		setRetainInstance(true);
		clickCount = 0;
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
		RelativeLayout clickable = (RelativeLayout) view.findViewById(R.id.tap_container);
		startButton = (Button) view.findViewById(R.id.tap_click_start_button);
		infoTextView = (TextView) clickable.findViewById(R.id.tap_click_info);
		clickCountTextView = (TextView) clickable.findViewById(R.id.tap_click_count);
		timeLeftTextView = (TextView) clickable.findViewById(R.id.tap_click_time);
		clickable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!running){
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
					clickCount ++;
					clickCountTextView.setText("Tap Count: " + Integer.toString(clickCount));
				}
			}

		});
		
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(running && startTime == 0){
					running = !running;
					infoTextView.setVisibility(View.VISIBLE);
				}
			}
			
		});
	}

	private void endTest() {		
		if(running){
			timerHandler.removeCallbacks(timerRunnable);
			startButton.setText(getResources().getString(R.string.restart));
			infoTextView.setVisibility(View.INVISIBLE);
			infoTextView.setText(getResources().getString(R.string.start_square));
			clickCountTextView.setText(getResources().getString(R.string.tap_click_info_count));
			startTime = 0;
			Results.insertResult(eventName, Integer.toString(clickCount),
					Calendar.getInstance().getTimeInMillis(), getActivity());
			new AlertDialog.Builder(getActivity())
			.setTitle("Tap Test Complete")
			.setMessage("Your score was " + Integer.toString(clickCount) + " taps in 5s. With an average of " +
					Double.toString((double) clickCount / (double) seconds) + " taps per second.")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 

						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}
	}
}
