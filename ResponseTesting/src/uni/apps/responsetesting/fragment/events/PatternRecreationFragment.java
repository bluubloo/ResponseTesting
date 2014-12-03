package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.PatternRecreationGridAdapter;
import uni.apps.responsetesting.interfaces.listener.PatternRecreationClickListener;
import uni.apps.responsetesting.models.DurationInfo;
import uni.apps.responsetesting.models.PatternRecreationData;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class PatternRecreationFragment extends Fragment implements PatternRecreationClickListener {

	private static final String TAG = "PatternRecreationFragment";
	private static final String eventName = "Pattern Recreation";
	private static final int maxTurns = 10;

	private int turns = 1;
	private int tiles = 3;
	private int maxTilesReached = tiles;
	private int coloumns = 3;
	private int rows = 3;
	private int clickCount = 0;
	private int errorClickCount = 3;
	private int counter = 0;
	private int playTimes = 0;

	private boolean canClick = false;

	private PatternRecreationData[] data;
	private DurationInfo[] results;

	private GridView grid;
	private Button start;

	private PatternRecreationGridAdapter adapter;
	private PatternRecreationClickListener listener;

	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerCoverRunnable = new Runnable() {

		@Override
		public void run(){
			for(PatternRecreationData prd : data){
				if(prd.isPartOfPattern())
					prd.switchCover();
			}
			results[counter] = new DurationInfo(Calendar.getInstance().getTimeInMillis());
			adapter.clear();
			adapter.update(data);
			canClick = true;
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.pattern_recreation_fragment, container, false);
		grid = (GridView) view.findViewById(R.id.pattern_grid);
		setUpGridData(3, 3);
		setUpGridOther();
		setUpStartButton(view);
		return view;
	}


	@Override
	public void onStop(){
		super.onStop();
		timerHandler.removeCallbacks(timerCoverRunnable);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (PatternRecreationClickListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + 
					" must implement PatternRecreationClickListener");
		}

	}

	private void setUpGridData(int col, int row) {
		coloumns = col;
		rows = row;
		grid.setNumColumns(col);
		data = new PatternRecreationData[col * row];
		for(int i = 0; i < (col * row); i ++){
			data[i] = new PatternRecreationData(false);
		}
	}


	private void setUpGridOther() {
		adapter = new PatternRecreationGridAdapter(data, getActivity());
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listener.gridItemClick(position);
			}

		});
	}

	private void setUpStartButton(View view) {
		start = (Button) view.findViewById(R.id.pattern_start);
		start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
					if(start.isEnabled()){
						start.setEnabled(false);
						tiles = 3;
						canClick = false;
						clickCount = 0;
						errorClickCount = 3;
						turns = 1;
						maxTilesReached = tiles;
						counter = 0;
						results = new DurationInfo[maxTurns];
						setUpGridData(3, 3);
						adapter.clear();
						adapter.update(data);
						playTimes++;
						startGame();
					}
				}else{
					ActivityUtilities.displayResults(getActivity(), eventName,
							"You have completed you daily 3 tries, please try a different test");
				}
			}
		});
	}


	private void startGame() {
		ArrayList<Integer> clickableTiles = getClickableTiles();
		for(int i = 0; i < tiles; i++){
			int j = clickableTiles.get(i);
			data[j].switchPattern();
			data[j].switchCover();;
		}
		updateAdapter();
		timerHandler.postDelayed(timerCoverRunnable, (1000 * tiles / 2));
	}

	private ArrayList<Integer> getClickableTiles(){
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(int i = 0; i < data.length; i++){
			tmp.add(i);
		}
		Collections.shuffle(tmp, new Random());
		return tmp;
	}

	@Override
	public void gridItemClick(int position) {
		if(canClick){
			PatternRecreationData p = data[position];
			if(p.isPartOfPattern() && !p.isUncovered()){
				p.switchCover();
				clickCount ++;
			} else if(!p.isPartOfPattern() && !p.isUncovered()){
				p.errorMade();
				p.switchCover();
				errorClickCount--;
			}		
			data[position] = p;
			updateAdapter();
			if(clickCount == tiles && errorClickCount != 0)
				next();
			else if(errorClickCount == 0 && clickCount != tiles)
				error();
		}
	}

	private void error() {
		canClick = false;
		if(turns == maxTurns){
			endTest();
		} else{
			if(tiles != 3)
				tiles --;
			errorClickCount = 3;
			resetTiles(true);
		}
	}

	private void next() {
		canClick = false;
		if(turns == maxTurns)
			endTest();
		else{
			errorClickCount = 3;
			tiles ++;
			maxTilesReached ++;
			resetTiles(false);
		}
	}


	private void resetTiles(boolean error) {
		turns ++;
		clickCount = 0;
		long time = Calendar.getInstance().getTimeInMillis();
		results[counter].addEndTime(time);
		counter ++;
		if(counter < results.length)
			results[counter] = new DurationInfo(time);
		if(error){
			if(coloumns < rows)
				setUpGridData(coloumns, rows - 1);
			else
				setUpGridData(coloumns - 1, rows);
		} else{
			if (coloumns < rows)
				setUpGridData(coloumns + 1, rows);
			else 
				setUpGridData(coloumns, rows + 1);
		}
		startGame();
	}

	private void endTest() {
		double timeMilli =  getResults();
		String tmp = Conversion.milliToStringSeconds(timeMilli, 3);
		String result = "Max Tiles: " + Integer.toString(maxTilesReached) + ". " + 
				 tmp + " average time (s)";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		Results.insertResult(eventName, Integer.toString(maxTilesReached) + "|" + tmp,
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, result);		
		start.setEnabled(true);
		setUpGridData(3,3);
		updateAdapter();
	}

	private double getResults() {
		double tmp = 0;
		for(DurationInfo d : results){
			tmp += d.getDuration();
		}
		return tmp / results.length;
	}

	private void updateAdapter(){
		adapter.clear();
		adapter.update(data);
	}
}
