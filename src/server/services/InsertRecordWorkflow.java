package server.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;


import server.ServerManifest;
import server.models.BaseRecord;
import server.models.CourseType;
import server.models.StudentRecord;
import server.models.StudentStatus;
import server.models.TeacherRecord;
import server.storage.DcmsDatabase;
import server.validation.CreateRecordValidator;
import server.validation.InsertStudentValidator;
import server.validation.InsertTeacherValidator;
import server.validation.ValidationException;

public class InsertRecordWorkflow {

	private DcmsDatabase database;
	private Logger logger;
	ServerManifest serverManifest= new ServerManifest();
	public InsertRecordWorkflow(DcmsDatabase database,  Logger logger) {
		
		this.database = database;
		this.logger = logger;
	}

	public TeacherRecord createTeacher(String firstName, String lastName, String address, String phone,
			CourseType specialization, String location, String clientId) throws Exception {
		TeacherRecord newRecord = new TeacherRecord(firstName, lastName, address, phone, specialization, location);
		return process(newRecord, new InsertTeacherValidator(serverManifest), clientId);
	}

	public StudentRecord createStudent(String firstName, String lastName, List<CourseType> coursesRegistered,
			StudentStatus status, LocalDateTime statusDate, String clientId) throws Exception {

		StudentRecord newRecord = new StudentRecord(firstName, lastName, coursesRegistered, status, statusDate);
		return process(newRecord, new InsertStudentValidator(), clientId);
	}

	public TeacherRecord insertExistingRecord(TeacherRecord record, String clientId) throws Exception {
		return process(record, new InsertTeacherValidator(serverManifest), clientId);
	}

	public StudentRecord insertExistingRecord(StudentRecord record, String clientId) throws Exception {
		return process(record, new InsertStudentValidator(), clientId);
	}

	public <T extends BaseRecord> T process(T record, CreateRecordValidator<T> validator, String clientId) throws Exception {

		try {

			validator.validate(record);
			logger.info(String.format("%s: Inserting %s", clientId, record.toString()));

			T insertedRecord = database.insert(record);
			logger.info(String.format("%s: Insertion successful %s", clientId, record.toString()));

			return insertedRecord;

		} catch (ValidationException e) {
			logger.warning(String.format("%s: Validation failed for %s: %s", clientId, record.toString(), e.getMessage()));
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			logger.severe(String.format("%s: Insertion error for %s: %s", clientId, record.toString(), e.getMessage()));
			throw new Exception(e.getMessage());
		}
	}

}
