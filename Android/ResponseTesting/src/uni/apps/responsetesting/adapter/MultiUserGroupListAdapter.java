package uni.apps.responsetesting.adapter;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.MultiUserGroupListener;
import uni.apps.responsetesting.interfaces.listener.MultiUserSettingsListener;
import uni.apps.responsetesting.models.MultiUserGroupInfo;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This adapter is for the MultiUserGroupFragment expandable listview
 * 
 * 
 * @author Mathew Andela
 *
 */
public class MultiUserGroupListAdapter extends BaseExpandableListAdapter {

	//variables
	private ArrayList<MultiUserGroupInfo> groups;
	private LayoutInflater inflater;
	private Activity activity;
	private MultiUserGroupListener listener = null;
	private MultiUserSettingsListener listListener;


	public MultiUserGroupListAdapter(ArrayList<MultiUserGroupInfo> groups, Activity activity, 
			MultiUserGroupListener listener, MultiUserSettingsListener listListener){
		//sets variables
		this.groups = groups;
		this.activity = activity;
		this.listener = listener;
		this.listListener = listListener;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).getUserCount();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition).getAllUsers();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).getUser(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		final int position = groupPosition;
		//creates new view 
		if(convertView == null){
			convertView = inflater.inflate(R.layout.multi_user_group_parent_item, null);
			holder = new GroupViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.multi_group_icon);
			holder.name = (TextView) convertView.findViewById(R.id.multi_group_name);
			holder.add = (Button) convertView.findViewById(R.id.multi_group_add);
			convertView.setTag(holder);
		} else{
			holder = (GroupViewHolder) convertView.getTag();
		}

		//sets view values
		holder.icon.setImageDrawable(activity.getResources().
				getDrawable(groups.get(groupPosition).getIconId()));

		holder.name.setText(groups.get(groupPosition).getGroup());

		holder.add.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onAddUserClick(position);
			}

		});

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final int pos1 = groupPosition;
		final int pos2 = childPosition;
		ChildViewHolder holder;
		//creates new view
		if(convertView == null){
			convertView = inflater.inflate(R.layout.multi_user_group_child_item, null);
			holder = new ChildViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.multi_group_name);
			holder.modify = (ImageView) convertView.findViewById(R.id.multi_group_move);
			holder.delete = (ImageView) convertView.findViewById(R.id.multi_group_remove);
			holder.settings = (ImageView) convertView.findViewById(R.id.multi_group_settings);
			convertView.setTag(holder);
		} else{
			holder = (ChildViewHolder) convertView.getTag();
		}
		//sets view values
		holder.name.setText(groups.get(groupPosition).getUser(childPosition).getName() + ", (" + 
				groups.get(groupPosition).getUser(childPosition).getId() + ")");
		holder.delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onDeleteUser(pos1, pos2);
			}

		});

		holder.modify.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onMoveUser(pos1, pos2);
			}

		});

		holder.settings.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listListener.onGroupChildClick(groups.get(pos1).getUser(pos2));
			}

		});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	//parent viewholder
	private class GroupViewHolder{
		TextView name;
		ImageView icon;
		Button add;
	}

	//child viewholder
	private class ChildViewHolder{
		TextView name;
		ImageView modify;
		ImageView delete;
		ImageView settings;
	}

}
