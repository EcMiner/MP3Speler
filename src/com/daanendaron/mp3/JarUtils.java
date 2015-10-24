package com.daanendaron.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

	public static void exportFolderInJar(final String resource, File folderToExtractTo) throws Exception {
		String resourceFolder = (resource.startsWith("/") && resource.length() > 1 ? resource.substring(1) : resource) + (resource.endsWith("/") ? "" : "/");
		File possibleJarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		if (possibleJarFile.isFile()) {
			JarFile jar = new JarFile(possibleJarFile);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith(resourceFolder) && !entry.isDirectory()) {
					exportFile(jar.getInputStream(entry), new File(folderToExtractTo.getAbsolutePath() + "/" + entry.getName().split(resourceFolder)[1]));
				}
			}
			jar.close();
		} else {
			URL url = Main.class.getResource("/" + resourceFolder);
			if (url != null) {
				File folder = new File(url.toURI());
				listAllFilesInAllDirectories(folder, new FileCallback() {

					@Override
					public void check(File file) throws Exception {
						exportFile(new FileInputStream(file), new File(folderToExtractTo.getAbsolutePath() + "/" + file.getAbsolutePath().replace("\\", "/").split(resourceFolder)[1]));
					}

				});
			}
		}
	}

	public static void listAllFilesInAllDirectories(File dir, FileCallback callback) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					listAllFilesInAllDirectories(file, callback);
				} else {
					try {
						callback.check(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			try {
				callback.check(dir);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void exportFile(InputStream input, File toExportTo) {
		try {
			if (!toExportTo.getParentFile().exists())
				toExportTo.getParentFile().mkdirs();

			int readBytes;
			byte[] buffer = new byte[8192];
			FileOutputStream output = new FileOutputStream(toExportTo);
			while ((readBytes = input.read(buffer)) > 0) {
				output.write(buffer, 0, readBytes);
			}
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface FileCallback {

		public void check(File file) throws Exception;

	}

}
