package uni.apps.responsetesting.mail;

import uni.apps.responsetesting.models.EmailTaskData;
import android.app.Activity;
import android.os.AsyncTask;
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
		//TODO
		String to = "matcour@windowslive.com";
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

}
