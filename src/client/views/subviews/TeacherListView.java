package client.views.subviews;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.eventbus.ApplicationEventBus;
import client.models.TeacherTableModel;
import client.services.ServerProxy;
import client.views.panels.NameFilterPanel;
import client.views.panels.RecordsTableActionsPanel;
import client.views.utilities.BorderBuilder;
import server.models.TeacherRecord;

public class TeacherListView extends SubView {

	private JTable table;  
	private TeacherTableModel tableModel;
	private ServerProxy serverProxy;
	private ApplicationEventBus eventBus;
	private NameFilterPanel nameFilterPanel;
	private RecordsTableActionsPanel actionsPanel; 
	
	public TeacherListView(ServerProxy serverProxy, ApplicationEventBus eventBus, 
			Consumer<String> recordEditListener, Consumer<String> recordTransferListener) {
		
		super();
		this.serverProxy = serverProxy;
		this.eventBus = eventBus;
		setLayout(new BorderLayout());
		
		setBorder(BorderBuilder.createTitledBorder("Teacher Records"));
		
		tableModel = new TeacherTableModel();		
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
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
		return SubViewType.TeacherList;
	}

	@Override
	public void initialize() {
		nameFilterPanel.reset();
		executeQuery(); 
	} 
	
	private void executeQuery() {
		
		String nameFilter = nameFilterPanel.getFilterValue();
		
		CompletionStage<List<TeacherRecord>> queryFuture = nameFilter.equals(NameFilterPanel.FILTER_OFF)
				? serverProxy.getTeacherRecords()
				: serverProxy.getTeacherRecords(nameFilter.charAt(0));
		
		queryFuture.thenAccept(records -> {
			tableModel.setData(records);
			tableModel.fireTableDataChanged();	
		});
	}
	
	
}
