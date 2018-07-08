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
		
		if(args.length != 1) {
			System.out.println("The server must be supplied with an alphanumeric identifier (such as MTL, LVL, DDO):");
			System.exit(1);	
		}
		
		// TODO: server id input
		String serverId = "MTL";
		
		String T = args[0].replaceAll(InvalidServerIdRegex, "").toUpperCase();
				
		if(serverId.isEmpty()) {
			System.out.println("Server identifier must be of alphannumeric characters only. All other characters will be removed.");
			System.exit(1);	
		}
		
		Logger logger = LoggerFactory.createLogger("serverLogs", serverId);
		logger.info(String.format("Server %s starting...", serverId));
		
	
		// Create the server
		ConcreteDcmsServer server = ServerFactory.createServer(serverId, logger, true);
	}
	
}
