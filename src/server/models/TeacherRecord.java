package server.models;

public class TeacherRecord extends BaseRecord {

	private String address;
	private String phone;
	private CourseType specialization;
	private String location;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public CourseType getSpecialization() {
		return specialization;
	}

	public void setSpecialization(CourseType specialization) {
		this.specialization = specialization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public TeacherRecord() {
		super();
	}

	public TeacherRecord(TeacherRecord other) {
		super(other); 
		address = other.address;
		phone = other.phone;
		specialization = other.specialization;
		location = other.location;
	}
	
	public TeacherRecord(String firstName, String lastName, String address, String phone, CourseType specialization,
			String location) {
		this(null, firstName, lastName, address, phone, specialization, location);
	}
	
	public TeacherRecord(String id, String firstName, String lastName, String address, String phone, CourseType specialization,
			String location) {
		super(id, firstName, lastName);
		this.address = address;
		this.phone = phone;
		this.specialization = specialization;
		this.location = location;
	}

	@Override
	public String getTypeCode() {
		return RecordId.TEACHER_CODE; 
	}
	
	@Override
	public String toString() {
		return "Teacher: " + super.toString() + " address=" + address + " phone=" + phone + ", specialization=" + specialization + ", location=" + location;
	}

	@Override
	public BaseRecord clone() {
		return new TeacherRecord(this); 
	}

}
