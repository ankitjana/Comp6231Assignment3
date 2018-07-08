package server.validation;

import server.constants.ErrorMessages;
import server.models.StudentRecord;

/**
 * Validation class for student records 
 */
public class InsertStudentValidator extends CreateRecordValidator<StudentRecord> {

	@Override
	public void validate(StudentRecord record) throws ValidationException {
		super.validate(record);

		if (record.getCoursesRegistered() == null || record.getCoursesRegistered().isEmpty()) {
			throw new ValidationException(ErrorMessages.REQUIRES_ONE_CLASS_REGISTERED);
		}
	}

}
