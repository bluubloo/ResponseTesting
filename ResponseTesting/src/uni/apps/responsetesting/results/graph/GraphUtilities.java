package uni.apps.responsetesting.results.graph;

import java.util.ArrayList;

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
}
