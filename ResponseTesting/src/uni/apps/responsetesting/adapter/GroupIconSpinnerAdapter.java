package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This adapter is for the spinner in multi user mode setup
 * 
 * 
 * @author Mathew Andela
 *
 */
public class GroupIconSpinnerAdapter extends BaseAdapter {

	//drawable ids
	private int[] icons = new int[] {R.drawable.uni_logo, R.drawable.ic_menu_magic, R.drawable.ic_menu_forestry};
	//varaibles
	private String[] names;
	private Activity activity;
	private LayoutInflater inflater;
	
	//sets up variables
	public GroupIconSpinnerAdapter(Activity activity){
		this.activity = activity;
		names = activity.getResources().getStringArray(R.array.group_image_names);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return icons[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		//creates new view and initalises viewholder
		if(convertView == null){
			convertView = inflater.inflate(R.layout.new_group, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.group_image);
			holder.name = (TextView) convertView.findViewById(R.id.image_name);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		//sets values
		holder.name.setText(names[position]);
		holder.icon.setImageDrawable(activity.getResources().getDrawable(icons[position]));
		return convertView;
	}
	
	//viewholder class
	class ViewHolder{
		ImageView icon;
		TextView name;
	}

}
