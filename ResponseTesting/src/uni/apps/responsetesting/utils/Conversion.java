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
		return milliToStringSeconds((double) value, dp);

	}

	public static String milliToStringSeconds(double value, int dp){
		return formatString(Double.toString(milliToDoubleSeconds(value)), dp);
	}

	public static double milliToDoubleSeconds(long value){
		return (double) value / 1000;
	}

	public static double milliToDoubleSeconds(double value){
		return value / 1000;
	}

	private static String formatString(String s, int dp){
		int index = s.indexOf(".");
		if(index + dp < s.length())
			return s.substring(0, index + dp);
		else
			return s;
	}
}
