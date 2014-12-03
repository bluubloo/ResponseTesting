package uni.apps.responsetesting.fragment.settings;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.MultiUserSelectionAdapter;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.interfaces.listener.MultiUserSelectionListener;
import uni.apps.responsetesting.models.MultiUserInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MultiUserSelectionFragment extends Fragment implements MultiUserSelectionListener{

	private static final String TAG = "MultiUserSelectionFragment";
	private TextView email;
	private ListView list;
	private MultiUserSelectionAdapter adapter;
	private MultiUserSelectionListener listener;
	private String userid = "";
	private String name = "";
	private int[] values;

	public static MultiUserSelectionFragment getInstance(MultiUserInfo user) {
		MultiUserSelectionFragment tmp = new MultiUserSelectionFragment();
		Bundle args = new Bundle();
		args.putString("id", user.getId());
		args.putString("name", user.getName());
		tmp.setArguments(args);
		return tmp;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		outState.putString("id", userid);
		outState.putString("name", name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.multi_user_selection_fragment, container, false);
		email = (TextView) view.findViewById(R.id.multi_email);
		list = (ListView) view.findViewById(R.id.multi_select_list);
		setUpListAdapter();
		setUpButtons(view);
		return view;
	}

	private void setUpListAdapter() {
		String[] names = getResources().getStringArray(R.array.event_name_array);
		names = removeQuestionaire(names);
		values = getValues(names);
		adapter = new MultiUserSelectionAdapter(names, values, getActivity(), listener);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_NONE);
	}

	private String[] removeQuestionaire(String[] names) {
		ArrayList<String> tmp = new ArrayList<String>();
		String q = getResources().getString(R.string.event_name_questionaire);
		for(String s : names)
			if(!s.equals(q))
				tmp.add(s);
		String[] nameTmp = new String[tmp.size()];
		for(int i = 0; i < tmp.size(); i++)
			nameTmp[i] = tmp.get(i);
		return nameTmp;
	}

	private int[] getValues(String[] names) {
		int[] tmp = new int[names.length];
		Cursor cursor = DatabaseHelper.getInstance(getActivity(), getResources()).getMultiSettings(userid);
		if(cursor.getCount() == 0){
			for(int i = 0; i < tmp.length; i ++)
				tmp[i] = 1;
		} else{
			int j = 0;
			if(cursor.moveToFirst()){
				email.setText(cursor.getString(4));
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

	private void setUpButtons(View view) {
		Button setEmail = (Button) view.findViewById(R.id.multi_email_set);
		Button submit = (Button) view.findViewById(R.id.multi_submit);

		setEmail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				updateEmail();
			}

		});

		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				updateSettings();
			}

		});
	}

	private void updateSettings() {
		String emailText = email.getText().toString();
		String settings = "";
		for(int i = 0; i < adapter.getCount(); i++){
			Log.d(TAG, "Count: " + Integer.toString(i));
			settings += adapter.getValue(i);
			if(i < adapter.getCount() - 1)
				settings += "|"; 
			Log.d(TAG, settings);
			/*if(i < list.getCount() - 1)
				settings += getValue(((CheckBox) list.getChildAt(i).
						findViewById(R.id.multi_value)).isChecked()) + "|";
			else
				settings += getValue(((CheckBox) list.getChildAt(i).
						findViewById(R.id.multi_value)).isChecked());*/
		}
		Resources r = getResources();
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity(), getResources());
		ContentValues values = new ContentValues();
		values.put(r.getString(R.string.user_email), emailText);
		values.put(r.getString(R.string.user_settings), settings);
		db.updateMultiSettings(userid, values);
		getActivity().onBackPressed();
	}

	private void updateEmail(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Enter Password");

		final EditText text = new EditText(getActivity());
		text.setInputType(InputType.TYPE_CLASS_TEXT);
		text.setText(email.getText());
		builder.setView(text);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				email.setText(text.getText().toString());
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});
		builder.show();
	}

	@Override
	public void onCheckChanged(int pos) {
		// TODO Auto-generated method stub
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
