package uni.apps.responsetesting.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.models.EmailTaskData;
import uni.apps.responsetesting.results.Results;
import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * This class retrieves results from the database and sends it to the specified email address
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Email {

	//sends results via email
	public static void sendResults(Activity activity, boolean all, boolean sendViaIntent){
		String body = "Hi\n\nAttached is the Test Results\n\nCheers\nResponse Testing";
		//decides what values to send
		if(all){
			Results.resultsToCSV(activity);
			sendEmail(activity, body, "All", sendViaIntent);
		}
		else{
			boolean recent = Results.resultsRecentToCSV(activity);
			if(recent){
				sendEmail(activity, body, "Recent", sendViaIntent);
			}
			else
				ActivityUtilities.displayResults(activity, "Email Attempt", "All recent results have already been sent.\n" +
						"Please select 'Send All Results' if you still wish to send them.");
		}
	}

	public static void sendResults(Activity activity, String testName, boolean sendViaIntent){
		sendEmail(activity, Results.getSingleResults(activity, testName), testName, sendViaIntent);	
	}

	public static void sendEmail(Activity activity, String body, String testName, boolean sendViaIntent) 
	{
		//gets file
		String fileName = "";
		String userName = ActivityUtilities.getName(activity);
		try {
			fileName = URLEncoder.encode(userName + "_Results.csv", "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String PATH =  Environment.getExternalStorageDirectory()+"/"+fileName.trim().toString();

		if(sendViaIntent){
			sendViaIntent(activity, PATH, testName, body);
		} else{
			//executes email task
			EmailTask task = new EmailTask();
			task.execute(new EmailTaskData(new String[]{body, "Test " + testName + " - Results", PATH},
					activity));
		}
	}
	


	private static void sendViaIntent(Activity activity, String PATH, String testName, String body){
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
	}
	
	private static String getMultiUserEmail(Activity activity, SharedPreferences prefs) {
		return prefs.getString(activity.getResources().getString(R.string.pref_key_multi_email), 
				activity.getResources().getString(R.string.setup_mode_default_email));
	
	}
}
