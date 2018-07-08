package client.views.subviews;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import client.eventbus.ApplicationEvent;
import client.eventbus.ApplicationEventBus;
import client.services.AuthenticationService;
import client.services.ServerProxy;
import server.ServerManifest;

public class TransferRecordView extends TargetsRecordSubView {

	private JButton transferButton = new JButton("Transfer");
	private JComboBox<String> locationComboBox = new JComboBox<>();
	
	private ServerProxy serverProxy;
	private ServerManifest serverManifest; 
	private ApplicationEventBus eventBus;
	private AuthenticationService authenticationService;
	
	private static final String NO_OTHER_SERVERS_PLACEHOLDER = "No Other Servers";
	
	public TransferRecordView(ServerProxy serverProxy, ServerManifest serverManifest, 
			AuthenticationService authenticationService, ApplicationEventBus eventBus) {
		super(authenticationService, "Transfer Record");
		this.serverProxy = serverProxy;
		this.serverManifest = serverManifest;
		this.authenticationService = authenticationService;
		this.eventBus = eventBus;

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++; 
		gc.insets = new Insets(10, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		JLabel editSectionTitle = new JLabel("Transfer Record Information:");
		editSectionTitle.setFont(new Font("Sans Serif", Font.BOLD, 14));
		add(editSectionTitle, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Transfer to: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		locationComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {NO_OTHER_SERVERS_PLACEHOLDER}));
		
		add(locationComboBox, gc);

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 3;
		gc.gridy++;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.LAST_LINE_END;
		transferButton.setPreferredSize(new Dimension(200, 32));
		add(transferButton, gc);
		
		transferButton.addActionListener(e -> executeTransfer());
	}
	
	@Override
	public SubViewType getViewType() {
		return SubViewType.TransferRecord;
	}

	@Override
	public void initialize() {
		super.initialize();
		eventBus.raiseEvent(ApplicationEvent.loading("Retrieving available locations for record transfer."));
		
		String connectedServer = authenticationService.getCurrentUserContext().getServerId();
		
		serverManifest.getServerListExcept(connectedServer)
			.exceptionally((e) -> {
				return new ArrayList<>();
			}).thenAccept((servers) -> {
				eventBus.raiseEvent(ApplicationEvent.success("Available transfer servers: " + servers)); 
				
				String[] serversDisplayed = servers.isEmpty() ? new String[] { NO_OTHER_SERVERS_PLACEHOLDER } : servers.toArray(new String[0]);
				
				SwingUtilities.invokeLater(() -> {
					locationComboBox.setModel(new DefaultComboBoxModel<String>(serversDisplayed));
				});
			});
	}
	
	private void executeTransfer() {
		
		String transferToLocation = (String)locationComboBox.getSelectedItem();
		
		if(transferToLocation == null || transferToLocation.isEmpty() || transferToLocation.equals(NO_OTHER_SERVERS_PLACEHOLDER)) {
			return;
		}
		
		String recordId = refreshGeneratedId();
		serverProxy.transferRecord(recordId, transferToLocation);
	}

}
