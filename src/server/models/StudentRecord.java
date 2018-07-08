package server.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StudentRecord extends BaseRecord  {

	private List<CourseType> coursesRegistered;
	private StudentStatus status;
	private LocalDateTime statusDate;

	public List<CourseType> getCoursesRegistered() {
		return coursesRegistered;
	}

	public void setCoursesRegistered(List<CourseType> courseRegistered) {
		this.coursesRegistered = courseRegistered;
	}

	public StudentStatus getStatus() {
		return status;
	}

	public void setStatus(StudentStatus status) {
		this.status = status;
	}

	public LocalDateTime getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(LocalDateTime statusDate) {
		this.statusDate = statusDate;
	}

	public StudentRecord() {
	}

	public StudentRecord(StudentRecord other) {
		super(other); 
		coursesRegistered = new ArrayList<>(other.coursesRegistered); 
		status = other.status; 
		statusDate = other.statusDate;
	}
	
	public StudentRecord(String firstName, String lastName, List<CourseType> courseRegistered, StudentStatus status,
			LocalDateTime statusDate) {
		this(null, firstName, lastName, courseRegistered, status, statusDate);
	}

	public StudentRecord(String id, String firstName, String lastName, List<CourseType> courseRegistered, StudentStatus status,
			LocalDateTime statusDate) {
		super(id, firstName, lastName);
		this.coursesRegistered = courseRegistered;
		this.status = status;
		this.statusDate = statusDate;
	}
	
	@Override
	public String getTypeCode() {
		return RecordId.STUDENT_CODE; 
	}
	
	@Override
	public String toString() {
		return "Student: " + super.toString() + " courses=" + coursesRegistered.stream().map(CourseType::toString).collect(Collectors.joining(",")) 
				+ " status=" + status + ", statusDate=" + statusDate;
	}

	@Override
	public BaseRecord clone() {
		return new StudentRecord(this);
	}

	
	
}
