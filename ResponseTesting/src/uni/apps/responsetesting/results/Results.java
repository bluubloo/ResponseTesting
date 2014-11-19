package uni.apps.responsetesting.results;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import uni.apps.responsetesting.R;
import uni.apps.responsetesting.database.DatabaseHelper;
import uni.apps.responsetesting.models.CorrectDurationInfo;
import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
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

	public static String[][] getResultsForInApp(Activity activity){
		Resources r = activity.getResources();
		ArrayList<String> nonBestList = new ArrayList<String>();
		nonBestList.addAll(Arrays.asList(r.getStringArray(R.array.non_numeric_events)));
		DatabaseHelper db = DatabaseHelper.getInstance(activity, r);
		String[] events = r.getStringArray(R.array.event_name_array);
		ArrayList<ArrayList<String>> tmp = new ArrayList<ArrayList<String>>();
		for(String s: events){
			ArrayList<String> tmp2 = new ArrayList<String>();
			tmp2.add("Recent:");
			Cursor results = db.getSingle(s);
			tmp2.addAll(getInAppResultList(results));

			//TODO RESULTS FIX
			if(!nonBestList.contains(s)){
				tmp2.add("Best:");
				results = db.getSingleBest(s);
				tmp2.addAll(getInAppResultList(results));
			}			

			tmp.add(tmp2);
		}
		String[][] list = listToArray(tmp);
		return list;
	}


	private static String[][] listToArray(ArrayList<ArrayList<String>> list){
		int i = 0;
		String[][] newList = new String[list.size()][8];
		for(ArrayList<String> a : list){
			String[] tmp = new String[a.size()];
			int j = 0; 
			for(String s : a){
				tmp[j] = s;
				j++;
			}
			newList[i] = tmp;
			i ++;
		}
		return newList;
	}

	private static ArrayList<String> getInAppResultList(Cursor results){
		ArrayList<String> tmp = new ArrayList<String>();
		int i = 1;
		if(results.moveToFirst()){
			do{
				String s1 = Integer.toString(i) + ".Score = " + results.getString(1); 
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(results.getLong(2));
				s1 += ". Time = " + c.getTime().toString() + ". Extra notes = " +
						results.getString(4);
				tmp.add(s1);
				i++;
			} while (results.moveToNext() && i < 4);
		}
		return tmp;
	}

	/*public static String getRecentResults(Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getMostRecent();
		return retrieveString(results);
	}

	public static String getAllResults(Activity activity){
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getAllResults();
		return retrieveString(results);
	}*/

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

	public static void insertResult(String eventName, int result,
			long time, Activity activity) {
		insertResult(eventName, Integer.toString(result), time, activity);		
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
		resultsToCSV(results, activity);
	}
	
	public static boolean resultsRecentToCSV(Activity activity) {
		DatabaseHelper db = DatabaseHelper.getInstance(activity, activity.getResources());
		Cursor results = db.getMostRecent();
		return resultsToCSV(results, activity);
	}

	public static boolean resultsToCSV(final Cursor results, Activity activity){

		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, "Results.csv");
		try {

			FileWriter fw = new FileWriter(file);

			fw.append("Event Name");
			fw.append(',');
			fw.append("Score");
			fw.append(',');
			fw.append("Time");
			fw.append(',');
			fw.append("Extra Notes");
			fw.append("\n");
			Log.d(TAG, Integer.toString(results.getCount()));
			if(results.moveToFirst()){
				do{

					fw.append(results.getString(0));
					fw.append(',');
					fw.append(results.getString(1));
					fw.append(',');
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(results.getLong(2));
					fw.append(c.getTime().toString());
					fw.append(',');
					fw.append(results.getString(4));
					fw.append("\n");
				} while(results.moveToNext());
			} else{
				fw.close();
				return false;
			}
			fw.flush();
			fw.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return true;
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
