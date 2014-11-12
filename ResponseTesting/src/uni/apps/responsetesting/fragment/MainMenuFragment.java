package uni.apps.responsetesting.fragment;

import java.util.ArrayList;
import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class is the fragment being displayed for the main menu
 *
 * 
 * @author Mathew Andela
 *
 */
public class MainMenuFragment extends ListFragment {

	private static final String TAG = "MainMenuFragment";
	private MainMenuListener listener;
	private ArrayAdapter<?> adapter;

	public MainMenuFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		//set up list adapter
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, createList());
	}

	private ArrayList<String> createList(){
		Resources r = this.getActivity().getResources();
		String[] tmp = r.getStringArray(R.array.event_name_array);
		ArrayList<String> list = new ArrayList<String>();
		for(String s: tmp){
			list.add(s);
		}
		return list;
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//set list adpater
		getListView().setAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setIsLoading(false);
	}

	public void setIsLoading(boolean is_loading) {
		setListShown(!is_loading);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (MainMenuListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement MainMenuListener");
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//on click navigate to new activity
		TextView txt = (TextView) v.findViewById(android.R.id.text1);
		String name = txt.getText().toString();
		listener.OnMenuItemClick(name);			
	}
}
