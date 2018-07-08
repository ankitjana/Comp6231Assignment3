package client.views.subviews;

import java.awt.BorderLayout;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import client.eventbus.ApplicationEventBus;
import client.models.StudentTableModel;
import client.services.ServerProxy;
import client.views.panels.NameFilterPanel;
import client.views.panels.RecordsTableActionsPanel;
import client.views.utilities.BorderBuilder;
import server.models.StudentRecord;
import server.models.StudentStatus;

public class StudentListView extends SubView {

	private JTable table;  
	private StudentTableModel tableModel;
	private ApplicationEventBus eventPublisher;
	private ServerProxy serverProxy;
	private NameFilterPanel nameFilterPanel; 
	private RecordsTableActionsPanel actionsPanel;
	
	public StudentListView(ServerProxy serverProxy, ApplicationEventBus eventBus,
			Consumer<String> recordEditListener, Consumer<String> recordTransferListener) {
		super();
		this.serverProxy = serverProxy;
		this.eventPublisher = eventBus;
		setLayout(new BorderLayout());
		
		setBorder(BorderBuilder.createTitledBorder("Student Records"));
		
		tableModel = new StudentTableModel();
		
		table = new JTable(tableModel);
		
		nameFilterPanel = new NameFilterPanel(this::executeQuery, eventBus);
		actionsPanel = new RecordsTableActionsPanel(eventBus, table, recordEditListener, recordTransferListener);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(nameFilterPanel, BorderLayout.LINE_START);
		topPanel.add(actionsPanel, BorderLayout.LINE_END);
		
		add(topPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		tableModel.setData(new ArrayList<>());
		tableModel.fireTableDataChanged();
	}

	@Override
	public SubViewType getViewType() {
		return SubViewType.StudentList;
	}

	@Override
	public void initialize() {
		nameFilterPanel.reset();
		executeQuery(); 
	} 
	
	private void executeQuery() {
		
		String nameFilter = nameFilterPanel.getFilterValue();
		
		CompletionStage<List<StudentRecord>> queryFuture = nameFilter.equals(NameFilterPanel.FILTER_OFF)
				? serverProxy.getStudentRecords()
				: serverProxy.getStudentRecords(nameFilter.charAt(0));
		
		queryFuture.thenAccept(records -> {
			tableModel.setData(records);
			tableModel.fireTableDataChanged();	
		});
	}
	
	
	
	
}
