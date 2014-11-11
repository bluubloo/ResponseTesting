package uni.apps.responsetesting.results;

import java.util.Calendar;

import uni.apps.responsetesting.database.DatabaseHelper;
import android.app.Activity;
import android.database.Cursor;

/**
 * This class will handle anything related to the results of the taken tests.
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Results {

	//--------------------------------------------------------------------------------------------
	//Get Results
	
	public static String getRecentResults(Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getMostRecent();
		return retrieveString(results);
	}
	
	public static String getAllResults(Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getAllResults();
		return retrieveString(results);
	}
	
	public static String getSingleResults(Activity activity, String testName) {
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getSingle(testName);
		return retrieveString(results);
	}
	
	private static String retrieveString(Cursor results){
		if(results.moveToFirst()){
			String total = "Results: \n";
			do{
				total += "Event = " + results.getString(0) + ". Score = " + results.getString(1); 
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(results.getLong(2));
				total += ". Time = " + c.getTime().toString() + ". Extra notes = " +
						results.getString(3) + ".\n";
			} while (results.moveToNext());
			return total;
		}
		return "";
	}

	//--------------------------------------------------------------------------------------------
	//INSERT Results
	
	
	
}
