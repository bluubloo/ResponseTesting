package uni.apps.responsetesting.results.graph;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import android.annotation.SuppressLint;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BarRenderer.BarRenderStyle;
import com.androidplot.xy.BarRenderer.BarWidthStyle;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

/**
 * This class handles bar graphs
 * 
 * 
 * @author Mathew Andela
 *
 */
public class ResultXYBarGraph extends ResultXYGraph {
	
	public ResultXYBarGraph(XYPlot plot, String yLabel, String xLabel,
			String eventName) {
		super(plot, yLabel, xLabel, eventName);
		//format axis labels
		setDomainSeriesFormat();
		setRangeSeriesFormat();
		setDomainLabelsOffset(-45);
		//set padding
		this.plot.setGridPadding(50, 0, 50, 0);
	}
	
	//create bar formatter
	public BarGraphFormatter createFormatter(int fillColour, int borderColour){
		return new BarGraphFormatter(fillColour, borderColour);
	}
	
	//create xyseries
	public XYSeries getXYSeries(Number[] data, String name){
		return new SimpleXYSeries(Arrays.asList(data), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, name);
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setDomainSeriesFormat(){
		//set x axis label format
		plot.setDomainValueFormat(new Format(){
			//variables
			private static final long serialVersionUID = 1L;
			private SimpleDateFormat dateFormat = new SimpleDateFormat("d/M");
			
			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				long timesatmp = ((Number) object).longValue();
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(timesatmp);
				
				return dateFormat.format(c.getTime(), buffer, field);
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}
			
		});
	}
	
	private void setRangeSeriesFormat(){
		//set y axis label format
		plot.setRangeValueFormat(new Format(){

			private static final long serialVersionUID = 1L;

			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				double tmp = round(((Number) object).doubleValue(), 2);
				return new StringBuffer(Double.toString(tmp));
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}
			
		});
	}
	
	//round value to x places
	private double round(double value, int places){
		if(places < 0) throw new IllegalArgumentException();
		//round value
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	//set offset
	private void setDomainLabelsOffset(float off){
		plot.getGraphWidget().setDomainLabelOrientation(off);
	}
	
	//update plot
	public void updatePlot(XYSeries[] series, int[][] colours){
		super.clearGraph();
		for(int i = 0; i < series.length; i++){
			super.addSeries(series[i] , createFormatter(colours[i][0],colours[i][1]));
		}
		//set renderer properties
		BarGraphRenderer renderer = (BarGraphRenderer) plot.getRenderer(BarGraphRenderer.class);
		renderer.setBarRenderStyle(BarRenderStyle.SIDE_BY_SIDE);
		renderer.setBarWidthStyle(BarWidthStyle.FIXED_WIDTH, 30);
		renderer.setBarGap(10);
		plot.redraw();
	}
	
	//update plot
	public void updatePlot(XYSeries[] series, int[] fill, int[] border){
		updatePlot(series, createColourArray(fill, border));
	}
	
	//update plot
	public void updatePlot(XYSeries[] series, int[] colour){
		updatePlot(series, createColourArray(colour, colour));
	}
	
	//create colour array
	public int[][] createColourArray(int[] fill, int[] border){
		int tmp[][] = new int[fill.length][2];
		for(int i = 0; i < fill.length; i++){
			tmp[i][0] = fill[i];
			tmp[i][1] = border[i];
		}
		return tmp;
	}
	
	//formatter class
	class BarGraphFormatter extends BarFormatter {

		public BarGraphFormatter(int fillColour, int borderColor){
			super(fillColour, borderColor);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Class<? extends SeriesRenderer> getRendererClass(){
			return BarGraphRenderer.class;
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public SeriesRenderer getRendererInstance(XYPlot plot){
			return new BarGraphRenderer(plot);
		}
	}
	
	//renderer class
	class BarGraphRenderer extends BarRenderer<BarGraphFormatter>{
		public BarGraphRenderer(XYPlot plot){
			super(plot);
		}
	}


}
