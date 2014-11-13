package uni.apps.responsetesting.utils;


/**
 * Converts milliseconds to seconds
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Conversion {

	public static String milliToStringSeconds(long value, int dp){
		return Double.toString(milliToDoubleSeconds(value)).substring(0, dp);
	}
	
	public static String milliToStringSeconds(double value, int dp){
		return Double.toString(milliToDoubleSeconds(value)).substring(0, dp);
	}
	
	public static double milliToDoubleSeconds(long value){
		return (double) value / 1000;
	}
	
	public static double milliToDoubleSeconds(double value){
		return value / 1000;
	}
	
}
