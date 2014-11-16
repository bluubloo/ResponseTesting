package uni.apps.responsetesting.fragment;

import uni.apps.responsetesting.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * This fragment deals with displaying and updating the applications preferences
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SettingsFragment extends PreferenceFragment {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//check for settings values
		String settings = this.getArguments().getString("settings_group");
		//sets up settings ui
		if("all_settings".equals(settings)){
			addPreferencesFromResource(R.xml.all_settings);
		}
	}
}
