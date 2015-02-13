package uni.apps.responsetesting.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * This class is the adapter for the CenterArrowFragment gridview
 * 
 * 
 * @author Mathew Andela
 *
 */
public class CenterArrowGridAdapter extends BaseAdapter {

	//Variables
	public int[] data;
	public Activity activity;
	
	//constructor
	public CenterArrowGridAdapter(int[] data, Activity activity){
		this.data = data;
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view;
		//create new image view
		if(convertView == null)
			view = new ImageView(activity);
		else
			view = (ImageView) convertView;
		//set image drawable and size
		int value = data[position];
		GridView p = (GridView) parent;
		view.setLayoutParams(new GridView.LayoutParams(p.getColumnWidth(), p.getColumnWidth()));
		if(value == -1)
			view.setImageDrawable(null);
		else
			view.setImageDrawable(activity.getResources().getDrawable(value));
		return view;
	}
	
	//clears data
	public void clear(){
		data = new int[25];
		notifyDataSetChanged();
	}
	
	//updates data
	public void update(int[] data){
		this.data = data;
		notifyDataSetChanged();
	}

}
