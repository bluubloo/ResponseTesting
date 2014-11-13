package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.QuestionaireListAdapter;
import uni.apps.responsetesting.results.Results;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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
		Button submit = (Button) view.findViewById(R.id.questionaire_submit);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				ArrayList<String> results = new ArrayList<String>();
				
				for(int i = 0; i < list_view.getCount(); i++){
					View view = list_view.getChildAt(i);
					TextView t = (TextView) view.findViewById(R.id.questionaire_text);
					RatingBar r = (RatingBar) view.findViewById(R.id.questionaire_rating);
					String s = Integer.toString(i + 1) + "." + t.getText().toString() + ":" +
							Float.toString(r.getRating()).substring(0, 3);
					results.add(s);
				}
				String finalResult = "";
				for(String s : results)
					finalResult += s + " ";
				Results.insertResult(eventName, finalResult, 
						Calendar.getInstance().getTimeInMillis(), getActivity());
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
