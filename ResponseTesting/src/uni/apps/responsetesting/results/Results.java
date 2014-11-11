package uni.apps.responsetesting.results;

import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
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
	
	public static void insertResult(String testName, String result, long time, Activity activity){
		Resources r = activity.getResources();
		ContentValues values = new ContentValues();
		values.put(r.getString(R.string.event_name), testName);
		values.put(r.getString(R.string.event_score), result);
		values.put(r.getString(R.string.timestamp), time);
		values.put(r.getString(R.string.notes), "");
		values.put(r.getString(R.string.sent), 0);
		DatabaseHelper db = DatabaseHelper.getInstance(activity, r);
		db.insert(values);
	}
	
}
