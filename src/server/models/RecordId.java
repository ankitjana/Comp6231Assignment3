package server.models;

public class RecordId {
	
	/**
	 * Format of id 
	 * {serverPrefix}_{SR/TR}{numericId}
	 */
	public static final String ID_FORMAT = "%s_%s%s"; 
	public static final int PREFIX_LENGTH = 2;
	public static final int NUMERIC_LENGTH = 5;
	public static final int LENGTH = PREFIX_LENGTH + NUMERIC_LENGTH;
	public static final String STUDENT_CODE = "SR"; 
	public static final String TEACHER_CODE = "TR"; 
	
	public static final String[] ALLOWED_TYPE_PREFIXES = new String[] {
		STUDENT_CODE, 
		TEACHER_CODE
	};
	
	public int numericId;

	private String recordTypeCode;

	private String serverPrefix; 

	public RecordId(String serverPrefix, String recordType, int numericId) {
		
		int maxNumericPart = (int)Math.pow(10, RecordId.NUMERIC_LENGTH);
		
		this.serverPrefix = serverPrefix; 
		this.numericId = numericId%maxNumericPart;
		this.recordTypeCode = recordType;
	}

	public int getNumericId() {
		return numericId;
	}

	public String getTypeCode() {
		return recordTypeCode;
	}
	
	public RecordType getRecordType() {
		return getTypeCode().equals(RecordId.STUDENT_CODE) ? RecordType.Student : RecordType.Teacher;
	}

	public String getServerPrefix() {
		return serverPrefix;
	}
	
	@Override
	public String toString() {
		String formattedNumericPart = String.format("%" + RecordId.NUMERIC_LENGTH +"s", numericId).replace(' ', '0');
		return String.format(RecordId.ID_FORMAT, serverPrefix, recordTypeCode, formattedNumericPart);
	}
	
}
