package uni.apps.responsetesting.results.graph;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.database.Cursor;
import android.util.Log;

/**
 * This class handles methods related to formatting data for the graphs
 * 
 * @author Mathew Andela
 *
 */
public class GraphUtilities {

	private static final String TAG = "GraphUtilities";

	//---------------------------------------------------------------------------------------------
	//Mins and Maxs
	public static double[] getMaxandMinDouble(ArrayList<double[]> values){
		double max = 0;
		double min = 100;

		for(double[] list: values)
			for(double n : list){
				if(n > max)
					max = n;
				if(n < min)
					min = n;
			}
		return new double[] {min, max};
	}

	public static double[] getMaxandMin(ArrayList<Number[]> series) {
		double max = 0;
		double min = 100;

		for(Number[] list: series)
			for(Number n : list){
				if(n != null){
					if(n.doubleValue() > max)
						max = n.doubleValue();
					if(n.doubleValue() < min)
						min = n.doubleValue();
				}
			}
		return new double[] {min, max};
	}

	public static long[] getMinandMaxLong(long[] values){
		long min = values[0];
		long max = values[0];

		for(long l: values){
			if(l > max)
				max = l;
			if(l < min)
				min = l;
		}
		min -= 1000;
		max += 1000;
		return new long[]{min, max};		
	}

	public static long[] getMinandMaxLong(Number[] numbers) {
		long min = numbers[0].longValue();
		long max = numbers[0].longValue();

		for(Number l: numbers){
			if(l.longValue() > max)
				max = l.longValue();
			if(l.longValue() < min)
				min = l.longValue();
		}
		min -= 1000;
		max += 1000;
		return new long[]{min, max};
	}

	//---------------------------------------------------------------------------------------------
	//steps
	public static double getStep(double min, double max){
		if(max != min)
			return (max - min) / 5;
		else 
			return max / 5;
	}

	//---------------------------------------------------------------------------------------------
	//Interweave values
	public static Number[] interweaveValues(double[] xs, double[] ys){
		if(xs.length == ys.length){
			Number[] tmp = new Number[xs.length + ys.length];
			int counter = 0;
			for(int i = 0; i < xs.length; i ++){
				tmp[counter] = xs[i];
				tmp[counter + 1] = ys[i];
				counter += 2;
			}
			return tmp;
		} else {
			throw new IllegalArgumentException("X array and Y array lengths need to be the same");
		}
	}

	public static Number[] interweaveValues(long[] xs, double[] ys) {
		if(xs.length == ys.length){
			Number[] tmp = new Number[xs.length + ys.length];
			int counter = 0;
			for(int i = 0; i < xs.length; i ++){
				tmp[counter] = xs[i];
				tmp[counter + 1] = ys[i];
				counter += 2;
			}
			return tmp;
		} else {
			throw new IllegalArgumentException("X array and Y array lengths need to be the same");
		}
	}

	public static Number[] interweaveValues(long[] xs, Number[] ys) {
		if(xs.length == ys.length){
			Number[] tmp = new Number[xs.length + ys.length];
			int counter = 0;
			for(int i = 0; i < xs.length; i ++){
				tmp[counter] = xs[i];
				tmp[counter + 1] = ys[i];
				counter += 2;
			}
			return tmp;
		} else {
			throw new IllegalArgumentException("X array and Y array lengths need to be the same");
		}
	}

	public static Number[] interweaveValues(Number[] xs, Number[] ys) {
		if(xs.length == ys.length){
			Number[] tmp = new Number[xs.length + ys.length];
			int counter = 0;
			for(int i = 0; i < xs.length; i ++){
				tmp[counter] = xs[i];
				tmp[counter + 1] = ys[i];
				counter += 2;
			}
			return tmp;
		} else {
			throw new IllegalArgumentException("X array and Y array lengths need to be the same");
		}
	}

	//---------------------------------------------------------------------------------------------
	//Array to string

	public static String seriesArrayToString(Number[] array){
		String tmp = "[";
		for(int i = 0; i < array.length; i++){
			if(i != array.length - 1)
				if(array[i] != null)
					tmp += Double.toString(array[i].doubleValue()) + ",";
				else
					tmp += "null,";
			else{
				if(array[i] != null)
					tmp += Double.toString(array[i].doubleValue()) + "]";
				else
					tmp += "null]";
			}
		}
		return tmp;
	}

	public static String seriesArrayToStringLong(Number[] array){
		String tmp = "[";
		for(int i = 0; i < array.length; i+=2){
			tmp += array[i].longValue() + ",";
			if(i + 1 < array.length - 1){
				if(array[i + 1] != null)
					tmp += array[i + 1].doubleValue() + ",";
				else
					tmp += "null,";
			} else{
				if(array[i + 1] != null)
					tmp += array[i + 1].doubleValue() + "]";
				else
					tmp += "null]";
			}
		}
		return tmp;
	}

