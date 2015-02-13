package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.ChangingDirectionsListener;
import uni.apps.responsetesting.models.CorrectDurationInfo;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.Conversion;
import android.app.Activity;
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
import android.widget.ImageView;

/**
 * This fragment is for the Changing directions test
 * Tests - response time, attention to detail & mental flexibility
 * 
 * 
 * @author Mathew Andela
 *
 */
public class ChangingDirectionsFragment extends Fragment implements ChangingDirectionsListener {

	private static final String TAG = "ChangingDirectionsFragment";
	private static final String eventName = "Changing Directions";
	private ChangingDirectionsListener listener;
	//clickable imageviews
	private ImageView[] clickable = new ImageView[4];
	//center image view
	private ImageView center;
	//order of the arrows to be put into the imageviews
	private ArrayList<Integer> order = new ArrayList<Integer>();
	private int centerIndex = 0;
	//start button
	private Button button;
	private Button instruct;
	//other variables used to measure test progress
	private int counter = 0;
	private int maxTurns = 10;
	private int totalTime = 1000 * 20;
	private int timeElapsed = 0;
	private int playTimes = 0;
	private Handler timerHandler = new Handler();
	private CorrectDurationInfo[] results;

	//drawable ids
	//NOTE: keep these in the same order i.e. arrow_right in position as blue_arrow_right 
	private static final int[] buttonArrows = new int[] {R.drawable.arrow_down, R.drawable.arrow_left,
		R.drawable.arrow_right, R.drawable.arrow_up};
	private static final int[] centerArrows = new int[] {R.drawable.blue_arrow_down, 
		R.drawable.blue_arrow_left, R.drawable.blue_arrow_right, R.drawable.blue_arrow_up};

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable() {

		@Override
		public void run(){
			//checks times
			timeElapsed += 100;
			if(totalTime - timeElapsed <= 0 || counter == maxTurns){
				endTest();
			} else{
				timerHandler.postDelayed(this, 100);
			}

		}
	};

	private Runnable centerImageAppear = new Runnable() {

		@Override
		public void run() {
			//makes image visible
			center.setVisibility(View.VISIBLE);
		}
	};

	//-----------------------------------------------------------------------------


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
			playTimes = savedInstanceState.getInt("playTime");
		setRetainInstance(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("playTime", playTimes);
	}

	@Override
	public void onStop(){
		super.onStop();
		removeTimerCallbacks();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.changing_directions_fragment, container, false);
		setUpButton(view);
		setUpImageViews(view);
		return view;
	}

	//attachs listener to fragment
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (ChangingDirectionsListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement ChangingDirectionsListener");
		}

	}

	private void setUpImageViews(View view) {
		//Initialize clickable imageviews
		center = (ImageView) view.findViewById(R.id.cd_center);
		clickable[0] = (ImageView) view.findViewById(R.id.cd_top_left);
		clickable[1] = (ImageView) view.findViewById(R.id.cd_top_right);
		clickable[2] = (ImageView) view.findViewById(R.id.cd_bottom_left);
		clickable[3] = (ImageView) view.findViewById(R.id.cd_bottom_right);
		clearImageViews();
		//Set imageviews click events
		clickable[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onImageClick(0);
			}

		});

		clickable[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onImageClick(1);
			}

		});

		clickable[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onImageClick(2);
			}

		});

		clickable[3].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onImageClick(3);
			}

		});

		//Add elements to order list
		order.add(0);
		order.add(1);
		order.add(2);
		order.add(3);
	}

	private void setUpButton(View view) {
		button = (Button) view.findViewById(R.id.button_start);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onButtonClick();
			}

		});
		instruct = (Button) view.findViewById(R.id.button_instruct);

		instruct.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ActivityUtilities.eventInfo(eventName, getActivity());
			}

		});
	}

	@Override
	public void onButtonClick() {
		//checks if test is unstarted
		if(button.getText().toString().equals(getResources().getString(R.string.start))){
			if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
				//initalises test variables
				results = new CorrectDurationInfo[maxTurns];
				button.setEnabled(false);
				instruct.setEnabled(false);
				counter = 0;
				results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
				timeElapsed = 0;
				changeImageViews();
				timerHandler.postDelayed(timerRunnable, 100);
			} else{
				ActivityUtilities.displayResults(getActivity(), eventName,
						"You have completed you daily 3 tries, please try a different test");
			}
		}
	}

	private void endTest() {
		//ends test
		//resets test vairables
		removeTimerCallbacks();
		button.setEnabled(true);
		instruct.setEnabled(true);
		clearImageViews();
		//gets results
		double[] results = Results.getResults(this.results);
		String tmp = Conversion.milliToStringSeconds(results[1], 3);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		String resultString = results[0] + " correct. " + tmp + " average time (s).";
		//inserts & displays results
		Results.insertResult(eventName, results[0] + "|" + tmp,
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, resultString);
	}

	private void clearImageViews() {
		//clears imageview
		center.setImageDrawable(null);
		for(ImageView iv: clickable)
			iv.setImageDrawable(null);
	}

	private void changeImageViews() {
		//alters imageviews
		//shuffles outer arrows
		if(toShuffle())
			Collections.shuffle(order, new Random());
		//gets next arrow direction
		centerIndex = new Random().nextInt(centerArrows.length);
		//redraws images
		for(int i = 0; i < clickable.length; i++){
			clickable[i].setImageDrawable(getResources().getDrawable(buttonArrows[order.get(i)]));
		}
		center.setImageDrawable(getResources().getDrawable(centerArrows[centerIndex]));
	}

	private boolean toShuffle(){
		//shuffle outer arrow turns
		int[] shuffle = new int[] {3,5,6,8,9};
		for(int i: shuffle)
			if(i == counter){
				return true;
			}
		return false;
	}

	@Override
	public void onImageClick(int view) {
		//image view clicks
		if(counter < maxTurns - 1){
			checkAnswer(view);
			changeImageViews();
		}else if(counter == maxTurns - 1){
			checkAnswer(view);
			endTest();
		}
	}

	//checks if answer is correct
	private void checkAnswer(int viewIndex) {
		center.setVisibility(View.INVISIBLE);
		boolean result = false;
		if(order.get(viewIndex) == centerIndex)
			result = true;
		results[counter].addResult(result);
		long time = Calendar.getInstance().getTimeInMillis();
		results[counter].addEndTime(time);
		moveToNext(time);
		timerHandler.postDelayed(centerImageAppear, 200);
	}

	//moves to net result position
	private void moveToNext(long time){
		counter++;
		if(counter < results.length)
			results[counter] = new CorrectDurationInfo(time);
	}

	//diables timers
	private void removeTimerCallbacks(){
		timerHandler.removeCallbacks(timerRunnable);
		timerHandler.removeCallbacks(centerImageAppear);
	}
}
