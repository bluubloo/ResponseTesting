package uni.apps.responsetesting.fragment.events;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.EventInstructionsListener;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EventInstructionsFragment extends Fragment {

	private static final String TAG = "EventInstructionsFragment";
	private String eventName = "";
	private EventInstructionsListener listener;
	//private TextView infoTextView;
	//private TextView eventNameTextView;
	
	public EventInstructionsFragment(){}
	
	public static EventInstructionsFragment getInstance(String eventName, Resources r){
		EventInstructionsFragment tmp = new EventInstructionsFragment();
		Bundle args = new Bundle();
		args.putString(r.getString(R.string.event_name), eventName);
		tmp.setArguments(args);
		return tmp;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		eventName = this.getArguments().getString(getResources().getString(R.string.event_name));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.instructions_fragment, container, false);
		String info = ActivityUtilities.getEventInfo(eventName, getResources());
		TextView infoTextView = (TextView) view.findViewById(R.id.instuct_info);
		infoTextView.setText(info);
		TextView eventNameTextView = (TextView) view.findViewById(R.id.instruct_event_name);
		eventNameTextView.setText(eventName);
		setUpButtons(view);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (EventInstructionsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + 
					" must implement EventInstructionsListener");
		}

	}

	private void setUpButtons(View view) {
		Button next = (Button) view.findViewById(R.id.instruct_next);
		next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.switchFragments();
			}
			
		});
		
		Button back = (Button) view.findViewById(R.id.instruct_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.goBack();
			}
			
		});
		
	}
	
}
