package uni.apps.responsetesting.adapter;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.results.graph.GraphUtilities;
import uni.apps.responsetesting.results.graph.ResultXYBarGraph;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class ResultsListAdapter extends BaseAdapter {

	private static final String TAG = "ResultsListAdapter";
	private String[] titles;
	private Cursor[] contents;
	private String questionaire = "";
	private Cursor questionCursor;
	private Activity activity;
	private static LayoutInflater inflater = null;
	
	public ResultsListAdapter(Activity activity){
		Resources r = activity.getResources();
		titles = r.getStringArray(R.array.event_name_array);
		questionaire = r.getString(R.string.event_name_questionaire);
		this.activity = activity;
		removeQuestionaire();
		updateCursors();
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		return contents[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.graph_item, null);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.graph = new ResultXYBarGraph((XYPlot) convertView.findViewById(R.id.results_plot),
				"Score", "Date", titles[position]);
		holder.graph.clearGraph();
		Cursor cursor = contents[position];
		//TODO below
		ArrayList<Number[]> series = GraphUtilities.getScores(cursor);
		long[] longTimes = GraphUtilities.getLongDates(cursor, 2);
		if(series == null){
			Log.d(TAG, "Is NULL");
		}
		XYSeries[] seriesList = new XYSeries[series.size()];
		for(int i = 0; i < series.size(); i++){
			
			XYSeries s = holder.graph.getXYSeries(GraphUtilities.interweaveValues(longTimes, series.get(i)),
					"Try " + Integer.toString(i + 1));
			seriesList[i] = s;
		}
		double[] minMax = GraphUtilities.getMaxandMin(series);
		holder.graph.setMaxMinY(0, Math.ceil(minMax[0]));
		double range = GraphUtilities.getStep(minMax[0], minMax[1]);
		holder.graph.setRangeAndDomainSteps(range, 1, 2, 2);
		holder.graph.updatePlot(seriesList, new int[] {Color.BLACK, Color.WHITE, Color.BLUE});
		return convertView;
	}
	
	public void clear(){
		contents = new Cursor[titles.length];
		questionCursor = null;
	}
	
	private void removeQuestionaire() {
		String[] tmp = new String[titles.length - 1];
		int j = 0;
		for(int i = 0; i < titles.length; i++){
			if(!titles[i].equals(questionaire)){
				tmp[j] = titles[i];
				j++;
			}
		}
		titles = tmp;
	}
	
	public void update(){
		updateCursors();
		notifyDataSetChanged();
	}
	
	private void updateCursors(){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		clear();
		Log.d(TAG, Integer.toString(titles.length));
		for(int i = 0; i < titles.length; i++){
			Log.d(TAG, titles[i]);
			contents[i] = db.getSingle(titles[i]);
			Log.d(TAG, Integer.toString(contents[i].getCount()));
		}
		questionCursor = db.getAllQuestionaireResults();
	}
	
	public static class ViewHolder{
		ResultXYBarGraph graph;
	}

}
