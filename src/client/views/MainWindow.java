package client.views;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import client.eventbus.ApplicationEvent;
import client.eventbus.ApplicationEventBus;
import client.models.AuthenticationContext;
import client.services.AuthenticationService;
import client.services.LoggingService;
import client.services.ServerProxy;
import client.views.panels.ConnectivityPanel;
import client.views.panels.MainDisplayPanel;
import client.views.panels.MenuPanel;
import client.views.panels.StatusBarPanel;
import client.views.subviews.SubViewType;
import server.ServerManifest;

public class MainWindow extends JFrame {

	private MenuPanel menuPanel;
	private MainDisplayPanel mainDisplay;
	private StatusBarPanel statusBarPanel; 
	private JDialog connectionDialog;
	private ApplicationEventBus applicationEventBus;
	private ConnectivityPanel connectivityPanel;
	private AuthenticationService authenticationService;
	
	public MainWindow(String title, ApplicationEventBus applicationEventBus, 
			ServerManifest serverManifest, 
			AuthenticationService authenticationService, 
			ServerProxy serverProxy,
			LoggingService loggingService) {
		
		super(title);
		this.applicationEventBus = applicationEventBus;
		this.authenticationService = authenticationService;
		
		setSize(1040, 600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		connectivityPanel = new ConnectivityPanel();
		statusBarPanel = new StatusBarPanel(applicationEventBus);
		menuPanel = new MenuPanel(this::onPageChanged, this::onLogout, applicationEventBus);
		connectionDialog = new ConnectionDialog(this, serverManifest, authenticationService);
		mainDisplay = new MainDisplayPanel(serverProxy, serverManifest, authenticationService, menuPanel, applicationEventBus);
		
		add(menuPanel, BorderLayout.WEST);
		add(statusBarPanel, BorderLayout.SOUTH);
		add(connectivityPanel, BorderLayout.NORTH);
		add(mainDisplay, BorderLayout.CENTER);
		
		setSize(1000, 600);
		setVisible(true);
		setLocationRelativeTo(null);
		
		authenticationService.addListener(this::onLogin);
		showLoginDialog(); 
	}
	
	private void onPageChanged(SubViewType newPage) {
		mainDisplay.showView(newPage);
	}
	
	private void onLogout() {
		showLoginDialog(); 
	}
	
	private void showLoginDialog() {
		applicationEventBus.raiseEvent(ApplicationEvent.loading("Waiting for connection."));
		connectionDialog.setVisible(true);
	}
	
	private void onLogin(AuthenticationContext authenticationContext) {
		connectionDialog.setVisible(false);
		applicationEventBus.raiseEvent(ApplicationEvent.success("Connection established as " + authenticationContext.getClientId()));
		mainDisplay.showView(SubViewType.TeacherList);
		menuPanel.reset();
		connectivityPanel.setConnectivityInfo(authenticationContext);
	}
	
	
}
