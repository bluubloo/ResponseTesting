package uni.apps.responsetesting.fragment;

import uni.apps.responsetesting.adapter.MultiUserNameListAdapter;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import uni.apps.responsetesting.models.MultiUserInfo;
import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 *  This fragment is user for selecting usernames in multi user mode
 * 
 * 
 * @author Mathew Andela
 *
 */
public class MultiUserNameFragment extends ListFragment {

	//variables
	private static final String TAG = "MultiUserNameFragment";
	private MultiUserInfo[] data;
	private MultiUserNameListAdapter adapter;
	private MainMenuListener listener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	//attachs listener to activity
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
	
	private void setUpList() {
		//gets data and creates adapter
		data = formatData(DatabaseHelper.getInstance(getActivity(), getResources()).getMultiUsers());
		adapter = new MultiUserNameListAdapter(data, getActivity());
		//sets listview values
		getListView().setAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setListShown(true);
	}

	private MultiUserInfo[] formatData(Cursor cursor) {
		//formats data into list from cursor
		MultiUserInfo[] tmp =  new MultiUserInfo[cursor.getCount()];
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				tmp[i] = new MultiUserInfo(cursor.getString(0), cursor.getString(1),
						cursor.getString(2));
				i++;
			} while(cursor.moveToNext());
		}
		return tmp;
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//sets up list
		setUpList();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		listener.OnMultiUserClick(data[position].getId());
	}
	
}
