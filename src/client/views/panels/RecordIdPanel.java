package client.views.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

import server.models.RecordType;

public class RecordIdPanel extends JPanel {

	private JSpinner recordNumericId;
	private JComboBox<RecordType> userTypeComboBox = new JComboBox<>();
	private JTextField serverIdField = new JTextField(8);
	
	public RecordIdPanel() {
		super();
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		setBorder(BorderFactory.createTitledBorder("Record Id"));
		
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;

		// Next Row 
		// -----------------------------------------------------
		gc.gridx = 0;
		gc.gridy++; 
		
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Record Type: "), gc);
		
		gc.gridx++;
		gc.anchor = GridBagConstraints.LINE_START;
		
		DefaultComboBoxModel<RecordType> recordTypesModel = new DefaultComboBoxModel<RecordType>(RecordType.values());
		userTypeComboBox.setModel(recordTypesModel);
		add(userTypeComboBox, gc);
		
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
		
		Dimension spinnerDimensions = recordNumericId.getPreferredSize();
		spinnerDimensions.width = 90;
		recordNumericId.setPreferredSize(spinnerDimensions);
		add(recordNumericId, gc);

		// Next Row 
		// -----------------------------------------------------		
		gc.gridx = 0;
		gc.gridy++;
		gc.gridwidth = 2;
		gc.anchor = GridBagConstraints.NORTH;
		gc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel("Generated id"), gc);
		gc.gridwidth = 1;
		
	}
	
	
	
}
