package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.models.CorrectDurationInfo;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.Conversion;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StroopTest2Fragment extends Fragment {

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
	private String[] colourNames = new String[] {"Black","Red","Blue","Green", "Yellow","Orange", "Pink"};

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
		colourTextView = (TextView) view.findViewById(R.id.stroop_colour);
		wordTextView = (TextView) view.findViewById(R.id.stroop_word);
		setUpButtons(view);
		return view;
	}

	private void setUpButtons(View view) {
		final Button match = (Button) view.findViewById(R.id.stroop_match);
		final Button noMatch = (Button) view.findViewById(R.id.stroop_no_match);
		final Resources r = getResources();
		match.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(match.getText().toString().equals(r.getString(R.string.start))){
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
					match.setText(r.getString(R.string.match));
					noMatch.setText(r.getString(R.string.no_match));
					noMatch.setEnabled(true);
					setTextViews();
					results = new CorrectDurationInfo[maxChanges];
					counter = 0;
					playTimes++;
					results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
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
				if(counter < maxChanges - 1){
					setResultsFalse();
					setTextViews();
				} else if(counter == maxChanges - 1){
					setResultsFalse();
					endTest(match, noMatch);
				}
			}

		});
	}

	private void endTest(Button match, Button noMatch) {
		Resources r = getResources();
		match.setText(r.getString(R.string.start));
		noMatch.setText("");
		noMatch.setEnabled(false);
		colourTextView.setText("");
		wordTextView.setText("");
		double[] tmp = Results.getResults(results);
		String value = tmp[0] + " correct. " + Conversion.milliToStringSeconds(tmp[1], 3) +
				" average time (s)"; 
		Results.insertResult(eventName, value, Calendar.getInstance().getTimeInMillis(),
				getActivity());
		ActivityUtilities.displayResults(getActivity(), eventName, value);
	}


	private void setResultsTrue(){
		results[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
		results[counter].addResult(colour == word2);
		moveToNext();
	}

	private void setResultsFalse(){
		results[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
		results[counter].addResult(colour != word2);
		moveToNext();
	}

	private void moveToNext(){
		counter ++;
		if(counter < maxChanges)
			results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
	}

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
