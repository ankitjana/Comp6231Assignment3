package server;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import server.constants.ErrorMessages;
import server.constants.ValidationConstants;
import server.models.BaseRecord;
import server.models.CourseType;
import server.models.RecordCount;
import server.models.StudentRecord;
import server.models.StudentStatus;
import server.models.TeacherRecord;
import server.recordcount.RecordCountTask;
import server.recordcount.RecordCountTaskFactory;
import server.recordcount.UdpServerInfo;
import server.services.EditFieldWorkflow;
import server.services.InsertRecordWorkflow;
import server.services.TransferRecordWorkflow;
import server.storage.DcmsDatabase;
import server.validation.ValidationException;

public class ConcreteDcmsServer implements DcmsServer {

	private DcmsDatabase database;
	private Logger logger;
	private InsertRecordWorkflow creationWorkflow;
	private RecordCountTaskFactory countRecordsTaskFactory;
	private EditFieldWorkflow editFieldWorkflow;
	private UdpServerInfo udpServerInfo;
	private TransferRecordWorkflow transferRecordWorkflow;
	private String serverId;

	public ConcreteDcmsServer(DcmsDatabase database, InsertRecordWorkflow creationWorkflow,
			RecordCountTaskFactory countRecordsTaskFactory, EditFieldWorkflow editFieldWorkflow,
			TransferRecordWorkflow transferRecordWorkflow, UdpServerInfo udpServerInfo, String serverId, 
			Logger logger) {

		this.database = database;
		this.creationWorkflow = creationWorkflow;
		this.countRecordsTaskFactory = countRecordsTaskFactory;
		this.editFieldWorkflow = editFieldWorkflow;
		this.transferRecordWorkflow = transferRecordWorkflow;
		this.udpServerInfo = udpServerInfo;
		this.serverId = serverId;
		this.logger = logger;
	}

	/**
	 * Creates a new teacher record
	 * 
	 * @param firstName
	 *            First name of record
	 * @param lastName
	 *            Last name of record
	 * @param address
	 *            Address of teacher record
	 * @param phone
	 *            Phone number of teacher record
	 * @param specialization
	 *            Specialization of teacher record
	 * @param location
	 *            Location of teacher
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return Created teacher record
	 * @throws Exception
	 */
	@Override
	public TeacherRecord createTeacherRecord(String firstName, String lastName, String address, String phone,
			CourseType specialization, String location, String clientId) throws Exception {

		TeacherRecord insertedRecord = this.creationWorkflow.createTeacher(firstName, lastName, address, phone,
				specialization, location, clientId);
		return insertedRecord;
	}

	/**
	 * Creates a new student record
	 * 
	 * @param firstName
	 *            First name of record
	 * @param lastName
	 *            Last name of record
	 * @param coursesRegistered
	 *            Courses that student is registered for. Student must be
	 *            registered for at least one course.
	 * @param status
	 *            Student status (either active or inactive)
	 * @param statusDate
	 *            Last date the status became active or inactive
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return Created student record
	 * @throws Exception
	 */	
	@Override
	public StudentRecord createStudentRecord(String firstName, String lastName,
			List<CourseType> coursesRegistered, StudentStatus status, String statusDate,
			String clientId) throws Exception {
		StudentRecord insertedRecord = this.creationWorkflow.createStudent(firstName, lastName,
				coursesRegistered, status,
				LocalDateTime.parse(statusDate, ValidationConstants.DATETIME_FORMAT), clientId);
		
		return insertedRecord;
	}

	/**
	 * Edits the field of the specified record
	 * 
	 * @param id
	 *            Record id to edit
	 * @param fieldName
	 *            Name of field on record to edit. For student records: Status,
	 *            StatusDate and CoursesRegistered can be changed. For teacher
	 *            records: address, phone and location can be changed.
	 * @param newValue
	 *            New value to apply to record.
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return Edited record
	 * @throws Exception
	 */
	@Override
	public BaseRecord editRecord(String id, String fieldName, String newValue, String clientId)
			throws Exception {
		return editFieldWorkflow.process(id, fieldName, newValue, clientId);
	}

	/**
	 * Retrieves record counts among all visible servers. In addition to
	 * returning a local count, this server will contact sibling servers via udp
	 * to obtain and return their counts.
	 * 
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return List of record counts organized on a per server basis
	 * @throws Exception
	 */
	@Override
	public List<RecordCount> getRecordCounts(String clientId) throws Exception {
		RecordCountTask countRecordsTask = countRecordsTaskFactory.create(clientId);
		return countRecordsTask.execute();
	}

	/***
	 * Retrieves all teacher records stored in this server.
	 * 
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return All teacher records stored in this server.
	 * @throws Exception
	 */
	@Override
	public List<TeacherRecord> getTeachers(String clientId) throws Exception {
		return getRecordsInternal(TeacherRecord.class, null, clientId);
	}

	/***
	 * Retrieves all student records stored in this server.
	 * 
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return All teacher records stored in this server.
	 * @throws Exception
	 */
	@Override
	public List<StudentRecord> getStudents(String clientId) throws Exception {
		return getRecordsInternal(StudentRecord.class, null, clientId);
	}

