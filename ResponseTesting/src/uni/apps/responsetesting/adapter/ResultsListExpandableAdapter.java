package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.results.Results;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResultsListExpandableAdapter extends BaseExpandableListAdapter {
	private Activity mActivity;
	private String[] mTitles;
	private String[][] mContents;	
	private static LayoutInflater inflater = null; 

	public ResultsListExpandableAdapter(Activity activity, String[][] contents){
		super();
		mTitles = activity.getResources().getStringArray(R.array.event_name_array);
		if(mTitles.length != contents.length){
			throw new IllegalArgumentException("Titles and arguments must be the same size");
		}

		mActivity = activity;
		mContents = contents;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

/*		if(childPosition < 1 || childPosition > 3){
			TextView title = (TextView) convertView;
			if(title == null){
				title = new TextView(mActivity);
			}
			title.setText(mContents[groupPosition][childPosition]);
			title.setTextSize(15);
			return title;
		} else{*/
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.results_display_item, null);
				holder.text = (TextView) convertView.findViewById(R.id.results_display_text);
				holder.button = (Button) convertView.findViewById(R.id.results_display_button);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(mContents[groupPosition][childPosition]);
			if(childPosition != 1){
				holder.button.setVisibility(View.GONE);
			} else {
				holder.button.setVisibility(View.VISIBLE);
				setUpButton(holder.button, groupPosition, 1);
			}
			return convertView;
		}

	private void setUpButton(Button button, final int groupPosition, final int childPosition) {
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("Add Notes");
				
				final EditText text = new EditText(mActivity);
				text.setInputType(InputType.TYPE_CLASS_TEXT);
				builder.setView(text);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Results.updateNotes(mTitles[groupPosition],
								text.getText().toString(), mActivity);
					}
				});
				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();						
					}
				});
				builder.show();
			}
			
		});
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

	public static class ViewHolder{
		TextView text;
		Button button;
	}
}
