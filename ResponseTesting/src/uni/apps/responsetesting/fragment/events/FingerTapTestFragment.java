package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;
import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.Results;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * TODO Time Left + add in Timer
 * 
 * 
 * @author Mathew Andela
 *
 */
public class FingerTapTestFragment extends Fragment {

	private static final String TAG = "FingerTapTestFragment";
	private static final String eventName = "Finger Tap Test";
	private static final int seconds = 5; 
	private TextView infoTextView;
	private TextView clickCountTextView;
	private boolean running = false;
	private int clickCount = 0;
	private long startTime = 0;	

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

	private void setupTest(View view) {
		RelativeLayout clickable = (RelativeLayout) view.findViewById(R.id.tap_container);
		infoTextView = (TextView) clickable.findViewById(R.id.tap_click_info);
		clickCountTextView = (TextView) clickable.findViewById(R.id.tap_click_count);
		clickable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!running){
					running = !running;
					clickCount = 0;
					startTime = Calendar.getInstance().getTimeInMillis();
					infoTextView.setText(getResources().getString(R.string.tap_click_info_3));
				} else if (running && (Calendar.getInstance().getTimeInMillis() - startTime) > (seconds * 1000)){
					//	running = !running;
					infoTextView.setText(getResources().getString(R.string.tap_click_info_2));
					endTest();
				} else {
					clickCount ++;
					clickCountTextView.setText("Tap Count: " + Integer.toString(clickCount));
				}
			}

		});
	}

	private void endTest() {
		new AlertDialog.Builder(getActivity())
		.setTitle("Tap Test Complete")
		.setMessage("Your score was " + Integer.toString(clickCount) + " taps in 5s. With an average of " +
		Double.toString((double) clickCount / (double) seconds) + " taps per second.")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				clickCountTextView.setText(getResources().getString(R.string.tap_click_info_count));
				running = !running;
				Results.insertResult(eventName, Integer.toString(clickCount),
						Calendar.getInstance().getTimeInMillis(), getActivity());
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}
}
