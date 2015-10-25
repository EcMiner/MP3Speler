package com.daanendaron.mp3;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DownloadUtils {

	public static void downloadAndExtractZip(URL source, File destination) throws IOException, ZipException {
		if (!destination.isDirectory() && destination.getName().endsWith(".zip")) {
			FileUtils.copyURLToFile(source, destination);

			ZipFile zip = new ZipFile(destination);
			zip.extractAll(destination.getParent());

			destination.delete();
		}
	}

}
