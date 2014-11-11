package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;

import uni.apps.responsetesting.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/***
 * 
 * 
 * 
 * @author Mathew Andela
 *
 */
public class FingerTapTestFragment extends SuperEventFragment {

	private static final String TAG = "FingerTapTestFragment";
	private static final String eventName = "Finger Tap Test";
	private TextView infoTextView;
	private boolean running = false;
	private int clickCount = 0;
	private long startTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		FrameLayout clickable = (FrameLayout) view.findViewById(R.id.tap_container);
		infoTextView = (TextView) clickable.findViewById(R.id.tap_click_info);
		clickable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!running){
					running = !running;
					clickCount = 0;
					startTime = Calendar.getInstance().getTimeInMillis();
					infoTextView.setText(getActivity().getResources().getString(R.string.tap_click_info_3));
				} else if (Calendar.getInstance().getTimeInMillis() - startTime > 5000){
					running = !running;
					infoTextView.setText(getActivity().getResources().getString(R.string.tap_click_info_2));
				}
				
				clickCount ++;
				
				if(!running){
					endTest();
				}
			}
			
		});
	}

	private void endTest() {
		// TODO Auto-generated method stub
	}
}
