package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ResultsListExpandableAdapter extends BaseExpandableListAdapter {
	private Activity mActivity;
	private String[] mTitles;
	private String[][] mContents;	
	
	public ResultsListExpandableAdapter(Activity activity, String[][] contents){
		super();
		mTitles = activity.getResources().getStringArray(R.array.event_name_array);
		if(mTitles.length != contents.length){
			throw new IllegalArgumentException("Titles and arguments must be the same size");
		}
		
		mActivity = activity;
		mContents = contents;
	}
	
	@Override
	public int getGroupCount() {
		return mContents.length;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mContents[groupPosition].length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mContents[groupPosition];
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mContents[groupPosition][childPosition];
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView title = (TextView) convertView;
		if(title == null){
			title = new TextView(mActivity);
		}
		title.setTypeface(Typeface.DEFAULT_BOLD);
		title.setTextSize(20);
		title.setText(mTitles[groupPosition]);
		return title;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView title = (TextView) convertView;
		if(title == null){
			title = new TextView(mActivity);
		}
		title.setText(mContents[groupPosition][childPosition]);
		title.setTextSize(15);
		return title;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void update(String[][] contents){
		if(mTitles.length != contents.length){
			throw new IllegalArgumentException("Titles and arguments must be the same size");
		}
		mContents = contents;
		//TODO Fix
		notifyDataSetChanged();
	}
}
