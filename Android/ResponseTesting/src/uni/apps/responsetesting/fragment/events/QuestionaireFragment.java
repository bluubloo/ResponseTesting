package uni.apps.responsetesting.fragment.events;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.QuestionaireListAdapter;
import uni.apps.responsetesting.interfaces.listener.EventInstructionsListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

/**
 * This fragment handles the questionnaire
 * 
 * 
 * @author Mathew Andela
 *
 */
public class QuestionaireFragment extends Fragment {

	//variables
	private static final String TAG = "QuestionaireFragment";
	private QuestionaireListAdapter adapter;
	private EventInstructionsListener listener;
	private ListView list_view;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.questionaire_fragment, container, false);
		//get list view
		//list_view = (ListView) view.findViewById(R.id.questionaire_list);
		//seListViewProperties();
		//set views
		final TimePicker totalSleep = (TimePicker) view.findViewById(R.id.questionaire_total);
		totalSleep.setIs24HourView(true);
		totalSleep.setCurrentHour(0);
		totalSleep.setCurrentMinute(0);		
		final TimePicker lightSleep = (TimePicker) view.findViewById(R.id.questionaire_light);
		lightSleep.setIs24HourView(true);
		lightSleep.setCurrentHour(0);
		lightSleep.setCurrentMinute(0);
		final TimePicker soundSleep = (TimePicker) view.findViewById(R.id.questionaire_sound);
		soundSleep.setIs24HourView(true);
		soundSleep.setCurrentHour(0);
		soundSleep.setCurrentMinute(0);
		
		final EditText heartRate = (EditText) view.findViewById(R.id.questionaire_hr);
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean sleep = prefs.getBoolean(getResources().getString(R.string.pref_key_sleep), false);
		if(!sleep){
			view.findViewById(R.id.sleep_con).setVisibility(View.GONE);
		}
		String theme = prefs.getString(getResources().getString(R.string.pref_key_theme), 
				getResources().getString(R.string.settings_theme_default));
		
		if(!theme.equals("Magic")){
			view.findViewById(R.id.questionaire_hr_container).setVisibility(View.GONE);
		}

		//set button click
		Button submit = (Button) view.findViewById(R.id.questionaire_submit);		
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				//get values
				String sleep = totalSleep.getCurrentHour() + ":" + totalSleep.getCurrentMinute();
				String light = "";
				String sound = "";
				if(lightSleep.getCurrentHour() != 0 || lightSleep.getCurrentMinute() != 0)
					light = lightSleep.getCurrentHour() + ":" + lightSleep.getCurrentMinute();
				if(soundSleep.getCurrentHour() != 0 || soundSleep.getCurrentMinute() != 0)
					sound = soundSleep.getCurrentHour() + ":" + soundSleep.getCurrentMinute();
				String hr = heartRate.getText().toString();
				
				listener.onNextClick(sleep, light, sound, hr);
					
			}
		});
		return view;
	}
	
	//attachs listener to activity
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (EventInstructionsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement EventInstructionsListener");
		}

	}

	@SuppressLint("ClickableViewAccessibility")
	private void seListViewProperties() {
		// TODO Auto-generated method stub
		adapter = new QuestionaireListAdapter(getActivity());
		list_view.setAdapter(adapter);
		list_view.setChoiceMode(ListView.CHOICE_MODE_NONE);
		list_view.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
			    return false;
			}
			
		});
		
		ViewGroup.LayoutParams params = list_view.getLayoutParams();
	    params.height = 1000 + (list_view.getDividerHeight() * (adapter.getCount() - 1));
	    list_view.setLayoutParams(params);
	    list_view.requestLayout();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		//set up list adapter
		
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();		
	}

}
