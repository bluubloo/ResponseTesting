package uni.apps.responsetesting.results.graph;

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

public class ResultXYBarGraph extends ResultXYGraph {
	
	public ResultXYBarGraph(XYPlot plot, String yLabel, String xLabel,
			String eventName) {
		super(plot, yLabel, xLabel, eventName);
		setDomainSeriesFormat();
		setDomainLabelsOffset(-45);
		this.plot.setGridPadding(50, 0, 50, 0);
	}
	
	public BarGraphFormatter createFormatter(int fillColour, int borderColour){
		return new BarGraphFormatter(fillColour, borderColour);
	}
	
	public XYSeries getXYSeries(Number[] data, String name){
		return new SimpleXYSeries(Arrays.asList(data), SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, name);
	}
	
	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("serial")
	private void setDomainSeriesFormat(){
		plot.setDomainValueFormat(new Format(){

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
	
	private void setDomainLabelsOffset(float off){
		plot.getGraphWidget().setDomainLabelOrientation(off);
	}
	
	public void updatePlot(XYSeries[] series, int[][] colours){
		super.clearGraph();
		for(int i = 0; i < series.length; i++){
			super.addSeries(series[i] , createFormatter(colours[i][0],colours[i][1]));
		}
		BarGraphRenderer renderer = (BarGraphRenderer) plot.getRenderer(BarGraphRenderer.class);
		renderer.setBarRenderStyle(BarRenderStyle.SIDE_BY_SIDE);
		renderer.setBarWidthStyle(BarWidthStyle.FIXED_WIDTH, 30);
		renderer.setBarGap(10);
		plot.redraw();
	}
	
	public void updatePlot(XYSeries[] series, int[] fill, int[] border){
		updatePlot(series, createColourArray(fill, border));
	}
	
	public void updatePlot(XYSeries[] series, int[] colour){
		updatePlot(series, createColourArray(colour, colour));
	}
	
	public int[][] createColourArray(int[] fill, int[] border){
		int tmp[][] = new int[fill.length][2];
		for(int i = 0; i < fill.length; i++){
			tmp[i][0] = fill[i];
			tmp[i][1] = border[i];
		}
		return tmp;
	}
	
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
	
	class BarGraphRenderer extends BarRenderer<BarGraphFormatter>{
		public BarGraphRenderer(XYPlot plot){
			super(plot);
		}
	}


}
