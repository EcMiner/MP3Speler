package com.daanendaron.mp3;

import java.awt.Color;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.NativeLibrary;

public class Main extends JFrame {

	private static final long serialVersionUID = 7700657015875708341L;

	public static URL getResource(String resource) {
		return Main.class.getResource(resource);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("user.home"));

		System.out.println(System.getProperty("os.name"));

		if (!new NativeDiscovery().discover()) {
			String vlcDir;

			if (System.getProperty("sun.arch.data.model").equals("32")) {
				File vlc32 = new File(System.getProperty("user.home") + (System.getProperty("os.name").startsWith("Windows") ? "/AppData/Roaming" : "Library/Application Support") + "/MP3/vlc32");

				if (!vlc32.exists()) {
					long start = System.currentTimeMillis();
					JarUtils.exportFolderInJar("vlc32/", vlc32);
					long end = System.currentTimeMillis();
					System.out.println("Took: " + (end - start) + "ms");
				}

				vlcDir = vlc32.getAbsolutePath();
			} else {
				File vlc64 = new File(System.getProperty("user.home") + (System.getProperty("os.name").startsWith("Windows") ? "/AppData/Roaming" : "Library/Application Support") + "/MP3/vlc64");

				if (!vlc64.exists()) {
					JarUtils.exportFolderInJar("vlc64/", vlc64);
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
		System.out.println(getClass().getResourceAsStream("pics/addbutton.png") == null);
		System.out.println(getClass().getClassLoader().getResource("pics/addbutton.png"));

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
