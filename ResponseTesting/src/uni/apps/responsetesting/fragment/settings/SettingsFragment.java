package uni.apps.responsetesting.fragment.settings;

import uni.apps.responsetesting.R;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TimePicker;


/**
 * This fragment deals with displaying and updating the applications preferences
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SettingsFragment extends Fragment {

	//variables
	private static final String TAG = "SettingsFragment";
	
	//views
	private CheckBox instruct;
	private CheckBox sleep;
	private CheckBox results;
	private CheckBox remind;
	private RadioButton single;
	private RadioButton multi;
	private TimePicker time;

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
		View view =  inflater.inflate(R.layout.settings_frament, container, false);
		//sets views
		ScrollView scroll = (ScrollView) view.findViewById(R.id.settings_view);
		scroll.requestFocus();
		scroll.scrollBy(0, 0);
		
		setUpInitialValues(view);
		setUpButtons(view);

		return view;
	}

	private void setUpInitialValues(View view) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Resources r = getResources();
		//set check boxes
		instruct = (CheckBox) view.findViewById(R.id.checkbox_instruct);
		instruct.setChecked(prefs.getBoolean(r.getString(R.string.pref_key_instruct), true));
		
		sleep = (CheckBox) view.findViewById(R.id.checkbox_sleep);
		sleep.setChecked(prefs.getBoolean(r.getString(R.string.pref_key_sleep), true));
		
		results = (CheckBox) view.findViewById(R.id.checkbox_results);
		results.setChecked(prefs.getBoolean(r.getString(R.string.pref_key_results), true));
		
		remind = (CheckBox) view.findViewById(R.id.checkbox_remind);
		boolean reminder = prefs.getBoolean(r.getString(R.string.pref_key_remind), false);
		remind.setChecked(reminder);
		remind.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				time.setEnabled(isChecked);
			}
			
		});
		
		//set radiobuttons
		single = (RadioButton) view.findViewById(R.id.user_mode_single);
		multi = (RadioButton) view.findViewById(R.id.user_mode_multi);
		boolean mode = prefs.getBoolean(r.getString(R.string.pref_key_user), false);
		if(mode)
			single.setChecked(true);
		else
			multi.setChecked(true);
	
		//set timepicker
		time = (TimePicker) view.findViewById(R.id.time_alert);
		String defaultTime = prefs.getString(r.getString(R.string.pref_key_alert), r.getString(R.string.settings_time_default));
		time.setIs24HourView(true);
		time.setCurrentHour(getHour(defaultTime));
		time.setCurrentMinute(getMin(defaultTime));
		if(!reminder)
			time.setEnabled(false);
		
		
	}

	//set submit button
	private void setUpButtons(View view) {
		Button submit = (Button) view.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				updateSettings();
			}
			
		});
	}
	
	private void updateSettings(){
		//get variables
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Resources r = getResources();
		Editor edit = prefs.edit();
		//update settings
		edit.putBoolean(r.getString(R.string.pref_key_instruct), instruct.isChecked());
		edit.putBoolean(r.getString(R.string.pref_key_remind), remind.isChecked());
		edit.putBoolean(r.getString(R.string.pref_key_results), results.isChecked());
		edit.putBoolean(r.getString(R.string.pref_key_sleep), sleep.isChecked());
		edit.putBoolean(r.getString(R.string.pref_key_user), single.isChecked());
		edit.putString(r.getString(R.string.pref_key_alert), getTime());
		edit.commit();
		//return to main menu
		getActivity().onBackPressed();
	}
	
	//get minutes
	private int getMin(String s){
		int index = s.indexOf(":");
		if(index != -1)
			return Integer.parseInt(s.substring(index + 1));
		return 0;
	}
	
	//get hours
	private int getHour(String s){
		int index = s.indexOf(":");
		if(index != -1)
			return Integer.parseInt(s.substring(0, index));
		return 9;
	}
	
	//get full time
	private String getTime(){
		return Integer.toString(time.getCurrentHour()) + ":" + Integer.toString(time.getCurrentMinute());
	}
}
