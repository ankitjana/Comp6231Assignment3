package client.services;

import java.util.logging.Logger;

import client.eventbus.ApplicationEventType;
import client.eventbus.ApplicationEvent;
import client.eventbus.ApplicationEventBus;
import client.models.AuthenticationContext;
import common.LoggerFactory;

public class LoggingService {

	private Logger logger;
	private ApplicationEventBus eventPublisher;
	private AuthenticationService authenticationService;
	
	public LoggingService(ApplicationEventBus eventPublisher, AuthenticationService authenticationService) {
		this.eventPublisher = eventPublisher;
		this.authenticationService = authenticationService; 
		
		eventPublisher.addListener(this::processAppEvent);
		authenticationService.addListener(this::onLogin);
	}
	
	public void onLogin(AuthenticationContext authenticationContext) {
		
		try {
			logger = LoggerFactory.createLogger("clientLogs", authenticationContext.getClientId());	
		}catch (Exception e) {
			logger = null;
		}
		
	}
	
	public Logger getActiveLogger() {
		return logger; 
	}
	
	private void processAppEvent(ApplicationEvent appEvent) {
		
		Logger localLogger = logger;
		if(localLogger == null)
			return;
		
		if(appEvent.getEventType() == ApplicationEventType.Loading) {
			localLogger.info(appEvent.getDetails());
		}else if(appEvent.getEventType() == ApplicationEventType.Success) {
			localLogger.info(appEvent.getDetails());
		}else if(appEvent.getEventType() == ApplicationEventType.Error) {
			localLogger.severe(appEvent.getDetails());
		}
	}
	
	
}
