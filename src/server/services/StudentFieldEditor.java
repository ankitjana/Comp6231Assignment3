package server.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.EditableFields;
import server.constants.ErrorMessages;
import server.constants.ValidationConstants;
import server.models.CourseType;
import server.models.StudentRecord;
import server.models.StudentStatus;
import server.storage.DcmsDatabase;
import server.validation.ValidationException;

public class StudentFieldEditor {

	private DcmsDatabase userDatabase;

	public StudentFieldEditor(DcmsDatabase database) {
		this.userDatabase = database;

	}

	public StudentRecord process(StudentRecord existingRecord, String field, String newValue)
			throws ValidationException {

		if (field.equalsIgnoreCase(EditableFields.STATUS)) {

			String trimmedNewValue = newValue.trim();
			Optional<StudentStatus> matchingStatus = Stream.of(StudentStatus.values())
					.filter(ss -> ss.toString().equalsIgnoreCase(trimmedNewValue)).findFirst();

			if (!matchingStatus.isPresent()) {
				throw new ValidationException(String.format(ErrorMessages.EDIT_STATUS_FIELD_INVALID, 
						trimmedNewValue, 
						StudentStatus.getList().stream().collect(Collectors.joining(", "))));
			}

			if (existingRecord.getStatus() != matchingStatus.get()) {
				existingRecord.setStatus(matchingStatus.get());
				existingRecord.setStatusDate(LocalDateTime.now());
			}

		} else if (field.equalsIgnoreCase(EditableFields.COURSES_REGISTERED)) {

			String[] courses = newValue.split(",");
			List<CourseType> mappedCourseTypes = new ArrayList<>();

			for (String course : courses) {

				String trimmedCourse = course.trim();
				Optional<CourseType> matchingCourse = Stream.of(CourseType.values())
						.filter(ss -> ss.toString().equalsIgnoreCase(trimmedCourse)).findFirst();

				if (!matchingCourse.isPresent()) {
					throw new ValidationException(String.format(ErrorMessages.EDIT_COURSES_FIELD_INVALID, 
							trimmedCourse, 
							Stream.of(CourseType.values()).map(CourseType::toString).collect(Collectors.joining(", "))));
				}

				mappedCourseTypes.add(matchingCourse.get());
			}

			existingRecord.setCoursesRegistered(mappedCourseTypes);

		} else if (field.equalsIgnoreCase(EditableFields.STATUS_DATE)) {

			LocalDateTime parsedDate;

			try {
				parsedDate = LocalDateTime.parse(newValue, ValidationConstants.DATETIME_FORMAT);
			} catch (DateTimeParseException e) {
				throw new ValidationException(
						e.getMessage() + " Accepted format: " + ValidationConstants.DATETIME_FORMAT_STRING);
			}

			// We add a small buffer in order to account for server client time drift
			if (parsedDate.isAfter(LocalDateTime.now().plusSeconds(20))) {
				throw new ValidationException(String.format(ErrorMessages.EDIT_STATUSDATE_FIELD_NOT_IN_FUTURE, newValue));
			}

			existingRecord.setStatusDate(parsedDate);

		} else {
			throw new ValidationException("Unknown student field " + newValue + " being edited");
		}

		return userDatabase.update(existingRecord);
	}

}
