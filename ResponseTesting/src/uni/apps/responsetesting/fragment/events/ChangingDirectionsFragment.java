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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
	//other variables used to measure test progress
	private int counter = 0;
	private int maxTurns = 10;
	private int totalTime = 1000 * 20;
	private int timeElapsed = 0;
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
			// TODO Auto-generated method stub
			center.setVisibility(View.VISIBLE);
		}
	};
	
	//-----------------------------------------------------------------------------
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
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
		button = (Button) view.findViewById(R.id.cd_button);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onButtonClick();
			}

		});
	}

	@Override
	public void onButtonClick() {
		if(button.getText().toString().equals(getResources().getString(R.string.start))){
			results = new CorrectDurationInfo[maxTurns];
			button.setText("");
			button.setEnabled(false);
			counter = 0;
			results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
			timeElapsed = 0;
			changeImageViews();
			timerHandler.postDelayed(timerRunnable, 100);
		}
	}
	
	private void endTest() {
		removeTimerCallbacks();
		button.setText(getResources().getString(R.string.start));
		button.setEnabled(true);
		clearImageViews();
		double[] results = Results.getResults(this.results);
		String resultString = results[0] + " correct. " + 
		Conversion.milliToStringSeconds(results[1], 3) + " average time (s).";
		Results.insertResult(eventName, resultString,
				Calendar.getInstance().getTimeInMillis(), getActivity());
		ActivityUtilities.displayResults(getActivity(), eventName, resultString);
	}

	private void clearImageViews() {
		center.setImageDrawable(null);
		for(ImageView iv: clickable)
			iv.setImageDrawable(null);
	}

	private void changeImageViews() {
		if(toShuffle())
			Collections.shuffle(order, new Random());
		centerIndex = new Random().nextInt(centerArrows.length);
		for(int i = 0; i < clickable.length; i++){
			clickable[i].setImageDrawable(getResources().getDrawable(buttonArrows[order.get(i)]));
		}
		center.setImageDrawable(getResources().getDrawable(centerArrows[centerIndex]));
	}
	
	private boolean toShuffle(){
		int[] shuffle = new int[] {3,5,6,8,9};
		for(int i: shuffle)
			if(i == counter){
				return true;
			}
		return false;
	}

	@Override
	public void onImageClick(int view) {
		if(counter < maxTurns - 1){
			checkAnswer(view);
			changeImageViews();
		}else if(counter == maxTurns - 1){
			checkAnswer(view);
			endTest();
		}
	}

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
	
	private void moveToNext(long time){
		counter++;
		if(counter < results.length)
			results[counter] = new CorrectDurationInfo(time);
	}
	
	private void removeTimerCallbacks(){
		timerHandler.removeCallbacks(timerRunnable);
		timerHandler.removeCallbacks(centerImageAppear);
	}
}
