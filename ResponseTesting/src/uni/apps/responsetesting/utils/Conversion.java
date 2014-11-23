package uni.apps.responsetesting.utils;


/**
 * Converts milliseconds to seconds
 * 
 * 
 * @author Mathew Andela
 *
 */
public class Conversion {

	//--------------------------------------------------------------------------------------
	//Millisecond Conversions

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

	//--------------------------------------------------------------------------------------
	//Sleep Duration Conversions

	public static Number sleepDurationStringToNumber(String duration){
		String[] tmp;
		//Get two parts of the time
		if(duration.contains(":")){
			tmp = duration.split(":");
		} else{
			int i = duration.indexOf('.');
			tmp = new String[] {duration.substring(0, i), duration.substring(i + 1)};
		}
		try{
			//parse into double
			//get minutes as decimal value
			double hours = Double.parseDouble(tmp[0]);
			double minutes = Double.parseDouble(tmp[1]) / 60;
			//get total time
			return hours + minutes;
		} catch (Exception e){
			e.printStackTrace();
			return 0;
		}
	}
}
