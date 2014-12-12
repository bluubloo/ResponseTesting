package uni.apps.responsetesting.results;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.ResultsListener;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GraphListFragment extends ListFragment {

	private static final String TAG = "GraphListFragment";
	private ResultsListener listener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//sets up list
		setUpList();
	}
	
	//attachs listener to activity
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (ResultsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ResultsListener");
		}

	}

	private void setUpList() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.event_name_array_graphs, 
				android.R.layout.simple_list_item_1);
		getListView().setAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setListShown(true);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TextView view = (TextView) v.findViewById(android.R.id.text1);
		listener.switchToGraph(view.getText().toString());
	}
}
