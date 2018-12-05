package musicplayer;

import java.io.File;
import DB.*;
import java.sql.*;

public class MusicChart {
	String[] musicList;
	String[][] musicTags;
	String[][] musicLyrics;
	public MusicChart() {
		File path = new File("../musicplayer/musicsrc/");
		File[] fileList = path.listFiles();
		musicList = path.list();
		musicTags = new String[musicList.length][4];
		musicLyrics = new String[musicList.length][];
		for (int i = 0; i < musicList.length; i++) {
			musicList[i] = fileList[i].getPath().toString();
			AudioTag tag = new AudioTag(musicList[i]);
			musicTags[i][0] = tag.title;
			musicTags[i][1] = String.format("%d:%02d%n", (int)tag.duration/60, (int)tag.duration%60);
			musicTags[i][2] = tag.artist;
			musicTags[i][3] = tag.album;
		}
	}
	public String[][] getMusicTags() {
		return musicTags;
	}
	public String getMusicPath(int i) {
		return musicList[i];
	}
}
