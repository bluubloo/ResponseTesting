package uni.apps.responsetesting.reminders;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * This service sets the alarm
 * 
 * 
 * @author Mathew Andela
 *
 */
public class ScheduleService extends Service {

	//variables
	private final IBinder binder = new ServiceBinder();
	private static final String TAG = "ScheduleService";
	
	public class ServiceBinder extends Binder{
		ScheduleService getService(){
			return ScheduleService.this;
		}
	}	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i(TAG, "Recieved start id " + startId + ": " + intent);
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	//sets alarm
	public void setAlarm(Calendar c){
		new AlarmTask(this, c).run();
	}

}
