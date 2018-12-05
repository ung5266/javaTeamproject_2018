package musicplayer;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class login extends JFrame {

	private JPanel contentPane;
	public JTextField idtext;
	public JTextField pintext;
	private JButton loginbtn;
	private Connection con;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	ImageIcon loginback;
	public static String userget ="";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					login framelog = new login();
					framelog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public login() {
		loginback = new ImageIcon(musicplayer.class.getResource("../picture/loginscreen.PNG"));
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(loginback.getImage(), 0, 0, 500, 500, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 100, 500, 530);
		// contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		idtext = new JTextField();
		idtext.setBounds(53, 223, 284, 66);
		// idtext.setBackground(new Color(0,0,0,0));
		contentPane.add(idtext);
		idtext.setColumns(10);

		pintext = new JPasswordField();
		pintext.setBounds(53, 288, 284, 59);
		contentPane.add(pintext);
		pintext.setColumns(10);

		loginbtn = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/loginbtn.PNG")));
		loginbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//String userget ="";
				String input = "";
				int count = 0;
				int index = 0;
				if (idtext.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please enter your ID", "Warning!", JOptionPane.ERROR_MESSAGE);
				} else if (pintext.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Please enter your Password", "Warning!",
							JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("SELECT loginid from music.user");

						while (rs.next()) {
							String loginid = rs.getString("loginid");//sql-loginid get-> loginid
							if ((idtext.getText()).equals(loginid)) { //idtext -txt == loginid
								count++;
								userget = loginid;
								break; //
							}
							index++;
						}
						rs.close();// 
						con.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					try {
						Class.forName("com.mysql.jdbc.Driver");
						con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery("SELECT password from music.user");

						while (rs.next()) {
							String password = rs.getString("password");
							if ((pintext.getText()).equals(password)) {
								count++;
								break; //
							}
							index--;
						}
						rs.close();//
						con.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					if ((count == 2) && (index == 0)) {
						musicplayer frame = new musicplayer();
						frame.setVisible(true);
						System.out.println(""+userget+"  login success");
						idtext.setText("");
						pintext.setText("");
						dispose();
					} else {
						JOptionPane.showMessageDialog(null, "The ID or password is incorrect.", "Warning!",
								JOptionPane.ERROR_MESSAGE);
						idtext.setText("");
						pintext.setText("");
					}
				}
			}
		});
		loginbtn.setBounds(337, 223, 111, 124);
		contentPane.add(loginbtn);

		JButton sign = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/sign.PNG")));
		sign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sign framesign = new sign();
				framesign.setVisible(true);
				dispose();
			}
		});
		sign.setBounds(53, 377, 99, 42);
		// sign.setBorderPainted(false);
		sign.setFocusPainted(false);
		contentPane.add(sign);

		JButton exit = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/exit.PNG")));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		exit.setBounds(384, 377, 64, 42);
		contentPane.add(exit);
	}
}