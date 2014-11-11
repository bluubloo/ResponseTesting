package uni.apps.responsetesting.fragment.events;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.QuestionaireListAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class QuestionaireFragment extends Fragment {

	private static final String TAG = "QuestionaireFragment";
	private static final String eventName = "Questionaire";
	private QuestionaireListAdapter adapter;
	private ListView list_view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.questionaire_fragment, container, false);
		list_view = (ListView) view.findViewById(R.id.questionaire_list);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		//set up list adapter
		adapter = new QuestionaireListAdapter(getActivity());
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//set list adpater
		list_view.setAdapter(adapter);
		list_view.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

}
