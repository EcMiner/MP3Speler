package com.daanendaron.mp3.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.daanendaron.mp3.Main;
import com.daanendaron.mp3.MusicFile;
import com.daanendaron.mp3.utilities.Utils;

public class SongsPanel extends JPanel {

	private static final long serialVersionUID = 7830875381072928952L;

	private final ExecutorService execAsync = Executors.newCachedThreadPool();

	private JLabel lblSearch = new JLabel("Search:");
	private JTextField fldSearch = new JTextField();
	private JButton btnClearSearch = new JButton("Clear search field");
	private JScrollPane jspTableScrollPane = new JScrollPane();
	protected JTable tblSongsTable = new JTable();
	private ControlsPanel pnlControls;

	private List<MusicFile> allSongs = new ArrayList<MusicFile>();

	public SongsPanel(Main main) {
		pnlControls = new ControlsPanel(this, main);
		setSize(1000, 700);
		setLayout(null);

		jspTableScrollPane.setViewportView(tblSongsTable);
		jspTableScrollPane.getViewport().setBackground(Color.WHITE);
		jspTableScrollPane.setSize(995, 540);
		jspTableScrollPane.setLocation(0, 30);
		jspTableScrollPane.setBackground(new Color(255, 255, 255));
		jspTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		DefaultTableModel model = new DefaultTableModel(new Object[] { "Song name", "Artist", "Time", "Genre", "FileLocation", "PartOfFolder" }, 0) {

			private static final long serialVersionUID = 7326679307166013511L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tblSongsTable.setSize(jspTableScrollPane.getWidth(), jspTableScrollPane.getHeight());
		tblSongsTable.setModel(model);
		tblSongsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblSongsTable.getColumn("Song name").setPreferredWidth((int) (tblSongsTable.getWidth() * .4));
		tblSongsTable.getColumn("Artist").setPreferredWidth((int) (tblSongsTable.getWidth() * .3));
		tblSongsTable.getColumn("Time").setPreferredWidth((int) (tblSongsTable.getWidth() * .1));
		tblSongsTable.getColumn("Genre").setPreferredWidth((tblSongsTable.getWidth() - (tblSongsTable.getColumn("Song name").getPreferredWidth() + tblSongsTable.getColumn("Artist").getPreferredWidth() + tblSongsTable.getColumn("Time").getPreferredWidth())) - 3);
		tblSongsTable.removeColumn(tblSongsTable.getColumn("PartOfFolder"));
		tblSongsTable.removeColumn(tblSongsTable.getColumnModel().getColumn(4));
		tblSongsTable.setBackground(new Color(255, 255, 255));
		tblSongsTable.getTableHeader().setBackground(Color.white);
		tblSongsTable.setRowHeight(25);
		tblSongsTable.getTableHeader().setReorderingAllowed(false);
		tblSongsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblSongsTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent evt) {
				int row = tblSongsTable.rowAtPoint(evt.getPoint());
				if (evt.getClickCount() == 2) {
					String fileLocation = (String) tblSongsTable.getModel().getValueAt(row, 4);
					File file = new File(fileLocation);
					pnlControls.playSong(file, row);
				} else if (evt.getClickCount() == 1) {
					pnlControls.btnPausePlaySong.setEnabled(true);
				}
			}

		});

		for (int i = 0; i < tblSongsTable.getColumnCount(); i++) {
			TableColumn column = tblSongsTable.getColumnModel().getColumn(i);
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

		fldSearch.setSize(600, 27);
		fldSearch.setLocation(100, 0);
		fldSearch.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				searchSongs();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				searchSongs();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				searchSongs();
			}

			private void searchSongs() {
				DefaultTableModel model = (DefaultTableModel) tblSongsTable.getModel();
				String searchString = fldSearch.getText().toLowerCase();
				model.setRowCount(0);
				for (MusicFile musicFileToSearch : allSongs) {
					if ((musicFileToSearch.getSongName() != null && musicFileToSearch.getSongName().toLowerCase().contains(searchString)) || (musicFileToSearch.getArtist() != null && musicFileToSearch.getArtist().toLowerCase().contains(searchString))
							|| (musicFileToSearch.getGenre() != null && musicFileToSearch.getGenre().toLowerCase().contains(searchString))) {
						model.addRow(new Object[] { musicFileToSearch.getSongName(), musicFileToSearch.getArtist(), musicFileToSearch.getDurationHoursMinutesSeconds(), musicFileToSearch.getGenre(), musicFileToSearch.getFileLocation().getPath(), musicFileToSearch.isPartOfFolder() });
					}
				}
				pnlControls.updateControlButtons();
			}

		});

		lblSearch.setSize(100, 27);
		lblSearch.setLocation(10, 0);
		lblSearch.setFont(lblSearch.getFont().deriveFont(18f));

		btnClearSearch.setSize(250, 27);
		btnClearSearch.setLocation(720, 0);
		btnClearSearch.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				fldSearch.setText("");
				model.setRowCount(0);
				for (MusicFile musicFile : allSongs) {
					model.addRow(new Object[] { musicFile.getSongName(), musicFile.getArtist(), musicFile.getDurationHoursMinutesSeconds(), musicFile.getGenre(), musicFile.getFileLocation().getPath(), musicFile.isPartOfFolder() });
				}
				pnlControls.updateControlButtons();
			}

		});

		add(jspTableScrollPane);
		add(fldSearch);
		add(lblSearch);
		add(btnClearSearch);
		add(pnlControls);

		setBackground(new Color(255, 255, 255));
		setVisible(true);
	}

	public void addSongToTable(final File file, boolean isFolder) {
		if (file != null) {
			execAsync.submit(new Runnable() {

				@Override
				public void run() {
					try {
						AudioFile f = AudioFileIO.read(file);
						AudioHeader header = f.getAudioHeader();
						Tag tag = f.getTag();

						String songName = tag.getFirst(FieldKey.TITLE) != "" ? tag.getFirst(FieldKey.TITLE) : file.getName().substring(0, file.getName().lastIndexOf('.'));
						String artist = tag.getFirst(FieldKey.ARTIST) != "" ? tag.getFirst(FieldKey.ARTIST) : tag.getFirst(FieldKey.ALBUM_ARTIST);
						String time = Utils.secondsToHoursMinutesSeconds(header.getTrackLength());
						String genre = tag.getFirst(FieldKey.GENRE);

						MusicFile musicFile = new MusicFile(file, songName, artist, header.getTrackLength(), genre, isFolder);
						if (!allSongs.contains(musicFile)) {
							((DefaultTableModel) tblSongsTable.getModel()).addRow(new Object[] { songName, artist, time, genre, file.getPath(), isFolder });
							allSongs.add(musicFile);
						}
						pnlControls.updateControlButtons();
					} catch (Exception e) {
						System.err.println("File is not an audio file!");
					}
				}

			});
		}
	}

	@SuppressWarnings("unchecked")
	public void removeSongFromTable(final File file) {
		if (file != null) {
			if (file.isDirectory()) {
				Iterator<MusicFile> iterator = allSongs.iterator();
				while (iterator.hasNext()) {
					MusicFile nextMusicFile = iterator.next();
					if (nextMusicFile.getFileLocation() != null) {
						if (nextMusicFile.isPartOfFolder()) {
							if (nextMusicFile.getFileLocation().getParentFile().getPath().equals(file.getPath()))
								iterator.remove();
						} else if (nextMusicFile.getFileLocation().getPath().equals(file.getPath()))
							iterator.remove();
					}
				}
			}
			Iterator<Vector<Object>> it = ((DefaultTableModel) tblSongsTable.getModel()).getDataVector().iterator();
			while (it.hasNext()) {
				Vector<Object> v = it.next();
				boolean partOfFolder = (boolean) v.get(5);
				if (v.size() >= 5 && v.get(4) != null) {

					if (partOfFolder) {
						if (new File((String) v.get(4)).getParentFile().getPath().equals(file.getPath())) {
							it.remove();
						}
					} else if (v.get(4).equals(file.getPath())) {
						it.remove();
					}
					if (!file.isDirectory()) {
						Iterator<MusicFile> iterator = allSongs.iterator();
						while (iterator.hasNext()) {
							MusicFile nextMusicFile = iterator.next();
							if (nextMusicFile.getFileLocation() != null && nextMusicFile.getFileLocation().getPath().equals(file.getPath()))
								iterator.remove();
						}
					}
				}
			}
			tblSongsTable.revalidate();
			pnlControls.updateControlButtons();
		}
	}
}
