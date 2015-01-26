package uni.apps.responsetesting.mail;

import java.io.File;

import uni.apps.responsetesting.MainMenuActivity;
import uni.apps.responsetesting.MainMenuActivityTabbed;
import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.models.EmailTaskData;
import uni.apps.responsetesting.results.Results;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This thread sends the result email
 * 
 * 
 * @author Mathew Andela
 *
 */
public class EmailTask extends AsyncTask<EmailTaskData, Void, Boolean> {
	private Activity activity;
	private static final int NOTIFICATION = 123;
	private static final String TAG = "EmailTask";
	private NotificationManager manager;
	private boolean recent = false;


	@Override
	protected Boolean doInBackground(EmailTaskData... params) {
		//Info for the email
		activity = params[0].activity;
		String body = params[0].strings[0];
		String testName = params[0].strings[1];
		String PATH = params[0].strings[2];
		boolean sendViaIntent = params[0].sendViaIntent;
		manager = (NotificationManager) activity.getSystemService(Service.NOTIFICATION_SERVICE);

		if(params[0].strings[3].equals("All"))
			Results.resultsToCSV(activity);
		else if (params[0].strings[3].equals("Recent")) {
			Log.d(TAG, "RECENT");
			boolean recent = Results.resultsRecentToCSV(activity);
			Log.d(TAG, Boolean.toString(recent));
			if(!recent){
				this.recent = true;
				return false;
			}

		}

		if(sendViaIntent){
			return sendViaIntent(PATH, testName, body);
		} else{
			//to and froms
			String from = "activitytrackers@gmail.com";
			String to = "";
			//to addresses
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
			boolean single = prefs.getBoolean(activity.getResources().getString(R.string.pref_key_user), true);
			if(single)
				to = prefs.getString(activity.getResources().getString(R.string.pref_key_email), 
						activity.getResources().getString(R.string.setup_mode_default_email));
			else
				to = getMultiUserEmail(activity, prefs);
			Log.d("EMAIL", to);
			//set up email
			//sets up client
			MailClient mail = new MailClient(from, "50waikato");
			mail.setTo(new String[] {to});
			mail.setFrom(from);
			mail.setBody(body);
			mail.setSubject("Test " + testName + " - Results");
			try{
				//add attatchment to email
				mail.addAttachment(PATH);
				//send email
				return mail.send();

			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
	}

	private boolean sendViaIntent(String PATH, String testName, String body){
		String to = "";
		//to addresses
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		boolean single = prefs.getBoolean(activity.getResources().getString(R.string.pref_key_user), true);
		if(single)
			to = prefs.getString(activity.getResources().getString(R.string.pref_key_email), 
					activity.getResources().getString(R.string.setup_mode_default_email));
		else
			to = getMultiUserEmail(activity, prefs);

		File file = new File(PATH);
		Uri uri = Uri.fromFile(file);

		Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
		sendEmailIntent.setType("message/rfc822");
		sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { to });                
		sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test " + testName + " - Results");
		sendEmailIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendEmailIntent.putExtra(Intent.EXTRA_STREAM, uri);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			sendEmailIntent.setType(null); // If we're using a selector, then clear the type to null. I don't know why this is needed, but it doesn't work without it.
			final Intent restrictIntent = new Intent(Intent.ACTION_SENDTO);
			Uri data = Uri.parse("mailto:?to=" + to);
			restrictIntent.setData(data);
			restrictIntent.putExtra(Intent.EXTRA_SUBJECT, "Test " + testName + " - Results");
			restrictIntent.putExtra(Intent.EXTRA_TEXT, body);
			restrictIntent.putExtra(Intent.EXTRA_STREAM, uri);
			sendEmailIntent.setSelector(restrictIntent);
		}
		activity.startActivity(Intent.createChooser(sendEmailIntent, "Choose an Email client :"));
		return true;
	}

	//get multi user mode email
	private String getMultiUserEmail(Activity activity, SharedPreferences prefs) {
		return prefs.getString(activity.getResources().getString(R.string.pref_key_multi_email), 
				activity.getResources().getString(R.string.setup_mode_default_email));

	}

	@Override
	public void onPostExecute(Boolean done){
		if(done){
			notification("Email sending was successful.");
			DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
			db.updateMostRecent();
			db.updateMostRecentQuest();
		}
		else{
			if(!recent)				
				notification("Email sending was unsuccessful.");
			else
				notification("No new data found");
		}

	}

	private void notification(String value){

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(activity)
		.setSmallIcon(R.drawable.uni_logo)
		.setContentTitle("Email")
		.setContentText(value)
		.setAutoCancel(true)
		.setDefaults(Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(activity, MainMenuActivityTabbed.class);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
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
	}
}
