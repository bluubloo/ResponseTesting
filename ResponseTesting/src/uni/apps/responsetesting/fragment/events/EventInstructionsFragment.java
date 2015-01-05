package uni.apps.responsetesting.fragment.events;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.EventInstructionsListener;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
	
	//TODO add final image files
	private static final int[] finalImageIds = new int[] {R.drawable.card_club_1, R.drawable.card_club_2, 
			R.drawable.card_club_3, R.drawable.card_club_4, R.drawable.card_club_5,	R.drawable.card_club_6,
			R.drawable.card_club_7, R.drawable.card_club_8, R.drawable.card_club_9};
	private int[] imageIds;

	private Gallery gallery;
	private ImageView largeImage;
	
	
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
		//TODO
	//	imageIds = getImages();
		imageIds = finalImageIds;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.instructions_fragment, container, false);
		//sets view values
		String info = ActivityUtilities.getEventInfo(eventName, getResources());
		TextView infoTextView = (TextView) view.findViewById(R.id.instuct_info);
		infoTextView.setText(info);
		TextView eventNameTextView = (TextView) view.findViewById(R.id.instruct_event_name);
		eventNameTextView.setText(eventName);
		setUpButtons(view);
		largeImage = (ImageView) view.findViewById(R.id.large_image);
		gallery = (Gallery) view.findViewById(R.id.gallery);
		if(eventName.equals("Questionaire")){
			largeImage.setVisibility(View.GONE);
			gallery.setVisibility(View.GONE);
		} else{
			largeImage.setBackground(getResources().getDrawable(imageIds[counter]));
			gallery.setAdapter(new ImageAdapter(getActivity()));
			gallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					largeImage.setBackground(getResources().getDrawable(imageIds[position]));
					counter = position;
				}

			});

			final GestureDetector gdt = new GestureDetector(getActivity(), new GestureListener());
			largeImage.setOnTouchListener(new OnTouchListener(){

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
	
	private int[] getImages() {
		String[] names = getResources().getStringArray(R.array.event_name_array);
		int j = -1;
		for(int i = 0; i < names.length; i++)
			if(names[i].equals(eventName)){
				j = i;
				break;
			}
				
		if(j != -1 || j != 0){
			//TODO alter min max positions
			switch(j){
			case 1:
			case 2:
				return getSnipit(0, 1);
			case 3:
				return getSnipit(2, 3);
			case 4:
				return getSnipit(4, 5);
			case 5:
				return getSnipit(6, 7);
			case 6:
				return getSnipit(8, 9);
			case 7:
				return getSnipit(10, 11);
			case 8:
				return getSnipit(12, 13);
			case 9:
				return getSnipit(14, 15);
			case 10:
				return getSnipit(16, 17);
			case 11:
				return getSnipit(18, 19);
			}
		}
		return new int[0];
	}

	private int[] getSnipit(int i, int j) {
		int[] tmp = new int[j - i];
		for(int n = i, k = 0; n <= j; n++, k++)
			tmp[k] = finalImageIds[n];
		return tmp;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context context;
		private int itemBackground;
		public ImageAdapter(Context c)
		{
			context = c;
			// sets a grey background; wraps around the images
			TypedArray a = getActivity().obtainStyledAttributes(R.styleable.MyGallery);
			itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
			a.recycle();
		}
		// returns the number of images
		public int getCount() {
			return imageIds.length;
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
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(imageIds[position]);
			imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
			imageView.setBackgroundResource(itemBackground);
			return imageView;
		}
	}

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(counter > 0){
					counter --;
					largeImage.setBackground(getResources().getDrawable(imageIds[counter]));
					gallery.setSelection(counter);
					return true; // Right to left
				}            	
			}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				if(counter < imageIds.length - 1){
					counter++;
					largeImage.setBackground(getResources().getDrawable(imageIds[counter]));
					gallery.setSelection(counter);
					return true; // Left to right
				}
			}

			if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				return false; // Bottom to top
			}  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				return false; // Top to bottom
			}
			return false;
		}
	}
}
