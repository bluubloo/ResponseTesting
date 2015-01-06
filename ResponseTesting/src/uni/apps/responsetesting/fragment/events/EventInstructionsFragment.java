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
	//NOTE: below two arrays need to be in the same order as the event name list
	private static final int[] finalImageIds = new int[] {R.drawable.tmp1, R.drawable.tmp2, R.drawable.tmp3,
		R.drawable.tmp4, R.drawable.tmp5, R.drawable.card_club_1, R.drawable.card_club_2, 
		R.drawable.card_club_3, R.drawable.card_club_4, R.drawable.card_club_5,	R.drawable.card_club_6,
		R.drawable.card_club_7, R.drawable.card_club_8, R.drawable.card_club_9};
	private static final int[] finalThumbnailIds = new int[] {R.drawable.tmp_thumb, R.drawable.tmp_thumb2,
		R.drawable.tmp_thumb, R.drawable.tmp_thumb2, R.drawable.tmp_thumb, R.drawable.tmp_thumb2,
		R.drawable.tmp_thumb, R.drawable.tmp_thumb2, R.drawable.tmp_thumb, R.drawable.tmp_thumb2,	
		R.drawable.tmp_thumb, R.drawable.tmp_thumb2, R.drawable.tmp_thumb, R.drawable.tmp_thumb2};
	private int[] imageIds;
	private int[] thumbnails;

	private Gallery gallery;
	private ImageView largeImage;
	public int biggerImageAtPosition;


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
		//imageIds = getImages(finalImageIds);
		//thumbnails = getImages(finalThumbnailIds);
		thumbnails = finalThumbnailIds;
		imageIds = finalImageIds;
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
		
		//set up images for instructions
		largeImage = (ImageView) view.findViewById(R.id.large_image);
		gallery = (Gallery) view.findViewById(R.id.gallery);
		
		if(eventName.equals("Questionaire")){
			//gets rid of images for questionaire
			largeImage.setVisibility(View.GONE);
			gallery.setVisibility(View.GONE);
		} else{
			//sets image adpaters and values
			largeImage.setBackground(getResources().getDrawable(imageIds[counter]));
			gallery.setAdapter(new ImageAdapter(getActivity()));
			gallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					largeImage.setBackground(getResources().getDrawable(imageIds[position]));
					counter = position;
					((ImageAdapter) gallery.getAdapter()).makeBigger(position);
				}

			});
			
			//swipe detector
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
			//TODO alter min max positions
			switch(j){
			case 1:
			case 2:
				return getSnipit(0, 1, ids);
			case 3:
				return getSnipit(2, 3, ids);
			case 4:
				return getSnipit(4, 5, ids);
			case 5:
				return getSnipit(6, 7, ids);
			case 6:
				return getSnipit(8, 9, ids);
			case 7:
				return getSnipit(10, 11, ids);
			case 8:
				return getSnipit(12, 13, ids);
			case 9:
				return getSnipit(14, 15, ids);
			case 10:
				return getSnipit(16, 17, ids);
			case 11:
				return getSnipit(18, 19, ids);
			}
		}
		return new int[0];
	}

	//gets subset of images
	private int[] getSnipit(int i, int j, int[] ids) {
		int[] tmp = new int[j - i];
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
			if(counter < imageIds.length - 1){
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
		largeImage.setBackground(getResources().getDrawable(imageIds[counter]));
		gallery.setSelection(counter);
		((ImageAdapter) gallery.getAdapter()).makeBigger(counter);
		return true;
	}
}
