package uni.apps.responsetesting.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This class is the adapter for the stroop test grid
 * 
 * 
 * @author Mathew Andela
 *
 */
public class StroopTestGridAdapter extends BaseAdapter {

	//variables
	private String[] data;
	private int[] colour;
	private Activity activity;

	//sets the variables
	public StroopTestGridAdapter(String[] data, int[] colour, Activity activity){
		this.data = data;
		this.colour = colour;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		if(position < data.length)
			return data[position];
		else
			return data[data.length - 1];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//creates the view
		TextView view = (TextView) convertView;
		if(view == null)
			view = new TextView(activity);
		//sets the view values
		view.setText(data[position]);
		view.setTextSize(30);
		view.setTypeface(Typeface.DEFAULT_BOLD);
		view.setTextColor(colour[position]);
		return view;
	}

	//clears the data arrays
	public void clear(){
		data = new String[] {"","","","",""};
		colour = new int[] {0,0,0,0,0};
	}

	//updates the data arrays
	public void update(String[] data, int[] colour){
		this.data = data;
		this.colour = colour;
		notifyDataSetChanged();
	}
}
