package server.factories;

import java.util.logging.Logger;

import server.ConcreteDcmsServer;
import server.ServerManifest;
import server.constants.SeedData;
import server.models.BaseRecord;

import server.recordcount.RecordCountTaskFactory;
import server.recordcount.UdpClient;
import server.recordcount.UdpServer;
import server.services.InsertRecordWorkflow;
import server.services.TransferRecordWorkflow;
import server.services.EditFieldWorkflow;
import server.storage.DcmsDatabase;
import server.storage.InMemoryDcmsDatabase;

public class ServerFactory {

	public static ConcreteDcmsServer createServer(String serverId, Logger logger, boolean seedData) throws Exception {
		ServerManifest  serverManifest = new ServerManifest();
		DcmsDatabase database = new InMemoryDcmsDatabase(serverId);
		InsertRecordWorkflow createRecordWorkflow = new InsertRecordWorkflow(database, logger);
		EditFieldWorkflow editFieldWorkflow = new EditFieldWorkflow(database, serverManifest, logger);
		TransferRecordWorkflow transferRecordWorkflow = new TransferRecordWorkflow(serverManifest, database, serverId, logger);
		
		UdpServer recordCountServer = new UdpServer(logger, database);
		UdpClient recordCountServerProxy = new UdpClient(logger, serverManifest, serverId);
		RecordCountTaskFactory countRecordsWorkflow = new RecordCountTaskFactory(recordCountServerProxy, database, serverManifest, serverId, logger);
		
		ConcreteDcmsServer server = new ConcreteDcmsServer(
				database, 
				createRecordWorkflow, 
				countRecordsWorkflow, 
				editFieldWorkflow, 
				transferRecordWorkflow,
				recordCountServer,
				logger);
		
		if(seedData) {
			seedData(database, serverId);
		}
		
		// Start UDP server for record counts
		new Thread(recordCountServer).start();
		
		// Start UDP server client proxy for records count
		new Thread(recordCountServerProxy).start();

		return server;
	}
	
	private static void seedData(DcmsDatabase database, String serverId) throws Exception {
		
		for(BaseRecord record : SeedData.getTeacherSeeds(serverId)) {
			database.insert(record);
		}
		for(BaseRecord record : SeedData.getStudentSeeds(serverId)) {
			database.insert(record);
		}
	}
	
}
