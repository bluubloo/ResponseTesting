package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.QuestionaireListAdapter;
import uni.apps.responsetesting.results.Results;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;

public class QuestionaireFragment extends Fragment {

	private static final String TAG = "QuestionaireFragment";
	private static final String eventName = "Questionaire";
	private QuestionaireListAdapter adapter;
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
		list_view = (ListView) view.findViewById(R.id.questionaire_list);

		final EditText totalSleep = (EditText) view.findViewById(R.id.questionaire_total);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean sleep = prefs.getBoolean(getResources().getString(R.string.pref_key_sleep), false);
		final EditText lightSleep = (EditText) view.findViewById(R.id.questionaire_light);
		final EditText soundSleep = (EditText) view.findViewById(R.id.questionaire_sound);
		final EditText heartRate = (EditText) view.findViewById(R.id.questionaire_hr);
		if(!sleep){
			view.findViewById(R.id.light_con).setVisibility(View.GONE);
			view.findViewById(R.id.sound_con).setVisibility(View.GONE);
		}

		Button submit = (Button) view.findViewById(R.id.questionaire_submit);		
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				ArrayList<String> results = new ArrayList<String>();

				String sleep = totalSleep.getText().toString();
				String light = lightSleep.getText().toString();
				String sound = soundSleep.getText().toString();
				String hr = heartRate.getText().toString();

				if(!checkSleepFormat(sleep, light, sound)){
					new AlertDialog.Builder(getActivity())
					.setTitle("Time Duration Error")
					.setMessage("Please the sleep duration formats. It needs to be in the form of:\n"
							+ "HH:mm or HH.mm")
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) { 
									//do nothing
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert)
							.show();
				}else{

					for(int i = 0; i < adapter.getCount(); i++){
						results.add(adapter.getRating(i));
					/*	View view = list_view.getChildAt(i);
						if(view != null){
							RatingBar r = (RatingBar) view.findViewById(R.id.questionaire_rating);
							String s = Float.toString(r.getRating()).substring(0, 3);
							results.add(s);
						}*/
					}
					
					String finalResult = "";
					for(String s : results)
						finalResult += s + "|";
					Log.d(TAG, finalResult);
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
					Results.insertQuestionaireResult(eventName, new String[] {sleep, light, sound, hr}, 
							finalResult, Calendar.getInstance().getTimeInMillis(), getActivity(), userId);

					new AlertDialog.Builder(getActivity())
					.setTitle("Questionaire Submitted")
					.setMessage("You will now be returned to the Main Menu.")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							getActivity().onBackPressed();
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
				}
			}
		});
		return view;
	}

	private boolean checkSleepFormat(String sleep, String light, String sound) {
		if(checkStringFormat(sleep) && checkStringFormat(light) && checkStringFormat(sound))
			return true;		
		return false;
	}

	private boolean checkStringFormat(String s){
		//check if the input hasn't been changed
		if(s.equals("") || s.equals(getResources().getString(R.string.quest_if_known)))
			return true;
		//remove spaces
		String[] tmpArray = s.split(" ");
		String noSpaces = "";
		for(String s1: tmpArray)
			noSpaces += s1;
		//check for : and .
		if(noSpaces.contains(":") || noSpaces.contains(".")){
			//get substrings
			if(noSpaces.contains(":"))
				tmpArray = noSpaces.split(":");
			else{
				int index = noSpaces.indexOf(".");
				tmpArray = new String[] {noSpaces.substring(0, index),
						noSpaces.substring(index + 1)};
				for(String s2 : tmpArray)
					Log.d(TAG, s2);
			}
				
			//check array size
			if(tmpArray.length != 2)
				return false;
			else{
				//check string size
				for(String s1: tmpArray){
					if(s1.length() < 1 || s1.length() >= 3)
						return false;
					//check if chars are digits
					for(char c: s1.toCharArray()){
						if(!Character.isDigit(c))
							return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		//set up list adapter
		adapter = new QuestionaireListAdapter(getActivity());
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//set list adpater
		list_view.setAdapter(adapter);
		list_view.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

}
