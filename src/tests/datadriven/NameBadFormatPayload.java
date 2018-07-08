package tests.datadriven;


/**
 * Container class used to store the values for malformed name data driven
 * inputs
 */
public class NameBadFormatPayload {
	String value;

	public NameBadFormatPayload(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}