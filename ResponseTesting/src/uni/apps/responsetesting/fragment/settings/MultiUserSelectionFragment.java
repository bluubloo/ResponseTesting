package uni.apps.responsetesting.fragment.settings;

import java.util.ArrayList;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.adapter.MultiUserSelectionAdapter;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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

public class MultiUserSelectionFragment extends Fragment {

	private static final String TAG = "MultiUserSelectionFragment";
	private TextView email;
	private ListView list;
	private MultiUserSelectionAdapter adapter;

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
		int[] values = getValues(names);
		adapter = new MultiUserSelectionAdapter(names, values, getActivity());
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
		//TODO
		int[] tmp = new int[names.length];
		for(int i = 0; i < tmp.length; i ++)
			tmp[i] = 1;
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
		// TODO Auto-generated method stub
		String emailText = email.getText().toString();
		
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

}
