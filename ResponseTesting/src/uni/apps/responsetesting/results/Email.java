package uni.apps.responsetesting.results;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * This class retrieves results from the database and sends it to the specified email address
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Email {

	public static void sendResults(Activity activity, boolean all){
		if(all)
			sendEmail(activity, Results.getAllResults(activity), "All");
		else
			sendEmail(activity, Results.getRecentResults(activity), "Recent");
	}

	public static void sendResults(Activity activity, String testName){
		sendEmail(activity, Results.getSingleResults(activity, testName), testName);	
	}

	private static void sendEmail(Activity activity, String body, String testName){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		//TODO Recipient mail address
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Test " + testName + " - Results");
		i.putExtra(Intent.EXTRA_TEXT   , body);
		try {
			activity.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}
