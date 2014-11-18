package uni.apps.responsetesting.results;

import java.net.URLEncoder;

import uni.apps.responsetesting.utils.ActivityUtilities;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
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
		String body = "Hi\n\nAttached is the Test Results\n\nCheers\nResponse Testing";
		if(all){
			Results.resultsToCSV(activity);
			sendEmail(activity, body, "All");
		}
		else{
			boolean recent = Results.resultsRecentToCSV(activity);
			if(recent)
				sendEmail(activity, body, "Recent");
			else
				ActivityUtilities.displayResults(activity, "Email Attempt", "All recent results have already been sent.\n" +
			"Please select 'Send All Results' if you still wish to send them.");
		}
	}

	public static void sendResults(Activity activity, String testName){
		sendEmail(activity, Results.getSingleResults(activity, testName), testName);	
	}

	public static void sendEmail(Activity activity, String body, String testName)
	{
		try{
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			String fileName = URLEncoder.encode("Results.csv", "UTF-8");
			String PATH =  Environment.getExternalStorageDirectory()+"/"+fileName.trim().toString();
			Uri uri = Uri.parse("file://"+PATH);
			//TODO Recipient mail address
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"matcour@windowslive.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, "Test " + testName + " - Results");
			i.putExtra(Intent.EXTRA_TEXT   , body);
			i.putExtra(Intent.EXTRA_STREAM, uri);
/*		
 * TODO Get info for mail client
 * TODO Test mail client	
			String from = "";
			String to = "";
			MailClient mail = new MailClient(from, "");
			mail.setTo(new String[] {to});
			mail.setFrom(from);
			mail.setBody(body);
			mail.setSubject("Test " + testName + " - Results");
			try{
				mail.addAttachment(PATH);
				if(mail.send())
					Toast.makeText(activity, "Email sent successfully", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(activity, "Email was not sent", Toast.LENGTH_LONG).show();
				
			}catch(Exception e){
				Log.e("EMAIL ATTEMPT", e.getLocalizedMessage());
			}*/
			
			try {
				activity.startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		} catch(Exception e){
			Log.e("ERROR", e.getMessage());
		}
	}
}
