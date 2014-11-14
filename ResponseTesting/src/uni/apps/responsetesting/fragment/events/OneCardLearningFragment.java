package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import uni.apps.responsetesting.utils.CardOperations;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class OneCardLearningFragment extends Fragment {

	private static final String TAG = "OneCardLearningFragment";
	private static final String eventName = "One Card Learning Test";
	private int counter = 0;
	private int maxCards = 20;
	private boolean[] results = new boolean[maxCards];
	private boolean running = false;
	private FrameLayout card;
	private Button yesButton;
	private Button noButton;
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
		card = (FrameLayout) view.findViewById(R.id.one_card_container);
		yesButton = (Button) view.findViewById(R.id.one_card_yes);
		noButton = (Button) view.findViewById(R.id.one_card_no);
		setUpClicks();

		return view;
	}

	private void setUpClicks() {
		final Resources r = getResources();
		yesButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(counter == 0 && !running){
					deck = CardOperations.setUpCards(maxCards);
					seen = new ArrayList<Integer>();
					running = !running;
					noButton.setText(r.getString(R.string.no));
					noButton.setEnabled(true);
					yesButton.setText(r.getString(R.string.yes));
					resetResults();
					changeToNextCard();				
				} else if(counter < maxCards - 1){
					counterLess(true);
				} else if(counter == maxCards - 1){
					updateResults(true);
					endTest();
				}
			}

		});

		noButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
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
		updateResults(button);
		counter ++;
		changeToBlankBackground();
		timerHandler.postDelayed(timerRunnable, 500);
	}

	private void updateResults(boolean button){
		results[counter] = CardOperations.checkCard(deck.get(counter), seen, button);
		seen.add(deck.get(counter));
	}

	private void changeToNextCard() {
		card.setBackground(getResources().getDrawable(deck.get(counter)));
	}
	
	private void changeToBlankBackground(){
		card.setBackground(null);
	}

	private void endTest() {
		Resources r = getResources();
		changeToBlankBackground();
		noButton.setText("");
		noButton.setEnabled(false);
		yesButton.setText(r.getString(R.string.start));
		counter = 0;
		running = !running;
		int result = getResults();
		Results.insertResult(eventName, result,
				Calendar.getInstance().getTimeInMillis(), getActivity());
		ActivityUtilities.displayResults(getActivity(), eventName, 
				"You got " + Integer.toString(result) + " correct.");
	}

	private int getResults() {
		int i = 0;
		for(boolean b: results){
			if(b)
				i++;
		}
		return i;
	}

	private void resetResults(){
		results = new boolean[maxCards];
	}



}
