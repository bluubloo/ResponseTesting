package uni.apps.responsetesting.fragment.results;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.results.graph.GraphUtilities;
import uni.apps.responsetesting.results.graph.ResultXYBarGraph;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
		ArrayList<Number[]> series = GraphUtilities.getDurations(cursor);
		XYSeries[] finalSeries = new XYSeries[3]; 
		if(!series.isEmpty()){
			for(int i = 1; i < series.size(); i++){
				Number[] tmp = GraphUtilities.interweaveValues(series.get(0), series.get(i));
				String title = getTitle(i);
				XYSeries s = graph.getXYSeries(tmp, title);
				finalSeries[i - 1] = s;
			}
			Number[] times = series.get(0);
			Log.d(TAG, Integer.toString(times.length));
			series.remove(0);
			Log.d(TAG, Integer.toString(times.length));
			double[] minMax = GraphUtilities.getMaxandMin(series);
			Log.d(TAG, minMax[0] + " " + minMax[1]);
			graph.setEventName("Sleep Durations");
			graph.setMaxMinY(0, Math.ceil(minMax[1]));
			graph.setRangeAndDomainSteps(GraphUtilities.getStep(minMax[0], minMax[1]), 86400000, 10, 1);
			long[] minMaxX = GraphUtilities.getMinandMaxLong(times);
			graph.setMaxMinX(minMaxX[0], minMaxX[1]);
			graph.updatePlot(finalSeries, new int[] {Color.BLACK, Color.WHITE, Color.BLUE});
		}
		else
			graph.clearGraph();

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

	private void setGraphForEvent(String value) {
		Log.d(TAG, value);
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
		Cursor cursor = db.getSingle(value);
		if(cursor.getCount() == 0)
			update();
		else{
			long[] longTimes = GraphUtilities.getLongDates(cursor, 2);

			ArrayList<Number[]> series = GraphUtilities.getScores(cursor);
			XYSeries[] finalSeries = new XYSeries[series.size()];
			for(int i = 0; i < series.size(); i ++){
				Number[] tmp = GraphUtilities.interweaveValues(longTimes, series.get(i));
				XYSeries s = graph.getXYSeries(tmp, "Try " + Integer.toString(i + 1));
				finalSeries[i] = s;
			}

			double[] minMax = GraphUtilities.getMaxandMin(series);
			graph.setMaxMinY(0, Math.ceil(minMax[1]));
			graph.setRangeAndDomainSteps(GraphUtilities.getStep(minMax[0], minMax[1]), 86400000, 10, 1);
			long[] minMaxX = GraphUtilities.getMinandMaxLong(longTimes);
			graph.setMaxMinX(minMaxX[0], minMaxX[1]);
			graph.updatePlot(finalSeries, new int[] {Color.BLACK, Color.WHITE, Color.BLUE});
			graph.setEventName(value);

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
