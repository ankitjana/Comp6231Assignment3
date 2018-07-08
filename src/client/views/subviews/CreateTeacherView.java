package client.views.subviews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.*;

import client.eventbus.ApplicationEvent;
import client.eventbus.ApplicationEventBus;
import client.services.ServerProxy;
import client.views.utilities.BorderBuilder;
import server.ServerManifest;
import server.models.CourseType;

public class CreateTeacherView extends SubView {

	private JTextField firstNameField = new JTextField(16);
	private JTextField lastNameField = new JTextField(16);
	private JTextField addressField = new JTextField(16);
	private JTextField phoneField = new JTextField(16);
	private JComboBox<CourseType> specializationComboBox = new JComboBox<>();
	private JComboBox<String> locationComboBox = new JComboBox<>();
	
	private JButton createButton = new JButton("Create");
	private ApplicationEventBus eventBus;
	private ServerProxy serverProxy;
	private ServerManifest serverManifest;
	
	public CreateTeacherView(ServerProxy serverProxy, ServerManifest serverManifest, ApplicationEventBus eventBus) {
		super();
		this.serverProxy = serverProxy;
		this.serverManifest = serverManifest;
		this.eventBus = eventBus;
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		setBorder(BorderBuilder.createTitledBorder("New Teacher Record"));
		
		gc.gridy = -1;
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++; 
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("First Name: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		add(firstNameField, gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Last Name: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		add(lastNameField, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Address: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		add(addressField, gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Phone: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		add(phoneField, gc);
		
		// Next Row 
		// -----------------------------------------------------
		
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Specialization: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		DefaultComboBoxModel<CourseType> specializationsModel = new DefaultComboBoxModel<CourseType>(CourseType.values());
		specializationComboBox.setModel(specializationsModel);
		add(specializationComboBox, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Location: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		locationComboBox.setModel(new DefaultComboBoxModel<String>(new String[0]));
		add(locationComboBox, gc);

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 3;
		gc.gridy++;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.LAST_LINE_END;
		createButton.setPreferredSize(new Dimension(200, 32));
		add(createButton, gc);
		
		createButton.addActionListener((e) -> createTeacher());
		eventBus.addListener(event -> createButton.setEnabled(!event.isLoading()));
	}

	@Override
	public SubViewType getViewType() {
		return SubViewType.CreateTeacher;
	}

	@Override
	public void initialize() {

		eventBus.raiseEvent(ApplicationEvent.loading("Retrieving available locations for teacher creation."));
		serverManifest.getServerList()
		.exceptionally((e) -> {
			return new ArrayList<>();
		}).thenAccept((servers) -> {
			eventBus.raiseEvent(ApplicationEvent.success("Locations resolved: " + servers)); 
			SwingUtilities.invokeLater(() -> {
				locationComboBox.setModel(new DefaultComboBoxModel<String>(servers.toArray(new String[0])));
			});
		});
	}
	
	private void createTeacher(){
		
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();
		String address = addressField.getText();
		String phone = phoneField.getText();
		CourseType specialization =(CourseType)specializationComboBox.getSelectedItem();
		String location = (String)locationComboBox.getSelectedItem();
		
		serverProxy.createTeacherRecord(firstName, lastName, address, phone, specialization, location)
			.thenAccept(teacher -> {
				SwingUtilities.invokeLater(() -> {
					firstNameField.setText("");
					lastNameField.setText("");
					addressField.setText("");
					phoneField.setText("");
					specializationComboBox.setSelectedIndex(0);
					locationComboBox.setSelectedIndex(0);
				});
			});
	}
	
}
