package server.services;

import java.util.Arrays;
import java.util.logging.Logger;

import common.EditableFields;
import server.ServerManifest;
import server.constants.ErrorMessages;
import server.models.BaseRecord;
import server.models.RecordType;
import server.models.StudentRecord;
import server.models.TeacherRecord;
import server.storage.DcmsDatabase;
import server.storage.RecordIdDeserializer;
import server.validation.ValidationException;

public class EditFieldWorkflow {

	private DcmsDatabase userDatabase;
	private ServerManifest serverManifest;
	private Logger logger;

	public EditFieldWorkflow(DcmsDatabase database, ServerManifest serverManifest, Logger logger) {
		this.userDatabase = database;
		this.serverManifest = serverManifest;
		this.logger = logger;
	}

	public BaseRecord process(String recordId, String field, String newValue, String clientId) throws ValidationException {
		
		try {
			
			logger.info(String.format("%s: Editing %s field=%s, newValue=%s", clientId, recordId, field, newValue));
			
			if(field == null || field.isEmpty()) {
				throw new ValidationException(ErrorMessages.FIELD_TO_EDIT_MISSING);
			}
			
			if(newValue == null || newValue.isEmpty()) {
				throw new ValidationException(ErrorMessages.FIELD_TO_EDIT_NEW_VALUE_MISSING);
			}
			
			boolean isStudentRecord = RecordIdDeserializer.getRecordType(recordId) == RecordType.Student;
			String[] editableFields = isStudentRecord
					? EditableFields.STUDENT_EDITABLE_FIELDS
					: EditableFields.TEACHER_EDITABLE_FIELDS; 
			
			if(Arrays.stream(editableFields).noneMatch(ef -> ef.equalsIgnoreCase(field))) {
				throw new ValidationException(String.format(ErrorMessages.NON_EDITABLE_FIELD,
						field, recordId, String.join(", ", editableFields)));
			}
			
			BaseRecord userRecord = userDatabase.get(recordId);
			
			if(userRecord == null) {
				throw ValidationException.NotFound(recordId);
			}
			
			BaseRecord updatedRecord = isStudentRecord
					? new StudentFieldEditor(userDatabase).process((StudentRecord)userRecord, field, newValue)
					: new TeacherFieldEditor(userDatabase, serverManifest).process((TeacherRecord)userRecord, field, newValue);
			
			logger.info(String.format("%s: Successful update %s to %s: %s", clientId, field, newValue, updatedRecord.toString()));
			return updatedRecord; 
			
		}catch (ValidationException e) {
			logger.warning(String.format("%s: Validation failed for %s field=%s, newValue=%s: %s", clientId, recordId, field, newValue, e.toString()));
			throw new ValidationException(e.getMessage());
		}catch (Exception e) {
			logger.severe(String.format("%s: Error during edit for %s field=%s, newValue=%s: %s", clientId, recordId, field, newValue, e.toString()));
			throw new ValidationException(e.getMessage());
		}
	}
	
}
