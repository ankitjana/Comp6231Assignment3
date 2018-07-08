package server.services;

import java.util.List;
import java.util.logging.Logger;

import server.DcmsServer;
import server.ServerManifest;
import server.constants.ErrorMessages;
import server.models.BaseRecord;
import server.models.StudentRecord;
import server.models.TeacherRecord;
import server.storage.DcmsDatabase;
import server.validation.ValidationException;

public class TransferRecordWorkflow {

	private ServerManifest serverManifest; 
	private DcmsDatabase database;
	private Logger logger;
	private String thisServerId;
	
	public TransferRecordWorkflow(ServerManifest serverManifest, DcmsDatabase database, String thisServerId, Logger logger) {
		this.serverManifest = serverManifest;
		this.database = database;
		this.thisServerId = thisServerId;
		this.logger = logger;
	}

	public boolean process(String recordId, String recipientServer, String clientId) throws ValidationException {
				
		BaseRecord removedRecord = null;
		try {
			logger.info(String.format("%s: Starting to transfer record %s to server %s", clientId, recordId, recipientServer));
			
			List<String> availableServers = serverManifest.getServerListExceptSync(thisServerId);
			
			if(!availableServers.contains(recipientServer)) {
				throw new ValidationException(String.format(ErrorMessages.LOCATION_UNSUPPORTED, recipientServer, String.join(", ", availableServers)));
			}
			
			DcmsServer server = serverManifest.getServerReferenceSync(recipientServer);
			BaseRecord record = database.remove(recordId);
			
			boolean transferSuccess = record instanceof TeacherRecord
					? server.acceptTeacherRecord((TeacherRecord)record, clientId) 
					: server.acceptStudentRecord((StudentRecord)record, clientId);	

			logger.info(String.format("%s: Success: transfer of record %s to server %s", clientId, recordId, recipientServer));
					
		} catch (ValidationException e) {
			logger.warning(String.format("%s: Validation failed during transfer of %s to %s: %s", clientId, recordId, recipientServer, e.getMessage()));
			rollBack(removedRecord);
			throw new ValidationException(e.getMessage());
		} catch (Exception e) {
			logger.severe(String.format("%s: Record transfer failed for %s to %s: %s", clientId, recordId, recipientServer, e.getMessage()));
			rollBack(removedRecord);
			throw new ValidationException(e.getMessage());
		}
		
		return true; 
	}
	
	private void rollBack(BaseRecord record) {
		
		if(record == null) {
			return; 
		}
		
		try {
			database.insert(record);	
		}catch (ValidationException e) {
			// Exception can only occur if the entry already exists
		}
	}
	
}
