package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.models.CorrectDurationInfo;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.Conversion;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * This fragment handles the stroop test
 * test - response time, attention to detail
 * 
 * 
 * @author Mathew Andela
 *
 */
public class StroopTest2Fragment extends Fragment {

	//variables
	private static final String TAG = "StroopTest2Fragment";
	private static final String eventName = "Stroop Test";
	private static CorrectDurationInfo[] results;
	private int counter = 0;
	private int maxChanges = 10;
	private int colour = 0;
	private int word1 = 0;
	private int word2 = 0;
	private int playTimes = 0;
	private TextView colourTextView;
	private TextView wordTextView;
	private Button instruct;
	//color names
	private String[] colourNames = new String[] {"Black","Red","Blue","Green", "Yellow","Orange", "Pink"};
	//color values
	private int[] colourValues = new int[] {Color.BLACK,Color.RED,Color.BLUE,Color.GREEN, Color.YELLOW, 
			Color.rgb(255, 153, 51), Color.rgb(255, 153, 255)};


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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.stroop_test2_fragment, container, false);
		//set views
		colourTextView = (TextView) view.findViewById(R.id.stroop_colour);
		wordTextView = (TextView) view.findViewById(R.id.stroop_word);
		setUpButtons(view);
		return view;
	}

	private void setUpButtons(View view) {
		//set buttons
		final Button match = (Button) view.findViewById(R.id.stroop_match);
		final Button noMatch = (Button) view.findViewById(R.id.stroop_no_match);
		final Resources r = getResources();
		match.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//check if startable
				if(match.getText().toString().equals(r.getString(R.string.start))){
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
						//initialise test variables
						match.setText(r.getString(R.string.match));
						noMatch.setText(r.getString(R.string.no_match));
						noMatch.setEnabled(true);
						instruct.setEnabled(false);
						instruct.setVisibility(View.GONE);
						setTextViews();
						results = new CorrectDurationInfo[maxChanges];
						counter = 0;
						playTimes++;
						results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
					//check answers
				} else if(counter < maxChanges - 1){
					setResultsTrue();
					setTextViews();
				} else if(counter == maxChanges - 1){
					setResultsTrue();
					endTest(match, noMatch);
				}
			}

		});

		noMatch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//check answers
				if(counter < maxChanges - 1){
					setResultsFalse();
					setTextViews();
				} else if(counter == maxChanges - 1){
					setResultsFalse();
					endTest(match, noMatch);
				}
			}

		});

		instruct = (Button) view.findViewById(R.id.button_instruct);

		instruct.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ActivityUtilities.eventInfo(eventName, getActivity());
			}

		});
	}

	private void endTest(Button match, Button noMatch) {
		//reset test vairables
		Resources r = getResources();
		match.setText(r.getString(R.string.start));
		noMatch.setText("");
		noMatch.setEnabled(false);
		instruct.setEnabled(true);
		instruct.setVisibility(View.VISIBLE);
		colourTextView.setText("");
		wordTextView.setText("");
		//get results
		double[] tmp = Results.getResults(results);
		String tmp2 = Conversion.milliToStringSeconds(tmp[1], 3);
		String value = tmp[0] + " correct. " + tmp2 + " average time (s)"; 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		//insert and display results
		Results.insertResult(eventName, tmp[0] + "|" + tmp2,
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, value);
	}

	//set results match click
	private void setResultsTrue(){
		results[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
		results[counter].addResult(colour == word2);
		moveToNext();
	}

	//set result no match click
	private void setResultsFalse(){
		results[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
		results[counter].addResult(colour != word2);
		moveToNext();
	}

	//move to next result position
	private void moveToNext(){
		counter ++;
		if(counter < maxChanges)
			results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
	}

	//set views
	private void setTextViews() {
		Random random = new Random();
		colour = random.nextInt(colourNames.length);
		word1 = random.nextInt(colourNames.length);
		word2 = random.nextInt(colourNames.length);
		colourTextView.setText(colourNames[word1]);
		wordTextView.setText(colourNames[word2]);
		colourTextView.setTextColor(colourValues[colour]);
	}

}
