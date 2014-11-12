package uni.apps.responsetesting.fragment.events;

import java.util.Calendar;
import java.util.Random;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.interfaces.listener.AppearingObjectImageClickListener;
import uni.apps.responsetesting.interfaces.listener.MainMenuListener;
import uni.apps.responsetesting.models.AppearingObjectInfo;
import uni.apps.responsetesting.results.Results;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * This fragment performs all actions required for the Appearing Object test
 * 
 * 
 * @author Mathew Andela
 *
 */
public class AppearingObjectFragment extends Fragment implements AppearingObjectImageClickListener{


	private static final String TAG = "AppearingObjectFragment";
	private static final String eventName = "Appearing Object";
	private AppearingObjectImageClickListener listener;
	private TextView startTextView;
	private Handler timerHandler = new Handler();
	private ImageView[] clickableImageView = new ImageView[5];
	private AppearingObjectInfo[] data = new AppearingObjectInfo[5];
	private int counter = 0;
	private int imageCounter = 0;
	private boolean running = false;
	private Random random = new Random();

	//-----------------------------------------------------------------------------
	//RUNNABLES

	private Runnable timerRunnableImageAppear = new Runnable(){

		@Override
		public void run() {
			imageCounter = (random.nextInt(5) % 5);
			if(running &&counter < 5){
				clickableImageView[imageCounter].setVisibility(View.VISIBLE);
				data[counter] = new AppearingObjectInfo(Calendar.getInstance().getTimeInMillis());
			} else {
				endTest();
			}
		}
	};

	//-----------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);
		try {
			listener = (AppearingObjectImageClickListener) this;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + 
					" must implement AppearingObjectImageClickListener");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.appearing_object_fragment, container, false);
		startTextView = (TextView) view.findViewById(R.id.appear_obj_start);
		setUpImageClickEvents(view);
		

		RelativeLayout frame = (RelativeLayout) view.findViewById(R.id.appear_obj_container);
		frame.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int delay = (random.nextInt(3) + 1) * 1000;
				if(!running){
					counter = 0;
					running = !running;
					for(ImageView i: clickableImageView)
						i.setVisibility(View.INVISIBLE);
					timerHandler.postDelayed(timerRunnableImageAppear, delay);
				}
			}

		});
		return view;
	}


	private void setUpImageClickEvents(View view) {
		// TODO Auto-generated method stub
		clickableImageView[0] = (ImageView) view.findViewById(R.id.appear_obj_image_1);
		clickableImageView[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[1] = (ImageView) view.findViewById(R.id.appear_obj_image_2);
		clickableImageView[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[2] = (ImageView) view.findViewById(R.id.appear_obj_image_3);
		clickableImageView[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[3] = (ImageView) view.findViewById(R.id.appear_obj_image_4);
		clickableImageView[3].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
		clickableImageView[4] = (ImageView) view.findViewById(R.id.appear_obj_image_5);
		clickableImageView[4].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				listener.onClick();
			}

		});
	}

	private void endTest(){
		if(running){
			counter = 0;
			running = !running;
			double average = getAverage();
			Results.insertResult(eventName, Double.toString(average),
					Calendar.getInstance().getTimeInMillis(), getActivity());
			new AlertDialog.Builder(getActivity())
			.setTitle(eventName + " Test Complete")
			.setMessage("Average Time to Click Image = " + Double.toString(average) + "s")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
				}
			})
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
		}
	}

	private double getAverage() {
		double tmp = 0;
		for(AppearingObjectInfo a: data)
			tmp += a.getDuration();
		return tmp / data.length / 1000;
	}

	@Override
	public void onClick() {
		int delay = (random.nextInt(3) + 1) * 1000;
		if (running && counter < 4){
			data[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
			counter ++;
			clickableImageView[imageCounter].setVisibility(View.INVISIBLE);
			timerHandler.postDelayed(timerRunnableImageAppear, delay);
		} else if (running && counter == 4){
			data[counter].addEndTime(Calendar.getInstance().getTimeInMillis());
			counter ++;
			endTest();
		} else{
			endTest();
		}
	}
}
