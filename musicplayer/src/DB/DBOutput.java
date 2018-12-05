package DB;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DBOutput {//Download at user Desktop - DB source
	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;


	public DBOutput() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
			//String sql = "SELECT * FROM music.main;"; // or main
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM music.main;");
			//
			if(rs.next()){
                InputStream is = rs.getBinaryStream("file");
                String sql ="/Users/seongwoongcho/Desktop/musicsrc/search.png";//set downloading path for user
                FileOutputStream fos = new FileOutputStream(sql);
                byte[] buff = new byte[8192];
                int len;
                while( (len = is.read(buff)) > 0){
                    fos.write(buff, 0, len);
                }
                fos.close();
                is.close();
                rs.close();
            }
            con.close();
 

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	
	}
	
	
}

