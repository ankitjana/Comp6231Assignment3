package server.validation;

import java.util.List;

import server.ServerManifest;
import server.constants.ErrorMessages;
import server.constants.ValidationConstants;
import server.models.TeacherRecord;

/**
 * Validation class for teacher records 
 */
public class InsertTeacherValidator extends CreateRecordValidator<TeacherRecord> {

	private ServerManifest serverManifest;
	
	public InsertTeacherValidator(ServerManifest serverManifest) {
		this.serverManifest = serverManifest;
		
	}
	
	@Override
	public void validate(TeacherRecord record) throws ValidationException {
		super.validate(record);
		
		if(record.getAddress() == null || record.getAddress().isEmpty()) {
			throw new ValidationException(ErrorMessages.ADDRESS_FIELD_MISSING);
		}
		
		if(!record.getAddress().matches(ValidationConstants.ADDRESS_REGEX)) {
			throw new ValidationException(ErrorMessages.ADDRESS_INVALID_FORMAT);
		}
		
		if(record.getPhone() == null || record.getPhone().isEmpty()) {
			throw new ValidationException(ErrorMessages.PHONE_FIELD_MISSING); 
		}
		
		if(!record.getPhone().matches(ValidationConstants.PHONE_REGEX)) {
			throw new ValidationException(ErrorMessages.PHONE_INVALID_FORMAT);
		}
		
		if(record.getLocation() == null || record.getLocation().isEmpty()) {
			throw new ValidationException(ErrorMessages.LOCATION_FIELD_MISSING); 
		}
		
		List<String> availableLocations = serverManifest.getServerListSync();
		if(!availableLocations.contains(record.getLocation().toUpperCase())) {
			throw new ValidationException(String.format(ErrorMessages.LOCATION_UNSUPPORTED, record.getLocation(), String.join(",", availableLocations)));
		}	
		
	}
	
}
