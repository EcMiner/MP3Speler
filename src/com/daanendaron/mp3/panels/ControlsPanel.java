package com.daanendaron.mp3.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import com.daanendaron.mp3.Main;
import com.daanendaron.mp3.utilities.Utils;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class ControlsPanel extends JPanel {

	private static final long serialVersionUID = -2917297485031219158L;

	private final SongsPanel pnlSongs;

	private EmbeddedMediaPlayerComponent vlcMediaPlayer = new EmbeddedMediaPlayerComponent();
	private JLabel lblSongPicture = new JLabel();
	private JLabel lblTime = new JLabel("00:00:00");
	private JSlider slider = new JSlider();
	private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	private Random randomGenerator = new Random();

	private JButton btnPreviousSong = new JButton(Utils.resizeImage(new ImageIcon(Main.getResource("pics/prevbutton.png")), 45, 45));
	private JButton btnPausePlaySong = new JButton(Utils.resizeImage(new ImageIcon(Main.getResource("pics/playbutton.png")), 70, 70));
	private JButton btnNextSong = new JButton(Utils.resizeImage(new ImageIcon(Main.getResource("pics/nextbutton.png")), 45, 45));
	private JButton btnManageLibrary = new JButton("<html>Library<br>Manager</html>");

	private JLabel lblSongTitle = new JLabel();
	private JLabel lblArtist = new JLabel();

	private int rowPlaying = -1;
	private boolean isMusicPlaying = false;

	public ControlsPanel(SongsPanel pnlSongs, Main main) {
		this.pnlSongs = pnlSongs;

		setSize(1000, 100);
		setLocation(0, 570);
		setLayout(null);
		setBackground(Color.WHITE);

		lblSongPicture.setSize(90, 90);
		lblSongPicture.setLocation(5, 5);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				lblSongPicture.setIcon(
						Utils.resizeImage(new ImageIcon(Main.getResource("pics/defaultalbumart.png")), lblSongPicture.getWidth(), lblSongPicture.getHeight()));
			}
		});

		slider.setSize(500, 30);
		slider.setLocation(110, 55);
		slider.setBackground(Color.WHITE);
		slider.setValue(0);
		slider.setEnabled(false);
		slider.setMinimum(0);
		slider.setMaximum(1000);

		lblTime.setSize(60, 30);
		lblTime.setLocation(615, 53);

		btnPreviousSong.setSize(btnPreviousSong.getIcon().getIconWidth(), btnPreviousSong.getIcon().getIconHeight());
		btnPreviousSong.setLocation(700, 28);
		btnPreviousSong.setBorderPainted(false);
		btnPreviousSong.setContentAreaFilled(false);
		btnPreviousSong.setEnabled(false);
		btnPreviousSong.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				playPreviousSong();
			}

		});

		btnPausePlaySong.setSize(btnPausePlaySong.getIcon().getIconWidth(), btnPausePlaySong.getIcon().getIconHeight());
		btnPausePlaySong.setLocation(755, 15);
		btnPausePlaySong.setBorderPainted(false);
		btnPausePlaySong.setContentAreaFilled(false);
		btnPausePlaySong.setEnabled(false);
		btnPausePlaySong.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					vlcMediaPlayer.getMediaPlayer().pause();
					updateControlButtons();
				} else if (vlcMediaPlayer.getMediaPlayer().isPlayable()) {
					vlcMediaPlayer.getMediaPlayer().start();
					updateControlButtons();
				}
			}

		});

		btnNextSong.setSize(btnNextSong.getIcon().getIconWidth(), btnNextSong.getIcon().getIconHeight());
		btnNextSong.setLocation(830, 28);
		btnNextSong.setBorderPainted(false);
		btnNextSong.setContentAreaFilled(false);
		btnNextSong.setEnabled(false);
		btnNextSong.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				playNextSong();
			}

		});

		lblSongTitle.setSize(slider.getWidth(), 20);
		lblSongTitle.setLocation(slider.getX(), slider.getY() - 40);
		lblSongTitle.setFont(lblSongTitle.getFont().deriveFont(15f));

		lblArtist.setSize(slider.getWidth(), 20);
		lblArtist.setLocation(lblSongTitle.getX() + 2, lblSongTitle.getY() + lblSongTitle.getHeight());

		btnManageLibrary.setLocation(btnNextSong.getX() + btnNextSong.getWidth() + 5, 30);
		btnManageLibrary.setSize(1000 - btnManageLibrary.getX() - 15, 50);
		btnManageLibrary.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.togglePanels();
			}
		});

		add(lblSongPicture);
		add(lblTime);
		add(slider);
		add(vlcMediaPlayer);
		add(btnPreviousSong);
		add(btnPausePlaySong);
		add(btnNextSong);
		add(lblSongTitle);
		add(lblArtist);
		add(btnManageLibrary);

		scheduledExecutor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					long timeInMillis = vlcMediaPlayer.getMediaPlayer().getTime();
					int timeInSeconds = Math.floorDiv((int) timeInMillis, 1000);
					int sliderPosition = Math.round(vlcMediaPlayer.getMediaPlayer().getPosition() * 1000f);

					updateTimeLabel(timeInSeconds);
					updateSlider(sliderPosition);
				}
			}

		}, 0, 500, TimeUnit.MILLISECONDS);
		slider.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlayable()) {
					long selectedTime = (int) Math.floor(((double) slider.getValue() / 1000d) * (double) vlcMediaPlayer.getMediaPlayer().getLength());

					vlcMediaPlayer.getMediaPlayer().setTime(selectedTime);
					updateTimeLabel(Math.floorDiv((int) selectedTime, 1000));

					vlcMediaPlayer.getMediaPlayer().start();
					updateControlButtons();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					vlcMediaPlayer.getMediaPlayer().pause();
					updateControlButtons();
				}
			}
		});
		slider.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlayable() && !vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					long selectedTime = (int) Math.floor(((double) slider.getValue() / 1000d) * (double) vlcMediaPlayer.getMediaPlayer().getLength());
					updateTimeLabel(Math.floorDiv((int) selectedTime, 1000));
				}
			}
		});
		vlcMediaPlayer.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
				isMusicPlaying = true;
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				isMusicPlaying = true;
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				isMusicPlaying = false;
			}

			@Override
			public void paused(MediaPlayer mediaPlayer) {
				isMusicPlaying = false;
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				isMusicPlaying = false;
				updateSlider(1000);
				if (canPlayNextSong()) {
					playNextSong();
				} else {
					updateControlButtons();
				}
			}

		});
	}

	private void updateTimeLabel(int seconds) {
		lblTime.setText(Utils.secondsToHoursMinutesSeconds(seconds));
	}

	private void updateSlider(int position) {
		slider.setValue(position);
	}

	private void setSongPicture(final ImageIcon fileIcon) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				lblSongPicture.setIcon(Utils.resizeImage(fileIcon, lblSongPicture.getWidth(), lblSongPicture.getHeight()));
			}

		});
	}

	public void playSong(File file, int rowPlaying) {
		if (file.exists()) {
			this.rowPlaying = rowPlaying;
			vlcMediaPlayer.getMediaPlayer().stop();
			vlcMediaPlayer.getMediaPlayer().playMedia(file.getAbsolutePath());
			ImageIcon fileIcon = getSongArtWork(file);
			if (fileIcon != null)
				setSongPicture(fileIcon);
			else
				setSongPicture(new ImageIcon(Main.getResource("pics/defaultalbumart.png")));
			slider.setEnabled(true);

			DefaultTableModel model = (DefaultTableModel) pnlSongs.table.getModel();
			lblSongTitle.setText((String) model.getValueAt(rowPlaying, 0));
			lblArtist.setText((String) model.getValueAt(rowPlaying, 1));

			updateControlButtons();
		}
	}

	public void updateControlButtons() {
		if (vlcMediaPlayer.getMediaPlayer().isPlayable()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (isMusicPlaying)
						btnPausePlaySong.setIcon(Utils.resizeImage(new ImageIcon(Main.getResource("pics/pausebutton.png")),
								btnPausePlaySong.getIcon().getIconWidth(), btnPausePlaySong.getIcon().getIconHeight()));
					else
						btnPausePlaySong.setIcon(Utils.resizeImage(new ImageIcon(Main.getResource("pics/playbutton.png")),
								btnPausePlaySong.getIcon().getIconWidth(), btnPausePlaySong.getIcon().getIconHeight()));
					btnPausePlaySong.setEnabled(true);
					btnPreviousSong.setEnabled(canPlayPreviousSong());
					btnNextSong.setEnabled(canPlayNextSong());
				}

			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					btnPausePlaySong.setIcon(Utils.resizeImage(new ImageIcon(Main.getResource("pics/playbutton.png")),
							btnPausePlaySong.getIcon().getIconWidth(), btnPausePlaySong.getIcon().getIconHeight()));
					btnPausePlaySong.setEnabled(false);
					btnPreviousSong.setEnabled(false);
					btnNextSong.setEnabled(false);
					rowPlaying = -1;
				}

			});
		}
	}

	private void playNextSong() {
		if (canPlayNextSong()) {
			playSong(new File((String) ((DefaultTableModel) pnlSongs.table.getModel()).getValueAt(rowPlaying + 1, 4)), rowPlaying + 1);
			pnlSongs.table.setRowSelectionInterval(rowPlaying, rowPlaying);
		}
	}

	private void playPreviousSong() {
		if (canPlayPreviousSong()) {
			playSong(new File((String) ((DefaultTableModel) pnlSongs.table.getModel()).getValueAt(rowPlaying - 1, 4)), rowPlaying - 1);
			pnlSongs.table.setRowSelectionInterval(rowPlaying, rowPlaying);
		}
	}

	private boolean canPlayNextSong() {
		return rowPlaying >= 0 && rowPlaying < pnlSongs.table.getRowCount() - 1;
	}

	private boolean canPlayPreviousSong() {
		return rowPlaying >= 1 && rowPlaying <= pnlSongs.table.getRowCount();
	}

	public ImageIcon getSongArtWork(File songFile) {
		try {
			AudioFile f = AudioFileIO.read(songFile);
			Tag tag = f.getTag();
			List<Artwork> artwork = tag.getArtworkList();
			if (artwork.size() > 0) {
				return new ImageIcon((BufferedImage) artwork.get(randomGenerator.nextInt(artwork.size())).getImage());
			}
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
		}
		return null;
	}
}
