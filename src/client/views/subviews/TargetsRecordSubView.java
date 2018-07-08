package client.views.subviews;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import client.services.AuthenticationService;
import client.views.utilities.BorderBuilder;
import server.models.RecordId;
import server.models.RecordType;
import server.storage.RecordIdDeserializer;

public abstract class TargetsRecordSubView extends SubView {

	protected JSpinner recordNumericId;
	protected JComboBox<RecordType> recordTypeComboBox = new JComboBox<>();
	protected JTextField serverPrefixField = new JTextField(8);
	protected JLabel generatedRecordIdLabel = new JLabel("");
	protected GridBagConstraints gc;
	private AuthenticationService authenticationService;
	
	protected TargetsRecordSubView(AuthenticationService authenticationService, String title) {
		super();
		this.authenticationService = authenticationService;

		setLayout(new GridBagLayout());
		gc = new GridBagConstraints();
		
		setBorder(BorderBuilder.createTitledBorder(title));
		
		gc.gridy = -1;
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++; 
		gc.anchor = GridBagConstraints.LINE_START;
		JLabel label = new JLabel("Record Id:");
		label.setFont(new Font("Sans Serif", Font.BOLD, 14));
		add(label, gc);
		
		// Next Row 
		// -----------------------------------------------------		
		gc.gridx = 0;
		gc.gridy++;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Generated id: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		generatedRecordIdLabel.setFont(new Font("Sans Serif", Font.PLAIN, 12));
		add(generatedRecordIdLabel, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Server Prefix: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		serverPrefixField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) { refreshGeneratedId();}
			@Override
			public void insertUpdate(DocumentEvent e) { refreshGeneratedId();}
			@Override
			public void changedUpdate(DocumentEvent e) { refreshGeneratedId();}
		});
		add(serverPrefixField, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++; 
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Record Type:"), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		DefaultComboBoxModel<RecordType> recordTypesModel = new DefaultComboBoxModel<RecordType>(RecordType.values());
		recordTypeComboBox.setModel(recordTypesModel);
		recordTypeComboBox.addItemListener(this::onRecordTypeChanged);
		add(recordTypeComboBox, gc);
		
		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Numeric Id: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		recordNumericId = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
		JFormattedTextField spinnerText = ((JSpinner.NumberEditor) recordNumericId.getEditor()).getTextField();
		((NumberFormatter) spinnerText.getFormatter()).setAllowsInvalid(false);
		recordNumericId.addChangeListener((e) -> refreshGeneratedId());
		
		Dimension spinnerDimensions = recordNumericId.getPreferredSize();
		spinnerDimensions.width = 80;
		recordNumericId.setPreferredSize(spinnerDimensions);
		add(recordNumericId, gc);
		
	}

	@Override
	public void initialize() {
		serverPrefixField.setText(authenticationService.getCurrentUserContext().getServerId());
		refreshGeneratedId();
	}
	
	protected String getTypeCode() {
		return (RecordType)recordTypeComboBox.getSelectedItem() == RecordType.Teacher 
				? RecordId.TEACHER_CODE 
				: RecordId.STUDENT_CODE;
	}
	
	public void setRecordId(String recordId) {
		RecordId deserializedId = RecordIdDeserializer.extractOrEmpty(recordId);
		serverPrefixField.setText(deserializedId.getServerPrefix());
		recordTypeComboBox.setSelectedItem(deserializedId.getRecordType());
		recordNumericId.setValue(deserializedId.getNumericId());
	}
	
	protected String refreshGeneratedId() {
		String recordId = new RecordId(serverPrefixField.getText(), getTypeCode(), (int)recordNumericId.getValue()).toString();
		generatedRecordIdLabel.setText(recordId);
		return recordId;
	}
	
	protected void onRecordTypeChanged(ItemEvent e) {
		refreshGeneratedId(); 
	}

}
