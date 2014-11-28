package uni.apps.responsetesting.fragment.results;

import java.util.ArrayList;
import java.util.Calendar;

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

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
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
		//TODO
		graph.clearGraph();
	}

	private void setGraphForEvent(String value) {
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
			
			//Correct above
			//TODO check below
			double[] minMax = GraphUtilities.getMaxandMin(series);
			Log.d(TAG, minMax[0] + " " + minMax[1]);
			graph.setEventName(value);
			graph.setMaxMinY(0, Math.ceil(minMax[1]));
			graph.setRangeAndDomainSteps(GraphUtilities.getStep(minMax[0], minMax[1]), 86400000, 2, 1);
			long[] minMaxX = GraphUtilities.getMinandMaxLong(longTimes);
			graph.setMaxMinX(minMaxX[0], minMaxX[1]);
			graph.updatePlot(finalSeries, new int[] {Color.BLACK, Color.WHITE, Color.BLUE});
			
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
