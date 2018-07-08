package server.models;

import java.io.Serializable;

/**
 * Base record class
 * Ideally this would be abstract however we make it creatable 
 * for deserialization usage with CORBA
 *
 */
public class BaseRecord implements Serializable {
	private String id;
	private String firstName;
	private String lastName;
	
	/**
	 * An extra version number field is included to keep track of how many times the entity has been updated. 
	 * This is useful when checking that concurrent updates are working correctly
	 */
	private int versionNumber;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public int getVersionNumber() { 
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public BaseRecord() {
		versionNumber = 1; 
	}

	public BaseRecord(BaseRecord other) {
		this(other.id, other.firstName, other.lastName);
		versionNumber = other.versionNumber;
	}
	
	public BaseRecord(String id, String firstName, String lastName) {
		this(); 
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getTypeCode() {
		return ""; 
	}
	
	@Override
	public String toString() {
		return "id=" + id + " firstName=" + firstName + " lastName=" + lastName;
	}

	public BaseRecord clone(){
		return new BaseRecord(id, firstName, lastName);
	}
	
}
