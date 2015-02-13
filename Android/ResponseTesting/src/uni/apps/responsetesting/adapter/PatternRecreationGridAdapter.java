package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.models.PatternRecreationData;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * This adpater is for the PatternRecreationFragment gridview
 * 
 * 
 * @author Mathew Andela
 *
 */
public class PatternRecreationGridAdapter extends BaseAdapter {

	//Variables 
	private PatternRecreationData[] data;
	private Activity activity;


	public PatternRecreationGridAdapter(PatternRecreationData[] data, Activity activity){
		//sets vairables
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
		//gets new view
		if(convertView == null){
			view = new ImageView(activity);
		} else {
			view = (ImageView) convertView;
		}		
		//sets view properties
		view.setScaleType(ImageView.ScaleType.CENTER_CROP);
		view.setPadding(10, 10, 10, 10);
		GridView p = (GridView) parent;
		view.setLayoutParams(new GridView.LayoutParams(p.getColumnWidth(), p.getColumnWidth()));
		//sets view background colours
		if(data[position].wasErrorMade())
			view.setBackgroundColor(Color.RED);
		else if(!data[position].isPartOfPattern() || !data[position].isUncovered())
			view.setBackgroundColor(Color.rgb(192, 192, 192));
		else
			view.setBackgroundColor(Color.rgb(24, 243, 100));
		return view;
	}

	//clears the data arrays
	public void clear(){
		data = new PatternRecreationData[1];
	}

	//updates the data arrays
	public void update(PatternRecreationData[] data){
		this.data = data;
		notifyDataSetChanged();
	}
}
