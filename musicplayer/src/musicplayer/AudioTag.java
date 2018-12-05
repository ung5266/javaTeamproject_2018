package musicplayer;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class AudioTag {
	public String artist = null;
	public String title = null;
	public String album = null;
	public String songwriter = null;
	public String year = null;
	public String track = null;
	public ImageIcon img = null;
	public File lyricFile;
	public int duration, len;

	public AudioTag(String path) {
		try {

			File file = new File(path);
			MP3File mp3 = (MP3File) AudioFileIO.read(file);
			AbstractID3v2Tag tag2 = mp3.getID3v2Tag();
			lyricFile = new File("../musicplayer/lyrics/" + file.getName().substring(0,file.getName().indexOf(".")) + ".txt");
			Tag tag = mp3.getTag();

			artist = tag.getFirst(FieldKey.ARTIST);
			title = tag.getFirst(FieldKey.TITLE);
			album = tag.getFirst(FieldKey.ALBUM);
			songwriter = tag.getFirst(FieldKey.COMPOSER);
			year = tag.getFirst(FieldKey.YEAR);
			track = tag.getFirst(FieldKey.TRACK);
			img = (ImageIcon) resizeIcon(new ImageIcon(tag2.getFirstArtwork().getBinaryData()), 360, 360);
			duration = mp3.getAudioHeader().getTrackLength();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
		Image img = icon.getImage();
		Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(resizedImage);
	}

	public String[][] getLyric() {
		String[][] lyric = null;
		try {
			FileReader filereader = new FileReader(lyricFile);
			BufferedReader bufReader = new BufferedReader(filereader);
			len = 0;
			while (bufReader.readLine() != null)
				len++;
			bufReader.close();
			
			filereader = new FileReader(lyricFile);
			bufReader = new BufferedReader(filereader);
			lyric = new String[len][2];
			for (int i = 0; i < len; i++) {
				lyric[i][0] = bufReader.readLine();
				lyric[i][1] = lyric[i][0].substring(11);
				double l = Double.parseDouble(lyric[i][0].substring(1, 3)) * 60
						+ Double.parseDouble(lyric[i][0].substring(4, 9));
				lyric[i][0] = String.valueOf(l);
			}
			bufReader.close();
		} 
		catch (IOException e) {
		}
		return lyric;
	}

}
