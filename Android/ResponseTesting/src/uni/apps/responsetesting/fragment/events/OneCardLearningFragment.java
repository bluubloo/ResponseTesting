package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.CardOperations;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * This fragment handles the one card learning test
 * tests - attention to detail, memory & visual learning
 * 
 * 
 * @author Mathew Andela
 *
 */
public class OneCardLearningFragment extends Fragment {

	//variables
	private static final String TAG = "OneCardLearningFragment";
	private static final String eventName = "One Card Learning Test";
	private int counter = 0;
	private int maxCards = 20;
	private int playTimes = 0;
	private boolean[] results = new boolean[maxCards];
	private boolean running = false;
	private FrameLayout card;
	private Button yesButton;
	private Button noButton;
	private Button instruct;
	private ArrayList<Integer> seen;
	private ArrayList<Integer> deck;
	private Handler timerHandler = new Handler();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnable = new Runnable() {

		@Override
		public void run(){
			changeToNextCard();
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null)
			playTimes = savedInstanceState.getInt("playTime");
		setRetainInstance(true);
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
		View view =  inflater.inflate(R.layout.one_card_learning_fragment, container, false);
		//set button views
		card = (FrameLayout) view.findViewById(R.id.one_card_container);
		yesButton = (Button) view.findViewById(R.id.one_card_yes);
		noButton = (Button) view.findViewById(R.id.one_card_no);

		instruct = (Button) view.findViewById(R.id.button_instruct);

		instruct.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ActivityUtilities.eventInfo(eventName, getActivity());
			}

		});
		setUpClicks();

		return view;
	}

	private void setUpClicks() {
		final Resources r = getResources();
		//sets match button
		yesButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//checks for startable
				if(counter == 0 && !running){
					if(ActivityUtilities.checkPlayable(eventName, playTimes, getActivity())){
						//initailises test variables
						deck = CardOperations.setUpCards(maxCards);
						seen = new ArrayList<Integer>();
						running = !running;
						playTimes++;
						noButton.setText(r.getString(R.string.no));
						noButton.setEnabled(true);
						instruct.setEnabled(false);
						instruct.setVisibility(View.GONE);
						yesButton.setText(r.getString(R.string.yes));
						resetResults();
						changeToNextCard();			
					} else{
						ActivityUtilities.displayResults(getActivity(), eventName,
								"You have completed you daily 3 tries, please try a different test");
					}
					//checks answers
				} else if(counter < maxCards - 1){
					counterLess(true);
				} else if(counter == maxCards - 1){
					updateResults(true);
					endTest();
				}
			}

		});

		//set no match button
		noButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//checks answers
				if(counter < maxCards - 1){
					counterLess(false);
				} else if(counter == maxCards - 1){
					updateResults(false);
					endTest();
				}
			}

		});
	}

	private void counterLess(boolean button){
		//updates results gets rid of card
		updateResults(button);
		counter ++;
		changeToBlankBackground();
		timerHandler.postDelayed(timerRunnable, 500);
	}


	private void updateResults(boolean button){
		//gets result value
		results[counter] = CardOperations.checkCard(deck.get(counter), seen, button);
		//add card to seen deck
		seen.add(deck.get(counter));
	}

	//changes card
	private void changeToNextCard() {
		card.setBackground(getResources().getDrawable(deck.get(counter)));
	}

	//removes card
	private void changeToBlankBackground(){
		card.setBackground(null);
	}

	//ends the test
	private void endTest() {
		//reset varaibles
		Resources r = getResources();
		changeToBlankBackground();
		noButton.setText("");
		noButton.setEnabled(false);
		yesButton.setText(r.getString(R.string.start));
		instruct.setEnabled(true);
		instruct.setVisibility(View.VISIBLE);
		counter = 0;
		running = !running;
		//gets results
		int result = getResults();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		//inserts and displays results
		Results.insertResult(eventName, result,
				Calendar.getInstance().getTimeInMillis(), getActivity(), userId);
		ActivityUtilities.displayResults(getActivity(), eventName, 
				"You got " + Integer.toString(result) + " correct.");
	}

	//get correct count
	private int getResults() {
		int i = 0;
		for(boolean b: results){
			if(b)
				i++;
		}
		return i;
	}

	//result results
	private void resetResults(){
		results = new boolean[maxCards];
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt("playTime", playTimes);
	}
}
