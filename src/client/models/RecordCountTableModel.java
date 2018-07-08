package client.models;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import server.models.RecordCount;

public class RecordCountTableModel extends AbstractTableModel {

	private List<RecordCount> records;

	private String[] columnNames = { "Center", "Count" };

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
		RecordCount record = records.get(row);

		switch (column) {
		case 0:
			return record.getServerId();
		case 1:
			return record.getCount();
		default:
			return null;
		}
	}

	public void setData(List<RecordCount> records) {
		this.records = records;
	}
}
