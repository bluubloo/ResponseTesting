package uni.apps.responsetesting.results.graph;

import java.util.Arrays;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

public class ResultXYGraph {

	private XYPlot plot;
	
	public ResultXYGraph(XYPlot plot, String yLabel, String xLabel, String eventName){
		this.plot = plot;
		this.plot.setRangeLabel(yLabel);
		this.plot.setDomainLabel(xLabel);
		this.plot.setTitle("Results - " + eventName);
	}
		
	public void setRangeAndDomainSteps(double range, double domain, int ticks){
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, range);
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, domain);
		plot.setTicksPerRangeLabel(ticks);
	}
	
	public void setMaxMinY(double min, double max){
		plot.setRangeBoundaries(min, max, BoundaryMode.FIXED);
	}
	
	public void addSeries(XYSeries data, LineAndPointFormatter formatter){
		plot.addSeries(data, formatter);
	}
	
	public void addSeries(Number[] data, String name, SimpleXYSeries.ArrayFormat format, LineAndPointFormatter formatter){
		XYSeries s = new SimpleXYSeries(Arrays.asList(data), format, name);
		addSeries(s, formatter);
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
