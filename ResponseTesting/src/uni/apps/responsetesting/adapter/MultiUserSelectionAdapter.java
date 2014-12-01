package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MultiUserSelectionAdapter extends BaseAdapter {

	private String[] data;
	private int[] values;
	private LayoutInflater inflater;
	
	public MultiUserSelectionAdapter(String[] data, int[] values, Activity activity){
		if(data.length != values.length)
			throw new IllegalArgumentException("Names and value array lengths must be the same");
		
		this.data = data;
		this.values = values;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
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
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.multi_user_select_list_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.multi_title);
			holder.value = (CheckBox) convertView.findViewById(R.id.multi_value);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(data[position]);
		holder.value.setChecked(values[position] == 1);
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView name;
		CheckBox value;
	}

}
