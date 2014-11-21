package uni.apps.responsetesting.fragment.events;

import java.util.Arrays;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.graph.ResultXYGraph;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class GraphTestFragment extends Fragment {

	
	private static final String TAG = "GraphTestFragment";
	private XYPlot plot;

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
		View view =  inflater.inflate(R.layout.graph_item, container, false);
		plot = (XYPlot) view.findViewById(R.id.results_plot);
		ResultXYGraph graph = new ResultXYGraph(plot, "Score", "Sleep Duration", "Appearing Object");
		
		Number[] series1 = new Number[] {7.45,0.5658,9.11,0.554,6.42,0.7096,7.48,0.7072,7.53,
				0.5384,9.3,0.6838,8.5,0.6266};
		Number[] series2 = new Number[] {2.98,0.5658,3.72,0.554,3.47,0.7096,2.88,0.7072,3.11,
				0.5384,4.38,0.6838,2.72,0.6266};
		Number[] series3 = new Number[] {4.47,0.5658,5.4,0.554,2.95,0.7096,4.6,0.7072,4.42,
				0.5384,4.92,0.6838,5.78,0.6266};
		
		XYSeries s1 = new SimpleXYSeries(Arrays.asList(series1),
				SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Total");
				
		LineAndPointFormatter series1Format = new LineAndPointFormatter(null, Color.YELLOW, null, null);
		LineAndPointFormatter series3Format = new LineAndPointFormatter(null, Color.BLUE, null, null);

		graph.addSeries(s1, series1Format);
		graph.addSeries(series2, "Sound", SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
				Color.GREEN);
		graph.addSeries(series3, "Light", SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, 
				series3Format);
		graph.setRangeAndDomainSteps(0.1, 1, 2);
		graph.setMaxMinY(0.4, 0.8);
		return view;
	}
}
