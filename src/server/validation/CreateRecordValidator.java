package server.validation;

import server.constants.ErrorMessages;
import server.constants.ValidationConstants;
import server.models.BaseRecord;

/**
 * Validator for base record fields
 *
 * @param <T> Type of concrete class validated
 */
public class CreateRecordValidator<T extends BaseRecord> {

	public void validate(T record) throws ValidationException {
		
		if(record.getFirstName() == null || record.getFirstName().isEmpty()) {
			throw new ValidationException(ErrorMessages.FIRST_NAME_MISSING);
		}
		
		if(!record.getFirstName().matches(ValidationConstants.NAME_REGEX)) {
			throw new ValidationException(ErrorMessages.FIRST_NAME_INVALID_FORMAT);	
		}
		
		if(record.getLastName() == null || record.getLastName().isEmpty()) {
			throw new ValidationException(ErrorMessages.LAST_NAME_MISSING);
		}
		
		if(!record.getLastName().matches(ValidationConstants.NAME_REGEX)) {
			throw new ValidationException(ErrorMessages.LAST_NAME_INVALID_FORMAT);
		}
		
		
	}
	
}
