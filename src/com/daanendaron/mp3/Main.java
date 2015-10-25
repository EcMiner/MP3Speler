package com.daanendaron.mp3;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.daanendaron.mp3.panels.LibraryManagerPanel;
import com.daanendaron.mp3.panels.SongsPanel;
import com.daanendaron.mp3.utilities.DownloadUtils;
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

		System.out.println(System.getProperty("os.name"));

		// Kijken of VLC al geinstalleerd is
		if (!new NativeDiscovery().discover()) {
			String vlcDir;

			// Kijken of de computer een windows machine is.
			boolean windows = System.getProperty("os.name").startsWith("Windows");

			// Bepalen of er 32 of 64 bit Java wordt gebruikt
			if (System.getProperty("sun.arch.data.model").equals("32")) {
				// De installatie folder bepalen (Windows: ~\AppData\Roaming\MP3Speler\vlc32, MAC: ~/Library/Application Support/MP3Speler/vlc32
				File vlc32 = new File(System.getProperty("user.home") + (windows ? "/AppData/Roaming" : "/Library/Application Support") + "/MP3/vlc32");

				System.out.println(vlc32.getAbsolutePath());

				if (!vlc32.exists()) {
					// Een popup openen om te laten weten dat VLC wordt gedownload
					UncloseablePopup popup = new UncloseablePopup(250, 100, "Please wait while we setup VLC");

					// 32 bit vlc downloaden voor windows of mac.
					DownloadUtils.downloadAndExtractZip(new URL(windows ? "https://www.dropbox.com/s/vncqql6jhx2dssx/vlc32win.zip?dl=1"
							: "https://www.dropbox.com/s/a2fuze65tkzzo21/vlc32mac.zip?dl=1"), new File(vlc32.getParentFile(), "vlc32.zip"));

					popup.closePopup();
				}

				// De installatie locatie van VLC definiëren 
				vlcDir = windows ? vlc32.getAbsolutePath() : vlc32.getAbsolutePath() + "/lib/";
			} else {
				// De installatie folder bepalen (Windows: ~\AppData\Roaming\MP3Speler\vlc64, MAC: ~/Library/Application Support/MP3Speler/vlc64
				File vlc64 = new File(System.getProperty("user.home") + (windows ? "/AppData/Roaming" : "/Library/Application Support") + "/MP3/vlc64");

				System.out.println(vlc64.getAbsolutePath());

				if (!vlc64.exists()) {
					// Een popup openen om te laten weten dat VLC wordt gedownload
					UncloseablePopup popup = new UncloseablePopup(250, 100, "Please wait while we setup VLC");

					// 64 bit vlc downloaden voor windows of mac.
					DownloadUtils.downloadAndExtractZip(new URL(windows ? "https://www.dropbox.com/s/p3pyl0p85ig7jgd/vlc64win.zip?dl=1"
							: "https://www.dropbox.com/s/ugvo248btwelq9y/vlc64mac.zip?dl=1"), new File(vlc64.getParentFile(), "vlc64.zip"));

					popup.closePopup();
				}

				// De installatie locatie van VLC definiëren 
				vlcDir = windows ? vlc64.getAbsolutePath() : vlc64.getAbsolutePath() + "/lib/";
			}
			System.out.println(vlcDir);
			
			// De installatie locatie van VLC instellen, anders werkt de VLCJ library niet.
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcDir);
		} else {
			System.out.println("Found VLC installed");
		}

		new Main();
	}

	public SongsPanel pnlSongs = new SongsPanel(this);
	private LibraryManagerPanel pnlLibraryManager = new LibraryManagerPanel(this);

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
