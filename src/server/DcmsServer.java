package server;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import server.models.BaseRecord;
import server.models.CourseType;
import server.models.RecordCount;
import server.models.StudentRecord;
import server.models.StudentStatus;
import server.models.TeacherRecord;

@WebService
@SOAPBinding(style=Style.RPC)
public interface DcmsServer {

	/**
	 * Creates a new teacher record
	 * @param firstName First name of record
	 * @param lastName Last name of record
	 * @param address Address of teacher record
	 * @param phone Phone number of teacher record
	 * @param specialization Specialization of teacher record
	 * @param location Location of teacher
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return Created teacher record
	 * @throws Exception
	 */
	TeacherRecord createTeacherRecord(String firstName, String lastName, String address, String phone,
			CourseType specialization, String location, String clientId) throws Exception;

	/**
	 * Creates a new student record
	 * @param firstName First name of record
	 * @param lastName Last name of record
	 * @param coursesRegistered Courses that student is registered for. Student must be registered for at least one course.
	 * @param status Student status (either active or inactive)
	 * @param statusDate Last date the status became active or inactive
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return Created student record
	 * @throws Exception
	 */
	StudentRecord createStudentRecord(String firstName, String lastName,
			List<CourseType> coursesRegistered, StudentStatus status, String statusDate,
			String clientId) throws Exception;
	
	/**
	 * Edits the field of the specified record
	 * @param id Record id to edit
	 * @param fieldName Name of field on record to edit. 
	 * For student records: Status, StatusDate and CoursesRegistered can be changed. 
	 * For teacher records: address, phone and location can be changed. 
	 * @param newValue New value to apply to record.
	 * @param clientId Manager client id that issued the request (used for logging purposes) 
	 * @return Edited record
	 * @throws Exception
	 */
	BaseRecord editRecord(String id, String fieldName, String newValue, String clientId) throws Exception;

	/**
	 * Retrieves record counts among all visible servers. In addition to returning a local count, 
	 * this server will contact sibling servers via udp to obtain and return their counts.
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return List of record counts organized on a per server basis
	 * @throws Exception
	 */
	List<RecordCount> getRecordCounts(String clientId) throws Exception;

	/***
	 * Retrieves all teacher records stored in this server.
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return All teacher records stored in this server.
	 * @throws Exception
	 */
	List<TeacherRecord> getTeachers(String clientId) throws Exception;

	/***
	 * Retrieves all student records stored in this server.
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return All teacher records stored in this server.
	 * @throws Exception
	 */
	List<StudentRecord> getStudents(String clientId) throws Exception;

	/***
	 * Retrieves all teacher records stored in this server that belong to the specified last name character bucket.
	 * @param lastNameCharFilter Last name character filter to apply
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return List of teacher records
	 * @throws Exception
	 */
	List<TeacherRecord> getTeachersWithNameFilter(char lastNameCharFilter, String clientId) throws Exception;

	/***
	 * Retrieves all student records stored in this server that belong to the specified last name character bucket.
	 * @param lastNameCharFilter Last name character filter to apply
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return List of student records
	 * @throws Exception
	 */
	List<StudentRecord> getStudentsWithNameFilter(char lastNameCharFilter, String clientId) throws Exception;

	/**
	 * Gets a specific teacher specified by id
	 * @param recordId Record id to search for
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return Retrieved teacher record. Null if not found
	 * @throws Exception
	 */
	TeacherRecord getTeacher(String recordId, String clientId) throws Exception;

	/**
	 * Gets a specific student specified by id
	 * @param recordId Record id to search for
	 * @param clientId Manager client id that issued the request (used for logging purposes)
	 * @return Retrieved student record. Null if not found
	 * @throws Exception
	 */
	StudentRecord getStudent(String recordId, String clientId) throws Exception;

	
	/**
	 * Initiates a transfer 
	 * @param recordId record to transfer
	 * @param recipientServer server to transfer to
	 * @param clientId manager client that initiated the request
	 * @throws Exception
	 * @return True if operation was successful
	 */
	boolean transferRecord(String recordId, String recipientServer, String clientId) throws Exception;

	/**
	 * Accepts an existing teacher record. Used during the transfer process.
	 * @param record record to transfer
	 * @param clientId manager client that initiated the request
	 * @throws Exception
	 * @return True if operation was successful
	 */
	boolean acceptTeacherRecord(TeacherRecord record, String clientId) throws Exception;

	/**
	 * Accepts an existing student record. Used during the transfer process.
	 * @param record record to transfer
	 * @param clientId manager client that initiated the request
	 * @throws Exception
	 * @return True if operation was successful
	 */
	boolean acceptStudentRecord(StudentRecord record, String clientId) throws Exception;

	/**
	 * Pings the server
	 * @return True if the ping succeeded.
	 */
	boolean ping();

	/**
	 * Fetches the UDP port used by the server
	 * @return UDP port used by the server
	 */
	int getUdpServerPort();
	
	/**
	 * Fetches the server id assigned to the server (such as MTL)
	 * @return Server id
	 */
	String getServerId();
	
}
