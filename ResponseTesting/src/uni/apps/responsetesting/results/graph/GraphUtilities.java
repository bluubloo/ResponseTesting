package uni.apps.responsetesting.results.graph;

import java.util.ArrayList;
import java.util.Calendar;

import android.database.Cursor;

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

	public static double getStep(double min, double max){
		if(max != min)
			return (max - min) / 10;
		else 
			return max / 10;
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

	public static ArrayList<Number[]> getScores(Cursor scores){
		ArrayList<String> times = getDates(scores, 2);
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
				String score = "";
				String s1 = scores.getString(1);
				if(s1.contains("|")){
					String[] tmp = s1.split("|");
					if(tmp.length == 1)
						score = tmp[0];
					else
						score = tmp[1];
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
			}while(scores.moveToNext());
		}
		ArrayList<Number[]> series = new ArrayList<Number[]>();
		series.add(try1);
		series.add(try2);
		series.add(try3);
		return series;
	}

	//TODO decide if this needs to be split into 3 arrays
	public static double[] getScoresAverage(Cursor cursor, Cursor question){
		double[] scores = new double[cursor.getCount()];
		String[] allTimes = new String[cursor.getCount()];
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(cursor.getLong(2));
				String value = Integer.toString(c.get(Calendar.DATE)) + "/"
						+ Integer.toString(c.get(Calendar.MONTH));
				allTimes[i] = value;
				String[] tmp = cursor.getString(1).split("|");
				if(tmp.length == 2)
					scores[i] = Double.parseDouble(tmp[1]);
				else
					scores[i] = Double.parseDouble(tmp[0]);
				i++;
			} while(cursor.moveToNext());
		}

		ArrayList<String> times = getDates(question, 2);
		double[] finalScores = new double[times.size()];
		int[] count = new int[times.size()];
		if(times.size() != 0){
			//initalizes tmp arrays
			for(int j = 0; j <times.size(); j++){
				finalScores[j] = 0;
				count[j] = 0;
			}

			//For each time check get index in time list
			//add score to final score
			//increment counter
			for(int k = 0; k < allTimes.length; k++){
				int index = times.indexOf(allTimes[k]);
				if(index != -1){
					finalScores[index] += scores[k];
					count[index] ++;
				}				
			}
			//calculate the average score
			for(int k = 0; k < finalScores.length; k++)
				finalScores[k] /= count[k];
		}	
		return scores;
	}

	private static ArrayList<String> getDates(Cursor cursor, int index){
		ArrayList<String> times = new ArrayList<String>();
		if(cursor.moveToFirst()){
			do{
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(cursor.getLong(index));
				String value = Integer.toString(c.get(Calendar.DATE)) + "/"
						+ Integer.toString(c.get(Calendar.MONTH));
				if(!times.contains(value))
					times.add(value);
			}while(cursor.moveToNext());
		}		
		return times;
	}

	public static long[] getLongDates(Cursor cursor, int index){
		long[] tmp = new long[cursor.getCount()];
		ArrayList<String> times = getDates(cursor, index);
		ArrayList<String> doneTimes = new ArrayList<String>();
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(cursor.getLong(index));
				String value = Integer.toString(c.get(Calendar.DATE)) + "/"
						+ Integer.toString(c.get(Calendar.MONTH));
				if(times.contains(value) && !doneTimes.contains(value)){
					tmp[i] = c.getTimeInMillis();
					i++;
					doneTimes.add(value);
				}
			} while(cursor.moveToNext());
		}
		return tmp;
	}






}
