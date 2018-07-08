package client.views.panels;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import client.eventbus.ApplicationEventBus;
import client.services.AuthenticationService;
import client.services.NavigationService;
import client.services.ServerProxy;
import client.views.subviews.CreateStudentView;
import client.views.subviews.CreateTeacherView;
import client.views.subviews.EditFieldView;
import client.views.subviews.RecordCountView;
import client.views.subviews.StudentListView;
import client.views.subviews.SubView;
import client.views.subviews.TeacherListView;
import client.views.subviews.TransferRecordView;
import client.views.subviews.SubViewType;
import client.views.subviews.TargetsRecordSubView;
import server.ServerManifest;

public class MainDisplayPanel extends JPanel {

	private CardLayout cardLayout = new CardLayout();
	private List<SubView> subViews = new ArrayList<>();
	private NavigationService navigationService;  
	
	public MainDisplayPanel(ServerProxy serverProxy, ServerManifest serverManifest, 
			AuthenticationService authenticationService, NavigationService navigationService, 
			ApplicationEventBus eventbus) {
		
		super();
		this.navigationService = navigationService;
		setLayout(cardLayout);
		
		subViews.add(new TeacherListView(serverProxy, eventbus, this::changeToEditView, this::changeToTransferView));
		subViews.add(new StudentListView(serverProxy, eventbus, this::changeToEditView, this::changeToTransferView));
		subViews.add(new CreateTeacherView(serverProxy, serverManifest, eventbus));
		subViews.add(new CreateStudentView(serverProxy, eventbus));
		subViews.add(new EditFieldView(serverProxy, authenticationService, eventbus));
		subViews.add(new TransferRecordView(serverProxy, serverManifest, authenticationService, eventbus));
		subViews.add(new RecordCountView(serverProxy, eventbus));
		subViews.forEach(subView -> add(subView, subView.getViewType().toString()));
	}

	public void showView(SubViewType subView) {
		showView(subView, null);
	}
	
	private void showView(SubViewType subView, String initialRecordId) {
		cardLayout.show(this, subView.toString());
		subViews.stream()
			.filter(sv -> sv.getViewType() == subView)
			.findFirst().get().initialize();
	}
	
	private SubView getView(SubViewType subView) {
		return subViews.stream().filter(sv -> sv.getViewType() == subView).findFirst().get();
	}
	
	private void changeToEditView(String recordId) {
		if(recordId == null) {
			return;
		}
		
		navigationService.requestNavigate(SubViewType.EditField);
		((TargetsRecordSubView)getView(SubViewType.EditField)).setRecordId(recordId);
	}
	
	private void changeToTransferView(String recordId) {
		if(recordId == null) {
			return;
		}
		
		navigationService.requestNavigate(SubViewType.TransferRecord);
		((TargetsRecordSubView)getView(SubViewType.TransferRecord)).setRecordId(recordId);
	}
	
}
