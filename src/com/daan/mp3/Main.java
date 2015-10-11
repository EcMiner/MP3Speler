package com.daan.mp3;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class Main extends JFrame {

	private static final long serialVersionUID = 7830875381072928952L;

	public static void main(String[] args) {
		new Main();
	}

	private final JLabel lblSearch = new JLabel("Search:");
	private final JTextField search = new JTextField();
	private final JButton clearSearch = new JButton("Clear search field");
	private final JScrollPane pane = new JScrollPane();
	private final JTable table = new JTable();
	private final ControlsPanel controlsPanel = new ControlsPanel();

	public Main() {
		super("MP3 Speler");
		setSize(1000, 700);
		setResizable(false);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		pane.setViewportView(table);
		pane.getViewport().setBackground(Color.WHITE);
		pane.setSize(1000, 540);
		pane.setLocation(0, 30);
		pane.setBackground(new Color(255, 255, 255));

		DefaultTableModel model = new DefaultTableModel(new Object[] { "Song name", "Artist", "Time", "Genre" }, 0) {

			private static final long serialVersionUID = 7326679307166013511L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setSize(pane.getWidth(), pane.getHeight());
		table.setModel(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumn("Song name").setPreferredWidth((int) (table.getWidth() * .4));
		table.getColumn("Artist").setPreferredWidth((int) (table.getWidth() * .3));
		table.getColumn("Time").setPreferredWidth((int) (table.getWidth() * .1));
		table.getColumn("Genre").setPreferredWidth((table.getWidth() - (table.getColumn("Song name").getPreferredWidth() + table.getColumn("Artist").getPreferredWidth() + table.getColumn("Time").getPreferredWidth())) - 3);
		table.setBackground(new Color(255, 255, 255));
		table.getTableHeader().setBackground(Color.white);
		table.setRowHeight(25);
		model.addRow(new Object[] { "Darude - Sandstorm", "Darude", "3:39", "" });

		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setResizable(false);
			column.setCellRenderer(new DefaultTableCellRenderer() {

				private static final long serialVersionUID = -8295090663179224261L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					setBorder(BorderFactory.createEmptyBorder());
					return this;
				}

			});
		}

		search.setSize(600, 27);
		search.setLocation(100, 0);

		lblSearch.setSize(100, 27);
		lblSearch.setLocation(10, 0);
		lblSearch.setFont(lblSearch.getFont().deriveFont(18f));

		clearSearch.setSize(250, 27);
		clearSearch.setLocation(720, 0);

		add(pane);
		add(search);
		add(lblSearch);
		add(clearSearch);
		add(controlsPanel);

		getContentPane().setBackground(new Color(255, 255, 255));
		setVisible(true);

		controlsPanel.playSong("C:\\Users\\daan\\Downloads\\Three Days Grace - Life Starts Now (2009) (mrsjs)\\12-Life Starts Now.mp3");
	}
}
