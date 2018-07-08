package server.storage;

import java.util.concurrent.atomic.AtomicInteger;

import server.models.StudentRecord;
import server.models.BaseRecord;
import server.models.RecordId;

public class RecordIdGenerator {
	
	private AtomicInteger studentAutoIncrementId = new AtomicInteger(0); 
	private AtomicInteger teacherAutoIncrementId = new AtomicInteger(0);
	private String serverId; 

	public RecordIdGenerator(String serverId) {
		this.serverId = serverId;
	}
	
	public static <T extends BaseRecord> String getTypeCode(Class<T> type) {
		return type.isAssignableFrom(StudentRecord.class) ? RecordId.STUDENT_CODE : RecordId.TEACHER_CODE;
	}
	
	public<T extends BaseRecord> String createId(Class<T> type) {
		
		String typeCode = getTypeCode(type);
		int numericPart = typeCode.equals(RecordId.STUDENT_CODE) 
				? studentAutoIncrementId.addAndGet(1) 
				: teacherAutoIncrementId.addAndGet(1);
				
		return new RecordId(serverId, typeCode, numericPart).toString();
	}
	
}
