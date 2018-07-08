package client.views.subviews;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import client.eventbus.ApplicationEventBus;
import client.services.AuthenticationService;
import client.services.ServerProxy;
import client.views.renderers.JCheckBoxList;
import common.EditableFields;
import server.constants.ValidationConstants;
import server.models.CourseType;
import server.models.RecordType;
import server.models.StudentStatus;

public class EditFieldView extends TargetsRecordSubView {

	private JComboBox<String> fieldToEditComboBox = new JComboBox<>();
	private JTextField newValueField = new JTextField(16);
	private JLabel dateTimeFormatInfoLabel = new JLabel(ValidationConstants.DATETIME_FORMAT_STRING);
	private JButton editButton = new JButton("Edit");
	private JComboBox<StudentStatus> statusComboBox = new JComboBox<>();
	private JCheckBoxList coursesRegisteredList = new JCheckBoxList();
	
	private ServerProxy serverProxy;
	private ApplicationEventBus eventBus;
	
	public EditFieldView(ServerProxy serverProxy, AuthenticationService authenticationService, ApplicationEventBus eventBus) {
		super(authenticationService, "Edit Field"); 
		this.serverProxy = serverProxy;
		this.eventBus = eventBus;
		
		DefaultListModel<JCheckBox> coursesRegisteredModel = new DefaultListModel<JCheckBox>();
		Arrays.stream(CourseType.values()).forEach((course) -> coursesRegisteredModel.addElement(new JCheckBox(course.toString())));
		coursesRegisteredList.setModel(coursesRegisteredModel);
		
		statusComboBox.setModel(new DefaultComboBoxModel<StudentStatus>(StudentStatus.values()));
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++; 
		gc.insets = new Insets(10, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		JLabel editSectionTitle = new JLabel("Edit Field Information:");
		editSectionTitle.setFont(new Font("Sans Serif", Font.BOLD, 14));
		add(editSectionTitle, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Edit Field: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		fieldToEditComboBox.setModel(new DefaultComboBoxModel<String>(EditableFields.TEACHER_EDITABLE_FIELDS));
		
		fieldToEditComboBox.addItemListener((e) -> {
			dateTimeFormatInfoLabel.setVisible(fieldToEditComboBox.getSelectedItem().equals(EditableFields.STATUS_DATE));
			refreshValueSelectionField(); 
		});
		
		add(fieldToEditComboBox, gc);

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("New Value: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		add(newValueField, gc);

		coursesRegisteredList.setVisible(false);
		statusComboBox.setVisible(false);
		add(coursesRegisteredList, gc);
		add(statusComboBox, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		gc.gridwidth = 2;
		gc.anchor = GridBagConstraints.NORTH;
		add(dateTimeFormatInfoLabel, gc);
		dateTimeFormatInfoLabel.setVisible(false);
		gc.gridwidth = 1;
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 3;
		gc.gridy++;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.LAST_LINE_END;
		editButton.setPreferredSize(new Dimension(200, 32));
		add(editButton, gc);
		
		editButton.addActionListener(e -> executeEdit());
	}

	@Override
	public SubViewType getViewType() {
		return SubViewType.EditField;
	}
	
	private void refreshValueSelectionField() {
		coursesRegisteredList.setVisible(fieldToEditComboBox.getSelectedItem().equals(EditableFields.COURSES_REGISTERED));
		statusComboBox.setVisible(fieldToEditComboBox.getSelectedItem().equals(EditableFields.STATUS));
		newValueField.setVisible(!fieldToEditComboBox.getSelectedItem().equals(EditableFields.COURSES_REGISTERED) 
				&& !fieldToEditComboBox.getSelectedItem().equals(EditableFields.STATUS));
	}
	
	
	
	@Override
	protected void onRecordTypeChanged(ItemEvent e) {
		super.onRecordTypeChanged(e);
		
		if(recordTypeComboBox.getSelectedItem() == RecordType.Teacher) {
			fieldToEditComboBox.setModel(new DefaultComboBoxModel<String>(EditableFields.TEACHER_EDITABLE_FIELDS));
			dateTimeFormatInfoLabel.setVisible(false);
		}else {
			fieldToEditComboBox.setModel(new DefaultComboBoxModel<String>(EditableFields.STUDENT_EDITABLE_FIELDS));
		}
		refreshValueSelectionField(); 
	}

	private void executeEdit() {
		
		String recordId = refreshGeneratedId(); 
		String newValue = newValueField.getText();
		
		if(fieldToEditComboBox.getSelectedItem().equals(EditableFields.STATUS)) {
			newValue = statusComboBox.getSelectedItem().toString();
		}else if(fieldToEditComboBox.getSelectedItem().equals(EditableFields.COURSES_REGISTERED)) {
			CourseType[] courses = CourseType.values();
			newValue =  IntStream.range(0, coursesRegisteredList.getModel().getSize())
					.filter(i -> coursesRegisteredList.getModel().getElementAt(i).isSelected())
					.mapToObj(i -> courses[i].toString())
					.collect(Collectors.joining(", "));
		}
		
		serverProxy.editRecord(recordId, fieldToEditComboBox.getSelectedItem().toString(), newValue);
	}
	
}
