package uni.apps.responsetesting.fragment.events;

import java.util.Locale;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.EventInstructionsListener;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This fragment shows the instructions for the events
 * 
 * 
 * @author Mathew Andela
 *
 */
@SuppressWarnings("deprecation")
public class EventInstructionsFragment extends Fragment {

	//variables
	private static final String TAG = "EventInstructionsFragment";
	private String eventName = "";
	private int counter = 0;
	private EventInstructionsListener listener;

	//NOTE: below array need to be in the same order as the event name list
	private static final int[] finalThumbnailIds = new int[] {R.drawable.appearingobject1_small,
		R.drawable.arrowignoring1_small, R.drawable.changingdirections1_small, R.drawable.changingdirections2_small, 
		R.drawable.chasetest1_small, R.drawable.evenorvowel1_small, R.drawable.evenorvowel2_small,
		R.drawable.fingertaptest1_small, R.drawable.monkeyladder1_small, R.drawable.onecardlearningtest1_small,
		R.drawable.patternrecreation1_small, R.drawable.strooptest1_small, R.drawable.strooptest2_small};
	private int[] thumbnails;

	private Gallery gallery;
	private WebView web;
	private int currentImageNum = 1;


	public EventInstructionsFragment(){}

	//sets event name 
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
		thumbnails = getImages(finalThumbnailIds);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.instructions_fragment, container, false);
		
		//sets view values		
		TextView infoTextView = (TextView) view.findViewById(R.id.instuct_info);
		infoTextView.setText(ActivityUtilities.getEventInfo(eventName, getResources()));
		TextView eventNameTextView = (TextView) view.findViewById(R.id.instruct_event_name);
		eventNameTextView.setText(eventName);
		setUpButtons(view);
		
		web = (WebView) view.findViewById(R.id.webView1);
		web.setBackgroundColor(Color.TRANSPARENT);
		//set up images for instructions
		gallery = (Gallery) view.findViewById(R.id.gallery);
		
		if(eventName.equals("Questionaire")){
			//gets rid of images for questionaire
			gallery.setVisibility(View.GONE);
			web.setVisibility(View.GONE);
		} else{
			//sets image adpaters and values
			setWebImage(currentImageNum);
			gallery.setAdapter(new ImageAdapter(getActivity()));
			gallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					counter = position;
					((ImageAdapter) gallery.getAdapter()).makeBigger(position);
					setWebImage(counter + 1);
				}

			});
			
			//swipe detector
			final GestureDetector gdt = new GestureDetector(getActivity(), new GestureListener());
			
			web.setOnTouchListener(new OnTouchListener(){
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					gdt.onTouchEvent(event);
					v.performClick();
					return true;
				}
				
			});
		}
		return view;
	}
	
	private void setWebImage(int i){
		web.loadDataWithBaseURL("file:///android_res/drawable/", "<img src='" + getImageName(i) + "' />", 
				"text/html", "utf-8", null);
	}

	private String getImageName(int i) {
		String name;
		Locale l = Locale.getDefault();
		if(eventName.equals(getResources().getString(R.string.event_name_appear_obj_fixed)))
			name = getResources().getString(R.string.event_name_appear_obj);
		else
			name = eventName;
		String start = "";
		for(char c : name.toCharArray())
			if(c != ' ')
				start += c;
		String file = start.toLowerCase(l) + i + ".gif";
		Log.d(TAG, file);
		return file;
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
		//sets button click events
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

	//gets sub list of images
	private int[] getImages(int[] ids) {
		//gets event position in name list
		String[] names = getResources().getStringArray(R.array.event_name_array);
		int j = -1;
		for(int i = 0; i < names.length; i++)
			if(names[i].equals(eventName)){
				j = i;
				break;
			}

		//gets sub list according to position in list
		if(j != -1 || j != 0){
			switch(j){
			case 1:
			case 2:
				return getSnipit(0, 0, ids);
			case 3:
				return getSnipit(1, 1, ids);
			case 4:
				return getSnipit(2, 3, ids);
			case 5:
				return getSnipit(4, 4, ids);
			case 6:
				return getSnipit(5, 6, ids);
			case 7:
				return getSnipit(7, 7, ids);
			case 8:
				return getSnipit(8, 8, ids);
			case 9:
				return getSnipit(9, 9, ids);
			case 10:
				return getSnipit(10, 10, ids);
			case 11:
				return getSnipit(11, 12, ids);
			}
		}
		return new int[0];
	}

	//gets subset of images
	private int[] getSnipit(int i, int j, int[] ids) {
		int[] tmp = new int[j - i + 1];
		for(int n = i, k = 0; n <= j; n++, k++)
			tmp[k] = ids[n];
		return tmp;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context context;
		private int itemBackground;
		private int selectedIndex = 0;
		public ImageAdapter(Context c)
		{
			context = c;
			// sets a grey background; wraps around the images
			TypedArray a = getActivity().obtainStyledAttributes(R.styleable.MyGallery);
			//itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
			//itemBackground = R.drawable.layout_border;
			a.recycle();
		}
		// returns the number of images
		public int getCount() {
			return thumbnails.length;
		}
		// returns the ID of an item
		public Object getItem(int position) {
			return position;
		}
		// returns the ID of an item
		public long getItemId(int position) {
			return position;
		}
		// returns an ImageView view
		public View getView(int position, View convertView, ViewGroup parent) {
			//creates view and sets values
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(thumbnails[position]);
			if (selectedIndex == position)
				imageView.setPadding(5, 0, 5, 0);
			else
				imageView.setPadding(5, 10, 5, 10);
			imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
			imageView.setBackgroundResource(itemBackground);
			return imageView;
		}

		//sets center thumbnail
		public void makeBigger(int position){
			selectedIndex = position;
			notifyDataSetChanged();
		}
	}

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Right to left
				return alterImage(true);
			}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Left to right
				return alterImage(false);
			}

			if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				return false; // Bottom to top
			}  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				return false; // Top to bottom
			}
			return false;
		}
	}

	//alters image displayed
	private boolean alterImage(boolean up){
		//checks direction going in
		if(up){
			//checks array bounds
			if(counter < thumbnails.length - 1){
				//increments counter
				counter += 1;
				return alterImageCommon();
			}
		}
		else
			//checks array bounds
			if(counter > 0){
				//decrements counter
				counter -= 1;
				return alterImageCommon();
			}  
		return false;
	}

	//alters image displayed
	//sets new image, gallery selection and new center gallery value
	private boolean alterImageCommon(){
		gallery.setSelection(counter);
		((ImageAdapter) gallery.getAdapter()).makeBigger(counter);
		setWebImage(counter + 1);
		return true;
	}
}
