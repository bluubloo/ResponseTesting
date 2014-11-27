package uni.apps.responsetesting.results.graph;

import java.util.ArrayList;
import java.util.Calendar;

import android.database.Cursor;

public class GraphUtilities {

	public static double[] getMaxandMin(ArrayList<double[]> values){
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

	public static double getStep(double min, double max){
		return (max - min) / 10;
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

	public static String seriesArrayToString(Number[] array){
		String tmp = "[";
		for(int i = 0; i < array.length; i++){
			if(i != array.length - 1)
				tmp += Double.toString(array[i].doubleValue()) + ",";
			else{
				tmp += Double.toString(array[i].doubleValue()) + "]";
			}
		}
		return tmp;
	}

	//TODO decide if this needs to be split into 3 arrays
	public double[] getScores(Cursor cursor){
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

		ArrayList<String> times = getDates(cursor);
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

	private ArrayList<String> getDates(Cursor cursor){
		ArrayList<String> times = new ArrayList<String>();
		if(cursor.moveToFirst()){
			do{
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(cursor.getLong(2));
				String value = Integer.toString(c.get(Calendar.DATE)) + "/"
						+ Integer.toString(c.get(Calendar.MONTH));
				if(!times.contains(value))
					times.add(value);
			}while(cursor.moveToNext());
		}		
		return times;
	}
	
	public long[] getLongDates(Cursor cursor){
		ArrayList<String> times = getDates(cursor);
		long[] tmp = new long[times.size()];
		int i = 0;
		Calendar c = Calendar.getInstance();
		if(cursor.moveToFirst()){
			do{
				Calendar c1 = Calendar.getInstance();
				c1.setTimeInMillis(cursor.getLong(2));
				String value = Integer.toString(c1.get(Calendar.DATE)) + "/"
						+ Integer.toString(c1.get(Calendar.MONTH));
				if(times.contains(value)){
					c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
					tmp[i] = c.getTimeInMillis();
					i++;
				}
					
			} while(cursor.moveToNext());
		}
		return tmp;
	}


}
