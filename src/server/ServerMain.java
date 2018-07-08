package server;
import java.util.logging.Logger;

import common.LoggerFactory;
import server.factories.ServerFactory;


/**
 * Server entry point. Contains main method for server process
 */
public class ServerMain {
	
	private static final String InvalidServerIdRegex = "[^A-Za-z0-9]"; 
	
	public static void main(String[] args) throws Exception {
		
		if(args.length != 2) {
			System.out.println("Usage: server.jar <serverId> <port>");
			System.out.println("Example: server.jar MTL 8080");
			System.exit(1);	
		}
		
		String serverId = args[0].replaceAll(InvalidServerIdRegex, "").toUpperCase();
		
		int serverPort = 0;
		try{
			serverPort = Integer.parseInt(args[1].trim());	
		}catch (Exception e) {
			System.out.println("Could not parse port number.");
			System.exit(1);
		}
		
		if(serverId.isEmpty()) {
			System.out.println("Server identifier must be of alphannumeric characters only. All other characters will be removed.");
			System.exit(1);	
		}
		
		Logger logger = LoggerFactory.createLogger("serverLogs", serverId);
		logger.info(String.format("Server %s starting...", serverId));
	
		// Create the server
		ConcreteDcmsServer server = ServerFactory.createServer(serverPort, serverId, logger, true);
	}
	
}
