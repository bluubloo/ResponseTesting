package uni.apps.responsetesting.reminders;

import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class AlertClient {

	private static final String TAG = "AlertClient";
	private ScheduleService boundService;
	private Context context;
	private boolean isBound;
	
	public AlertClient(Context context){
		this.context = context;
	}
	
	public void doBindService(){
		Log.d(TAG, "doBindService()");
		context.bindService(new Intent(context, ScheduleService.class), connection, Context.BIND_AUTO_CREATE);
		isBound = true;
	}
	
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
	
	public void setAlarmForNotification(Calendar c){
		if(boundService == null)
			Log.d(TAG, "boundService is null");
		boundService.setAlarm(c);
	}
	
	public void doUnbindService(){
		if(isBound){
			context.unbindService(connection);
			isBound = false;
		}
	}
	
	public boolean isBound(){
		return isBound;
	}
	
}
