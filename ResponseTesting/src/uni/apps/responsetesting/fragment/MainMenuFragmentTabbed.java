package uni.apps.responsetesting.fragment;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
				return R.drawable.hand;
			}
			return R.drawable.uni_logo;
		}

		//gets lists of tests
		private String[] getList(int position) {
			switch (position - 1) {
			case 0:
				return ActivityUtilities.checkList(getResources().getStringArray(R.array.event_name_attention), getActivity());
			case 1:
				return ActivityUtilities.checkList(getResources().getStringArray(R.array.event_name_memory), getActivity());
			case 2:
				return ActivityUtilities.checkList(getResources().getStringArray(R.array.event_name_motor), getActivity());
			}
			return null;
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
