package uni.apps.responsetesting.results;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashMap;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.models.CorrectDurationInfo;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This class will handle anything related to the results of the taken tests.
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Results {

	private static final String TAG = "Results";

	//--------------------------------------------------------------------------------------------
	//Get Results

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
						results.getString(4) + ".\n";
			} while (results.moveToNext());
			return total;
		}
		return "";
	}

	//--------------------------------------------------------------------------------------------
	//INSERT Results

	public static void insertResult(String testName, String result, long time, Activity activity, String id){
		Resources r = activity.getResources();
		DatabaseHelper db = DatabaseHelper.getInstance(activity, r);
		String[] info = db.getSingleTries(testName, id);

		ContentValues values = new ContentValues();
		if(resultIsBetter(result, info[1], testName, r))
			values.put(r.getString(R.string.event_score), result);		
		values.put(r.getString(R.string.sent), 0);
		values.put(r.getString(R.string.tries), Integer.parseInt(info[0]));
		values.put(r.getString(R.string.all_scores), info[2] + "|" + result);

		if(Integer.parseInt(info[0]) == 1){
			values.put(r.getString(R.string.event_name), testName);
			values.put(r.getString(R.string.timestamp), time);
			values.put(r.getString(R.string.notes), "");
			values.put(r.getString(R.string.user_id), id);
			db.insert(values);
		} else {
			db.updateSingleTries(testName, values, id);
		}

	}

	private static boolean resultIsBetter(String result, String score, String testName, Resources r) {
		if(score.equals(""))
			return true;
		int indexP = result.indexOf('|');
		int indexC = score.indexOf('|');
		if(indexP != -1 && indexC != -1){
			double b = Double.parseDouble(score.substring(0, indexC));
			double a = Double.parseDouble(result.substring(0, indexP));
			if(a != b)
				return isBetter(a, b, false);
			else{
				double d = Double.parseDouble(score.substring(indexC + 1));
				double c = Double.parseDouble(result.substring(indexP + 1));
				return isBetter(c, d, true);
			}
		} else{
			double a = Double.parseDouble(result);
			double b = Double.parseDouble(score);
			if(r.getString(R.string.event_name_appear_obj).equals(testName) ||
					r.getString(R.string.event_name_appear_obj_fixed).equals(testName))
				return isBetter(a, b, true);
			else{
				return isBetter(a, b, false);
			}
				
		}
	}

	private static boolean isBetter(double newV, double oldV, boolean smaller){
		if(smaller)
			return newV < oldV;
		else
			return newV > oldV;
	}

	public static void insertResult(String eventName, int result,
			long time, Activity activity, String id) {
		insertResult(eventName, Integer.toString(result), time, activity, id);		
	}

	public static void insertResult(String eventName, double result,
			long time, Activity activity, String id) {
		insertResult(eventName, Double.toString(result), time, activity, id);	
	}

	public static void insertQuestionaireResult(String eventName, String[] durations, String ratings, 
			long time, Activity activity, String id){
		Resources r = activity.getResources();
		ContentValues values = new ContentValues();
		values.put(r.getString(R.string.timestamp), time);
		Log.d(TAG, Long.toString(time));
		values.put(r.getString(R.string.total_sleep), durations[0]);
		Log.d(TAG, durations[0]);
		values.put(r.getString(R.string.light_sleep), durations[1]);
		Log.d(TAG, durations[1]);
		values.put(r.getString(R.string.sound_sleep), durations[2]);
		Log.d(TAG, durations[2]);
		values.put(r.getString(R.string.ratings), ratings);
		Log.d(TAG, ratings);
		values.put(r.getString(R.string.sent), 0);
		values.put(r.getString(R.string.user_id), id);
		Log.d(TAG, id);
		values.put(r.getString(R.string.heart_rate), durations[3]);
		Log.d(TAG, durations[3]);
		DatabaseHelper db = DatabaseHelper.getInstance(activity, r);
		db.insertQuestionarie(values);
	}

	//--------------------------------------------------------------------------------------------
	//DELETE Results

	public static void deleteAllResults(Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		db.deleteAll();
	}

	//--------------------------------------------------------------------------------------------
	//UPDATE Results

	public static void updateNotes(String eventName, String notes, Activity activity) {
		Resources r = activity.getResources();
		DatabaseHelper db = DatabaseHelper.getInstance(activity, r);
		ContentValues values = new ContentValues();
		values.put(r.getString(R.string.notes), notes);
		String selection = r.getString(R.string.event_name) + "=? AND " 
				+ r.getString(R.string.timestamp) + "=?";
		db.updateSingle(selection,new String[] {eventName}, values);
	}

	//--------------------------------------------------------------------------------------------
	//Results to CSV

	public static void resultsToCSV(Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getAllResults();
		Cursor quest = db.getAllQuestionaireResults();
		resultsToCSV(results, quest, activity);
	}

	public static boolean resultsRecentToCSV(Activity activity) {
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getMostRecent();
		Cursor quest = db.getMostRecentQuestionaire();
		return resultsToCSV(results, quest, activity);
	}

	public static boolean resultsToCSV(final Cursor results, final Cursor quest, Activity activity){

		if(results.getCount() == 0 && quest.getCount() == 0)
			return false;

		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "Results.csv");
		try {
			HashMap<String, String> nameMap = cursorToMap(DatabaseHelper.getInstance(activity,
					activity.getResources()).getMultiUsers(), activity);

			FileWriter fw = new FileWriter(file);

			fw.append("Event Name");
			fw.append(',');
			fw.append("Score");
			fw.append(',');
			fw.append("Time");
			fw.append(',');
			fw.append("Extra Notes");
			fw.append(',');
			fw.append("Tries");
			fw.append(',');
			fw.append("All Scores");
			fw.append(',');
			fw.append("User Id");
			fw.append(',');
			fw.append("Username");
			fw.append("\n");

			if(results.moveToFirst()){
				do{
					Log.d(TAG, "sent =" + results.getString(3));
					fw.append(results.getString(0));
					fw.append(',');
					fw.append(results.getString(1));
					fw.append(',');
					/*Calendar c = Calendar.getInstance();
					c.setTimeInMillis(results.getLong(2));
					fw.append(c.getTime().toString());*/
					fw.append(Long.toString(results.getLong(2)));
					fw.append(',');
					fw.append(results.getString(4));
					fw.append(',');
					fw.append(results.getString(6));
					fw.append(',');
					fw.append(results.getString(7));
					fw.append(',');
					String id = results.getString(5);
					fw.append(id);
					fw.append(',');
					if(nameMap.containsKey(id))
						fw.append(nameMap.get(id));
					else
						fw.append("Unknown");
					fw.append("\n");
				} while(results.moveToNext());
			}

			fw.flush();
			fw.append("\n");
			fw.append("\n");
			fw.append("Time");
			fw.append(',');
			fw.append("Total Sleep");
			fw.append(',');
			fw.append("Light Sleep");
			fw.append(',');
			fw.append("Sound Sleep");
			fw.append(',');
			fw.append("Resting HR");
			fw.append(',');
			fw.append("Ratings");
			fw.append(',');
			fw.append("User Id");
			fw.append(',');
			fw.append("Username");
			fw.append("\n");

			if(quest.moveToFirst()){
			/*Calendar c = Calendar.getInstance();
				c.setTimeInMillis(quest.getLong(0));
				fw.append(c.getTime().toString());*/
				fw.append(Long.toString(quest.getLong(0)));
				fw.append(',');
				fw.append(quest.getString(1));
				fw.append(',');
				fw.append(quest.getString(2));
				fw.append(',');
				fw.append(quest.getString(3));
				fw.append(',');
				fw.append(quest.getString(7));
				fw.append(',');
				fw.append(quest.getString(4));
				fw.append(',');
				String id = quest.getString(6);
				fw.append(id);
				fw.append(',');
				if(nameMap.containsKey(id))
					fw.append(nameMap.get(id));
				else
					fw.append("Unknown");
				fw.append("\n");
			}

			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return true;
	}

	private static HashMap<String, String> cursorToMap(Cursor cursor, Activity activity) {
		HashMap<String, String> tmp = new HashMap<String, String>();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		tmp.put("single", prefs.getString(activity.getResources().getString(R.string.pref_key_user_name),
				activity.getResources().getString(R.string.setup_mode_default_user_name_single)));
		if(cursor.moveToFirst()){
			do{
				if(!tmp.containsKey(cursor.getString(2)))
					tmp.put(cursor.getString(2), cursor.getString(1));
			} while(cursor.moveToNext());
		}
		return tmp;
	}


	//--------------------------------------------------------------------------------------------
	//Auxiliary Methods

	public static double[] getResults(CorrectDurationInfo[] results) {
		double time = 0;
		double correct = 0;
		for(CorrectDurationInfo c: results){
			if(c.getResult())
				correct++;
			time += c.getDuration();
		}
		return new double[] {correct, time / results.length};
	}
}
