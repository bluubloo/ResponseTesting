package uni.apps.responsetesting.mail;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.models.EmailTaskData;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This thread sends the result email
 * 
 * 
 * @author Mathew Andela
 *
 */
public class EmailTask extends AsyncTask<EmailTaskData, Void, Boolean> {

	@Override
	protected Boolean doInBackground(EmailTaskData... params) {
		//Info for the email
		String body = params[0].strings[0];
		String testName = params[0].strings[1];
		String PATH = params[0].strings[2];
		Activity activity = params[0].activity;
		
		String from = "activitytrackers@gmail.com";
		String to = "";
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		boolean single = prefs.getBoolean(activity.getResources().getString(R.string.pref_key_user), true);
		if(single)
			to = prefs.getString(activity.getResources().getString(R.string.pref_key_email), 
					activity.getResources().getString(R.string.setup_mode_default_email));
		else
			to = getMultiUserEmail(activity, prefs);
		Log.d("EMAIL", to);
		//set up email
		MailClient mail = new MailClient(from, "50waikato");
		mail.setTo(new String[] {to});
		mail.setFrom(from);
		mail.setBody(body);
		mail.setSubject("Test " + testName + " - Results");
		try{
			//add attatchment to email
			mail.addAttachment(PATH);
			//send email
			if(mail.send())
				Toast.makeText(activity, "Email sent successfully", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(activity, "Email was not sent", Toast.LENGTH_LONG).show();

		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String getMultiUserEmail(Activity activity, SharedPreferences prefs) {
		return prefs.getString(activity.getResources().getString(R.string.pref_key_multi_email), 
				activity.getResources().getString(R.string.setup_mode_default_email));
	
	}

}