	/***
	 * Retrieves all teacher records stored in this server that belong to the
	 * specified last name character bucket.
	 * 
	 * @param lastNameCharFilter
	 *            Last name character filter to apply
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return List of teacher records
	 * @throws Exception
	 */
	@Override
	public List<TeacherRecord> getTeachersWithNameFilter(char lastNameCharFilter, String clientId) throws Exception {
		return getRecordsInternal(TeacherRecord.class, new Character(lastNameCharFilter), clientId);
	}

	/***
	 * Retrieves all student records stored in this server that belong to the
	 * specified last name character bucket.
	 * 
	 * @param lastNameCharFilter
	 *            Last name character filter to apply
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return List of student records
	 * @throws Exception
	 */
	@Override
	public List<StudentRecord> getStudentsWithNameFilter(char lastNameCharFilter, String clientId) throws Exception {
		return getRecordsInternal(StudentRecord.class, new Character(lastNameCharFilter),
				clientId);
	}

	/**
	 * Gets a specific teacher specified by id
	 * 
	 * @param recordId
	 *            Record id to search for
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return Retrieved teacher record. Null if not found
	 * @throws Exception
	 */
	@Override
	public TeacherRecord getTeacher(String recordId, String clientId) throws Exception {
		return getRecordInternal(TeacherRecord.class, recordId, clientId);
	}

	/**
	 * Gets a specific student specified by id
	 * 
	 * @param recordId
	 *            Record id to search for
	 * @param clientId
	 *            Manager client id that issued the request (used for logging
	 *            purposes)
	 * @return Retrieved student record. Null if not found
	 * @throws Exception
	 */
	@Override
	public StudentRecord getStudent(String recordId, String clientId) throws Exception {
		return getRecordInternal(StudentRecord.class, recordId, clientId);
	}

	/**
	 * Initiates a transfer
	 * 
	 * @param recordId
	 *            record to transfer
	 * @param recipientServer
	 *            server to transfer to
	 * @param clientId
	 *            manager client that initiated the request
	 * @throws Exception
	 * @return True if operation was successful
	 */
	@Override
	public boolean transferRecord(String recordId, String recipientServer, String clientId) throws Exception {
		return transferRecordWorkflow.process(recordId, recipientServer, clientId);
	}

	/**
	 * Accepts an existing teacher record. Used during the transfer process.
	 * 
	 * @param record
	 *            record to transfer
	 * @param clientId
	 *            manager client that initiated the request
	 * @throws Exception
	 * @return True if operation was successful
	 */
	@Override
	public boolean acceptTeacherRecord(TeacherRecord record, String clientId) throws Exception {
		TeacherRecord insertedRecord = this.creationWorkflow.insertExistingRecord(record,
				clientId);
		return insertedRecord != null;
	}

	/**
	 * Accepts an existing student record. Used during the transfer process.
	 * 
	 * @param record
	 *            record to transfer
	 * @param clientId
	 *            manager client that initiated the request
	 * @throws Exception
	 * @return True if operation was successful
	 */
	@Override
	public boolean acceptStudentRecord(StudentRecord record, String clientId) throws Exception {
		StudentRecord insertedRecord = this.creationWorkflow.insertExistingRecord(record,
				clientId);
		return insertedRecord != null;
	}

	/**
	 * Pings the server
	 * 
	 * @return True if the ping succeeded.
	 */
	@Override
	public boolean ping() {
		return true;
	}

	/**
	 * Fetches the UDP port used by the server
	 * @return UDP port used by the server
	 */
	@Override
	public int getUdpServerPort() {
		return udpServerInfo.getPort(); 
	}
	
	/**
	 * Fetches the server id assigned to the server (such as MTL)
	 * @return Server id
	 */
	@Override
	public String getServerId() {
		return serverId;
	}

	private <T extends BaseRecord> List<T> getRecordsInternal(Class<T> type, Character lastNameFiler, String clientId) {
		try {
			logger.info(String.format("%s: Retrieving %s records", clientId, type.getSimpleName()));
			List<T> records = lastNameFiler == null ? database.list(type) : database.list(lastNameFiler, type);
			logger.info(String.format("%s: Returning %d %s records.", clientId, records.size(), type.getSimpleName()));
			return records.stream().sorted(Comparator.comparing(BaseRecord::getId).reversed())
					.collect(Collectors.toList());
		} catch (Exception e) {
			logger.severe(String.format("%s: Error while retrieving %s records: %s", clientId, type.getSimpleName(),
					e.toString()));
			// throw new RemoteException(e.getMessage());
			return null;
		}
	}

	private <T extends BaseRecord> T getRecordInternal(Class<T> type, String recordId, String clientId)
			throws Exception {
		try {
			logger.info(String.format("%s: Retrieving %s", clientId, recordId));
			T record = (T) database.get(recordId);
			logger.info(String.format("%s: Retrieved %s", clientId, record == null ? "NotFound" : record.toString()));

			if (record == null) {
				throw new ValidationException(String.format(ErrorMessages.RECORD_NOT_FOUND, recordId));
			}

			return record;
		} catch (Exception e) {
			logger.severe(String.format("%s: Error while retrieving %s: %s", clientId, recordId, e.toString()));
			throw new Exception(e.getMessage());
		}
	}

}
