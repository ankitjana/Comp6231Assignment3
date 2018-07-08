package client.views.panels;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.models.AuthenticationContext;

public class ConnectivityPanel extends JPanel {

	private JLabel connectivityInfoField = new JLabel("");

	private static final String CONNECTED_PREFIX = "Client id: ";
	
	public ConnectivityPanel() {
		super();

		connectivityInfoField.setFont(new Font("Sans Serif", Font.PLAIN, 18));

		setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(connectivityInfoField);
	}
	
	public void setConnectivityInfo(AuthenticationContext authenticationContext) {
		connectivityInfoField.setText(CONNECTED_PREFIX + authenticationContext.getClientId());
	}
	
}
