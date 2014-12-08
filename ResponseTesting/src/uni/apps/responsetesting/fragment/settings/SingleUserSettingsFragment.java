package uni.apps.responsetesting.fragment.settings;

import uni.apps.responsetesting.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * This fragment deals with single user mode setup
 * 
 * 
 * @author Mathew Andela
 *
 */
public class SingleUserSettingsFragment extends PreferenceFragment {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//sets up settings ui
		addPreferencesFromResource(R.xml.setup_mode);

	}
}
