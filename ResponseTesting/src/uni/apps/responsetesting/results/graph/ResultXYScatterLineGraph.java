package uni.apps.responsetesting.results.graph;

import java.util.Arrays;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class ResultXYScatterLineGraph extends ResultXYGraph {

	public ResultXYScatterLineGraph(XYPlot plot, String yLabel, String xLabel,
			String eventName) {
		super(plot, yLabel, xLabel, eventName);
	}
	
	public void addSeries(Number[] data, String name, SimpleXYSeries.ArrayFormat format, LineAndPointFormatter formatter){
		XYSeries s = new SimpleXYSeries(Arrays.asList(data), format, name);
		super.addSeries(s, formatter);
	}
	
	public void addSeries(Number[] data, String name, SimpleXYSeries.ArrayFormat format, int c){
		LineAndPointFormatter formatter = new LineAndPointFormatter(null, c, null, null);
		addSeries(data, name, format, formatter);
	}
	
	public void addSeries(Number[] data, String name, SimpleXYSeries.ArrayFormat format, int c1, int c2){
		LineAndPointFormatter formatter = new LineAndPointFormatter(c1, c2, null, null);
		addSeries(data, name, format, formatter);
	}
}
