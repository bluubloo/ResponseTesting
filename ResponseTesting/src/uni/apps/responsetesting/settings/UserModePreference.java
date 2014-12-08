package uni.apps.responsetesting.settings;


import uni.apps.responsetesting.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;

/**
 * This dialogpreference if for the user mode selection
 * 
 * 
 * @author Mathew Andela
 *
 */
public class UserModePreference extends DialogPreference {
	
	//variables
	private RadioButton single;
	private RadioButton multi;
	private final boolean  default_value = true;
	private boolean current_value;
	
	public UserModePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		//sets dialog properties
		setDialogLayoutResource(R.layout.settings_user_mode);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		
		setDialogIcon(R.drawable.uni_logo);
	}

	@Override
	public void onBindDialogView(View view){
		super.onBindDialogView(view);
		
		//sets view up
		single = (RadioButton) view.findViewById(R.id.user_mode_single);
		multi = (RadioButton) view.findViewById(R.id.user_mode_multi);
		
		if(current_value)
			single.setChecked(true);
		else
			multi.setChecked(true);
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray attr, int index){
		//sets default value
		return attr.getBoolean(index, default_value);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue){
		//sets value
		if(restorePersistedValue)
			storeValue(getPersistedBoolean(current_value));
		else
			storeValue((Boolean) defaultValue);
	}

	private void storeValue(boolean value) {
		//sets value
		current_value = value;
		persistBoolean(current_value);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult){
		//sets value
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
