package com.daanendaron.mp3;

import java.io.File;

import com.daanendaron.mp3.utilities.Utils;

public class MusicFile {

	private final File fileLocation;
	private final String songName;
	private final String artist;
	private final int time;
	private final String genre;
	private final String formattedTime;
	private final boolean isPartOfFolder;

	public MusicFile(File fileLocation, String songName, String artist, int time, String genre, boolean isPartOfFolder) {
		this.fileLocation = fileLocation;
		this.songName = songName;
		this.artist = artist;
		this.time = time;
		this.genre = genre;
		this.formattedTime = Utils.secondsToHoursMinutesSeconds(time);
		this.isPartOfFolder = isPartOfFolder;
	}

	public File getFileLocation() {
		return fileLocation;
	}

	public String getSongName() {
		return songName;
	}

	public String getArtist() {
		return artist;
	}

	public int getTime() {
		return time;
	}

	public String getGenre() {
		return genre;
	}

	public String getFormattedTime() {
		return formattedTime;
	}

	public boolean isPartOfFolder() {
		return isPartOfFolder;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MusicFile) {
			MusicFile other = (MusicFile) obj;
			if ((fileLocation == null && other.fileLocation == null) || ((fileLocation != null && other.fileLocation != null) && (fileLocation.getPath().equals(other.fileLocation.getPath())))) {
				if ((songName == null && other.songName == null) || ((songName != null && other.songName != null) && (songName.equals(other.songName)))) {
					if ((artist == null && other.artist == null) || ((artist != null && other.artist != null) && (artist.equals(other.artist)))) {
						if (time == other.time) {
							if ((genre == null && other.genre == null) || ((genre != null && other.genre != null) && (genre.equals(other.genre)))) {
								return isPartOfFolder == other.isPartOfFolder;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