	public static String seriesArrayToStringLong(long[] array){
		String tmp = "[";
		for(int i = 0; i < array.length; i++){
			if(i < array.length - 1)
				tmp += array[i] + ",";
			else
				tmp += array[i] + "]";
		}
		return tmp;
	}

	//---------------------------------------------------------------------------------------------
	//get data
	
	//gets data for specified events
	public static ArrayList<Number[]> getScores(Cursor cursor, String id){
		//list variables
		ArrayList<Number> dates = new ArrayList<Number>();
		ArrayList<Number> scores = new ArrayList<Number>();
		ArrayList<Number> times = new ArrayList<Number>();
		
		if(cursor.moveToFirst()){
			do{
				//checks id
				if(cursor.getString(5).equals(id)){
					//adds date
					dates.add(cursor.getLong(2));
					//checks scrore type
					//adds scores to list
					String s = cursor.getString(1);
					int index = s.indexOf('|');
					if(index != -1){
						scores.add(Double.parseDouble(s.substring(0, index)));
						times.add(Double.parseDouble(s.substring(index + 1)));
					} else
						scores.add(Double.parseDouble(s));
				}				
			} while(cursor.moveToNext());
		}
		
		//checks if mandatory lists are empty
		if(!dates.isEmpty() && !scores.isEmpty()){
			//new variable
			ArrayList<Number[]> tmp = new ArrayList<Number[]>();
			Number[] datesA = new Number[dates.size()];
			Number[] scoresA = new Number[dates.size()];
			//adds to array
			for(int i = 0; i < dates.size(); i ++){
				datesA[i] = dates.get(i);
				scoresA[i] = scores.get(i);
			}
			//adds to list
			tmp.add(datesA);
			tmp.add(scoresA);
			//checks if there is extra series
			if(!times.isEmpty()){
				//creates list data for new series
				Number[] timesA = new Number[dates.size()];
				for(int i = 0; i < dates.size(); i ++){
					timesA[i] = times.get(i);
				}
				//adds to list
				tmp.add(timesA);
			}
			return tmp;
		}
		return new ArrayList<Number[]>();
	}

