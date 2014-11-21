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
		ResultXYGraph graph = new ResultXYGraph(plot);
		
		Number[] series1 = new Number[] {1,2,3,4,5,6};
		Number[] series2 = new Number[] {6,5,4,3,2,1};
				
		XYSeries s1 = new SimpleXYSeries(Arrays.asList(series1),
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series 1");
		
		//XYSeries s2 = new SimpleXYSeries(Arrays.asList(series2),
		//		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series 2");
				
		LineAndPointFormatter series1Format = new LineAndPointFormatter(null,Color.YELLOW, null, null);
		//LineAndPointFormatter series2Format = new LineAndPointFormatter(null,Color.GREEN,null, null);

		graph.addSeries(s1, series1Format);
		graph.addSeries(series2, "Series 2", SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
				Color.GREEN);
		
		return view;
	}
}
