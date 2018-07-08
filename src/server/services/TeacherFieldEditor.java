package server.services;

import java.util.List;

import common.EditableFields;
import server.ServerManifest;
import server.constants.ErrorMessages;
import server.constants.ValidationConstants;
import server.models.TeacherRecord;
import server.storage.DcmsDatabase;
import server.validation.ValidationException;

public class TeacherFieldEditor {

	private DcmsDatabase userDatabase;
	private ServerManifest serverManifest;

	public TeacherFieldEditor(DcmsDatabase userDatabase, ServerManifest serverManifest) {
		this.userDatabase = userDatabase;
		this.serverManifest = serverManifest;
		
	}

	public TeacherRecord process(TeacherRecord existingRecord, String field, String newValue) throws ValidationException {

		if(field.equalsIgnoreCase(EditableFields.ADDRESS)) {
			
			if(!newValue.matches(ValidationConstants.ADDRESS_REGEX)) {
				throw new ValidationException(ErrorMessages.ADDRESS_INVALID_FORMAT);
			}
			
			existingRecord.setAddress(newValue);
			
		}else if(field.equalsIgnoreCase(EditableFields.PHONE)) {
			
			if(!newValue.matches(ValidationConstants.PHONE_REGEX)) {
				throw new ValidationException(ErrorMessages.PHONE_INVALID_FORMAT);
			}
			
			existingRecord.setPhone(newValue);
			
		}else if(field.equalsIgnoreCase(EditableFields.LOCATION)) {
			
			List<String> availableLocations = serverManifest.getServerListSync();
			if(!availableLocations.contains(newValue.toUpperCase())) {
				throw new ValidationException(String.format(ErrorMessages.LOCATION_UNSUPPORTED, 
						newValue.toUpperCase(), 
						String.join(", ", availableLocations)));
			}	
			
			existingRecord.setLocation(newValue.toUpperCase());
			
		}else {
			throw new ValidationException("Unknown teacher field " + field + " being edited");
		}
		
		return userDatabase.update(existingRecord);
		
	}
	
}
