package client.models;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import server.models.TeacherRecord;

public class TeacherTableModel extends AbstractTableModel {

	private List<TeacherRecord> records;

	private String[] columnNames = { "Id", "First Name", "Last Name", "Address", "Phone", "Specialization", "Location" };

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		TeacherRecord record = records.get(row);

		switch (column) {
		case 0:
			return record.getId();
		case 1:
			return record.getFirstName();
		case 2:
			return record.getLastName();
		case 3:
			return record.getAddress();
		case 4:
			return record.getPhone();
		case 5:
			return record.getSpecialization();
		case 6:
			return record.getLocation();
		default:
			return null;
		}
	}

	public void setData(List<TeacherRecord> records) {
		this.records = records;
	}
}
