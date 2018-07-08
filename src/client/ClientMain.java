package client;

import javax.swing.SwingUtilities;

import client.eventbus.ApplicationEventBus;
import client.services.AuthenticationService;
import client.services.LoggingService;
import client.services.ServerProxy;
import client.views.MainWindow;
import server.ServerManifest;

/**
 * Entry point for client application
 * 
 * NOTE: The Orb Daemon process must be ran prior:
 * 
 * If orbd is on path variable: orbd -ORBInitialPort 1050
 * 
 * If not on path variable: C:\Program Files\Java\jdk\bin\orbd.exe"
 * -ORBInitialPort 1050
 *
 */
public class ClientMain {

	public static void main(String[] args) throws Exception {

		// OrbContext orbContext = new OrbContext(args);
		AuthenticationService authenticationService = new AuthenticationService();
		ApplicationEventBus eventBus = new ApplicationEventBus();
		LoggingService loggingService = new LoggingService(eventBus, authenticationService);
		ServerManifest serverManifest = new ServerManifest();
		ServerProxy serverProxy = new ServerProxy(eventBus, authenticationService);

		SwingUtilities.invokeLater(() -> new MainWindow("Manager Client", eventBus, serverManifest,
				authenticationService, serverProxy, loggingService));
	}

}
