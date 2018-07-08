package client.services;

import java.util.ArrayList;
import java.util.List;

import client.models.AuthenticationContext;
import client.models.LoginListener;
import server.DcmsServer;

/**
 * Service for managing user login and the current authentication context
 */
public class AuthenticationService {

	private AuthenticationContext currentUserContext; 
	
	private List<LoginListener> loginListeners;
	
	public AuthenticationService() {
		loginListeners = new ArrayList<>();
	}

	public void login(String serverId, int numericId, DcmsServer connectedServer) {
		currentUserContext = new AuthenticationContext(serverId, numericId, connectedServer);
		loginListeners.forEach(Listener -> Listener.accept(currentUserContext));
	}

	public void addListener(LoginListener listener) {
		loginListeners.add(listener);
	}
	
	public AuthenticationContext getCurrentUserContext() {
		return currentUserContext;
	}
}
