package uni.apps.responsetesting.reminders;

import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * This is the Client for the reminder services
 * 
 * @author Mathew Andea
 *
 */
public class AlertClient {

	//variables
	private static final String TAG = "AlertClient";
	private ScheduleService boundService;
	private Context context;
	private boolean isBound;
	
	//set context
	public AlertClient(Context context){
		this.context = context;
	}
	
	//binds service to context
	public void doBindService(){
		Log.d(TAG, "doBindService()");
		context.bindService(new Intent(context, ScheduleService.class), connection, Context.BIND_AUTO_CREATE);
		isBound = true;
	}
	
	//service connection
	private ServiceConnection connection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName className, IBinder service){
			Log.d(TAG, "onServiceConnected()");
			boundService = ((ScheduleService.ServiceBinder) service).getService();
		}
		@Override
		public void onServiceDisconnected(ComponentName className){
			boundService = null;
		}
	};
	
	//sets alarm
	public void setAlarmForNotification(Calendar c){
		if(boundService == null)
			Log.d(TAG, "boundService is null");
		boundService.setAlarm(c);
	}
	
	//unbinds service
	public void doUnbindService(){
		if(isBound){
			context.unbindService(connection);
			isBound = false;
		}
	}
	
	//gets isbound value
	public boolean isBound(){
		return isBound;
	}
	
}
