package uni.apps.responsetesting.fragment.results;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.ResultsListExpandableAdapter;
import uni.apps.responsetesting.results.Results;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class ResultsDisplayFragment extends Fragment {
	
	
	private static final String TAG = "ResultsDisplayFragment";
	private ExpandableListView list_view;
	private ResultsListExpandableAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.results_display_fragment, container, false);
		list_view = (ExpandableListView) view.findViewById(R.id.results_dis_expand_list);		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		String[][] contents = Results.getResultsForInApp(getActivity());
		//set up list adapter
		adapter = new ResultsListExpandableAdapter(getActivity(), contents);
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//set list adpater
		list_view.setAdapter(adapter);
		list_view.setChoiceMode(ListView.CHOICE_MODE_NONE);
		list_view.setChildDivider(null);
		list_view.setDividerHeight(0);
	}
	
	public void update(){
		adapter.update(Results.getResultsForInApp(getActivity()));
	}
}
