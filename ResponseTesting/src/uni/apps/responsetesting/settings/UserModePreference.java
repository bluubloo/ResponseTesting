package uni.apps.responsetesting.settings;


import uni.apps.responsetesting.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;

public class UserModePreference extends DialogPreference {
	private RadioButton single;
	private RadioButton multi;
	private final boolean  default_value = true;
	private boolean current_value;
	
	public UserModePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.settings_user_mode);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		
		setDialogIcon(R.drawable.uni_logo);
	}

	@Override
	public void onBindDialogView(View view){
		super.onBindDialogView(view);
		
		single = (RadioButton) view.findViewById(R.id.user_mode_single);
		multi = (RadioButton) view.findViewById(R.id.user_mode_multi);
		
		if(current_value)
			single.setChecked(true);
		else
			multi.setChecked(true);
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray attr, int index){
		return attr.getBoolean(index, default_value);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue){
		if(restorePersistedValue)
			storeValue(getPersistedBoolean(current_value));
		else
			storeValue((Boolean) defaultValue);
	}

	private void storeValue(boolean value) {
		current_value = value;
		persistBoolean(current_value);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult){
		if(positiveResult){
			boolean tmp = true;
			if(single.isChecked())
				tmp = true;
			else
				tmp = false;
			storeValue(tmp);
		}			
	}
}
