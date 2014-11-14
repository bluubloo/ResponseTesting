package uni.apps.responsetesting.adapter;

import uni.apps.responsetesting.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
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
	private static LayoutInflater inflater = null;

	//sets variables
	public QuestionaireListAdapter(Activity activity){
		data = activity.getResources().getStringArray(R.array.questionaire_array);
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
		//creates new view
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.questionaire_item, null);
			//set views
			holder.question = (TextView) convertView.findViewById(R.id.questionaire_text);
			holder.rating	= (RatingBar) convertView.findViewById(R.id.questionaire_rating);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//set view values
		holder.question.setText(question);
		holder.rating.setRating(3);

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

	public static class ViewHolder{
		TextView question;
		RatingBar rating;
	}

}
