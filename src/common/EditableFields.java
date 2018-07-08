package common;

public class EditableFields {

	public static final String ADDRESS = "Address";
	
	public static final String PHONE = "Phone"; 
	
	public static final String LOCATION = "Location"; 
	
	public static final String STATUS = "Status"; 
	
	public static final String STATUS_DATE = "StatusDate"; 
	
	public static final String COURSES_REGISTERED = "CoursesRegistered";
	
	/**
	 * Editable fields on a teacher entity
	 */
	public static final String[] TEACHER_EDITABLE_FIELDS = {ADDRESS, PHONE, LOCATION};
	
	/**
	 * Editable fields on a student entity
	 */
	public static final String[] STUDENT_EDITABLE_FIELDS = {STATUS, STATUS_DATE, COURSES_REGISTERED};
	
}
