package uni.apps.responsetesting.fragment;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This fragment holds the information used to display the ui in the main menu page viewer
 * 
 * @author Mathew Andela
 *
 */
public class MainMenuFragmentTabbed extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		//variables
		private static final String TAG = "MainMenuFragmentTabbed";
		private MainMenuListener listener;
		private static String multiUserSettings = "";
		private static String userId = "";
		ArrayAdapter<String> adapter;
		ListView listV;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static MainMenuFragmentTabbed newInstance(int sectionNumber) {
			MainMenuFragmentTabbed fragment = new MainMenuFragmentTabbed();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public MainMenuFragmentTabbed() {
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			Log.d(TAG, "onCreate");
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
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
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			//creates view
			View rootView = inflater.inflate(R.layout.fragment_test, container,
					false);
			//gets sub views
			TextView title = (TextView) rootView.findViewById(R.id.section_title);
			ImageView image = (ImageView) rootView.findViewById(R.id.section_image);
			//sets sub view data
			int i = (int) getArguments().get(ARG_SECTION_NUMBER);
			title.setText(getTitle(i));
			image.setBackground(getResources().getDrawable(getImage(i)));
			//gets list of tests
			String[] listS = getList(i);
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listS);
			//gets listview and sets its data
			listV = (ListView) rootView.findViewById(R.id.section_list);
			listV.setAdapter(adapter);
			listV.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//moves to new activity
					TextView text = (TextView) view.findViewById(android.R.id.text1);
					listener.OnMenuItemClick(text.getText().toString());
				}
				
			});
			return rootView;
		}
		
		//gets image id
		private int getImage(int position) {
			switch (position - 1) {
			case 0:
				return R.drawable.attention;
			case 1:
				return R.drawable.memory;
			case 2:
				return R.drawable.motor;
			}
			return R.drawable.uni_logo;
		}

		//gets lists of tests
		private String[] getList(int position) {
			switch (position - 1) {
			case 0:
				return checkList(getResources().getStringArray(R.array.event_name_attention));
			case 1:
				return checkList(getResources().getStringArray(R.array.event_name_memory));
			case 2:
				return checkList(getResources().getStringArray(R.array.event_name_motor));
			}
			return null;
		}

		//checks list of tests for playable
		private String[] checkList(String[] stringArray) {
			//get user id
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
			Resources r = getResources();
			ArrayList<String> list = new ArrayList<String>();
			//gets data from database
			DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
			for(String s: stringArray){
				boolean addable = true;
				
				//checks if event is addable
				if(s.equals(r.getString(R.string.event_name_questionaire)))
					addable = db.checkQuestionaire(s, userId);
				else
					addable = checkPreferences(s) && db.checkRecent(s, userId);
				if(addable)
					list.add(s);
			}
			
			String[] tmp = new String[list.size()];
			for(int i = 0; i < tmp.length; i ++)
				tmp[i] = list.get(i);
			return tmp;
		}

		//checks preferencs
		private boolean checkPreferences(String name) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
			//checks user mode
			if(single)
				return checkSinglePrefereneces(prefs, getResources(), name);
			else
				return checkMultiUserPrferences(getResources(), name);
		}

		//checks multi user mode settings
		private boolean checkMultiUserPrferences(Resources resources,
				String name) {
			//gets settings string
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

		//checks single user mode preferences
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

		//gets section title
		private String getTitle(int position){
			switch (position - 1) {
			case 0:
				return getString(R.string.title_section1);
			case 1:
				return getString(R.string.title_section2);
			case 2:
				return getString(R.string.title_section3);
			}
			return null;
		}
	
}
