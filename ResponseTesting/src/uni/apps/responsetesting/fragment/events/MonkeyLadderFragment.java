package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.MonkeyLadderGridAdapter;
import uni.apps.responsetesting.models.DurationInfo;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.Conversion;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class MonkeyLadderFragment extends Fragment {


	private static final String TAG = "MonkeyLadderFragment";
	private static final String eventName = "Monkey Ladder";
	private static final int maxTurns = 10;
	private static final int gridColumns = 5;
	private static final int waitTime = 250;
	private static final int maxErrors = 3;

	private GridView grid;
	private Button button;

	private int playTimes = 0;
	private int tiles = 3;
	private int clickedTiles = 0;
	private int errorCount = 0;
	private int counter = 0;
	private int currentMax = 0;
	private ArrayList<Integer> tileList;
	private boolean clickable = false;

	private DurationInfo[] results;
	private MonkeyLadderGridAdapter adapter;
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnableGridAppear = new Runnable(){

		@Override
		public void run() {
			grid.setVisibility(View.VISIBLE);
			timerHandler.postDelayed(timerRunnableTextDisappear, tiles * 500);
		}
	};

	private Runnable timerRunnableTextDisappear = new Runnable(){

		@Override
		public void run() {
			adapter.update(false);
			clickable = true;
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
			playTimes = savedInstanceState.getInt("playTime");
		setRetainInstance(true);
		setUpTiles();
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
		View view =  inflater.inflate(R.layout.monkey_ladder_fragment, container, false);
		grid = (GridView) view.findViewById(R.id.monkey_grid);
		button = (Button) view.findViewById(R.id.monkey_button);
		adapter = new MonkeyLadderGridAdapter(getActivity(), tileList, tiles);
		grid.setAdapter(adapter);
		setUpButton();
		setUpGridItemClick();
		return view;
	}

	private void setUpGridItemClick() {
		grid.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(clickable){	
					if((int) adapter.getItem(position) <= tiles){
						boolean result = checkClick(position);
						if(result){
							clickedTiles ++;
							if(clickedTiles >= tiles){
								clickable = false;
								currentMax ++;
								if(counter < maxTurns - 1)
									next(true);
								else
									endTest();
							} else{
								adapter.update();
							}						
						}else{
							clickable = false;
							errorCount ++;
							if(errorCount == maxErrors)
								endTest();
							else{
								next(false);
							}						
						}
					}
				}
			}
		});
	}

	private void setUpButton() {
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
					if(button.isEnabled()){
						button.setEnabled(false);
						playTimes++;
						counter = 0;
						currentMax = 0;
						tiles = 3;
						clickedTiles = 0;
						errorCount = 0;
						clickable = false;
						results = new DurationInfo[maxTurns];
						results[counter] = new DurationInfo(Calendar.getInstance().getTimeInMillis());
						alterTiles(false);
						grid.setVisibility(View.VISIBLE);
						timerHandler.postDelayed(timerRunnableTextDisappear, tiles * 500);
					}
				}else{
					ActivityUtilities.displayResults(getActivity(), eventName,
							"You have completed you daily 3 tries, please try a different test");
				}
			} 

		});
	}

	private void endTest(){
		removeTimerCallbacks();
		button.setEnabled(true);
		grid.setVisibility(View.INVISIBLE);
		String time = getResult();
		String resultString = Integer.toString(currentMax) + " reached. " + time + " average time (s).";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		Results.insertResult(eventName, Integer.toString(currentMax) + "|" + time,
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, resultString);
	}

	private String getResult() {
		double tmp = 0;
		int i = 0;
		for(DurationInfo d : results)
			if(d != null){
				tmp += d.getDuration();
				i ++;
			}
		tmp /= i;
		return Conversion.milliToStringSeconds(tmp, 3);
	}

	private void setUpTiles(){
		tileList = new ArrayList<Integer>();
		for(int i = 1; i <= gridColumns * gridColumns; i++)
			tileList.add(i);
	}

	private void alterTiles(boolean inc){
		if(inc)
			tiles ++;
		else if(tiles > 3)
			tiles --;
		Collections.shuffle(tileList, new Random());
		adapter.clear();
		adapter.update(tileList, tiles);
	}

	private boolean checkClick(int position) {
		int clicked = (int) adapter.getItem(position);
		return clicked == clickedTiles + 1;
	}

	private void moveToNext() {
		long time = Calendar.getInstance().getTimeInMillis();
		results[counter].addEndTime(time);
		counter ++;
		if(counter < maxTurns)
			results[counter] = new DurationInfo(time + waitTime);
	}


	private void next(boolean inc) {
		moveToNext();
		grid.setVisibility(View.INVISIBLE);
		clickedTiles = 0;
		alterTiles(inc);
		timerHandler.postDelayed(timerRunnableGridAppear, waitTime);
	}

	private void removeTimerCallbacks(){
		timerHandler.removeCallbacks(timerRunnableGridAppear);
		timerHandler.removeCallbacks(timerRunnableTextDisappear);
	}
}
