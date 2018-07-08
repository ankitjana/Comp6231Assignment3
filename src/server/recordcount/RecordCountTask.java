package server.recordcount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.xml.bind.ValidationException;

import server.models.RecordCount;
import server.storage.DcmsDatabase;

public class RecordCountTask implements Consumer<RecordCount> {

	private UdpClient udpServerProxy;
	private String clientId; 
	private Logger logger;
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private HashMap<String, RecordCount> recordCountResponses = new HashMap<>();  
	private List<String> otherServers;
	private DcmsDatabase database;
	
	public RecordCountTask(UdpClient udpServerProxy, DcmsDatabase database, List<String> targetServers, String clientId, Logger logger) {
		this.udpServerProxy = udpServerProxy;
		this.database = database;
		this.otherServers = targetServers; 
		this.clientId = clientId; 
		this.logger = logger;
	}
	
	public List<RecordCount> execute() throws ValidationException{
		
		try {
				
			logger.info(String.format("%s: Calculating record counts", clientId));
			
			// No other servers to query. Just return result from ourselves
			if(otherServers.isEmpty()) {
				RecordCount recordCount = database.getCount();
				logger.info(String.format("%s: Records counted: %s", clientId, recordCount.toString())); 
				return new ArrayList<>(Arrays.asList(recordCount)); 
			}
			
			udpServerProxy.addReponseListener(this);
			logger.info(String.format("%s: Preparing to query other servers: %s", clientId, String.join(",", otherServers))); 
			otherServers.forEach(serverId -> udpServerProxy.getCount(clientId, serverId));
			
			// Wait 6 seconds to receive a response from the server
			boolean completed = countDownLatch.await(6, TimeUnit.SECONDS);
			
			if(!completed) {
				logger.warning(String.format("%s: Record count request timed out. Returning partial results", clientId));
			}
			
			ArrayList<RecordCount> results = new ArrayList<>(recordCountResponses.values()); 
			results.add(database.getCount());
			logger.info(String.format("%s: Records count complete: %s", clientId, results.toString()));
			return results;
		}catch (Exception e) {
			logger.severe(String.format("%s: Error while counting records: %s", clientId, e.getMessage()));
			throw new ValidationException(e.getMessage());
		}
		finally {
			udpServerProxy.removeResponseListener(this);
		}
	}
	
	
	@Override
	public void accept(RecordCount response) {
		logger.info(String.format("%s: Received response: %s", clientId, response)); 
		
		synchronized (recordCountResponses) {
			recordCountResponses.put(response.getServerId(), response);
			
			// All servers have responded, signal the countdown latch 
			if(otherServers.stream().allMatch(s -> recordCountResponses.containsKey(s))) {
				countDownLatch.countDown();
			}
		}
		
	}
	
}
