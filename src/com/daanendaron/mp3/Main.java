package com.daanendaron.mp3;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Main extends JFrame {

	private static final long serialVersionUID = 7700657015875708341L;
	public static List<String> supportedFormats = Arrays.asList("mp3", "ogg", "wav", "wma", "flac");

	public static URL getResource(String resource) {
		return Main.class.getClassLoader().getResource(resource);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("user.home"));

		System.out.println(System.getProperty("os.hame"));

		if (!new NativeDiscovery().discover()) {
			String vlcDir;

			if (System.getProperty("sun.arch.data.model").equals("32")) {
				File vlc32 = new File(System.getProperty("user.home")
						+ (System.getProperty("os.name").startsWith("Windows") ? "/AppData/Roaming" : "Library/Application Support") + "/MP3/vlc32");

				System.out.println(vlc32.getAbsolutePath());
				
				if (!vlc32.exists()) {
					UncloseablePopup popup = new UncloseablePopup(250, 100, "Please wait while we setup VLC");

					DownloadUtils.downloadAndExtractZip(new URL("https://www.dropbox.com/s/l9cv9ldxfviksbh/vlc32.zip?dl=1"),
							new File(vlc32.getParentFile(), "vlc32.zip"));

					popup.close();
				}

				vlcDir = vlc32.getAbsolutePath();
			} else {
				File vlc64 = new File(System.getProperty("user.home")
						+ (System.getProperty("os.name").startsWith("Windows") ? "/AppData/Roaming" : "Library/Application Support") + "/MP3/vlc64");

				System.out.println(vlc64.getAbsolutePath());
				
				if (!vlc64.exists()) {
					UncloseablePopup popup = new UncloseablePopup(250, 100, "Please wait while we setup VLC");

					DownloadUtils.downloadAndExtractZip(new URL("https://www.dropbox.com/s/64sjgh9pz0aqp2w/vlc64.zip?dl=1"),
							new File(vlc64.getParentFile(), "vlc64.zip"));

					popup.close();
				}

				vlcDir = vlc64.getAbsolutePath();
			}
			System.out.println(vlcDir);
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcDir);
		} else {
			System.out.println("Found VLC installed");
		}

		new Main();
	}

	protected SongsPanel pnlSongs = new SongsPanel(this);
	protected LibraryManagerPanel pnlLibraryManager = new LibraryManagerPanel(this);

	public Main() {
		setSize(1000, 700);
		setResizable(false);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.white);
		setVisible(true);

		add(pnlSongs);
		add(pnlLibraryManager);

		pnlLibraryManager.setVisible(false);
	}

	public void togglePanels() {
		if (pnlSongs.isVisible()) {
			pnlSongs.setVisible(false);
			pnlLibraryManager.setVisible(true);
		} else {
			pnlSongs.setVisible(true);
			pnlLibraryManager.setVisible(false);
		}
	}

}
