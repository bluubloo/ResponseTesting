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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenuFragmentTabbed extends Fragment {
	/**
	 * A placeholder fragment containing a simple view.
	 */
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private static final String TAG = "PlaceholderFragment";
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
		public void onResume() {
			super.onResume();
			if(adapter != null)
				adapter.notifyDataSetChanged();
			Log.d(TAG, "onResume");
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
			View rootView = inflater.inflate(R.layout.fragment_test, container,
					false);
			TextView title = (TextView) rootView.findViewById(R.id.section_title);
			int i = (int) getArguments().get(ARG_SECTION_NUMBER);
			title.setText(getTitle(i));
			String[] listS = getList(i);
			adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listS);
			listV = (ListView) rootView.findViewById(R.id.section_list);
			listV.setAdapter(adapter);
			listV.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView text = (TextView) view.findViewById(android.R.id.text1);
					listener.OnMenuItemClick(text.getText().toString());
				}
				
			});
			return rootView;
		}
		
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

		private String[] checkList(String[] stringArray) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			userId = prefs.getString(getResources().getString(R.string.pref_key_user_id), "single");
			Resources r = getResources();
			ArrayList<String> list = new ArrayList<String>();
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

		private boolean checkPreferences(String name) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			boolean single = prefs.getBoolean(getResources().getString(R.string.pref_key_user), true);
			if(single)
				return checkSinglePrefereneces(prefs, getResources(), name);
			else
				return checkMultiUserPrferences(getResources(), name);
		}

		private boolean checkMultiUserPrferences(Resources resources,
				String name) {
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
