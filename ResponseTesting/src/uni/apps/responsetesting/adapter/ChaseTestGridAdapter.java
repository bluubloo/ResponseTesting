package uni.apps.responsetesting.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ChaseTestGridAdapter extends BaseAdapter {

	private int targetPos;
	private int userPos;
	private boolean[] data;
	private Activity activity;

	public ChaseTestGridAdapter(Activity activity){
		reset();
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

		if(convertView == null){
			view = new ImageView(activity);
		} else {
			view = (ImageView) convertView;
		}		

		view.setScaleType(ImageView.ScaleType.CENTER_CROP);
		view.setPadding(10, 10, 10, 10);
		GridView p = (GridView) parent;
		view.setLayoutParams(new GridView.LayoutParams(p.getColumnWidth(), p.getColumnWidth()));

		if(data[position] && position == targetPos)
			view.setBackgroundColor(Color.RED);
		else if(data[position] && position == userPos)
			view.setBackgroundColor(Color.BLUE);
		else
			view.setBackgroundColor(Color.rgb(192, 192, 192));

		return view;
	}

	public void reset(){
		targetPos = 42;
		userPos = 6;
		data = new boolean[49];
		data[targetPos] = true;
		data[userPos] = true;
		notifyDataSetChanged();
	}

	public void update(int target, int user){
		setPosFalse();
		targetPos = target;
		userPos = user;
		setPosTrue();
		notifyDataSetChanged();
	}

	private void setPosTrue(){
		data[targetPos] = true;
		data[userPos] = true;
	}

	private void setPosFalse(){
		data[targetPos] = false;
		data[userPos] = false;
	}

}
