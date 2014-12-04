package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.models.MultiUserInfo;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MultiUserNameListAdapter extends BaseAdapter {

	private MultiUserInfo[] data;
	private Context context;
	
	public MultiUserNameListAdapter(MultiUserInfo[] data, Activity activity){
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
		TextView view = (TextView) convertView;
		if(view == null)
			view = new TextView(context);
		
		view.setText(data[position].getName());
		view.setTextSize(40);
		return view;
	}

}
