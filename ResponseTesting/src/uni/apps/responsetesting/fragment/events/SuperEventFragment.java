package uni.apps.responsetesting.fragment.events;

import android.app.Fragment;
import android.os.Bundle;

public class SuperEventFragment extends Fragment {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
	}
}
