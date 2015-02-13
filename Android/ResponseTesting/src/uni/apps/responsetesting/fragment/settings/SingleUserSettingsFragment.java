package uni.apps.responsetesting.fragment.settings;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.MultiUserSelectionAdapter;
import uni.apps.responsetesting.interfaces.listener.MultiUserSelectionListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This fragment deals with single user mode setup
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SingleUserSettingsFragment extends Fragment implements MultiUserSelectionListener{

	//variables
	private static final String TAG = "SingleUserSettingsFragment";
	private MultiUserSelectionAdapter adapter;
	private MultiUserSelectionListener listener;
	private int[] values;
	
	//views
	private EditText userName;
	private EditText email;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.settings_single_mode_fragment, container, false);
		//sets views
		
		setUpInitialValues(view);
		setUpButtons(view);

		return view;
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

	private void setUpButtons(View view) {
		Button submit = (Button) view.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setValues();
			}			
		});
	}
	
	private void setUpInitialValues(View view) {
		// variables
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Resources r = getResources();
		//set edittexts
		userName = (EditText) view.findViewById(R.id.settings_user_name);
		userName.setText(prefs.getString(r.getString(R.string.pref_key_user_name),
				r.getString(R.string.setup_mode_default_user_name_single)));
		
		email = (EditText) view.findViewById(R.id.settings_email);
		email.setText(prefs.getString(r.getString(R.string.pref_key_email),
				r.getString(R.string.setup_mode_default_email)));
		
		list = (ListView) view.findViewById(R.id.test_list);
		String[] names = getResources().getStringArray(R.array.event_name_array_noq);
		values = getValues(names.length);
		adapter = new MultiUserSelectionAdapter(names, values, getActivity(), listener);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

	//gets initial values
	private int[] getValues(int length) {
		int[] tmp = new int[length];
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Resources r = getResources();
		tmp[0] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_ao), true));
		tmp[1] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_aof), true));
		tmp[2] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_ai), true));
		tmp[3] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_cd), true));
		tmp[4] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_ch), true));
		tmp[5] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_eov), true));
		tmp[6] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_ftt), true));
		tmp[7] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_ml), true));
		tmp[8] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_oclt), true));
		tmp[9] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_pr), true));
		tmp[10] = getValue(prefs.getBoolean(r.getString(R.string.pref_key_s), true));
		return tmp;		
	}
	
	//set settings values
	private void setValues(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Resources r = getResources();
		Editor edit = prefs.edit();
		edit.putBoolean(r.getString(R.string.pref_key_ao), getValue(values[0]));
		edit.putBoolean(r.getString(R.string.pref_key_aof), getValue(values[1]));
		edit.putBoolean(r.getString(R.string.pref_key_ai), getValue(values[2]));
		edit.putBoolean(r.getString(R.string.pref_key_cd), getValue(values[3]));
		edit.putBoolean(r.getString(R.string.pref_key_ch), getValue(values[4]));
		edit.putBoolean(r.getString(R.string.pref_key_eov), getValue(values[5]));
		edit.putBoolean(r.getString(R.string.pref_key_ftt), getValue(values[6]));
		edit.putBoolean(r.getString(R.string.pref_key_ml), getValue(values[7]));
		edit.putBoolean(r.getString(R.string.pref_key_oclt), getValue(values[8]));
		edit.putBoolean(r.getString(R.string.pref_key_pr), getValue(values[9]));
		edit.putBoolean(r.getString(R.string.pref_key_s), getValue(values[10]));
		edit.putString(r.getString(R.string.pref_key_user_name), userName.getText().toString());
		edit.putString(r.getString(R.string.pref_key_email), email.getText().toString());
		edit.commit();
		getActivity().onBackPressed();
	}
	
	//get boolean value
	private boolean getValue(int i){
		return i == 1;
	}
	
	//gets int value
	private int getValue(boolean b){
		if(b)
			return 1;
		return 0;
	}

	//alters values
	@Override
	public void onCheckChanged(int pos) {
		int tmp = values[pos];
		Log.d(TAG, Integer.toString(tmp));
		if(tmp == 1)
			values[pos] = 0;
		else
			values[pos] = 1;
		adapter.update(values);
	}
		
}
