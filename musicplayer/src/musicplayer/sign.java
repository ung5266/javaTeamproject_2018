package musicplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseAdapter;
import java.awt.event.InputMethodEvent;

public class sign extends JFrame {

	private JPanel contentPane;
	private JTextField mypin;
	private JTextField myid;
	private JTextField pincheck;
	private String password;
	private String loginid;
	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	int count = 0;
	int idcheckok = 0;
	//String pcget = login.userget;
	String usersignid ="";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					sign framesign = new sign();
					framesign.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 */
	public void getForList(String ID, String password) {
		this.loginid = loginid;
		this.password = password;
	}

	public void createTable(){
		try {

			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
			String sql = "CREATE TABLE IF NOT EXISTS "+usersignid+"(" + 
					"  id int(11) NOT NULL," + 
					"  filename varchar(100) NOT NULL," + 
					"  file longblob," + 
					"  musicname varchar(100) NOT NULL," + 
					"  singer varchar(100) NOT NULL," + 
					"  genre varchar(100) NOT NULL," + 
					"  filePath varchar(100) NOT NULL," + 
					"  PRIMARY KEY(id,filename,musicname,singer,genre,filePath)) ";
					
			pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();

		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void Insert() {
		try {

			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
			String sql = "insert into user(loginid,password) values (?,?)";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, loginid);
			pstmt.setString(2, password);

			int cnt = pstmt.executeUpdate();
			if (cnt == 1)
				System.out.println("User information is registered");
			else
				System.out.println("Fail to register user information");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public sign() {
		ImageIcon signback = new ImageIcon(musicplayer.class.getResource("../picture/signscreen.PNG"));
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(signback.getImage(), 0, 0, 500, 500, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 100, 500, 530);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		myid = new JTextField();
		myid.setBounds(182, 130, 194, 46);
		contentPane.add(myid);
		myid.setColumns(10);

		mypin = new JPasswordField();

		mypin.setBounds(182, 176, 279, 51);
		contentPane.add(mypin);
		mypin.setColumns(10);

		pincheck = new JPasswordField();
		pincheck.setBounds(182, 227, 279, 46);
		contentPane.add(pincheck);
		pincheck.setColumns(10);

		JRadioButton woman = new JRadioButton("여");
		woman.setFont(new Font("Serif", Font.BOLD, 15));
		woman.setBackground(Color.WHITE);
		woman.setBounds(261, 279, 50, 36);
		contentPane.add(woman);

		JRadioButton man = new JRadioButton("남");
		man.setFont(new Font("Serif", Font.BOLD, 15));
		man.setBackground(Color.WHITE);
		man.setBounds(397, 279, 50, 36);
		contentPane.add(man);

		JTextField call = new JTextField();
		call.setBounds(182, 321, 279, 46);
		contentPane.add(call);
		call.setColumns(10);

		JButton signok = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/ok.PNG")));
		signok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (myid.getText().equals("") || mypin.getText().equals("") || call.getText().equals("")
						|| pincheck.getText().equals("")
						|| (woman.isSelected() != true) && (man.isSelected() != true)) {
					JOptionPane.showMessageDialog(null, "There is information not entered", "Warning!",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (idcheckok != 1) {
						JOptionPane.showMessageDialog(null, "ID has not been duplicated", "Warning!",
								JOptionPane.ERROR_MESSAGE);
					} else if (!(mypin.getText().equals(pincheck.getText()))) {
						JOptionPane.showMessageDialog(null, "Passwords do not match", "Warning!",
								JOptionPane.ERROR_MESSAGE);
						mypin.setText("");
						pincheck.setText("");
					} else {
						loginid = new String(myid.getText());
						password = new String(mypin.getText());
						getForList(loginid, password);
						usersignid = loginid;
						Insert(); // insert data in db table - user
						createTable(); // create user playlist table in db schema

						login framelog = new login();
						framelog.setVisible(true);
						dispose();
					}
				}
			}
		});
		signok.setBounds(137, 405, 95, 51);
		contentPane.add(signok);

		JButton signcancel = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/cancel.PNG")));
		signcancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login framelog = new login();
				framelog.setVisible(true);
				dispose();
			}
		});
		signcancel.setBounds(280, 405, 95, 51);
		contentPane.add(signcancel);

		JButton idcheck = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/idcheck.PNG")));
		//
		idcheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if ((myid.getText().equals(""))) {//
					JOptionPane.showMessageDialog(null, "Please enter your ID", "Warning!", JOptionPane.ERROR_MESSAGE);
				} else {//
					try {
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("SELECT loginid from music.user ");

						while (rs.next()) {
							String loginid = rs.getString("loginid");
							if ((myid.getText()).equals(loginid)) {
								count++; //
							}
						}
						if (count > 0) {
							count = 0;
							JOptionPane.showMessageDialog(null, "Unavailable ID", "Warning", JOptionPane.ERROR_MESSAGE);
							myid.setText("");
						} else if (count == 0) {
							JOptionPane.showMessageDialog(null, "Available ID", "Warning", JOptionPane.ERROR_MESSAGE);
							idcheckok = 1;
						}

						rs.close();// 
						con.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		idcheck.setBounds(376, 130, 85, 46);
		contentPane.add(idcheck);
	}

}