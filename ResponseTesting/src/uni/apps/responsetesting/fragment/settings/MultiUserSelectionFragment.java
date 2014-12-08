package uni.apps.responsetesting.fragment.settings;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.MultiUserSelectionAdapter;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.interfaces.listener.MultiUserSelectionListener;
import uni.apps.responsetesting.models.MultiUserInfo;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

/**
 * This fragment handles the selection of the visible tests
 * 
 * @author Mathew Andela
 *
 */
public class MultiUserSelectionFragment extends Fragment implements MultiUserSelectionListener{

	//variables
	private static final String TAG = "MultiUserSelectionFragment";
	private ListView list;
	private MultiUserSelectionAdapter adapter;
	private MultiUserSelectionListener listener;
	private String userid = "";
	private String name = "";
	private int[] values;

	public static MultiUserSelectionFragment getInstance(MultiUserInfo user) {
		MultiUserSelectionFragment tmp = new MultiUserSelectionFragment();
		//sets arguments
		Bundle args = new Bundle();
		args.putString("id", user.getId());
		args.putString("name", user.getName());
		tmp.setArguments(args);
		return tmp;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//gets values
		if(savedInstanceState != null){
			userid = savedInstanceState.getString("id"); 
			name = savedInstanceState.getString("name");
		}
		setRetainInstance(true);
		Bundle b = getArguments();
		if(b != null){
			userid = b.getString("id");	
			name = b.getString("name");	
		}
		getActivity().getActionBar().setSubtitle(name);
	}

	//attachs listener to fragment
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (MultiUserSelectionListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement MultiUserSelectionListener");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		//saves values
		outState.putString("id", userid);
		outState.putString("name", name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.multi_user_selection_fragment, container, false);
		//sets views
		list = (ListView) view.findViewById(R.id.multi_select_list);
		setUpListAdapter();
		setUpButtons(view);
		return view;
	}

	private void setUpListAdapter() {
		//get values
		String[] names = getResources().getStringArray(R.array.event_name_array_noq);
		values = getValues(names);
		//set list adapter
		adapter = new MultiUserSelectionAdapter(names, values, getActivity(), listener);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

	private int[] getValues(String[] names) {
		//get values from db
		int[] tmp = new int[names.length];
		Cursor cursor = DatabaseHelper.getInstance(getActivity(), getResources()).getMultiSettings(userid);
		if(cursor.getCount() == 0){
			//set defaults
			for(int i = 0; i < tmp.length; i ++)
				tmp[i] = 1;
		} else{
			//get values from string
			int j = 0;
			if(cursor.moveToFirst()){
				String settings = cursor.getString(3);
				for(int i = 0; i < settings.length(); i += 2){
					tmp[j] = Integer.parseInt(new String(new char[] {settings.charAt(i)}));
					j++;
				}
			} else{
				for(int i = 0; i < tmp.length; i ++)
					tmp[i] = 1;
			}
		}
		return tmp;
	}

	//sets button
	private void setUpButtons(View view) {
		Button submit = (Button) view.findViewById(R.id.multi_submit);
		
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				updateSettings();
			}

		});
	}

	//updates settings
	private void updateSettings() {
		String settings = "";
		//gets settings
		//formats string
		for(int i = 0; i < adapter.getCount(); i++){
			settings += adapter.getValue(i);
			if(i < adapter.getCount() - 1)
				settings += "|"; 
		}
		Resources r = getResources();
		//updates db
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
		ContentValues values = new ContentValues();
		values.put(r.getString(R.string.user_settings), settings);
		db.updateMultiSettings(userid, values);
		getActivity().onBackPressed();
	}

	@Override
	public void onCheckChanged(int pos) {
		//changes values
		int tmp = values[pos];
		Log.d(TAG, Integer.toString(tmp));
		if(tmp == 1)
			values[pos] = 0;
		else
			values[pos] = 1;
		Log.d(TAG, Integer.toString(values[pos]));
		adapter.update(values);
	}



}
