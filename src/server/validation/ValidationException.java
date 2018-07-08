package server.validation;

import server.constants.ErrorMessages;

/**
 * Exception launched by server when an input is determined 
 * to be invalid and is thus rejected from the server. 
 */
public class ValidationException extends Exception {

	public ValidationException(String message) {
		super(message);
	}
	
	public static ValidationException NotFound(String recordId) {
		return new ValidationException(String.format(ErrorMessages.RECORD_NOT_FOUND, recordId)); 
	}
}
