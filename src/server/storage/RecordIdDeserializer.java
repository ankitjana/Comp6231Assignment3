package server.storage;

import java.util.Arrays;

import server.constants.ErrorMessages;
import server.models.RecordId;
import server.models.RecordType;
import server.validation.ValidationException;

public class RecordIdDeserializer {
	
	public static RecordId extract(String id) throws ValidationException {
		
		if(id == null || id.isEmpty()) {
			throw new ValidationException(ErrorMessages.RECORD_ID_MISSING);
		}
		
		String[] sections = id.split("_");
		
		if(sections.length != 2) {
			throw new ValidationException(ErrorMessages.RECORD_ID_MALFORMED);
		}
		
		String serverId = sections[0]; 
		String recordTypeAndId = sections[1]; 
		
		if(recordTypeAndId.length() != RecordId.LENGTH) {
			throw new ValidationException(String.format(ErrorMessages.RECORD_ID_INVALID_LENGTH, id, RecordId.LENGTH));
		}
		
		String recordType = recordTypeAndId.substring(0, RecordId.PREFIX_LENGTH);
		if(!Arrays.asList(RecordId.ALLOWED_TYPE_PREFIXES).contains(recordType)) {
			throw new ValidationException(String.format(ErrorMessages.RECORD_ID_INVALID_RECORD_TYPE_CODE, id));
		}
		
		int numericPart; 
		try {  
	    	 numericPart = Integer.parseInt(recordTypeAndId.substring(RecordId.PREFIX_LENGTH, recordTypeAndId.length()));  
	    } catch (NumberFormatException e) {  
	         throw new ValidationException(String.format(ErrorMessages.RECORD_ID_INVALID_NUMERIC_PORTION, id));
	    }
	    
		return new RecordId(serverId, recordType, numericPart);
	}
	
	public static RecordId extractOrEmpty(String id) {
		
		try {
			return extract(id); 
		}catch (Exception e) {
			return new RecordId("", "", 0); 
		}
	}
	
	public static RecordType getRecordType(String id) throws ValidationException {
		return extract(id).getTypeCode().equals(RecordId.STUDENT_CODE) ? RecordType.Student : RecordType.Teacher;
	}
	
}