	//gets sleep durations
	public static ArrayList<Number[]> getDurations(Cursor cursor, String id) {
		if(cursor.getCount() > 0){
			//vairables
			Number[] total = new Number[cursor.getCount()];
			Number[] light = new Number[cursor.getCount()];
			Number[] sound = new Number[cursor.getCount()];
			Number[] times = new Number[cursor.getCount()];
			int counter = 0;
			Log.d(TAG, Integer.toString(cursor.getCount()));
			if(cursor.moveToFirst()){
				do{
					//check user id
					if(id.equals(cursor.getString(6))){
						//get time
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(cursor.getLong(0));
						c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
								c.get(Calendar.DATE), 0, 0, 0);
						c.setTimeZone(TimeZone.getTimeZone("GMT+12"));
						//sets values
						times[counter] = c.getTimeInMillis();
						total[counter] = formatDouble(cursor.getString(1));
						light[counter] = formatDouble(cursor.getString(2));
						sound[counter] = formatDouble(cursor.getString(3));
						counter++;
					}
				} while(cursor.moveToNext());
			}
			//return list of arrays
			ArrayList<Number[]> tmp = new ArrayList<Number[]>();
			tmp.add(times);
			tmp.add(total);
			tmp.add(light);
			tmp.add(sound);
			return tmp;
		} 
		return new ArrayList<Number[]>();
	}

	//gets list of dates
	//no double ups
	private static ArrayList<String> getDates(Cursor cursor, int index, String id, int idIndex){
		ArrayList<String> times = new ArrayList<String>();
		if(cursor.moveToFirst()){
			do{
				//checks user id 
				if(id.equals(cursor.getString(idIndex))){
					//gets date
					//checks if exists
					//adds to list if not
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(cursor.getLong(index));
					String value = Integer.toString(c.get(Calendar.DATE)) + "/"
							+ Integer.toString(c.get(Calendar.MONTH));
					if(!times.contains(value))
						times.add(value);
				}
			}while(cursor.moveToNext());
		}		
		return times;
	}

	//get dates as longs
	//no double ups
	public static long[] getLongDates(Cursor cursor, int index, String id){
		//variables
		long[] tmp = new long[cursor.getCount()];
		ArrayList<String> times = getDates(cursor, index, id, 5);
		ArrayList<String> doneTimes = new ArrayList<String>();
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				//checks user id
				if(id.equals(cursor.getString(5))){
					//gets time
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(cursor.getLong(index));
					c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 
							c.get(Calendar.DATE), 0, 0, 0);
					c.setTimeZone(TimeZone.getTimeZone("GMT+12"));
					String value = Integer.toString(c.get(Calendar.DATE)) + "/"
							+ Integer.toString(c.get(Calendar.MONTH));
					//checks if exists
					if(times.contains(value) && !doneTimes.contains(value)){
						if(c.getTimeInMillis() != 0){
							//add to array
							tmp[i] = c.getTimeInMillis();
							i++;
						}	
						doneTimes.add(value);
					}
				}
			} while(cursor.moveToNext());
		}
		//Ensures no 0 added
		ArrayList<Long> tmptmp = new ArrayList<Long>();
		for(int j = 0; j < tmp.length; j ++){
			if(tmp[j] != 0){
				tmptmp.add(tmp[j]);
			}				
		}
		tmp = new long[tmptmp.size()];
		for(int j = 0; j < tmptmp.size(); j++){
			tmp[j] = tmptmp.get(j);
		}
		return tmp;
	}


	//Gets resting HR
	public static ArrayList<Number[]> getHR(Cursor cursor, String userId) {
		//vaiables
		ArrayList<Number> tmp = new ArrayList<Number>();
		ArrayList<Number> times = new ArrayList<Number>();
		if(cursor.moveToFirst()){
			do{
				//checks user id
				if(cursor.getString(6).equals(userId)){
					//gets time and hr
					if(!cursor.getString(7).equals(""))
						tmp.add(Double.parseDouble(cursor.getString(7)));
					else
						tmp.add(null);
					times.add(cursor.getLong(0));
				}
			} while(cursor.moveToNext());
		}
		//from list to array
		if(!tmp.isEmpty()){
			Number[] values = new Number[tmp.size()];
			Number[] times2 = new Number[tmp.size()];
			for(int i = 0; i < tmp.size(); i++){
				values[i] = tmp.get(i);
				times2[i] = times.get(i);
			}
			ArrayList<Number[]> array =  new ArrayList<Number[]>();
			array.add(times2);
			array.add(values);
			return array;
		}			
		return new ArrayList<Number[]>();
	} 

	//---------------------------------------------------------------------------------------------
	//Auxiliary methods

	//rounds value up
	public static double roundUp(double value){
		String stringValue = Double.toString(value);
		int index = stringValue.indexOf('.');
		if(index != -1){
			if(stringValue.charAt(index + 1) != '0'){
				double tmp = value * 100 + 5;
				tmp = Math.round(tmp);
				return tmp /= 100;
			}
		}
		return value + 1;
	}

	//rounds value down
	public static double roundDown(double value){
		String stringValue = Double.toString(value);
		int index = stringValue.indexOf('.');
		if(index != -1){
			if(stringValue.charAt(index + 1) != '0'){
				double tmp = value * 100 - 5;
				tmp = Math.round(tmp);
				return tmp /= 100;
			}
		}
		return value - 1;
	}

	//formats time as number
	private static Number formatDouble(String string) {
		Log.d(TAG, string);
		int index = string.indexOf(':');
		if(index != -1){
			String s1 = string.substring(0, index);
			String s2 = string.substring(index + 1);
			return Double.parseDouble(s1) + (Double.parseDouble(s2) / 60);
		}
		return null;
	}

	public static ArrayList<Number[]> getData(Cursor cursor, String userId) {
		ArrayList<Number> scores = new ArrayList<Number>();
		ArrayList<Number> times = new ArrayList<Number>();

		if(cursor.moveToFirst()){
			do{
				String s1 = cursor.getString(1);
				if(s1.contains("|") || s1.indexOf('|') != -1){
					int index = s1.indexOf('|');
					times.add(Double.parseDouble(s1.substring(index + 1)));
					scores.add(Double.parseDouble(s1.substring(0, index)));
				}else {
					scores.add(null);
					times.add(Double.parseDouble(s1));
				}

			} while(cursor.moveToNext());
			
			if(!times.isEmpty() || !scores.isEmpty()){
				Number[] timeTmp = new Number[times.size()];
				Number[] scoreTmp = new Number[times.size()];
				for(int i = 0; i < times.size(); i ++){
					timeTmp[i] = times.get(i);
					scoreTmp[i] = scores.get(i);
				}
				ArrayList<Number[]> tmp = new ArrayList<Number[]>();
				tmp.add(scoreTmp);
				tmp.add(timeTmp);
				return tmp;
			}
		}		
		return new ArrayList<Number[]>();
	}

	public static Number[] normalize(Number[] numbers, double[] minMaxY) {
		Number[] tmp = new Number[numbers.length];
		for(int i = 0; i < numbers.length; i ++){
			if(numbers[i] == null)
				tmp[i] = null;
			else{
				double n = numbers[i].doubleValue();
				double a = n - minMaxY[0];
				double b = minMaxY[1] - minMaxY[0];
				double j = a / b;
				tmp[i] = j;
			}
		}
		return tmp;
	}
}
