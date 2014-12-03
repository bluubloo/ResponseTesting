package uni.apps.responsetesting.fragment.results;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.results.graph.GraphUtilities;
import uni.apps.responsetesting.results.graph.ResultXYBarGraph;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class ResultsFragment extends Fragment implements OnItemSelectedListener{

	private static final String TAG = "ResultsFragment";
	private ResultXYBarGraph graph;

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
		View view =  inflater.inflate(R.layout.results_display_fragment2, container, false);
		XYPlot plot = (XYPlot) view.findViewById(R.id.results_plot);
		graph = new ResultXYBarGraph(plot, "Score", "Date", "");
		setUpSpinner(view);
		return view;
	}

	private void setUpSpinner(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.result_dis_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.event_name_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String value = (String) parent.getItemAtPosition(position);
		if(value.equals(getResources().getString(R.string.event_name_questionaire))){
			setGraphForQuestionaire();
		}else{
			setGraphForEvent(value);
		}
	}

	private void setGraphForQuestionaire() {
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
		Cursor cursor = db.getAllQuestionaireResults();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		ArrayList<Number[]> series = GraphUtilities.getDurations(cursor, userId);
		XYSeries[] finalSeries = new XYSeries[3]; 
		if(!series.isEmpty()){
			for(int i = 1; i < series.size(); i++){
				Number[] tmp = GraphUtilities.interweaveValues(series.get(0), series.get(i));
				String title = getTitle(i);
				XYSeries s = graph.getXYSeries(tmp, title);
				finalSeries[i - 1] = s;
			}
			
			long[] minMaxX = GraphUtilities.getMinandMaxLong(series.get(0));
			series.remove(0);
			double[] minMaxY = GraphUtilities.getMaxandMin(series);			
			setCommonGraphValues("Sleep Duration", minMaxX, minMaxY, finalSeries);
		}
		else
			update();

	}

	private void setGraphForEvent(String value) {
		Log.d(TAG, value);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		Log.d(TAG, userId);
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
		Cursor cursor = db.getSingle(value, userId);
		//Cursor cursor = db.getSingle(value);
		Log.d(TAG, Integer.toString(cursor.getCount()));
		if(cursor.getCount() == 0)
			update();
		else{
			long[] longTimes = GraphUtilities.getLongDates(cursor, 2, userId);

			ArrayList<Number[]> series = GraphUtilities.getScores(cursor, userId);
			XYSeries[] finalSeries = new XYSeries[series.size()];
			for(int i = 0; i < series.size(); i ++){
				Number[] tmp = GraphUtilities.interweaveValues(longTimes, series.get(i));
				XYSeries s = graph.getXYSeries(tmp, "Try " + Integer.toString(i + 1));
				finalSeries[i] = s;
			}

			double[] minMaxY = GraphUtilities.getMaxandMin(series);
			long[] minMaxX = GraphUtilities.getMinandMaxLong(longTimes);
			setCommonGraphValues(value, minMaxX, minMaxY, finalSeries);

		}		
	}
	
	private void setCommonGraphValues(String eventName, long[] minMaxX, 
			double[] minMaxY, XYSeries[] finalSeries){
		graph.updatePlot(finalSeries, new int[] {Color.BLACK, Color.WHITE, Color.BLUE});
		graph.setEventName(eventName);
		graph.setMaxMinX(minMaxX[0], minMaxX[1]);
		graph.setMaxMinY(GraphUtilities.roundDown(minMaxY[0]), GraphUtilities.roundUp(minMaxY[1]));
		//graph.setRangeAndDomainSteps(GraphUtilities.getStep(minMaxY[0], minMaxY[1]), 86400000, 2, 1);
		graph.setDomainSteps(86400000, 1);
	}
	
	private String getTitle(int i) {
		switch(i){
		case 1:
			return "Total";
		case 2: 
			return "Light";
		case 3:
			return "Sound";
		default:
			return "Other";
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		update();
	}

	public void update(){
		graph.clearGraph();
	}
}
