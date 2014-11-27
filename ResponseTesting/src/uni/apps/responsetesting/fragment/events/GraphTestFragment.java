package uni.apps.responsetesting.fragment.events;

import java.util.ArrayList;
import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.graph.GraphUtilities;
import uni.apps.responsetesting.results.graph.ResultXYBarGraph;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		/*ResultXYScatterLineGraph graph = new ResultXYScatterLineGraph(plot, "Score (s)", "Sleep Duration (hr)", "Appearing Object");
		
		double[] ys = new double[] {0.5658, 0.554, 0.7096, 0.7072, 0.5384, 0.6838, 0.6266, 0.728};
		
		double[] series1XS = new double[] {7.45, 9.11, 6.42, 7.48, 7.53, 9.3, 8.5, 6.67};
		double[] series2XS = new double[] {2.98, 3.72, 3.47, 2.88, 3.11, 4.38, 2.72, 3.0};
		double[] series3XS = new double[] {4.47, 5.4, 2.95, 4.6, 4.42, 4.92, 5.78, 3.67};
		
		Number[] series1 = GraphUtilities.interweaveValues(series1XS, ys);
		Number[] series2 = GraphUtilities.interweaveValues(series2XS, ys);
		Number[] series3 = GraphUtilities.interweaveValues(series3XS, ys);
		
		Log.d(TAG, GraphUtilities.seriesArrayToString(series1));
		Log.d(TAG, GraphUtilities.seriesArrayToString(series2));
		Log.d(TAG, GraphUtilities.seriesArrayToString(series3));
				
		XYSeries s1 = new SimpleXYSeries(Arrays.asList(series1),
				SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Total");
				
		LineAndPointFormatter series1Format = new LineAndPointFormatter(null, Color.YELLOW, null, null);
		LineAndPointFormatter series3Format = new LineAndPointFormatter(null, Color.BLUE, null, null);

		graph.addSeries(s1, series1Format);
		graph.addSeries(series2, "Sound", SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED,
				Color.GREEN);
		graph.addSeries(series3, "Light", SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, 
				series3Format);
		
		graph.setRangeAndDomainSteps(0.1, 1, 1, 2);
		graph.setMaxMinY(0.5, 0.8);
		graph.setMaxMinX(2, 10);*/
		
		ResultXYBarGraph graph = new ResultXYBarGraph(plot, "Score (s)", "Sleep Duration (hr)", "Appearing Object");
		long t = Calendar.getInstance().getTimeInMillis();
		long[] times = new long[] {t,t+1,t+2,t+3,t+4,t+5,t+6,t+7};
		double[] s1XS = new double[] {7.45, 9.11, 6.42, 7.48, 7.53, 9.3, 8.5, 6.67};
		double[] s2XS = new double[] {2.98, 3.72, 3.47, 2.88, 3.11, 4.38, 2.72, 3.0};
		
		XYSeries s1 = graph.getXYSeries(GraphUtilities.interweaveValues(times, s1XS), "test1");
		XYSeries s2 = graph.getXYSeries(GraphUtilities.interweaveValues(times, s2XS), "test2");
		
		graph.updatePlot(new XYSeries[] { s1,  s2}, new int[] {Color.BLACK, Color.WHITE}, new int[] { Color.BLUE, Color.GREEN});
		ArrayList<double[]> tmp = new ArrayList<double[]>();
		tmp.add(s1XS);
		tmp.add(s2XS);
		double[] minMax = GraphUtilities.getMaxandMin(tmp);
		graph.setMaxMinY(0, Math.ceil(minMax[1]));
		graph.setRangeAndDomainSteps(1, 1, 2, 2);
		return view;
	}
}
