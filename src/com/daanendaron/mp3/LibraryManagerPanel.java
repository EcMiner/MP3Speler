package com.daanendaron.mp3;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class LibraryManagerPanel extends JPanel {

	private SQLite sql;
	private Main main;

	public LibraryManagerPanel(Main main) {
		this.main = main;

		File mp3Folder = new File(System.getProperty("user.home")
				+ (System.getProperty("os.name").startsWith("Windows") ? "/AppData/Roaming" : "Library/Application Support") + "/MP3/");

		sql = new SQLite(new File(mp3Folder, "/filelocations.sql"));
		sql.executeUpdate("CREATE TABLE IF NOT EXISTS filelocations(path varchar(255));");

		setSize(1000, 700);
		setVisible(true);
		setLayout(null);

		int buttonwidth = 44;
		int buttonheight = 44;
		int tablePanelheight = getHeight() - 80;
		int controlPanelheight = 50;
		int buttonsrightpadding = 7;
		int buttonstoppadding = 3;

		JPanel pnlMainPanel = new JPanel();
		pnlMainPanel.setSize(getWidth(), getHeight());
		pnlMainPanel.setLayout(null);
		pnlMainPanel.setBackground(Color.white);
		pnlMainPanel.setLocation(0, 0);

		JPanel pnlTablePanel = new JPanel();
		pnlTablePanel.setSize(getWidth(), tablePanelheight);
		pnlTablePanel.setBackground(Color.white);
		pnlTablePanel.setLayout(null);
		pnlTablePanel.setLocation(0, 0);

		JPanel pnlControls = new JPanel();
		pnlControls.setSize(getWidth(), controlPanelheight);
		pnlControls.setBackground(Color.white);
		pnlControls.setLayout(null);
		pnlControls.setLocation(0, pnlTablePanel.getHeight());

		JScrollPane jspTableScrollPane = new JScrollPane();
		jspTableScrollPane.setSize(pnlTablePanel.getPreferredSize());
		jspTableScrollPane.getViewport().setBackground(Color.white);

		final MyTableModel model = new MyTableModel();
		ResultSet result = sql.executeQuery("select * from filelocations");
		try {
			while (result.next()) {
				File fileLocation = new File(result.getString("path"));
				model.addRow(new Object[] { false, fileLocation.getAbsolutePath() });
				if (fileLocation.isDirectory()) {
					for (File song : fileLocation.listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							String extension = name.substring(name.lastIndexOf('.') + 1, name.length());
							return Main.supportedFormats.contains(extension.toLowerCase());
						}
					})) {
						main.pnlSongs.addSong(song, true);
					}
				} else {
					main.pnlSongs.addSong(fileLocation, false);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		final JTable tblDirectoriesTable = new JTable();
		tblDirectoriesTable.setRowSelectionAllowed(false);
		tblDirectoriesTable.setSize(jspTableScrollPane.getPreferredSize());
		tblDirectoriesTable.setModel(model);
		tblDirectoriesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblDirectoriesTable.getColumnModel().getColumn(0).setPreferredWidth((int) (jspTableScrollPane.getWidth() * 0.1));
		tblDirectoriesTable.getColumnModel().getColumn(1).setPreferredWidth(jspTableScrollPane.getWidth() - ((int) (jspTableScrollPane.getWidth() * 0.1)) - 3);
		tblDirectoriesTable.getTableHeader().setBackground(Color.white);
		tblDirectoriesTable.getTableHeader().setResizingAllowed(false);
		tblDirectoriesTable.getTableHeader().setReorderingAllowed(false);

		JButton btnAdd = new JButton();
		btnAdd.setSize(buttonwidth, buttonheight);
		btnAdd.setToolTipText("Add new directories to the library");
		btnAdd.setBorderPainted(false);
		btnAdd.setContentAreaFilled(false);
		btnAdd.setIcon(Utils.resizeImage(new ImageIcon(Main.getResource("pics/addbutton.png")), buttonwidth, buttonheight));
		btnAdd.setLocation(buttonsrightpadding, buttonstoppadding);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAddEvents(tblDirectoriesTable);
			}
		});

		JButton btnDelete = new JButton();
		btnDelete.setSize(buttonwidth, buttonheight);
		btnDelete.setToolTipText("Remove new directories from the library");
		btnDelete.setBorderPainted(false);
		btnDelete.setContentAreaFilled(false);
		btnDelete.setIcon(Utils.resizeImage(new ImageIcon(Main.getResource("pics/removebutton.png")), buttonwidth, buttonheight));
		btnDelete.setLocation(btnAdd.getX() + btnAdd.getWidth() + buttonsrightpadding, buttonstoppadding);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnDeleteEvents(tblDirectoriesTable);
			}
		});

		JButton btnBack = new JButton("Back");
		btnBack.setSize(100, pnlControls.getHeight());
		btnBack.setLocation(getWidth() - btnBack.getWidth(), 0);
		btnBack.setToolTipText("Go back to the library");
		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.togglePanels();
			}

		});

		jspTableScrollPane.setViewportView(tblDirectoriesTable);
		pnlTablePanel.add(jspTableScrollPane);
		pnlControls.add(btnDelete);
		pnlControls.add(btnAdd);
		pnlControls.add(btnBack);
		pnlMainPanel.add(pnlTablePanel);
		pnlMainPanel.add(pnlControls);
		add(pnlMainPanel);
	}

	public JFileChooser initfilechooser(JFrame folderselectorframe) {
		FileNameExtensionFilter audiofiles = new FileNameExtensionFilter("Audio Files",
				Main.supportedFormats.toArray(new String[Main.supportedFormats.size()]));
		JFileChooser mp3folderselector = new JFileChooser(System.getProperty("user.home") + System.getProperty("file.separator") + "Music");
		mp3folderselector.setControlButtonsAreShown(true);
		mp3folderselector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		mp3folderselector.setAcceptAllFileFilterUsed(false);
		mp3folderselector.setApproveButtonText("Select");
		mp3folderselector.addChoosableFileFilter(audiofiles);
		mp3folderselector.setSize(folderselectorframe.getWidth(), folderselectorframe.getHeight());
		mp3folderselector.setLocation(0, 0);
		mp3folderselector.setDialogType(JFileChooser.OPEN_DIALOG);
		folderselectorframe.add(mp3folderselector);
		return mp3folderselector;
	}

	public void btnAddEvents(JTable tblDirectoriesTable) {
		MyTableModel model = (MyTableModel) tblDirectoriesTable.getModel();
		JFrame filechoserframe = new JFrame();
		JFileChooser mp3choser = initfilechooser(filechoserframe);
		filechoserframe.add(mp3choser);
		int i = mp3choser.showSaveDialog(getParent());
		if (i == JFileChooser.APPROVE_OPTION) {
			// check if the directory you are trying to add already
			// exists
			for (int i1 = 0; i1 < tblDirectoriesTable.getModel().getRowCount(); i1++) {
				if (tblDirectoriesTable.getModel().getValueAt(i1, 1).equals(mp3choser.getSelectedFile().getAbsolutePath())) {
					return;
				}
			}

			// add the directory to the table and the text file
			File chosenFile = mp3choser.getSelectedFile();
			model.addRow(new Object[] { false, mp3choser.getSelectedFile().getAbsolutePath() });
			sql.executeUpdate("INSERT INTO filelocations(path) VALUES('" + chosenFile.getAbsolutePath() + "');");
			if (chosenFile.isDirectory()) {
				for (File song : chosenFile.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						String extension = name.substring(name.lastIndexOf('.') + 1, name.length());
						return Main.supportedFormats.contains(extension.toLowerCase());
					}
				})) {
					main.pnlSongs.addSong(song, true);
				}
			} else {
				main.pnlSongs.addSong(chosenFile, false);
			}
		} else {
			filechoserframe.dispose();
		}
	}

	public void btnDeleteEvents(JTable tblDirectoriesTable) {
		for (int i = tblDirectoriesTable.getModel().getRowCount() - 1; i >= 0; i--) {
			if ((boolean) tblDirectoriesTable.getModel().getValueAt(i, 0)) {
				File fileLocation = new File((String) tblDirectoriesTable.getModel().getValueAt(i, 1));
				((MyTableModel) tblDirectoriesTable.getModel()).removeRow(i);
				sql.executeUpdate("DELETE FROM filelocations WHERE path='" + fileLocation.getAbsolutePath() + "';");
				main.pnlSongs.removeSong(fileLocation);
				break;
			}
		}
	}
}
