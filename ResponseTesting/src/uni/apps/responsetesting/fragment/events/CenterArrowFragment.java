package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.CenterArrowGridAdapter;
import uni.apps.responsetesting.interfaces.listener.CenterArrowListener;
import uni.apps.responsetesting.models.CenterArrowInfo;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * This fragment handles the arrow ignoring test
 * Tests - attention & response time
 * 
 * @author Mathew Andela
 *
 */
public class CenterArrowFragment extends Fragment implements CenterArrowListener {

	//constants
	private static final String TAG = "CenterArrowFragment";
	private static final String eventName = "Arrow Ignoring Test";
	private static final int maxTurns = 10;
	private static final int waitTime = 250;

	//variables
	private CenterArrowListener listener;
	private CenterArrowGridAdapter adapter;

	private ImageView[] buttons;
	private GridView grid;
	private Button start;
	private Button instruct;
	private LinearLayout container;

	private boolean running = false;
	private boolean clickable = false;
	private int counter = 0;
	private int playTimes = 0;

	private CorrectDurationInfo[] results;

	private ArrayList<CenterArrowInfo> arrows;
	private CenterArrowInfo center;

	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable(){

		@Override
		public void run() {
			//makes grid visible and clickable
			grid.setVisibility(View.VISIBLE);
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
		setUpArrowInfo();
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("playTime", playTimes);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (CenterArrowListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + 
					" must implement CenterArrowListener");
		}

	}

	@Override
	public void onStop(){
		super.onStop();
		timerHandler.removeCallbacks(timerRunnable);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.center_arrow_fragment, container, false);
		//sets view variables
		grid = (GridView) view.findViewById(R.id.center_arrow_grid);
		this.container = (LinearLayout) view.findViewById(R.id.clickable_container);
		//creates and sets adapter
		adapter = new CenterArrowGridAdapter(getInitalData(), getActivity());
		grid.setAdapter(adapter);
		//sets up click events
		setUpContainerClick(view);
		setUpButtons(view);
		return view;
	}

	//gets initial data
	private int[] getInitalData(){
		int[] tmp = new int[25];
		for(int i = 0; i < tmp.length; i++)
			tmp[i] = -1;
		return tmp;
	}

	private void setUpContainerClick(View view) {
		//sets up container click event
		start = (Button) view.findViewById(R.id.button_start);
		instruct = (Button) view.findViewById(R.id.button_instruct);

		instruct.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ActivityUtilities.eventInfo(eventName, getActivity());
			}

		});
		//RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.center_arrow_container);
		start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.containerClick();
			}

		});
	}

	private void setUpButtons(View view) {
		//sets up imageviews
		buttons = new ImageView[4];
		buttons[0] = (ImageView) view.findViewById(R.id.center_arrow_button_down);
		buttons[0].setTag("down");
		buttons[1] = (ImageView) view.findViewById(R.id.center_arrow_button_up);
		buttons[1].setTag("up");
		buttons[2] = (ImageView) view.findViewById(R.id.center_arrow_button_left);
		buttons[2].setTag("left");
		buttons[3] = (ImageView) view.findViewById(R.id.center_arrow_button_right);
		buttons[3].setTag("right");

		//sets up imageview click events
		for(final ImageView b: buttons){
			b.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String s = (String) b.getTag();
					listener.buttonClick(s);
				}

			});
		}
	}

	@Override
	public void buttonClick(String direction) {
		//on imageview click
		//check answer
		//alter arrows
		//run timer
		if(running && clickable){
			clickable = false;
			if(counter < maxTurns - 1){
				checkAnswer(direction);
				grid.setVisibility(View.INVISIBLE);
				alterArrowSetup();
				timerHandler.postDelayed(timerRunnable, waitTime);
			} else{
				checkAnswer(direction);
				endTest();
			}
		}
	}

	@Override
	public void containerClick() {
		//on container click
		//initailises test vairables
		if(!running){
			if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
				start.setEnabled(false);
				instruct.setEnabled(false);
				container.setVisibility(View.VISIBLE);
				running = true;
				grid.setVisibility(View.VISIBLE);
				counter = 0;
				clickable = true;
				results = new CorrectDurationInfo[maxTurns];
				results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
				center = null;
				playTimes ++;
				alterArrowSetup();
			} else{
				ActivityUtilities.displayResults(getActivity(), eventName,
						"You have completed you daily 3 tries, please try a different test");
			}
		}
	}

	private void alterArrowSetup() {
		//shuffles arrow icons
		Collections.shuffle(arrows, new Random());
		Log.d(TAG, Integer.toString(arrows.size()));
		//get new arrows
		center = arrows.get(0);
		ArrayList<Integer> positions = new ArrayList<Integer>();
		positions.add(12);
		Random random = new Random();
		for(int i = 1; i < 5; i++){
			int tmp = random.nextInt(25);
			while(positions.contains(tmp)){
				tmp = random.nextInt(25);
			}
			positions.add(tmp);
		}
		int[] data = new int[25];
		int j = 1;
		for(int i = 0; i < data.length; i++){
			if(i == 12)
				data[12] = arrows.get(0).getId();
			else if(positions.contains(i)){
				data[i] = arrows.get(j).getId();
				j++;
			}
			else
				data[i] = -1;
		}
		//update adapter
		adapter.update(data);
	}

	//initail arrow drawable values
	private void setUpArrowInfo() {
		arrows = new ArrayList<CenterArrowInfo>();
		//NOTE: keep below arrows in the order down, up, left, right
		int[] arrowDrawables = new int[] {R.drawable.blue_arrow_down,
				R.drawable.blue_arrow_up, R.drawable.blue_arrow_left, R.drawable.blue_arrow_right,
				R.drawable.green_arrow_down, R.drawable.green_arrow_up, R.drawable.green_arrow_left,
				R.drawable.green_arrow_right, R.drawable.orange_arrow_down, R.drawable.orange_arrow_up,
				R.drawable.orange_arrow_left, R.drawable.orange_arrow_right	};	
		for(int i = 0; i < arrowDrawables.length; i += 4){
			arrows.add(new CenterArrowInfo(arrowDrawables[i], "down"));
			arrows.add(new CenterArrowInfo(arrowDrawables[i + 1], "up"));
			arrows.add(new CenterArrowInfo(arrowDrawables[i + 2], "left"));
			arrows.add(new CenterArrowInfo(arrowDrawables[i + 3], "right"));
		}
	}


	//checks answer
	private void checkAnswer(String direction) {
		boolean correct = center.checkCorrect(direction);
		long time = Calendar.getInstance().getTimeInMillis();
		results[counter].addEndTime(time);
		results[counter].addResult(correct);
		moveToNext(time + waitTime);
	}

	//move to next result position
	private void moveToNext(long l) {
		counter ++;
		if(counter < maxTurns)
			results[counter] = new CorrectDurationInfo(l);
	}

	//ends test
	private void endTest() {
		//resets variables
		start.setEnabled(true);
		instruct.setEnabled(true);
		container.setVisibility(View.GONE);
		timerHandler.removeCallbacks(timerRunnable);
		running = false;
		grid.setVisibility(View.INVISIBLE);
		//gets results
		double[] result = Results.getResults(results);
		String tmp = Conversion.milliToStringSeconds(result[1], 3);
		String resultString = result[0] + " correct. " + 
				tmp + " average time (s)"; 
		//gets user id
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		//insert results to db and displays
		Results.insertResult(eventName, result[0] + "|" + tmp,
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, resultString);
	}
}
