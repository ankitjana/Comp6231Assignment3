package server.constants;

import java.time.format.DateTimeFormatter;

/**
 * Formats used for validation of server input data
 */
public class ValidationConstants {

	/**
	 * For this application we assume that the name must be in a western format
	 * Must start with an alphabetical character. Subsequent characters can also be -, ., ' or spaces
	 */
	public static final String NAME_REGEX = "^[a-zA-Z]+[a-zA-Z\\-\\.\\'\\s]*$";
	
	/**
	 * No restriction on address format for now
	 */
	public static final String ADDRESS_REGEX = ".*";
	
	public static final String PHONE_REGEX = "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

	public static final String DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss"; 
	
	public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STRING);
	
}
