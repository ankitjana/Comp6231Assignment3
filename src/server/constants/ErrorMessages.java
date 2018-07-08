package server.constants;

/**
 * Error message constants used during server validation
 */
public class ErrorMessages {

	public static final String FIRST_NAME_MISSING = "First name field is required.";
	
	public static final String LAST_NAME_MISSING = "Last name field is required.";
	
	public static final String ADDRESS_FIELD_MISSING = "Address field is required.";
	
	public static final String ADDRESS_INVALID_FORMAT = String.format("Invalid addresss input (regex %s).", ValidationConstants.ADDRESS_REGEX); 
	
	public static final String PHONE_FIELD_MISSING = "Phone number field is required.";
	
	public static final String PHONE_INVALID_FORMAT = String.format("Invalid phone input (ex: 514-514-5145, regex: %s)", ValidationConstants.PHONE_REGEX);
	
	public static final String FIRST_NAME_INVALID_FORMAT = String.format("First name is in an invalid format (regex: %s).", ValidationConstants.NAME_REGEX); 
	
	public static final String LAST_NAME_INVALID_FORMAT = String.format("Last name is in an invalid format (regex: %s).", ValidationConstants.NAME_REGEX); 
	
	public static final String LOCATION_FIELD_MISSING = "Location field is required.";

	public static final String LOCATION_UNSUPPORTED = "Unknown location %s. Available locations: %s";
	
	public static final String REQUIRES_ONE_CLASS_REGISTERED = "Student must be registered to at least one class.";
	
	public static final String RECORD_ID_MISSING = "Id is required"; 
	
	public static final String RECORD_ID_INVALID_LENGTH = "%s has invalid length. Expected %d.";

	public static final String RECORD_ID_INVALID_RECORD_TYPE_CODE = "Unknown record type code for %s. Expected either SR or TR"; 
	
	public static final String RECORD_ID_INVALID_NUMERIC_PORTION = "%s has invalid numeric portion";
	
	public static final String RECORD_ID_MALFORMED = "Record id is malformed";
	
	public static final String RECORD_NOT_FOUND = "%s not found";
	
	public static final String FIELD_TO_EDIT_MISSING = "Field to edit must be specified"; 
	
	public static final String FIELD_TO_EDIT_NEW_VALUE_MISSING = "New value must be specified";
	
	public static final String EDIT_STATUS_FIELD_INVALID = "Status %s is invalid. Accepted values = %s";
	
	public static final String EDIT_STATUSDATE_FIELD_NOT_IN_FUTURE = "%s cannot be in the future";
	
	public static final String EDIT_COURSES_FIELD_INVALID = "Course %s is invalid. Accepted values = %s";
	
	public static final String NON_EDITABLE_FIELD = "Field %s for record %s is not ediable. Only editable fields are %s.";
	
	
}
