package client.views.subviews;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import client.eventbus.ApplicationEventBus;
import client.models.RecordCountTableModel;
import client.services.ServerProxy;
import client.views.utilities.BorderBuilder;
import server.models.RecordCount;

public class RecordCountView extends SubView {

	private JButton refreshButton = new JButton("Refresh");
	private JTable table;  
	private RecordCountTableModel tableModel = new RecordCountTableModel();
	private ApplicationEventBus eventBus;
	private ServerProxy serverProxy;
	
	public RecordCountView(ServerProxy serverProxy, ApplicationEventBus eventBus) {
		super();
		this.serverProxy = serverProxy;
		this.eventBus = eventBus;
		setLayout(new BorderLayout());

		setBorder(BorderBuilder.createTitledBorder("Record Count By Center"));

		table = new JTable(tableModel);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPanel.add(refreshButton);
		add(topPanel, BorderLayout.NORTH);
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		tableModel.setData(new ArrayList<>());
		tableModel.fireTableDataChanged();

		refreshButton.addActionListener(e -> refresh());
		eventBus.addListener(e -> refreshButton.setEnabled(!e.isLoading()));
	}

	@Override
	public SubViewType getViewType() {
		return SubViewType.RecordCount;
	}

	@Override
	public void initialize() {
		refresh();
	}

	private void refresh(){
		serverProxy.getRecordCounts().thenAccept(records -> {
			SwingUtilities.invokeLater(() -> {
				tableModel.setData(records);
				tableModel.fireTableDataChanged();
			});
		});
	}

}
