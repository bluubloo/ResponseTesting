package uni.apps.responsetesting.settings;

import uni.apps.responsetesting.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class ReminderTimePreference extends DialogPreference {

	private TimePicker time;
	private final String default_value = "9:00";
	private int current_hour;
	private int current_min;
	private String current_value;
	
	public ReminderTimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.settings_reminder_time);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		
		setDialogIcon(R.drawable.uni_logo);
	}

	@Override
	public void onBindDialogView(View view){
		super.onBindDialogView(view);
		
		time = (TimePicker) view.findViewById(R.id.remind_time);
		time.setIs24HourView(true);
		time.setCurrentHour(current_hour);
		time.setCurrentMinute(current_min);
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray attr, int index){
		return default_value;
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue){
		if(restorePersistedValue)
			storeValue(getPersistedString(current_value));
		else
			storeValue((String) defaultValue);
	}

	private void storeValue(String value) {
		current_value = value;
		getCurrentHourMin(value);
		persistString(current_value);
	}
	
	private void getCurrentHourMin(String value) {
		int index = value.indexOf(":");
		if(index != -1){
			current_hour = Integer.parseInt(value.substring(0, index));
			current_min = Integer.parseInt(value.substring(index + 1));
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult){
		if(positiveResult){
			current_hour = time.getCurrentHour();
			current_min = time.getCurrentMinute();
			String tmp = Integer.toString(current_hour) + ":" + Integer.toString(current_min);
			storeValue(tmp);
		}			
	}
	
}
