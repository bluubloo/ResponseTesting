package uni.apps.responsetesting.results.graph;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYLegendWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

public class ResultXYGraph {

	private XYPlot plot;
	private ArrayList<XYSeries> series;
	
	public ResultXYGraph(XYPlot plot, String yLabel, String xLabel, String eventName){
		this.plot = plot;
		this.plot.setRangeLabel(yLabel);
		this.plot.setDomainLabel(xLabel);
		this.plot.setTitle("Results - " + eventName);
		series = new ArrayList<XYSeries>();
		alterLegend();
		alterDomainLabel();
	}
	
	private void alterLegend(){
		XYLegendWidget legend = plot.getLegendWidget();
		legend.setTableModel(new DynamicTableModel(2,2));
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.FILL);
		p.setAlpha(255);
		legend.setMargins(10, 0, 0, 20);
		legend.setHeight(PixelUtils.dpToPix(40), SizeLayoutType.ABSOLUTE);
	}
	
	private void alterDomainLabel(){
		TextLabelWidget domain = plot.getDomainLabelWidget();
		domain.position(0, XLayoutStyle.ABSOLUTE_FROM_LEFT,
				300, YLayoutStyle.ABSOLUTE_FROM_BOTTOM);
		domain.setSize(new SizeMetrics(400, SizeLayoutType.ABSOLUTE, 
				400, SizeLayoutType.ABSOLUTE));
		domain.getLabelPaint().setTextScaleX(1);
		domain.getLabelPaint().setTextSize(30);
	}
		
	public void setRangeAndDomainSteps(double range, double domain, int rTicks, int dTicks){
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, range);
		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, domain);
		plot.setTicksPerRangeLabel(rTicks);
		plot.setTicksPerDomainLabel(dTicks);
	}
	
	public void setMaxMinY(double min, double max){
		plot.setRangeBoundaries(min, max, BoundaryMode.FIXED);
	}
	
	public void setMaxMinX(double min, double max){
		plot.setDomainBoundaries(min, max, BoundaryMode.FIXED);
	}
	
	public void addSeries(XYSeries data, LineAndPointFormatter formatter){
		series.add(data);
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

	public void clearGraph(){
		for(XYSeries s: series)
			plot.removeSeries(s);
		plot.redraw();
	}
}
