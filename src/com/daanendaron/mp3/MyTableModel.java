package com.daanendaron.mp3;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class MyTableModel extends DefaultTableModel {

	public MyTableModel() {
		super(new String[] { "Selected", "Location" }, 0);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		@SuppressWarnings("rawtypes")
		Class theClass = String.class;
		switch (columnIndex) {
		case 0:
			theClass = Boolean.class;
			break;
		}
		return theClass;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue instanceof Boolean && column == 0) {
			@SuppressWarnings("rawtypes")
			Vector rowData = (Vector) getDataVector().get(row);
			rowData.set(0, (boolean) aValue);
			fireTableCellUpdated(row, column);
		}
	}

	public boolean getValueAt(Object aValue, int row, int column) {
		boolean test = false;
		if (aValue instanceof Boolean && column == 0) {
			if ((boolean) aValue == true) {
				test = true;
			} else {
				test = false;
			}
		}
		return test;
	}
}
