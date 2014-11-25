package uni.apps.responsetesting.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MonkeyLadderGridAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Integer> data;
	private int showableTiles;
	private boolean showText = true;
	private int clicked = 0;
	
	public MonkeyLadderGridAdapter(Activity activity, ArrayList<Integer> data, 
			int showableTiles){
		this.activity = activity;
		this.data = data;
		this.showableTiles = showableTiles;
		showText = true;
		clicked = 0;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view;
		if(convertView == null)
			view = new TextView(activity);
		else
			view = (TextView) convertView;
		
		int text = data.get(position);
		GridView p = (GridView) parent;
		view.setLayoutParams(new GridView.LayoutParams(p.getColumnWidth(), p.getColumnWidth()));
		if(text > showableTiles || text <= clicked){
			view.setBackgroundColor(Color.rgb(192, 192, 192));
			view.setText("");
		}
		else{
			view.setBackgroundColor(Color.rgb(153, 255, 153));
			if(showText){
				view.setText("\t\t\t" + Integer.toString(text));
				view.setTextSize(30);
				view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			} else
				view.setText("");
		}
			
		return view;
	}
	
	public void clear(){
		data = new ArrayList<Integer>();
		showableTiles = 3;
		showText = true;
		clicked = 0;
	}
	
	public void update(ArrayList<Integer> data, int showableTiles){
		this.data = data;
		this.showableTiles = showableTiles;
		showText = true;
		clicked = 0;
		notifyDataSetChanged();
	}
	
	public void update(boolean showText){
		this.showText = showText;
		notifyDataSetChanged();
	}
	
	public void update(){
		clicked ++;
		notifyDataSetChanged();
	}

}
