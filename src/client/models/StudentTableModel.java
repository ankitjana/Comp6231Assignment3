package client.models;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import server.constants.ValidationConstants;
import server.models.CourseType;
import server.models.StudentRecord;

public class StudentTableModel extends AbstractTableModel {

	private List<StudentRecord> records;

	private String[] columnNames = { "Id", "First Name", "Last Name", "Courses Registered", "Status", "Status Date" };

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
		StudentRecord record = records.get(row);

		switch (column) {
		case 0:
			return record.getId();
		case 1:
			return record.getFirstName();
		case 2:
			return record.getLastName();
		case 3:
			return record.getCoursesRegistered().stream().map(CourseType::toString).collect(Collectors.joining(", "));
		case 4:
			return record.getStatus();
		case 5:
			return record.getStatusDate().format(ValidationConstants.DATETIME_FORMAT);
		default:
			return null;
		}
	}

	public void setData(List<StudentRecord> records) {
		this.records = records;
	}
}
