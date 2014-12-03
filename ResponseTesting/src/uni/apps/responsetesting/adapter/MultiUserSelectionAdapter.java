package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.MultiUserSelectionListener;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MultiUserSelectionAdapter extends BaseAdapter {

	private String[] data;
	private int[] values;
	private LayoutInflater inflater;
	private MultiUserSelectionListener listener;

	public MultiUserSelectionAdapter(String[] data, int[] values, Activity activity,
			MultiUserSelectionListener listener){
		if(data.length != values.length)
			throw new IllegalArgumentException("Names and value array lengths must be the same");
		this.listener = listener;
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
		final int pos = position;
		holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.multi_user_select_list_item, null);
		holder.name = (TextView) convertView.findViewById(R.id.multi_title);
		holder.value = (CheckBox) convertView.findViewById(R.id.multi_value);
		convertView.setTag(holder);

		holder.name.setText(data[position]);
		holder.value.setChecked(values[position] == 1);
		holder.value.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				listener.onCheckChanged(pos);
			}

		});
		return convertView;
	}

	public int getValue(int i){
		return values[i];
	}

	private class ViewHolder{
		TextView name;
		CheckBox value;
	}

	public void update(int[] values) {
		this.values = values;
		notifyDataSetChanged();
	}

}
