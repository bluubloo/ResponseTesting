package uni.apps.responsetesting.results;

import uni.apps.responsetesting.models.EmailTaskData;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class EmailTask extends AsyncTask<EmailTaskData, Void, Boolean> {

	@Override
	protected Boolean doInBackground(EmailTaskData... params) {
		String body = params[0].strings[0];
		String testName = params[0].strings[1];
		String PATH = params[0].strings[2];
		Activity activity = params[0].activity;
		
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
		//Log.e("EMAIL ATTEMPT", e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
