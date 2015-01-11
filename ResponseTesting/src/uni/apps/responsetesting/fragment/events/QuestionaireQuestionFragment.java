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
import android.widget.ListView;

public class QuestionaireQuestionFragment extends Fragment {

	
	private static final String TAG = "QuestionaireQuestionFragment";
	private static final String eventName = "Questionaire";
	
	private QuestionaireListAdapter adapter;
	private ListView list_view;
	
	private String total = "0.0";
	private String light = "0.0";
	private String sound = "0.0";
	private String hr = "0";
	
	
	public static QuestionaireQuestionFragment getInstance(String total, String light, String sound, String hr){
		QuestionaireQuestionFragment frag = new QuestionaireQuestionFragment();
		Bundle args = new Bundle();
		args.putString("total", total);
		args.putString("light", light);
		args.putString("sound", sound);
		args.putString("hr", hr);
		frag.setArguments(args);
		return frag;		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle args = getArguments();
		if(args != null){
			total = args.getString("total");
			light = args.getString("light");
			sound = args.getString("sound");
			hr = args.getString("hr");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.questionaire_fragment_questions, container, false);
		//get list view
		list_view = (ListView) view.findViewById(R.id.questionaire_list);
		adapter = new QuestionaireListAdapter(getActivity());
		list_view.setAdapter(adapter);
		list_view.setChoiceMode(ListView.CHOICE_MODE_NONE);
		
		Button submit = (Button) view.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ArrayList<String> results = new ArrayList<String>();
				for(int i = 0; i < adapter.getCount(); i++){
					results.add(adapter.getRating(i));
				}
				String finalResult = "";
				for(String s : results)
					finalResult += s + "|";
				Log.d(TAG, finalResult);
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
				
				Results.insertQuestionaireResult(eventName, new String[] {total, light, sound, hr}, 
						finalResult, Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
				
				//return to main menu
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
			
		});
		
		
		return view;
	}
}
