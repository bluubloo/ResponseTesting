package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.StroopTestGridAdapter;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.Conversion;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

/**
 * This fragment performs the Stroop test
 * 
 * 
 * @author Mathew Andela
 *
 */
public class StroopTestFragment extends Fragment {

	private static final String TAG = "StroopTestFragment";
	private static final String eventName = "Stroop Test";
	private StroopTestGridAdapter adapter;
	private GridView grid_view;
	private long startTime1 = 0;
	private long endTime1 = 0;
	private long startTime2 = 0;
	private int run = 1;
	private boolean firstTime = true;
	
	
	//NOTE: The below two arrays colour must correspond e.g. "Red" -> Color.RED
	private String[] colourNames1 = new String[] {"Black","Red","Blue","Green", "Yellow", "Red", "Orange", "Black", "Black",
			"Blue", "Pink", "Green", "Yellow", "Orange", "Pink", "Red", "Yellow", "Blue" };
	
	private int[] colourValues1 = new int[] {Color.BLACK,Color.RED,Color.BLUE,Color.GREEN, Color.YELLOW, Color.RED, 
			Color.rgb(255, 153, 51),Color.BLACK,Color.BLACK, Color.BLUE, Color.rgb(255, 153, 255), Color.GREEN, Color.YELLOW,
			Color.rgb(255, 153, 51), Color.rgb(255, 153, 255), Color.RED, Color.YELLOW, Color.BLUE};
	

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
		final Resources r = getResources();
		View view =  inflater.inflate(R.layout.stroop_test_fragment, container, false);
		grid_view = (GridView) view.findViewById(R.id.stroop_grid);
		final Button button = (Button) view.findViewById(R.id.stroop_button);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String text = button.getText().toString();
				if(text.equals(r.getString(R.string.start))){
					button.setText(r.getString(R.string.done));
					startTime1 = Calendar.getInstance().getTimeInMillis();
					run = 1;
					if(firstTime){
						grid_view.setAdapter(adapter);
						firstTime = !firstTime;
					} else {
						grid_view.setVisibility(View.VISIBLE);
					}					
				} else if(text.equals(r.getString(R.string.done))){
					if(run == 1){
						endTime1 = Calendar.getInstance().getTimeInMillis();
						startTime2 = Calendar.getInstance().getTimeInMillis();
						setUpGrid();
						run = 2;
					} else{
						long endTime2 = Calendar.getInstance().getTimeInMillis();
						setUpGrid();
						button.setText(r.getString(R.string.start));
						grid_view.setVisibility(View.INVISIBLE);
						endTest(endTime2);
					}
				}
			}

		});
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		//set up list adapter
		adapter = new StroopTestGridAdapter(colourNames1, colourValues1, getActivity());
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//set list adpater
		grid_view.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}
	
	private void setUpGrid() {
		adapter.clear();
		if(run == 1){
			adapter.update(shuffleNames(), shuffleValues());
		} else {
			adapter.update(colourNames1, colourValues1);
		}
	}
	
	private String[] shuffleNames(){
		ArrayList<String> tmpValues = new ArrayList<String>();
		for(String n: colourNames1)
			tmpValues.add(n);
		Collections.shuffle(tmpValues, new Random());
		String[] tmpArray = new String[tmpValues.size()];
		int i = 0;
		for(String n: tmpValues){
			tmpArray[i] = n;
			i++;
		}
		return tmpArray;
	}
	
	private int[] shuffleValues(){
		ArrayList<Integer> tmpValues = new ArrayList<Integer>();
		for(int n: colourValues1)
			tmpValues.add(n);
		Collections.shuffle(tmpValues, new Random());
		int[] tmpArray = new int[tmpValues.size()];
		int i = 0;
		for(int n: tmpValues){
			tmpArray[i] = n;
			i++;
		}
		return tmpArray;
	}
	
	private void endTest(long endTime2) {
		long duration1 = endTime1 - startTime1;
		long duration2 = endTime2 - startTime2;
		long difference = duration2 - duration1;
		String timeDiff = Conversion.milliToStringSeconds(difference, 5);
		String timeDur1 = Conversion.milliToStringSeconds(duration1, 3);
		String timeDur2 = Conversion.milliToStringSeconds(duration2, 3);
		Results.insertResult(eventName, timeDiff,
				Calendar.getInstance().getTimeInMillis(), getActivity());
		new AlertDialog.Builder(getActivity())
		.setTitle("Stroop Test Complete")
		.setMessage("First Run: " + timeDur1 + "s\nSecond Run: " + timeDur2 + 
				"s\nRun Difference: " + timeDiff + "s")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 

					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
		
		
	}
}
