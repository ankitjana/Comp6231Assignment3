package client.views.subviews;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import client.eventbus.ApplicationEvent;
import client.eventbus.ApplicationEventBus;
import client.services.ServerProxy;
import client.views.renderers.JCheckBoxList;
import client.views.utilities.BorderBuilder;
import server.ServerManifest;
import server.models.CourseType;
import server.models.StudentStatus;

public class CreateStudentView extends SubView {

	private JTextField firstNameField = new JTextField(16);
	private JTextField lastNameField = new JTextField(16);
	
	private JCheckBoxList coursesRegisteredList = new JCheckBoxList();
	private JComboBox<StudentStatus> statusComboBox = new JComboBox<>();
	
	private JButton createButton = new JButton("Create");
	private ApplicationEventBus eventPublisher;
	private ServerProxy serverProxy;
	
	public CreateStudentView(ServerProxy serverProxy, ApplicationEventBus applicationEventBus) {
		super();
		this.serverProxy = serverProxy;
		this.eventPublisher = applicationEventBus;
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		setBorder(BorderBuilder.createTitledBorder("New Student Record"));
		
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
		add(new JLabel("Status: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		DefaultComboBoxModel<StudentStatus> statusesModel = new DefaultComboBoxModel<StudentStatus>(StudentStatus.values());
		statusComboBox.setModel(statusesModel);
		add(statusComboBox, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.weighty = 0.1;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		gc.insets = new Insets(5, 0, 0, 5);
		add(new JLabel("Registered Courses: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		DefaultListModel<JCheckBox> coursesRegisteredModel = new DefaultListModel<JCheckBox>();
		Arrays.stream(CourseType.values()).forEach((course) -> coursesRegisteredModel.addElement(new JCheckBox(course.toString())));
		coursesRegisteredList.setModel(coursesRegisteredModel);
		add(coursesRegisteredList, gc);
				
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 3;
		gc.gridy++;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.LAST_LINE_END;
		createButton.setPreferredSize(new Dimension(200, 32));
		add(createButton, gc);
		
		createButton.addActionListener(e -> createStudent());
		applicationEventBus.addListener(event -> createButton.setEnabled(!event.isLoading()));
	}

	@Override
	public SubViewType getViewType() {
		return SubViewType.CreateStudent;
	}

	@Override
	public void initialize() {
		// No initialization needed for this component
	}

	private void createStudent(){
		
		CourseType[] courses = CourseType.values();
		
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();

		List<CourseType> coursesRegistered = IntStream.range(0, coursesRegisteredList.getModel().getSize())
				.filter(i -> coursesRegisteredList.getModel().getElementAt(i).isSelected())
				.mapToObj(i -> courses[i]).collect(Collectors.toList());
		
		StudentStatus status = (StudentStatus)statusComboBox.getSelectedItem();
		
		serverProxy.createStudentRecord(firstName, lastName, coursesRegistered, status, LocalDateTime.now())
			.thenAccept(student -> {
				SwingUtilities.invokeLater(() -> {
					firstNameField.setText("");
					lastNameField.setText("");
					statusComboBox.setSelectedIndex(0);
					ListModel<JCheckBox> coursesRegisteredListModel = coursesRegisteredList.getModel();
					for(int i = 0; i < coursesRegisteredListModel.getSize(); i++) {
						coursesRegisteredListModel.getElementAt(i).setSelected(false);
					}
					coursesRegisteredList.clearSelection();
					coursesRegisteredList.repaint();
				});
			});
	}
	
}
