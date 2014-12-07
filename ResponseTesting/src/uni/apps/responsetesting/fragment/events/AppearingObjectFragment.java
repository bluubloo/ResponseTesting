package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.AppearingObjectImageClickListener;
import uni.apps.responsetesting.models.DurationInfo;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * This fragment performs all actions required for the Appearing Object test
 * 
 * 
 * @author Mathew Andela
 *
 */
public class AppearingObjectFragment extends Fragment implements AppearingObjectImageClickListener{


	//Variables
	private static final String TAG = "AppearingObjectFragment";
	private static String eventName = "Appearing Object";
	private AppearingObjectImageClickListener listener;
	private TextView startTextView;
	private Handler timerHandler = new Handler();
	private ImageView[] clickableImageView = new ImageView[5];
	private DurationInfo[] data = new DurationInfo[5];
	private int counter = 0;
	private int imageCounter = 0;
	private int playTimes = 0;
	private boolean running = false;
	private boolean fixed = false;
	private Random random = new Random();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnableImageAppear = new Runnable(){

		@Override
		public void run() {
			//checks if it is fixed point test
			if(!fixed)
				imageCounter = (random.nextInt(5) % 5);
			else
				imageCounter = 0;
			//shows clickable image
			if(running &&counter < 5){
				clickableImageView[imageCounter].setVisibility(View.VISIBLE);
				data[counter] = new DurationInfo(Calendar.getInstance().getTimeInMillis());
			} else {
				endTest();
			}
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//gets values from bundles
		if(savedInstanceState != null){
			playTimes = savedInstanceState.getInt("playTime");
			fixed = savedInstanceState.getBoolean("fixed");
		}
		setRetainInstance(true);
		Bundle args = this.getArguments();
		if(args != null)
			fixed = args.getBoolean("fixed");
		checkName();
	}
	
	private void checkName(){
		//sets event names
		if(fixed)
			eventName = "Appearing Object - Fixed Point";
		else
			eventName = "Appearing Object";
	}
	
	//gets new instance of fragment
	public static AppearingObjectFragment getInstance(boolean fixed){
		AppearingObjectFragment frag = new AppearingObjectFragment();
		//sets bundle values
		Bundle args = new Bundle();
		args.putBoolean("fixed", fixed);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//sets out values
		outState.putInt("playTime", playTimes);
		outState.putBoolean("fixed", fixed);
	}
	
	//ataches listener to fragment
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (AppearingObjectImageClickListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + 
					" must implement AppearingObjectImageClickListener");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.appearing_object_fragment, container, false);
		//sets up view values and proertioes
		startTextView = (TextView) view.findViewById(R.id.appear_obj_start);
		setUpImageClickEvents(view);
		

		RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.appear_obj_container);
		frame.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//gets timer delay
				int delay = (random.nextInt(3) + 1) * 1000;
				if(!running){
					//check pay times
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
					counter = 0;
					running = !running;
					//makes images invisible
					for(ImageView i: clickableImageView)
						i.setVisibility(View.INVISIBLE);
					startTextView.setVisibility(View.INVISIBLE);
					playTimes++;
					//starts timer
					timerHandler.postDelayed(timerRunnableImageAppear, delay);
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
				}
			}

		});
		return view;
	}


	//image view click events
	private void setUpImageClickEvents(View view) {
		clickableImageView[0] = (ImageView) view.findViewById(R.id.appear_obj_image_1);
		clickableImageView[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[1] = (ImageView) view.findViewById(R.id.appear_obj_image_2);
		clickableImageView[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[2] = (ImageView) view.findViewById(R.id.appear_obj_image_3);
		clickableImageView[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[3] = (ImageView) view.findViewById(R.id.appear_obj_image_4);
		clickableImageView[3].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[4] = (ImageView) view.findViewById(R.id.appear_obj_image_5);
		clickableImageView[4].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
	}

	//ends the test
	private void endTest(){
		if(running){
			//resets variables
			//insert results to db
			//displays results
			counter = 0;
			running = !running;
			clickableImageView[imageCounter].setVisibility(View.INVISIBLE);
			double average = getAverage();
			String avg =  Conversion.milliToStringSeconds(average, 5);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
			Results.insertResult(eventName,avg,
					Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
			startTextView.setText(getResources().getString(R.string.restart_square));
			startTextView.setVisibility(View.VISIBLE);
			ActivityUtilities.displayResults(getActivity(), eventName + " Test", 
					"Average Time to Click Image = " + avg + "s");
		}
	}

	//gets average test time
	private double getAverage() {
		double tmp = 0;
		for(DurationInfo a: data)
			tmp += a.getDuration();
		return tmp / data.length;
	}

	@Override
	public void onClick() {
		//handles click event
		int delay = (random.nextInt(3) + 1) * 1000;
		if (running && counter < 4){
			data[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
			counter ++;
			clickableImageView[imageCounter].setVisibility(View.INVISIBLE);
			timerHandler.postDelayed(timerRunnableImageAppear, delay);
		} else if (running && counter == 4){
			data[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
			counter ++;
			endTest();
		} else{
			endTest();
		}
	}
}
