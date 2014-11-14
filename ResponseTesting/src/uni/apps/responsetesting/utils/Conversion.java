package uni.apps.responsetesting.utils;


/**
 * Converts milliseconds to seconds
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Conversion {

	//casts long to double
	public static String milliToStringSeconds(long value, int dp){
		return milliToStringSeconds((double) value, dp);

	}

	//converts double to string
	public static String milliToStringSeconds(double value, int dp){
		return formatString(Double.toString(milliToDoubleSeconds(value)), dp);
	}

	//gets seconds from long milliseconds
	public static double milliToDoubleSeconds(long value){
		return (double) value / 1000;
	}

	//gets seconds from double milliseconds
	public static double milliToDoubleSeconds(double value){
		return value / 1000;
	}

	//Formats String to specified decimal place
	private static String formatString(String s, int dp){
		int index = s.indexOf(".");
		if(index + dp < s.length())
			return s.substring(0, index + dp);
		else
			return s;
	}
}
