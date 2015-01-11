package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/**
 * This class is the adapter for the questionaire list
 * 
 * 
 * @author Mathew Andela
 *
 */
public class QuestionaireListAdapter extends BaseAdapter {

	//variables
	String[] data;
	float[] ratings;
	private static LayoutInflater inflater = null;

	//sets variables
	public QuestionaireListAdapter(Activity activity){
		data = activity.getResources().getStringArray(ActivityUtilities.getThemeQuestionsId(activity));
		ratings = new float[data.length];
		for(int i = 0; i < ratings.length; i++)
			ratings[i] = 0;
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
		String question = data[position];
		final int pos = position;
		//creates new view
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.questionaire_item, null);
			//set views
			holder.question = (TextView) convertView.findViewById(R.id.questionaire_text);
			holder.rating	= (RatingBar) convertView.findViewById(R.id.questionaire_rating);
			holder.low = (TextView) convertView.findViewById(R.id.quest_low);
			holder.high = (TextView) convertView.findViewById(R.id.quest_high);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//set view values
		holder.question.setText(question);
		holder.rating.setRating(ratings[position]);
		
		holder.rating.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				ratings[pos] = rating;
			}
			
		});
		
		if(position == 2 || position == 3){
			holder.low.setText("Very Poor");
			holder.high.setText("Very Good");
		}
		
		
		return convertView;
	}

	public void clear() {
		//clears list
		data = new String[data.length];
	}

	public void addAll(String[] list) {
		//sets list to data list
		String[] tmp = new String[data.length + list.length];
		int i = 0;
		for(String s : data){
			tmp[i] = s;
			i++;
		}
		for (String s: list){
			tmp[i] = s;
			i++;
		}
		data = tmp;
	}
	
	//gets rating values
	public String getRating(int pos){
		return Float.toString(ratings[pos]);
	}

	public static class ViewHolder{
		TextView question;
		RatingBar rating;
		TextView low;
		TextView high;
	}

}
