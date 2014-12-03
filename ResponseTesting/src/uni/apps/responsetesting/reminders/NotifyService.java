package uni.apps.responsetesting.reminders;

import uni.apps.responsetesting.MainMenuActivity;
import uni.apps.responsetesting.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifyService extends Service {

	private static final int NOTIFICATION = 123;
	public static final String INTENT_NOTIFY = "uni.apps.responsetesting.reminders.INTENT_NOTIFY";
	private static final String TAG = "NotifyService";
	private NotificationManager manager;
	private final IBinder binder =  new ServiceBinder();
	
	private class ServiceBinder extends Binder{
		NotifyService getService(){
			return NotifyService.this;
		}
	}
	
	@Override
	public void onCreate(){
		Log.i(TAG, "onCreate()");
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.i(TAG, "Recieved start id " + startId + ": " + intent);
		if(intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification();
		return START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {		
		return binder;
	}
	
	private void showNotification(){
		//builds notification
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(this)
				        .setSmallIcon(R.drawable.uni_logo)
				        .setContentTitle("Reminder")
				        .setContentText("Remember to do the tests in this app")
				        .setAutoCancel(true)
				        .setDefaults(Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, MainMenuActivity.class);
				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(MainMenuActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				
				// mId allows you to update the notification later on.
				manager.notify(NOTIFICATION, mBuilder.build());
				//stops the service
				stopSelf();
	}

}
