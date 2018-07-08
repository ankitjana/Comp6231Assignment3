package server.recordcount;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import server.ServerManifest;
import server.storage.DcmsDatabase;

public class RecordCountTaskFactory {

	private UdpClient udpServerProxy;
	private Logger logger;
	private ServerManifest serverManifest;
	private String thisServerId;
	private DcmsDatabase database; 
	
	public RecordCountTaskFactory(UdpClient udpServerProxy, DcmsDatabase database, ServerManifest serverManifest, String thisServerId, Logger logger) {
		this.udpServerProxy = udpServerProxy;
		this.database = database;
		this.serverManifest = serverManifest;
		this.thisServerId = thisServerId; 
		this.logger = logger;
	}
	
	public RecordCountTask create(String clientId) {
		
		// Get other servers except this one
		List<String> otherServers = serverManifest.getServerListExceptSync(thisServerId);
		return new RecordCountTask(udpServerProxy, database, otherServers, clientId, logger);
	}
	
}
