package uni.apps.responsetesting.fragment.settings;

import uni.apps.responsetesting.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SingleUserSettingsFragment extends PreferenceFragment {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//sets up settings ui
		addPreferencesFromResource(R.xml.setup_mode);

	}
}
