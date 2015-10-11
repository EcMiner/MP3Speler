package com.daan.mp3;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class ControlsPanel extends JPanel {

	private static final long serialVersionUID = -2917297485031219158L;

	private EmbeddedMediaPlayerComponent vlcMediaPlayer = new EmbeddedMediaPlayerComponent();
	private JLabel lblSongPicture = new JLabel();
	private JLabel lblTime = new JLabel("00:00:00");
	private JSlider slider = new JSlider();
	private ScheduledExecutorService scheduledExecutor = Executors
			.newSingleThreadScheduledExecutor();
	private Random random = new Random();

	public ControlsPanel() {
		new NativeDiscovery().discover();
		setSize(1000, 100);
		setLocation(0, 570);
		setLayout(null);
		setBackground(Color.WHITE);

		lblSongPicture.setSize(90, 90);
		lblSongPicture.setLocation(5, 5);
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				lblSongPicture.setIcon(resizeImage(new ImageIcon(
						"pics/defaultalbumart.png"), lblSongPicture.getWidth(),
						lblSongPicture.getHeight()));
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

		add(lblSongPicture);
		add(lblTime);
		add(slider);
		add(vlcMediaPlayer);

		scheduledExecutor.scheduleAtFixedRate(new Runnable() {

			public void run() {
				if (vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					long timeInMillis = vlcMediaPlayer.getMediaPlayer()
							.getTime();
					int timeInSeconds = Math.floorDiv((int) timeInMillis, 1000);
					int sliderPosition = Math.round(vlcMediaPlayer
							.getMediaPlayer().getPosition() * 1000f);

					updateTimeLabel(timeInSeconds);
					updateSlider(sliderPosition);
				}
			}

		}, 0, 500, TimeUnit.MILLISECONDS);
		slider.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlayable()) {
					long selectedTime = (int) Math.floor(((double) slider
							.getValue() / 1000d)
							* (double) vlcMediaPlayer.getMediaPlayer()
									.getLength());

					vlcMediaPlayer.getMediaPlayer().setTime(selectedTime);
					updateTimeLabel(Math.floorDiv((int) selectedTime, 1000));

					vlcMediaPlayer.getMediaPlayer().start();
				}
			}

			public void mousePressed(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					vlcMediaPlayer.getMediaPlayer().pause();
				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
		slider.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {
			}

			public void mouseDragged(MouseEvent e) {
				if (vlcMediaPlayer.getMediaPlayer().isPlayable()
						&& !vlcMediaPlayer.getMediaPlayer().isPlaying()) {
					long selectedTime = (int) Math.floor(((double) slider
							.getValue() / 1000d)
							* (double) vlcMediaPlayer.getMediaPlayer()
									.getLength());
					updateTimeLabel(Math.floorDiv((int) selectedTime, 1000));
				}
			}
		});
	}

	private void updateTimeLabel(int seconds) {
		int hours = Math.floorDiv(seconds, 3600);
		seconds -= hours * 3600;

		int minutes = Math.floorDiv(seconds, 60);
		seconds -= minutes * 60;

		String timeString = ((hours < 10 ? "0" : "") + hours) + ":"
				+ ((minutes < 10 ? "0" : "") + minutes) + ":"
				+ ((seconds < 10 ? "0" : "") + seconds);
		lblTime.setText(timeString);
	}

	private void updateSlider(int position) {
		slider.setValue(position);
	}

	private void setSongPicture(final ImageIcon fileIcon) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				lblSongPicture.setIcon(resizeImage(fileIcon,
						lblSongPicture.getWidth(), lblSongPicture.getHeight()));
			}

		});
	}

	private ImageIcon resizeImage(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		return new ImageIcon(image.getScaledInstance(width, height,
				Image.SCALE_SMOOTH));
	}

	public void playSong(String locationPath) {
		File file = new File(locationPath);
		if (file.exists()) {
			vlcMediaPlayer.getMediaPlayer().playMedia(file.getAbsolutePath());
			ImageIcon fileIcon = getSongArtWork(file);
			if (fileIcon != null)
				setSongPicture(fileIcon);
			else
				setSongPicture(new ImageIcon("pics/defaultalbumart.png"));
			slider.setEnabled(true);
		}
	}

	public ImageIcon getSongArtWork(File songFile) {
		try {
			AudioFile f = AudioFileIO.read(songFile);
			Tag tag = f.getTag();
			List<Artwork> artwork = tag.getArtworkList();
			if (artwork.size() > 0) {
				return new ImageIcon((BufferedImage) artwork.get(
						random.nextInt(artwork.size())).getImage());
			}
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException e) {
		}
		return null;
	}
}
