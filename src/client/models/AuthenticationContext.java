package client.models;

import server.DcmsServer;

public class AuthenticationContext {

	public String getClientId() {
		return serverId + numericId;
	}
	
	private String serverId; 
	
	private DcmsServer connectedServer; 
	
	private String numericId;

		public String getServerId() {
		return serverId;
	}

	public DcmsServer getConnectedServer() {
		return connectedServer;
	} 
		
	public String getNumericId() {
		return numericId;
	}

		public AuthenticationContext(String serverId, int numericId, DcmsServer connectedServer) {
		this.serverId = serverId;
		this.connectedServer = connectedServer;
		this.numericId = String.format("%4s", numericId%10000).replace(' ', '0');
	}

		
}
