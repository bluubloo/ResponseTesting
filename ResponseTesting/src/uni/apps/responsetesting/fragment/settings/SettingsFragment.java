package uni.apps.responsetesting.fragment.settings;

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

		//sets up settings ui
		addPreferencesFromResource(R.xml.all_settings);

	}
}
