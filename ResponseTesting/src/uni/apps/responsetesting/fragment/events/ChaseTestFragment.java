package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.ChaseTestGridAdapter;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.Conversion;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class ChaseTestFragment extends Fragment {

	private static final String TAG = "ChaseTestFragment";
	private static final String eventName = "Chase Test";
	private ChaseTestGridAdapter adapter;
	private GridView grid;
	private Button button;
	private boolean running = false;
	private int targetPos = 42;
	private int userPos = 6;
	private int counter = 0;
	private int playTimes = 0;
	private ArrayList<Integer> results;
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable() {

		@Override
		public void run(){
			endTest();
		}
	};

	private Runnable timerClicks = new Runnable() {

		@Override
		public void run(){
			results.add(counter);
			counter = 0;
			timerHandler.postDelayed(this, 1000);
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
		timerHandler.removeCallbacks(timerRunnable);
		timerHandler.removeCallbacks(timerClicks);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.chase_test_fragment, container, false);
		grid = (GridView) view.findViewById(R.id.chase_grid);
		grid.setVisibility(View.INVISIBLE);
		adapter = new ChaseTestGridAdapter(getActivity());
		grid.setAdapter(adapter);
		setUpGridItemClick();
		setUpButtonClick(view);
		return view;
	}

	private void setUpButtonClick(View view) {
		final Resources r = getResources();
		button = (Button) view.findViewById(R.id.chase_button);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(button.getText().toString().equals(r.getString(R.string.start))){
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
					button.setEnabled(false);
					grid.setVisibility(View.VISIBLE);
					running = true;
					counter = 0;
					targetPos = 42;
					userPos = 6;
					results = new ArrayList<Integer>();
					timerHandler.postDelayed(timerRunnable, 30 * 1000);
					timerHandler.postDelayed(timerClicks, 1000);
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
				}
			}

		});
	}

	private void setUpGridItemClick() {
		grid.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(running){
					counter ++;
					if(targetPos == position){
						endTest();
					}else if(position != userPos){
						boolean clickable = moveable(position, userPos);
						if(clickable){
							moveToNext(position);
						}
					}
				}
			}

		});
	}

	protected void moveToNext(int position) {
		userPos = position;
		int[] newTarget = new int[] {targetPos + 1, targetPos + 7,
				targetPos - 1, targetPos - 7};
		boolean[] moveable = new boolean[newTarget.length];
		for(int i = 0; i < newTarget.length; i++)
			moveable[i] = moveableTarget(newTarget[i], targetPos);

		ArrayList<Integer> moves = new ArrayList<Integer>();
		for(int i = 0; i < moveable.length; i++)
			if(moveable[i])
				moves.add(newTarget[i]);
		Collections.shuffle(moves);
		targetPos = moves.get(0);
		adapter.update(targetPos, userPos);
	}

	private boolean moveableTarget(int i, int current) {
		//checks for user position
		if(i == userPos)
			return false;
		//Checks corners
		switch(current){
		case 0:
			if(i == 1 || i == 7)
				return true;
			else
				return false;
		case 6:
			if(i == 5 || i == 13)
				return true;
			else
				return false;
		case 42:
			if(i == 43 || i == 35)
				return true;
			else
				return false;
		case 48:
			if(i == 47 || i == 41)
				return true;
			else
				return false;
		}
		//checks top and bottom
		if(current > 0 && current < 6){
			if(i == current + 1 || i == current - 1 || i == current + 7)
				return true;
			else 
				return false;
		} else if(current > 42 && current < 48){
			if(i == current + 1 || i == current - 1 || i == current - 7)
				return true;
			else 
				return false;
		}
		//checks sides
		if(current % 7 == 0){
			if(i == current + 7 || i == current - 7 || i == current + 1)
				return true;
			else 
				return false;
		} else if (current % 7 == 6){
			if(i == current + 7 || i == current - 7 || i == current - 1)
				return true;
			else 
				return false;
		}
		return true;
	}

	private boolean moveable(int position, int current) {
		if(position == userPos - 1 || position == userPos + 1 || 
				position == userPos - 7 || position == userPos + 7)
			return true;
		return false;
	}

	private void endTest(){
		timerHandler.removeCallbacks(timerRunnable);
		timerHandler.removeCallbacks(timerClicks);
		results.add(counter);
		counter = 0;
		running = false;
		grid.setVisibility(View.INVISIBLE);
		adapter.reset();
		button.setEnabled(true);
		String result = getResults();
		Results.insertResult(eventName, result,
				Calendar.getInstance().getTimeInMillis(), getActivity());
		ActivityUtilities.displayResults(getActivity(), eventName,
				"You had an average of " + result + " moves a second");
	}

	private String getResults() {
		double tmp = 0;
		for(int i : results)
			tmp += i;
		return Conversion.formatString(Double.toString(tmp / results.size()), 4);
	}
}
