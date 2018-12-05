package musicplayer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import DB.DBconn;
import net.proteanit.sql.DbUtils;

public class musicplayer extends JFrame {

	private JPanel contentPane;
	private JScrollPane charttable;
	private JTable playlisttable;
	private JTable searchtable;
	private JTextField find;
	public JTextField idtext;
	private JTable table;
	private JLabel cover, songname, singer, playTime, currentTime, currentLyric, nextLyric;
	private JButton allplay, shuffle;
	private JProgressBar seekBar;
	private Audio song;
	private int framePos, musicIndex = -1, curIndex = -1, repeatMod = 0, index = 0;
	private int[] randIndex;
	private MusicList musicList = new MusicList();
	private MusicChart musicChart = new MusicChart();
	private String[] header = { "1", "2", "3", "4" };
	private String[] header2 = { "1", "2", "3", "4" };
	private String[][] contents = musicList.getMusicTags(), lyric;
	private String[][] contents2 = musicChart.getMusicTags(), lyric2;
	private boolean playing = false, shuffleMod = false;
	private double volume = -37.5, cDuration, pDuration;
	private Timer t;
	//
	protected Connection con;
	protected Statement stmt;
	protected PreparedStatement pstmt;
	protected ResultSet rs;
	//
	private JTable table1;
	private JTable table2;
	//
	// private JPanel contentPane;
	private JTable charttable2;
	// private JTable favoritetable;
	private JTextField find2;
	private DBconn dbc;
	JLabel label;
	//
	String pcget = login.userget;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					musicplayer frame = new musicplayer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public musicplayer() {
		ImageIcon mainback = new ImageIcon(musicplayer.class.getResource("../picture/mainback.PNG"));
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(mainback.getImage(), 0, 0, 1280, 700, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		setUndecorated(false);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1280, 750);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setBackground(Color.LIGHT_GRAY);
		setTitle("Calamansi Player");
		setContentPane(contentPane);

		MusicAddListener1 musicadd = new MusicAddListener1(); // listener music add on playlist
		// MusicAddListener2 musicadd2 = new MusicAddListener2();
		label = new JLabel();
		label.setBounds(871, 93, 363, 356);// music album image - label
		add(label);

		idtext = new JTextField();
		idtext.setBounds(631, 50, 114, 30);
		contentPane.add(idtext);
		idtext.setColumns(10);
		idtext.setText("" + pcget);
		// get login.userget(username) -> set by string instance -pcget(username)
		// useful for playlist

		JButton logout = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/logout.PNG"))); // logout
																												// button
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { // if user click logout button -> return to the login window
				login framelog = new login();
				framelog.setVisible(true);
				dispose();
			}
		});

		JButton btnAddMusic = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/addmusic.PNG")));

		btnAddMusic.setBounds(821, 5, 44, 44);
		btnAddMusic.addActionListener(musicadd);
		// btnAddMusic.addActionListener(musicadd2);
		btnAddMusic.setFocusPainted(false);
		btnAddMusic.setContentAreaFilled(false);
		contentPane.add(btnAddMusic);
		// add music at playlist button in chart

		JButton btnDelMusic = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/delmusic.PNG")));

		btnDelMusic.setBounds(875, 5, 44, 44);
		// btnDelMusic.addActionListener(musicadd);
		btnDelMusic.setFocusPainted(false);
		btnDelMusic.setContentAreaFilled(false);
		contentPane.add(btnDelMusic);
		// delete music at playlist button in chart

		btnDelMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int selRow = playlisttable.getSelectedRow() + 1;//select music in playlistable
					String query = "delete from " +pcget+ " where id="+selRow +"" ;
					//"SELECT musicname,singer,genre FROM " + pcget + ""
					pstmt = con.prepareStatement(query);
					pstmt.execute();

					JOptionPane.showMessageDialog(null, "Date Deleted");
					
					pstmt.close();
					
				} catch (Exception e) {
					e.printStackTrace();

				}
			}

		});

		// screen 3 - searching table
		JPanel screen3 = new JPanel(); // include searchtable, find textfield, findicon button
		screen3.setLayout(null);
		screen3.setBounds(14, 209, 799, 480);
		contentPane.add(screen3);

		JScrollPane scrollPane3 = new JScrollPane(); // show playlist by MusicList.java(contents,header)
		scrollPane3.setBounds(14, 86, 771, 382);
		screen3.add(scrollPane3);

		searchtable = new JTable();
		searchtable.setBounds(14, 86, 771, 382);
		scrollPane3.setViewportView(searchtable);

		JButton findicon = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/find.PNG"))); // findicon
																												// button
		findicon.setLayout(null);
		findicon.setBounds(654, 25, 52, 44);
		screen3.add(findicon);
		findicon.setToolTipText("\uAC80\uC0C9");

		find = new JTextField(); // Text field -> users will write what they want to search
		find.setBounds(64, 27, 568, 36);
		screen3.add(find);
		find.setLayout(null);
		find.setColumns(10);

		find.addKeyListener(new KeyAdapter() { // searching by key
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					String query = "SELECT musicname,singer,genre from music.main where musicname=?"; // search by
																										// musicname
					pstmt = con.prepareStatement(query);
					pstmt.setString(1, find.getText());
					rs = pstmt.executeQuery();

					searchtable.setModel(DbUtils.resultSetToTableModel(rs));// using rs2xml library ->list by db

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		findicon.addActionListener(new ActionListener() { // Pressing the findicon button without writing anything to a
															// text field -> print warning
			public void actionPerformed(ActionEvent arg0) {
				if ((find.getText()).equals("")) {
					JOptionPane.showMessageDialog(null, "please enter your content.", "Warning!",
							JOptionPane.ERROR_MESSAGE);
				}
			}

		});

		// screen1 - musicChart table
		JPanel screen1 = new JPanel(); // include playlisttable
		screen1.setLayout(null);
		screen1.setBounds(14, 209, 799, 480);
		contentPane.add(screen1);

		JScrollPane scrollPane1 = new JScrollPane(); // show playlist by MusicList.java(contents,header)
		scrollPane1.setBounds(31, 24, 740, 430);
		screen1.add(scrollPane1);

		charttable2 = new JTable(contents2, header2);
		// charttable2.addMouseListener(new ChartEvent());
		scrollPane1.setViewportView(charttable2);

		// screen2 - playlist table
		JPanel screen2 = new JPanel(); // include playlisttable
		screen2.setLayout(null);
		screen2.setBounds(14, 209, 799, 480);
		contentPane.add(screen2);

		JScrollPane scrollPane2 = new JScrollPane(); // show playlist by MusicList.java(contents,header)
		scrollPane2.setBounds(31, 24, 740, 430);
		screen2.add(scrollPane2);

		playlisttable = new JTable(contents, header);
		playlisttable.addMouseListener(new ChartEvent());
		scrollPane2.setViewportView(playlisttable);
		// my ver

		screen1.setVisible(false);
		screen2.setVisible(false);
		screen3.setVisible(false);
		//
		logout.setToolTipText("\uB85C\uADF8\uC544\uC6C3");
		logout.setBounds(771, 0, 42, 41);
		logout.setBorderPainted(false);
		logout.setFocusPainted(false);
		contentPane.add(logout);

		JButton sound = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/sound.PNG"))); // sound
																											// button
		sound.setBounds(1112, 497, 53, 47);
		contentPane.add(sound);

		JButton fastf = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/fastf.PNG"))); // forward
																											// button
		fastf.setBounds(1133, 588, 64, 70);
		// NextSong play button action
		fastf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (song != null) {
					song.stop();
					// ShuffleMod on
					if (shuffleMod) {
						index = (index + 1) % randIndex.length;
						musicIndex = randIndex[index];
						playSong(musicList.getMusicPath(randIndex[index]), volume, 0);
					}
					// ShuffleMod off
					else {
						curIndex = (curIndex + 1) % musicList.musicList.length;
						musicIndex = curIndex;
						playSong(musicList.getMusicPath(curIndex), volume, 0);
					}

				}
			}
		});
		contentPane.add(fastf);

		JButton fastb = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/fastb.PNG"))); // back
																											// button
		fastb.setBounds(897, 588, 64, 70);
		// PreviousSong play button action
		fastb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (song != null) {
					song.stop();
					// ShuffleMod on
					if (shuffleMod) {
						index = (index - 1 + randIndex.length) % randIndex.length;
						musicIndex = randIndex[index];
						playSong(musicList.getMusicPath(randIndex[index]), volume, 0);
					}
					// ShuffleMod off
					else {
						curIndex = (curIndex - 1 + musicList.musicList.length) % musicList.musicList.length;
						musicIndex = curIndex;
						playSong(musicList.getMusicPath(curIndex), volume, 0);
					}

				}
			}
		});
		contentPane.add(fastb);

		JButton play = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/play.PNG"))); // play button
		play.setToolTipText("\uC7AC\uC0DD");
		play.setBounds(983, 564, 127, 125);
		// play.setBorderPainted(false);
		play.setFocusPainted(false);
		play.setContentAreaFilled(false);
		// Play & Pause button Action
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Default (Not selected any song)
				if (musicIndex == -1)
					;
				// Play action
				else if (curIndex != musicIndex) {
					if (song != null)
						song.stop();
					playSong(musicList.getMusicPath(musicIndex), volume, 0);
					curIndex = musicIndex;
					playing = true;
					t.start();
				}
				// Resume action
				else if (!song.getIsRunning()) {
					song.playAfterPause(framePos);
					playing = true;
					t.start();
				}
				// Pause action
				else if (song.getIsRunning()) {
					framePos = song.getFramePosition();
					song.stop();
					playing = false;
					t.stop();
				}

			}

		});
		contentPane.add(play);

		JButton shuffle = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/shuffleoff.PNG"))); // shuffle
																													// button
		shuffle.setBounds(1024, 497, 53, 47);
		// Shuffle button action
		shuffle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ShuffleMod off
				if (shuffleMod) {
					shuffleMod = false;
					shuffle.setIcon(new ImageIcon(musicplayer.class.getResource("../picture/shuffleoff.png")));
				}
				// ShuffleMod on
				else {
					shuffleMod = true;
					index = 0;
					shuffle.setIcon(new ImageIcon(musicplayer.class.getResource("../picture/shuffle.png")));
					// Create random music list (De-duplication)
					Random r = new Random();
					randIndex = new int[musicList.musicList.length];
					for (int i = 0; i < musicList.musicList.length; i++) {
						if (i == 0 && song != null) {
							randIndex[0] = curIndex;
							continue;
						}
						randIndex[i] = r.nextInt(musicList.musicList.length);
						for (int j = 0; j < i; j++) {
							if (randIndex[i] == randIndex[j])
								i--;
						}
					}
				}
			}
		});
		contentPane.add(shuffle);

		JButton allplay = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/allplayoff.png")));
		allplay.setBounds(937, 496, 53, 47);
		// RepeatMod button action
		allplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// RepeatAll on
				if (repeatMod == 0) {
					repeatMod = 1;
					allplay.setIcon(new ImageIcon(musicplayer.class.getResource("../picture/allplay.png")));
				}
				// Repeat one song on
				else if (repeatMod == 1) {
					repeatMod = 2;
					allplay.setIcon(new ImageIcon(musicplayer.class.getResource("../picture/playone.png")));
				}
				// RepeatAll off
				else {
					repeatMod = 0;
					allplay.setIcon(new ImageIcon(musicplayer.class.getResource("../picture/allplayoff.png")));
				}
			}
		});
		contentPane.add(allplay);

		JButton search = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/search.PNG"))); // search
																												// button
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // if user click search button -> The screen3 containing the
															// searchtable is shown.
				screen1.setVisible(false);
				screen2.setVisible(false);
				screen3.setVisible(true);
			}
		});
		search.setToolTipText("search");
		search.setBounds(461, 54, 94, 30);
		contentPane.add(search);

		JButton playlist = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/playlist.PNG"))); // playlist
																													// button
		playlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // if user click playlist button -> The screen2 containing the
															// playlisttable is shown.
				screen1.setVisible(false);
				screen2.setVisible(true);
				screen3.setVisible(false);

				try {
					Class.forName("com.mysql.jdbc.Driver");
					con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
					stmt = con.createStatement();
					// rs = stmt.executeQuery("SELECT musicname,singer,genre FROM music.playlist");
					rs = stmt.executeQuery("SELECT musicname,singer,genre FROM " + pcget + "");
					playlisttable.setModel(DbUtils.resultSetToTableModel(rs));

				} catch (Exception arg0) {
					arg0.printStackTrace();
				}

			}

		});
		playlist.setToolTipText("Favorite Music");
		playlist.setBounds(264, 54, 110, 30);
		contentPane.add(playlist);

		JButton musicchart = new JButton(new ImageIcon(musicplayer.class.getResource("../picture/musicchart.PNG"))); // musicchart
																														// button
		musicchart.setToolTipText("music chart");
		musicchart.setOpaque(true);
		musicchart.addActionListener(new ActionListener() { // if user click musicchart button -> The screen1 containing
															// the charttable is shown.
			public void actionPerformed(ActionEvent arg0) {
				screen1.setVisible(true);
				screen2.setVisible(false);
				screen3.setVisible(false);
				try {
					Class.forName("com.mysql.jdbc.Driver");
					con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
					stmt = con.createStatement();
					rs = stmt.executeQuery("SELECT musicname,singer,genre FROM music.main;");
					charttable2.setModel(DbUtils.resultSetToTableModel(rs));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		musicchart.setBounds(33, 54, 143, 30);
		contentPane.add(musicchart);

		// Volume slider
		JSlider slider = new JSlider(-100, 100, 0);
		slider.setBackground(Color.GRAY);
		slider.setBounds(1165, 512, 100, 15);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider) e.getSource();
				double val = (double) s.getValue();
				volume = val / 200 * 84 - 37.5;
				if (song != null)
					song.changeVolume(volume);
			}

		});
		contentPane.add(slider);

		cover = new JLabel(new ImageIcon(musicplayer.class.getResource("../picture/coverex.PNG"))); // print
																									// music
																									// album // pictures
		cover.setBounds(871, 93, 363, 356);
		contentPane.add(cover);

		songname = new JLabel("SOLO"); // Print music title
		songname.setForeground(Color.BLACK);
		songname.setFont(new Font("Serif", Font.BOLD, 30));
		songname.setHorizontalAlignment(JLabel.CENTER);
		songname.setBounds(871, 23, 360, 35);
		contentPane.add(songname);

		singer = new JLabel("Jenney"); // Print singer
		singer.setForeground(Color.BLACK);
		singer.setFont(new Font("Serif", Font.BOLD, 20));
		singer.setHorizontalAlignment(JLabel.CENTER);
		singer.setBounds(871, 57, 360, 27);

		contentPane.add(singer);

		seekBar = new JProgressBar();
		seekBar.setBounds(845, 460, 400, 10);
		seekBar.setValue(0);
		seekBar.addMouseListener(new SeekbarEvent());
		contentPane.add(seekBar);

		playTime = new JLabel("0:00");
		playTime.setBounds(1215, 470, 40, 20);
		contentPane.add(playTime);

		currentTime = new JLabel("0:00");
		currentTime.setBounds(845, 470, 40, 20);
		contentPane.add(currentTime);

		currentLyric = new JLabel("");
		currentLyric.setHorizontalAlignment(JLabel.CENTER);
		currentLyric.setForeground(Color.BLACK);
		setFont(new Font("Serif", Font.BOLD, 20));
		currentLyric.setBounds(80, 110, 565, 27);
		contentPane.add(currentLyric);

		nextLyric = new JLabel("");
		nextLyric.setHorizontalAlignment(JLabel.CENTER);
		nextLyric.setForeground(Color.GRAY);
		nextLyric.setFont(new Font("Serif", Font.BOLD, 20));
		nextLyric.setBounds(80, 145, 565, 27);
		contentPane.add(nextLyric);

		// TimeCheck for current playtime & seekbar
		t = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cDuration = (double) song.getFramePosition() / song.getFrameLength() * pDuration;
				double cSeek = cDuration / pDuration * 100;
				currentTime.setText(String.format("%d:%02d%n", (int) cDuration / 60, (int) cDuration % 60));
				seekBar.setValue((int) cSeek);
				// When the song is over, play next song
				playNextSong();
				// Lyrics synchronized
				syncLyric();

			}
		});
	}

	private void playSong(String path, double volume, int framePos) {
		try {
			song = new Audio(path);
			AudioTag tag = new AudioTag(path);
			pDuration = tag.duration;
			songname.setText(tag.title);
			singer.setText(tag.artist);
			cover.setIcon(tag.img);
			playTime.setText(String.format("%d:%02d%n", (int) pDuration / 60, (int) pDuration % 60));
			lyric = tag.getLyric();

		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}

		song.play(volume, framePos);
	}

	private void playNextSong() {
		if (!song.getIsRunning()) {
			// repeatMod off
			if (repeatMod == 0) {
				if (shuffleMod) {
					if (index == randIndex.length)
						t.stop();
					else
						index++;
				} else {
					if (curIndex == musicList.musicList.length)
						t.stop();
					else
						curIndex++;
				}
			}
			// repeat one song
			else if (repeatMod == 2)
				;

			// repeatMod on
			else if (repeatMod == 1) {
				if (shuffleMod)
					index = (index + 1) % randIndex.length;
				else
					curIndex = (curIndex + 1) % musicList.musicList.length;
			}
			if (shuffleMod)
				if (t.isRunning())
					playSong(musicList.getMusicPath(randIndex[index]), volume, 0);
				else if (t.isRunning())
					playSong(musicList.getMusicPath(curIndex), volume, 0);
		}
	}

	private void syncLyric() {
		if (lyric != null) {
			for (int i = lyric.length - 1; i >= 0; i--) {
				if (Double.parseDouble(lyric[i][0]) < cDuration) {
					currentLyric.setText(lyric[i][1]);
					if (i == lyric.length - 1)
						nextLyric.setText("");
					else
						nextLyric.setText(lyric[i + 1][1]);
					break;
				}
			}
		}
	}

	private class ChartEvent extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			JTable jt = (JTable) e.getSource();
			musicIndex = jt.getSelectedRow() + 1; // start at 0 . so, we add +1 for instance
		}
	}

	private class SeekbarEvent extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			double prog = (double) e.getX() / 400;
			cDuration = prog * pDuration;
			seekBar.setValue((int) (prog * 100));
			song.playAfterPause((int) ((double) prog * song.getFrameLength()));
		}
	}

	class MusicAddListener1 implements ActionListener { // add music on playlist at charttable(MusicChart)
		public void actionPerformed(ActionEvent e) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root", "tjddnd1004");
				stmt = con.createStatement();
				int selRow = charttable2.getSelectedRow()+1;
				rs = stmt.executeQuery("select * from main where id= " + selRow + "");

				if (rs.next()) {

					int ID = rs.getInt("id");
					String fileName = rs.getString("filename");

					File file = new File(fileName);
					int fileLength = (int) file.length();
					System.out.println("fileLength : " + fileLength);
					InputStream is = new FileInputStream(file);
					//
					//

					//
					String title = rs.getString("musicname");
					String artist = rs.getString("singer");
					String genre = rs.getString("genre");
					String filePath = rs.getString("filePath");
					//

					String sql = "insert into " + pcget
							+ "(id, filename, file,musicname,singer,genre,filePath) values (?,?,?,?,?,?,?)";
					PreparedStatement pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, ID);
					pstmt.setString(2, fileName);
					pstmt.setBinaryStream(3, is, fileLength);
					pstmt.setString(4, title);
					pstmt.setString(5, artist);
					pstmt.setString(6, genre);
					pstmt.setString(7, filePath);
					pstmt.executeUpdate();
					pstmt.close();
					con.close();

				} else {
					JOptionPane.showMessageDialog(null, "no data");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	/*
	 * class MusicAddListener2 implements ActionListener { // add music on playlist
	 * at searchtable(searching) public void actionPerformed(ActionEvent e) { try {
	 * Class.forName("com.mysql.jdbc.Driver"); con =
	 * DriverManager.getConnection("jdbc:mysql://localhost:3306/music", "root",
	 * "tjddnd1004"); stmt = con.createStatement(); int selRow =
	 * searchtable.getSelectedRow() + 1; rs =
	 * stmt.executeQuery("select * from main where id= " + selRow + "");
	 * 
	 * if (rs.next()) {
	 * 
	 * int ID = rs.getInt("id"); String fileName = rs.getString("filename");
	 * 
	 * File file = new File(fileName); int fileLength = (int) file.length();
	 * System.out.println("fileLength : " + fileLength); InputStream is = new
	 * FileInputStream(file); // //
	 * 
	 * // String title = rs.getString("musicname"); String artist =
	 * rs.getString("singer"); String genre = rs.getString("genre"); String filePath
	 * = rs.getString("filePath"); //
	 * 
	 * String sql = "insert into " + pcget +
	 * "(id, filename, file,musicname,singer,genre,filePath) values (?,?,?,?,?,?,?)"
	 * ; PreparedStatement pstmt = con.prepareStatement(sql); pstmt.setInt(1, ID);
	 * pstmt.setString(2, fileName); pstmt.setBinaryStream(3, is, fileLength);
	 * pstmt.setString(4, title); pstmt.setString(5, artist); pstmt.setString(6,
	 * genre); pstmt.setString(7, filePath); pstmt.executeUpdate(); pstmt.close();
	 * con.close();
	 * 
	 * } else { JOptionPane.showMessageDialog(null, "no data"); } } catch (Exception
	 * e2) { e2.printStackTrace(); } } }
	 */

	// Timer timer;
	public ImageIcon format = null;
}