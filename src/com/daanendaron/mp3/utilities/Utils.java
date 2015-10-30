package com.daanendaron.mp3.utilities;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Utils {

	public static String secondsToHoursMinutesSeconds(int seconds) {
		int hours = Math.floorDiv(seconds, 3600);
		seconds -= hours * 3600;

		int minutes = Math.floorDiv(seconds, 60);
		seconds -= minutes * 60;

		return ((hours < 10 ? "0" : "") + hours) + ":" + ((minutes < 10 ? "0" : "") + minutes) + ":" + ((seconds < 10 ? "0" : "") + seconds);
	}

	public static ImageIcon resizeImage(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

}
