package DB;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

public class DBConnection { // Upload for music at musicChart table - DB source

	private Connection con;

	public DBConnection(String fileName) throws Exception { 

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");

			String decoding = "ISO-8859-1";
			String encoding = "EUC-KR";
			int maxID = getMaxID(con) + 1;
			//
			File file = new File(fileName);
			int fileLength = (int) file.length();
			System.out.println("fileLength : " + fileLength);
			InputStream is = new FileInputStream(file);
			//
			MP3File mp3 = (MP3File) AudioFileIO.read(file);
			AbstractID3v2Tag tag2 = mp3.getID3v2Tag();
			Tag tag = mp3.getTag();
			String title = tag.getFirst(FieldKey.TITLE);
			String artist = tag.getFirst(FieldKey.ARTIST);
			String album = tag.getFirst(FieldKey.ALBUM);
			String year = tag.getFirst(FieldKey.YEAR);
			String genre = tag.getFirst(FieldKey.GENRE);
			
			String filePath = "../musicplayer/musicsrc/";
			//

			/*File pic = new File(picName);
			int picLength = (int) pic.length();
			System.out.println("picLength : " + picLength);
			InputStream is2 = new FileInputStream(pic);*/
			//
			String sql = "insert into main (id, filename, file, musicname,singer,genre,filePath) values (?,?,?,?,?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, maxID);
			pstmt.setString(2, fileName);
			pstmt.setBinaryStream(3, is, fileLength);
			pstmt.setString(4, title);
			pstmt.setString(5, artist);
			pstmt.setString(6, genre);
			pstmt.setString(7, filePath);
			pstmt.executeUpdate();
			pstmt.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected int getMaxID(Connection con) throws SQLException {

		int maxID = 0;
		Statement stmt = con.createStatement();
		ResultSet result = stmt.executeQuery("select max(id) from main");
		while (result.next()) {
			maxID = result.getInt(1);
		}
		result.close();
		stmt.close();
		return maxID;
	}

	public static void main(String[] args) throws Exception {
		String fileName = "../musicplayer/musicsrc/1.mp3";
		//String picName = "../musicplayer/musicpic/crypic.png";
		//
		String fileName2 = "../musicplayer/musicsrc/2.mp3";
		//String picName2 = "../musicplayer/musicpic/wouldyoupic.png";
		//
		String fileName3 = "../musicplayer/musicsrc/3.mp3";
		String fileName4 = "../musicplayer/musicsrc/4.mp3";
		//String picName3 = "../musicplayer/musicpic/anotherdaypic.png";
		//
		// String fileName4 = "/Users/seongwoongcho/Desktop/musicsrc/oo.mp3";
		// String picName4 = "/Users/seongwoongcho/Desktop/musicsrc/thatboytinypic.png";
		// System.out.println("Tag : " + tag2);
		// System.out.println("Song Name : " + title);
		// System.out.println("Artist : " + artist);
		// System.out.println("Album : " + album);
		// System.out.println("Year : " + year);
		// System.out.println("Genre : " + genre);

		//

		DBConnection fileup = new DBConnection(fileName);
		DBConnection fileup2 = new DBConnection(fileName2);
		DBConnection fileup3 = new DBConnection(fileName3);
		DBConnection fileup4 = new DBConnection(fileName4);
		// DBConnection fileup4 = new DBConnection(fileName4, picName4);
		//
		System.out.println("music uproading success");

	}

}
