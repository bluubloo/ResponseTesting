package uni.apps.responsetesting.fragment;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import android.app.Activity;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

	//variables
	private static final String TAG = "MainMenuFragment";
	private MainMenuListener listener;
	private ArrayAdapter<?> adapter;
	private String userId = "";
	private String userName = "";
	private String multiUserSettings = "";

	public MainMenuFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		//gets user id and username
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
		userName = DatabaseHelper.getInstance(getActivity(), getResources()).getMultiUserName(userId);
		getActivity().getActionBar().setSubtitle("Main Menu - " + userName);
	//	setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		//set up list adapter
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, createList());
		getListView().setAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setIsLoading(false);
	}

	//Creates main menu list
	private ArrayList<String> createList(){
		//gets string array from rescoures
		Resources r = this.getActivity().getResources();
		String[] tmp = r.getStringArray(R.array.event_name_array);
		//adds from array to list
		ArrayList<String> list = new ArrayList<String>();
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
		for(String s: tmp){
			boolean addable = true;
			//checks if event is addable
			if(s.equals(r.getString(R.string.event_name_questionaire)))
				addable = db.checkQuestionaire(s, userId);
			else
				addable = checkPreferences(s) && db.checkRecent(s, userId);
			if(addable)
				list.add(s);
		}
		return list;
	}

	private boolean checkPreferences(String name) {
		//checks user mode
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
		if(single)
			return checkSinglePrefereneces(prefs, getResources(), name);
		else
			return checkMultiUserPrferences(getResources(), name);
	}

	private boolean checkMultiUserPrferences(Resources resources, String name) {
		//gets user settings
			multiUserSettings = DatabaseHelper.getInstance(getActivity(), resources).
			getMultiSettingsString(userId);
			//gets setting value
		if(!multiUserSettings.equals("")){
			int index = getSettingIndex(name);
			if(index != -1)
				return getPreferenceValue(index);
		}
		return true;
	}

	//gets settings value
	private boolean getPreferenceValue(int index) {
		int i = index * 2;
		if(multiUserSettings.length() > i && i >= 0)
			return multiUserSettings.charAt(i) == '1';
		return true;
	}

	//gets setting index
	private int getSettingIndex(String name) {
		Log.d(TAG, name);
		String[] tmp = getResources().getStringArray(R.array.event_name_array_noq);
		for(int i = 0; i < tmp.length; i ++)
			if(tmp[i].equals(name))
				return i;
		return -1;
	}

	//checks single mode settings value
	private boolean checkSinglePrefereneces(SharedPreferences prefs,
			Resources r, String name) {
		switch(name){
		case "Appearing Object":
			return prefs.getBoolean(r.getString(R.string.pref_key_ao), true);
		case "Appearing Object - Fixed Point":
			return prefs.getBoolean(r.getString(R.string.pref_key_aof), true);
		case "Arrow Ignoring Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_ai), true);
		case "Changing Directions":
			return prefs.getBoolean(r.getString(R.string.pref_key_cd), true);
		case "Chase Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_ch), true);
		case "Even or Vowel":
			return prefs.getBoolean(r.getString(R.string.pref_key_eov), true);
		case "Finger Tap Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_ftt), true);
		case "Monkey Ladder":
			return prefs.getBoolean(r.getString(R.string.pref_key_ml), true);
		case "One Card Learning Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_oclt), true);
		case "Pattern Recreation":
			return prefs.getBoolean(r.getString(R.string.pref_key_pr), true);
		case "Stroop Test":
			return prefs.getBoolean(r.getString(R.string.pref_key_s), true);
		default:
			return true;
		}
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		//set list adpater
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, createList());
		getListView().setAdapter(adapter);
	}

	public void setIsLoading(boolean is_loading) {
		setListShown(!is_loading);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//on click navigate to new activity
		TextView txt = (TextView) v.findViewById(android.R.id.text1);
		String name = txt.getText().toString();
		listener.OnMenuItemClick(name);			
	}
	
	//updates list
	public void update(){
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, createList());
		getListView().setAdapter(adapter);
	}
}
