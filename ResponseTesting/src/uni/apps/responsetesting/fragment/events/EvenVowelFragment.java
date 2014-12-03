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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EvenVowelFragment extends Fragment {

	private static final String TAG = "EvenVowelFragment";
	private static final String eventName = "Even or Vowel";
	private String testText = "";

	private static final int maxTurns = 10;
	private int counter = 0;
	private int playTimes = 0;

	private CorrectDurationInfo[] results;

	private Button yesButton;
	private Button noButton;
	private TextView evenTextView;
	private TextView vowelTextView;

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
		View view =  inflater.inflate(R.layout.even_vowel_fragment, container, false);
		evenTextView = (TextView) view.findViewById(R.id.even_vowel_even);
		vowelTextView = (TextView) view.findViewById(R.id.even_vowel_vowel);
		setUpButtons(view);
		return view;
	}

	private void setUpButtons(View view) {
		yesButton = (Button) view.findViewById(R.id.even_vowel_yes);
		noButton = (Button) view.findViewById(R.id.even_vowel_no);

		yesButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(yesButton.getText().equals(getResources().getString(R.string.start)))
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
						startTest();
						playTimes++;
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
				else
					checkAnswer(true);				
			}
		});

		noButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				checkAnswer(false);
			}
		});
	}

	private void startTest() {
		counter = 0;
		testText = "";
		results = new CorrectDurationInfo[maxTurns];
		results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
		Resources r = getResources();
		yesButton.setText(r.getString(R.string.yes));
		noButton.setText(r.getString(R.string.no));
		noButton.setEnabled(true);
		switchText();
	}

	private void checkAnswer(boolean input) {
		results[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
		if(evenTextView.getVisibility() == View.VISIBLE){
			boolean even = isEven();
			if((input && even) || (!input && !even))
				results[counter].addResult(true);
			else
				results[counter].addResult(false);
		}else if(vowelTextView.getVisibility() == View.VISIBLE){
			boolean vowel = isVowel();
			if((input && vowel) || (!input && !vowel))
				results[counter].addResult(true);
			else
				results[counter].addResult(false);
		}

		if(counter != maxTurns - 1){
			switchText();
			moveToNext();
		} else 
			endTest();

	}

	private void endTest() {
		double[] result = Results.getResults(results);
		String tmp = Conversion.milliToStringSeconds(result[1], 3);
		String resultString = result[0] + " correct. " + tmp + " average time (s).";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		Results.insertResult(eventName, result[0] + "|" + tmp, 
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, resultString);
		Resources r = getResources();
		yesButton.setText(r.getString(R.string.start));
		noButton.setText("");
		noButton.setEnabled(false);
		evenTextView.setText("");
		evenTextView.setVisibility(View.INVISIBLE);
		vowelTextView.setText("");
		vowelTextView.setVisibility(View.INVISIBLE);
	}

	private boolean isVowel() {
		char l = testText.charAt(1);
		char[] tmp = new char[] {'a','e','i','o','u'};
		for(char c: tmp)
			if(c == l)
				return true;
		return false;
	}

	private boolean isEven() {
		char l = testText.charAt(0);
		int n = Character.getNumericValue(l);
		if(n % 2 == 0)
			return true;
		return false;
	}

	private void switchText() {
		Random random = new Random();
		int number = random.nextInt(9) + 1;
		int letter = random.nextInt(90 - 65) + 65;
		char l = Character.toChars(letter)[0];
		testText = Integer.toString(number) + l;
		int box = random.nextInt(11);
		if(box < 5){
			evenTextView.setText(testText);
			evenTextView.setVisibility(View.VISIBLE);
			vowelTextView.setText("");
			vowelTextView.setVisibility(View.INVISIBLE);
		} else {
			vowelTextView.setText(testText);
			vowelTextView.setVisibility(View.VISIBLE);
			evenTextView.setText("");
			evenTextView.setVisibility(View.INVISIBLE);
		}
	}

	private void moveToNext(){
		counter ++;
		if(counter < results.length)
			results[counter] = new CorrectDurationInfo(Calendar.getInstance().getTimeInMillis());
	}
}
