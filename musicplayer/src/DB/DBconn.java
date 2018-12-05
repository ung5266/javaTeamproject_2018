package DB;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBconn {
	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	public String filepath;

	public DBconn() {//Set Playlist Path - DB source
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
			stmt = con.createStatement();
			//PreparedStatement pstmt = con.prepareStatement("select * from music.main");
			rs = stmt.executeQuery("select * from music.main");
			if(rs.next()) {
			 int ID = rs.getInt("id");
			String filepath = rs.getString("filePath"); // file address -file Path
			System.out.println(":"+filepath);
			

			
			}
		} catch (Exception arg0) {
			arg0.printStackTrace();
		}
		

	}

	public String getfilepath() {
		return filepath;
	}

	public void setfilepath(String filePath) {
		this.filepath = filePath;
	}
	public static void main(String args[]) {
		DBconn a = new DBconn();
		System.out.println("suc:");
		
	
	
	}
}
