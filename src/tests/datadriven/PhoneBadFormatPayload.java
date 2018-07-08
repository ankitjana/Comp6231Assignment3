package tests.datadriven;

/**
 * Container class used to store the values for malformed phone number data
 * driven inputs
 */
public class PhoneBadFormatPayload {
	String phone;

	public PhoneBadFormatPayload(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}
}
