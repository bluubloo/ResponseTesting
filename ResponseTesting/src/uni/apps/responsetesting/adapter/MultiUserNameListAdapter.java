package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.models.MultiUserInfo;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This adapter is for the MultuUserNameFragment listview
 * 
 * 
 * @author Mathew Andela
 *
 */
public class MultiUserNameListAdapter extends BaseAdapter {

	//variables
	private MultiUserInfo[] data;
	private Context context;
	
	public MultiUserNameListAdapter(MultiUserInfo[] data, Activity activity){
		//sets variables
		this.data = data;
		context = activity;
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
		//creates new view
		TextView view = (TextView) convertView;
		if(view == null)
			view = new TextView(context);
		//sets view values
		view.setText(data[position].getName());
		view.setTextSize(40);
		return view;
	}

}
