package client.views;

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.text.NumberFormatter;

import client.models.AuthenticationContext;
import client.models.LoginListener;
import client.services.AuthenticationService;
import server.ServerManifest;

import javax.swing.JComboBox;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ConnectionDialog extends JDialog {
	private JComboBox<String> serverComboBox = new JComboBox<>();
	private JButton connectButton = new JButton("Connect");
	private JButton refreshButton = new JButton("Refresh");
	private JLabel statusText = new JLabel("Loading.");
	private JProgressBar loadingIndicator = new JProgressBar();
	private ServerManifest serverManifest;
	private AuthenticationService authenticationService;
	private JSpinner userIdSpinner;
	private static final String NoServerPlaceholder = "None";
	
	public ConnectionDialog(JFrame parent, ServerManifest serverManifest, AuthenticationService authenticationService) {
		super(parent, "Server Connection", true);
		this.serverManifest = serverManifest;
		this.authenticationService = authenticationService;

		JPanel controlsPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();

		int space = 15;
		Border spaceBorder = BorderFactory.createEmptyBorder(space, space, space, space);
		Border titleBorder = BorderFactory.createTitledBorder("Connection Information");
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(spaceBorder, titleBorder));

		controlsPanel.setLayout(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();

		gc.gridy = 0;

		Insets rightPadding = new Insets(0, 0, 0, 15);
		Insets noPadding = new Insets(0, 0, 0, 0);

		// Next Row
		// -----------------------------------------------------
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = rightPadding;
		controlsPanel.add(new JLabel("Server: "), gc);

		gc.gridx++;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = noPadding;

		
		JPanel serverSelectionControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		serverSelectionControl.add(serverComboBox);
		serverSelectionControl.add(refreshButton);
		controlsPanel.add(serverSelectionControl, gc);

		// Next Row
 		// -----------------------------------------------------
 		gc.gridy++;
 		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = rightPadding;
		controlsPanel.add(new JLabel("Numeric User Id: "), gc);

		gc.gridx++;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = noPadding;

		userIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
		JFormattedTextField spinnerText = ((JSpinner.NumberEditor) userIdSpinner.getEditor()).getTextField();
		((NumberFormatter) spinnerText.getFormatter()).setAllowsInvalid(false);
		
		Dimension spinnerDimensions = userIdSpinner.getPreferredSize();
		spinnerDimensions.width = 70;
		userIdSpinner.setPreferredSize(spinnerDimensions);
		controlsPanel.add(userIdSpinner, gc);
		
		// Next Row
		// -----------------------------------------------------
		gc.gridy++;
		gc.gridx =0;
		gc.gridwidth = 2;
		gc.anchor = GridBagConstraints.CENTER;
		controlsPanel.add(statusText, gc);

		// Bottom panel
		// -----------------------------------------------------

		Dimension loadingIndicatorSize = loadingIndicator.getPreferredSize();
		loadingIndicatorSize.width = 60;
		loadingIndicator.setPreferredSize(loadingIndicatorSize);
		loadingIndicator.setIndeterminate(true);
		
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(loadingIndicator);
		buttonsPanel.add(refreshButton);
		buttonsPanel.add(connectButton);
		
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
		connectButton.addActionListener(e -> connect());
		refreshButton.addActionListener(e -> refreshServerList());
		
		setLayout(new BorderLayout());
		add(controlsPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);

		setResizable(false);
		setSize(360, 280);
		setLocationRelativeTo(parent);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) { refreshServerList(); }
			@Override
			public void componentResized(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		
	}

	private void refreshServerList() {

		setEmptyServerList();
		setLoadingView(true);
		setInfoText("Resolving server list.");
		
		serverManifest.getServerList().thenAccept((serverList) -> {
			SwingUtilities.invokeLater(() -> {
				setInfoText("Found " + serverList.size() + " server(s) online");
				if(!serverList.isEmpty()) {
					serverComboBox.setModel(new DefaultComboBoxModel<>(serverList.toArray(new String[serverList.size()])));
				}
			});
		}).exceptionally(ex -> {
			SwingUtilities.invokeLater(() -> {
				setErrorText("Could not retrieve server list.");
				setEmptyServerList();
			});

			return null;
		}).whenComplete((a, b) -> SwingUtilities.invokeLater(() -> setLoadingView(false)));
	}

	private void connect() {

		String selectedServer = (String) serverComboBox.getSelectedItem();

		if (selectedServer.equalsIgnoreCase(NoServerPlaceholder)) {
			statusText.setText("No server available.");
			return;
		}
		
		setLoadingView(true);
		setInfoText("Attempting to connect to " + selectedServer);
		
		serverManifest.getServerReference(selectedServer)
		.thenAccept(server -> {

			SwingUtilities.invokeLater(() -> {
				setInfoText("Connection successful.");
				authenticationService.login(selectedServer, (int)userIdSpinner.getValue(), server);
			});
		}).exceptionally(ex -> {
			SwingUtilities.invokeLater(() -> {
				setErrorText("Failed to connect to server " + selectedServer);
			});
			return null;
		}).whenComplete((a, b) -> SwingUtilities.invokeLater(() -> setLoadingView(false)));
		
	}

	private void setLoadingView(boolean isLoading) {
		loadingIndicator.setVisible(isLoading);
		connectButton.setEnabled(!isLoading);
		refreshButton.setEnabled(!isLoading);
	}

	private void setInfoText(String text) {
		statusText.setText(text);
		statusText.setForeground(Color.BLACK);
	}

	private void setErrorText(String text) {
		statusText.setText(text);
		statusText.setForeground(Color.RED);
	}

	private void setEmptyServerList() {
		serverComboBox.setModel(new DefaultComboBoxModel<String>(new String[] { NoServerPlaceholder }));
	}
	
}
