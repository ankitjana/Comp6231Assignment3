package client.views.panels;

import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import client.eventbus.ApplicationEventBus;

public class RecordsTableActionsPanel extends JPanel {

	private JButton editButton = new JButton("Edit");
	private JButton transferButton = new JButton("Transter");
	private JTable table;
	
	
	public RecordsTableActionsPanel(ApplicationEventBus eventPublisher, JTable table,
			Consumer<String> recordEditListener, Consumer<String> recordTransferListener) {
		
		setLayout(new FlowLayout(FlowLayout.TRAILING));
		editButton.setEnabled(false);
		transferButton.setEnabled(false);
		
		this.table = table;
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					int row = table.getSelectedRow();
					editButton.setEnabled(row >= 0);
					transferButton.setEnabled(row >= 0);
				}
			}
		});
		
		add(editButton);
		add(transferButton);
		
		eventPublisher.addListener(e -> {
			boolean isEnabled = table.getSelectedRow() >= 0 && !e.isLoading();
			editButton.setEnabled(isEnabled);
			transferButton.setEnabled(isEnabled);	
		}); 
		
		editButton.addActionListener(e -> recordEditListener.accept(getSelectedRecordId()));
		transferButton.addActionListener(e -> recordTransferListener.accept(getSelectedRecordId()));
	}
	
	private String getSelectedRecordId() {
		
		int selectedRow = table.getSelectedRow();
		AbstractTableModel tableModel = (AbstractTableModel)table.getModel();
		return selectedRow >= 0 ? tableModel.getValueAt(selectedRow, 0).toString() : null; 
	}
	
	
}
