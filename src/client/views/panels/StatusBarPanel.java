package client.views.panels;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import client.eventbus.ApplicationEventBus;
import client.eventbus.ApplicationEventType;

public class StatusBarPanel extends JPanel {

	private JProgressBar loadingProgressBar = new JProgressBar();
	private JLabel statusText = new JLabel("Application started.");

	public StatusBarPanel(ApplicationEventBus eventBus) {
		super();
		setBorder(new EmptyBorder(0, 8, 4, 8));

		setLayout(new FlowLayout(FlowLayout.RIGHT));
		loadingProgressBar.setIndeterminate(true);
		
		eventBus.addListener((event) -> {
			statusText.setText(event.getDetails());
			statusText.setForeground(event.getEventType() == ApplicationEventType.Error ? Color.RED : Color.BLACK);
			loadingProgressBar.setVisible(event.getEventType() == ApplicationEventType.Loading);
		});

		add(statusText);
		add(loadingProgressBar);
		loadingProgressBar.setVisible(false);
	}

}
