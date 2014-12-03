package uni.apps.responsetesting.results.graph;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.database.Cursor;
import android.util.Log;

public class GraphUtilities {

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


	public static double getStep(double min, double max){
		if(max != min)
			return (max - min) / 5;
		else 
			return max / 5;
	}

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

	public static ArrayList<Number[]> getScores(Cursor scores, String id){
		ArrayList<String> times = getDates(scores, 2, id, 5);
		Number[] try1 = new Number[times.size()];
		Number[] try2 = new Number[times.size()];
		Number[] try3 = new Number[times.size()];
		for(int i = 0; i < try1.length; i++){
			try1[i] = null;
			try2[i] = null;
			try3[i] = null;
		}
		if(scores.moveToFirst()){
			do{
				if(id.equals(scores.getString(5))){
					String score = "";
					String s1 = scores.getString(1);
					if(s1.contains("|") || s1.indexOf('|') != -1){
						int index = s1.indexOf('|');
						String[] tmp = s1.split("|");
						if(tmp.length == 1)
							score = tmp[0];
						else
							score = tmp[1];
						score = s1.substring(index + 1);
					}else 
						score = s1;
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(scores.getLong(2));
					String value = Integer.toString(c.get(Calendar.DATE)) + "/"
							+ Integer.toString(c.get(Calendar.MONTH));
					int index = times.indexOf(value);
					if(index != -1 && index < times.size()){
						if(try1[index] == null){
							try1[index] = Double.parseDouble(score);
						}else if (try2[index] == null){
							try2[index] = Double.parseDouble(score);
						}else if( try3[index] == null){
							try3[index] = Double.parseDouble(score);
						}
					}
				}
			}while(scores.moveToNext());
		}
		ArrayList<Number[]> series = new ArrayList<Number[]>();
		series.add(try1);
		series.add(try2);
		series.add(try3);
		return series;
	}

	public static ArrayList<Number[]> getDurations(Cursor cursor, String id) {
		if(cursor.getCount() > 0){
			Number[] total = new Number[cursor.getCount()];
			Number[] light = new Number[cursor.getCount()];
			Number[] sound = new Number[cursor.getCount()];
			Number[] times = new Number[cursor.getCount()];
			int counter = 0;

			if(cursor.moveToFirst()){
				do{
					if(id.equals(cursor.getString(6))){
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(cursor.getLong(0));
						c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
								c.get(Calendar.DATE), 0, 0, 0);
						c.setTimeZone(TimeZone.getTimeZone("GMT+12"));
						times[counter] = c.getTimeInMillis();
						total[counter] = formatDouble(cursor.getString(1));
						light[counter] = formatDouble(cursor.getString(2));
						sound[counter] = formatDouble(cursor.getString(3));
					}
				} while(cursor.moveToNext());
			}

			ArrayList<Number[]> tmp = new ArrayList<Number[]>();
			tmp.add(times);
			tmp.add(total);
			tmp.add(light);
			tmp.add(sound);
			return tmp;
		} 
		return new ArrayList<Number[]>();
	}

	private static Number formatDouble(String string) {
		int index = string.indexOf('.');
		if(index == -1)
			index = string.indexOf(':');
		if(index != -1){
			String s1 = string.substring(0, index);
			String s2 = string.substring(index + 1);
			return Double.parseDouble(s1) + (Double.parseDouble(s2) / 60);
		}
		return null;
	}

	private static ArrayList<String> getDates(Cursor cursor, int index, String id, int idIndex){
		ArrayList<String> times = new ArrayList<String>();
		if(cursor.moveToFirst()){
			do{
				if(id.equals(cursor.getString(idIndex))){
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

	public static long[] getLongDates(Cursor cursor, int index, String id){
		long[] tmp = new long[cursor.getCount()];
		ArrayList<String> times = getDates(cursor, index, id, 5);
		ArrayList<String> doneTimes = new ArrayList<String>();
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				if(id.equals(cursor.getString(5))){
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(cursor.getLong(index));
					c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 
							c.get(Calendar.DATE), 0, 0, 0);
					c.setTimeZone(TimeZone.getTimeZone("GMT+12"));
					String value = Integer.toString(c.get(Calendar.DATE)) + "/"
							+ Integer.toString(c.get(Calendar.MONTH));
					if(times.contains(value) && !doneTimes.contains(value)){
						if(c.getTimeInMillis() != 0){
							tmp[i] = c.getTimeInMillis();
							i++;
						}	
						doneTimes.add(value);
					}
				}
			} while(cursor.moveToNext());
		}
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

	public static double roundUp(double value){
		String stringValue = Double.toString(value);
		int index = stringValue.indexOf('.');
		Log.d("TEST", stringValue);
		if(index != -1){
			if(stringValue.charAt(index + 1) != '0'){
				double tmp = value * 100 + 5;
				tmp = Math.round(tmp);
				return tmp /= 100;
			}
		}
		return value + 1;
	}

	public static double roundDown(double value){
		String stringValue = Double.toString(value);
		int index = stringValue.indexOf('.');
		Log.d("TEST", stringValue);
		if(index != -1){
			if(stringValue.charAt(index + 1) != '0'){
				double tmp = value * 100 - 5;
				tmp = Math.round(tmp);
				return tmp /= 100;
			}
		}
		return value - 1;
	} 
}
