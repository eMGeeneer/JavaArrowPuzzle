// String name = "Ming Gao";
// String date = "1/25/22";
// Purpose: to make the arrow puzzle from Exponential Idle

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ArrowPuzzle extends JPanel implements ActionListener, ChangeListener, MouseListener {

	public static int n = 3;
	public static byte d = 4;
	public static int l, w, sl, sw;
	public static int screen = 0;
	public static boolean win = false;
	public static boolean suffer = false;
	public static long timer, time;
	public static String game = "sq";
	public static int[] hint = new int[3];
	public static HashMap<CirclePoint, int[]> clickCircles;
	public static HexGrid h = new HexGrid(3, 6);
	public static SqGrid sq = new SqGrid(3, 4);
	public static Clip bgm;	
	public final Font titleFont = new Font("Courier New", Font.BOLD, 72);
	public final Font buttonFont = new Font("Courier New", Font.BOLD, 24);
	public final Font instructFont = new Font("Courier New", Font.PLAIN, 14);
	public static JFrame frame;
	public static JPanel aPanel, pPanel, tPanel;
	public static JLabel dLabel;
	public static JScrollPane scroll;

	public ArrowPuzzle() {
		setPreferredSize(new Dimension(960, 600));
		frame.setResizable(false);
		setBackground(Color.WHITE);
		screen();
	}

	// graphics class
	// used to enter a name into the leaderboard
	public class EnterName extends JPanel implements ActionListener, Runnable {
		public boolean done = false;
		public JFrame nameFrame;
		public String name = "";
		public boolean error = false;

		public EnterName() {
			nameFrame = new JFrame("Enter your name");
			nameFrame.setPreferredSize(new Dimension(300, 150));

			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.PAGE_AXIS));
			namePanel.setBackground(Color.WHITE);

			JPanel textPanel = new JPanel();
			JPanel entryPanel = new JPanel();
			JPanel confirmPanel = new JPanel();
			JLabel nameLabel = new JLabel("Enter your name (max 16 chars)");
			JTextField nameEntry = new JTextField(16);
			JButton confirmButton = new JButton("Confirm");
			textPanel.setBackground(Color.WHITE);
			nameEntry.addActionListener(this);
			nameEntry.setActionCommand("name");
			entryPanel.setBackground(Color.WHITE);
			confirmButton.addActionListener(this);
			confirmButton.setActionCommand("confirm");
			confirmButton.setBackground(Color.WHITE);
			confirmPanel.setBackground(Color.WHITE);
			textPanel.add(nameLabel);
			entryPanel.add(nameEntry);
			confirmPanel.add(confirmButton);
			namePanel.add(textPanel);
			namePanel.add(entryPanel);
			namePanel.add(confirmPanel);

			nameFrame.add(namePanel);
			nameFrame.setResizable(false);
			nameFrame.pack();
			nameFrame.setVisible(true);

			Thread thread = new Thread(this);
			thread.start();
		}

		// graphics class
		// used to give an error pop-up if a name is not entered
		public class NameError extends JPanel implements ActionListener, Runnable {
			public JFrame errFrame;

			public NameError() {
				error = true;
				errFrame = new JFrame("ERROR");
				errFrame.setPreferredSize(new Dimension(300, 150));

				JPanel errPanel = new JPanel();
				errPanel.setLayout(new BoxLayout(errPanel, BoxLayout.PAGE_AXIS));
				errPanel.setBorder(BorderFactory.createEmptyBorder (5, 0, 0, 0));
				errPanel.setBackground(Color.WHITE);

				JPanel messagePanel = new JPanel();
				JPanel errButtonPanel = new JPanel();
				JLabel errLabel = new JLabel("<html><center>YOUR NAME CAN NOT BE BLANK<br>OR SOLELY COMPRISED OF SPACES</center></html>");
				messagePanel.setBackground(Color.WHITE);
				JButton errButton = new JButton("OK");
				errButton.addActionListener(this);
				errButton.setBackground(Color.WHITE);
				errButtonPanel.setBackground(Color.WHITE);
				messagePanel.add(errLabel);
				errButtonPanel.add(errButton);
				errPanel.add(messagePanel);
				errPanel.add(errButtonPanel);

				errFrame.add(errPanel);
				errFrame.setResizable(false);
				errFrame.pack();
				errFrame.setVisible(true);

				Thread thread = new Thread(this);
				thread.start();
			}

			// executes code on button press
			// @param e The ActionEvent from the JButton
			public void actionPerformed(ActionEvent e) {
				error = false;
				errFrame.dispose();
			}

			// executes brings this JFrame to the front of the screen
			public void run() {
				while (error) {
					errFrame.setVisible(true);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {

					}
				}
			}
		}

		// executes code on button press or read from a JTextField
		// @param e The ActionEvent from the JButton or JTextField
		public void actionPerformed(ActionEvent e) {
			String eName = e.getActionCommand();
			if (eName.equals("name")) {
				name = ((JTextField) e.getSource()).getText().trim();
				if (name.length() == 0 && !error) {
					new NameError();
				}
			}
			else if (eName.equals("confirm") && name.length() > 0) {
				done = true;
				screen = 0;
				screen();
				File leaderBoard = new File(String.format("%s%03d-%d.txt", game, n, d));
				Ranking newRank = new Ranking(name, time);
				try {
					BufferedReader input = new BufferedReader(new FileReader(leaderBoard));
					try {
						int numEntries = Integer.parseInt(input.readLine());
						TreeSet<Ranking> entry = new TreeSet<Ranking> ();
						for (int i = 0; i < numEntries; i++) {
							String line = input.readLine();
							entry.add(new Ranking(line.substring(0, 16).trim(), Long.parseLong(line.substring(17, line.lastIndexOf(' '))), Long.parseLong(line.substring(line.lastIndexOf(' ') + 1))));
						}
						input.close();
						entry.add(newRank);

						PrintWriter outFile = new PrintWriter(new FileWriter(leaderBoard));
						outFile.println(numEntries + 1);
						for (Ranking rank : entry) {
							outFile.println(rank.fileString());
						}
						outFile.close();
					} catch (NumberFormatException e1) {

					}
				} catch (FileNotFoundException e1) {
					try {
						PrintWriter outFile = new PrintWriter(new FileWriter(leaderBoard));
						outFile.print("1\n" + newRank.fileString());
						outFile.close();
					} catch (IOException e2) {

					}
				}
				catch (IOException e1) {

				}
				nameFrame.dispose();
			}
			else if (eName.equals("confirm") && !error) {
				new NameError();
			}
		}

		// executes brings this JFrame to the front of the screen
		public void run() {
			while (!done) {
				if (!error) {
					nameFrame.setVisible(true);
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	// graphics class
	// the game screen
	public class ArrowPanel extends JPanel {
		public int[][] hGrid = h.getGrid();
		public int[][] sqGrid = sq.getGrid(); 

		// draws the game screen
		// @param g Graphics
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (screen == 4) {
				g.drawOval(w / 2 - 50, l / 2 - 50, 101, 101);
				drawArrow(g, hGrid[0][0], d, w / 2, l / 2);
				for (int i = 1; i <= n; i++) {
					for (int j = 0; j < i; j++) {
						g.drawOval(w / 2 - 50 + j * 113, l / 2 - 50 - i * 130 + j * 65, 101, 101);
						drawArrow(g, hGrid[i][j], d, w / 2 + j * 113, l / 2 - i * 130 + j * 65);
						g.drawOval(w / 2 - 50 + i * 113, l / 2 - 50 - i * 65 + j * 130, 101, 101);
						drawArrow(g, hGrid[i][hGrid[i].length / 6 + j], d, w / 2 + i * 113, l / 2 - i * 65 + j * 130);
						g.drawOval(w / 2 - 50 + i * 113 - j * 113, l / 2 - 50 + i * 65 + j * 65, 101, 101);
						drawArrow(g, hGrid[i][hGrid[i].length / 3 + j], d, w / 2 + i * 113 - j * 113, l / 2 + i * 65 + j * 65);
						g.drawOval(w / 2 - 50 - j * 113, l / 2 - 50 + i * 130 - j * 65, 101, 101);
						drawArrow(g, hGrid[i][hGrid[i].length / 2 + j], d, w / 2 - j * 113, l / 2 + i * 130 - j * 65);
						g.drawOval(w / 2 - 50 - i * 113, l / 2 - 50 + i * 65 - j * 130, 101, 101);
						drawArrow(g, hGrid[i][hGrid[i].length * 2 / 3 + j], d, w / 2 - i * 113, l / 2 + i * 65 - j * 130);
						g.drawOval(w / 2 - 50 - i * 113 + j * 113, l / 2 - 50 - i * 65 - j * 65, 101, 101);
						drawArrow(g, hGrid[i][hGrid[i].length * 5 / 6 + j], d, w / 2 - i * 113 + j * 113, l / 2 - i * 65 - j * 65);
					}
				}
				if (hint[2] != 0) {
					g.setColor(Color.RED);
					if (hint[0] == 0) {
						g.drawOval(w / 2 - 57, l / 2 - 57, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2, l / 2 - 61);
					}
					else if (hint[1] / hint[0] == 0) {
						g.drawOval(w / 2 - 57 + hint[1] % hint[0] * 113, l / 2 - 57 - hint[0] * 130 + hint[1] % hint[0] * 65, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2 + hint[1] % hint[0] * 113, l / 2 - 61 - hint[0] * 130 + hint[1] % hint[0] * 65);
					}
					else if (hint[1] / hint[0] == 1) {
						g.drawOval(w / 2 - 57 + hint[0] * 113, l / 2 - 57 - hint[0] * 65 + hint[1] % hint[0] * 130, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2 + hint[0] * 113, l / 2 - 61 - hint[0] * 65 + hint[1] % hint[0] * 130);
					}
					else if (hint[1] / hint[0] == 2) {
						g.drawOval(w / 2 - 57 + hint[0] * 113 - hint[1] % hint[0] * 113, l / 2 - 57 + hint[0] * 65 + hint[1] % hint[0] * 65, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2 + hint[0] * 113 - hint[1] % hint[0] * 113, l / 2 - 61 + hint[0] * 65 + hint[1] % hint[0] * 65);
					}
					else if (hint[1] / hint[0] == 3) {
						g.drawOval(w / 2 - 57 - hint[1] % hint[0] * 113, l / 2 - 57 + hint[0] * 130 - hint[1] % hint[0] * 65, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2 - hint[1] % hint[0] * 113, l / 2 - 61 + hint[0] * 130 - hint[1] % hint[0] * 65);
					}
					else if (hint[1] / hint[0] == 4) {
						g.drawOval(w / 2 - 57 - hint[0] * 113, l / 2 - 57 + hint[0] * 65 - hint[1] % hint[0] * 130, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2 - hint[0] * 113, l / 2 - 61 + hint[0] * 65 - hint[1] % hint[0] * 130);
					}
					else {
						g.drawOval(w / 2 - 57 - hint[0] * 113 + hint[1] % hint[0] * 113, l / 2 - 57 - hint[0] * 65 - hint[1] % hint[0] * 65, 115, 115);
						g.drawString(Integer.toString(hint[2]), w / 2 - hint[0] * 113 + hint[1] % hint[0] * 113, l / 2 - 61 - hint[0] * 65 - hint[1] % hint[0] * 65);
					}
				}
			}
			else {
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						g.drawOval(25 + i * 126, 25 + j * 126, 101, 101);
						drawArrow(g, sqGrid[i + 1][j + 1], d, 75 + i * 126, 75 + j * 126);
					}
				}
				if (hint[2] != 0) {
					g.setColor(Color.RED);
					g.drawOval(hint[0] * 126 - 108, hint[1] * 126 - 108, 115, 115);
					g.drawString(Integer.toString(hint[2]), hint[0] * 126 - 51, hint[1] * 126 - 112);
				}
			}
		}
	}

	// graphics class
	// a small screen where the grid be can previewed
	public class PreviewPanel extends JPanel {
		
		// draws the preview screen
		// @param g Graphics
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (screen == 3) {
				g.drawOval(sw / 2 - 10, sl / 2 - 10, 21, 21);
				for (int i = 1; i <= n; i++) {
					for (int j = 0; j < i; j++) {
						g.drawOval(sw / 2 - 10 + j * 23, sl / 2 - 10 - i * 26 + j * 13, 21, 21);
						g.drawOval(sw / 2 - 10 + i * 23, sl / 2 - 10 - i * 13 + j * 26, 21, 21);
						g.drawOval(sw / 2 - 10 + i * 23 - j * 23, sl / 2 - 10 + i * 13 + j * 13, 21, 21);
						g.drawOval(sw / 2 - 10 - j * 23, sl / 2 - 10 + i * 26 - j * 13, 21, 21);
						g.drawOval(sw / 2 - 10 - i * 23, sl / 2 - 10 + i * 13 - j * 26, 21, 21);
						g.drawOval(sw / 2 - 10 - i * 23 + j * 23, sl / 2 - 10 - i * 13 - j * 13, 21, 21);
					}
				}
			}
			else {
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						g.drawOval(5 + i * 26, 5 + j * 26, 21, 21);
					}
				}
			}
		}
	}

	// graphics class
	// displays the timer
	public class TimePanel extends JPanel implements Runnable {
		
		// draws the timer
		// @param g Graphics
		public void paintComponent(Graphics g) {
			time = win ? time : System.currentTimeMillis() - timer;
			super.paintComponent(g);
			g.setFont(buttonFont);
			g.drawString(String.format("%02d:%05.2f", time / 60000, time / 1000.0 % 60), w / 2 - 40, 25);
			Thread thread = new Thread(this);
			thread.start();
		}

		// updates the timer
		public void run() {
			while (!win && (screen == 2 || screen == 4)) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	// graphics class
	// demonstrates how the arrows are rotated
	public class DemoPanel extends JPanel {
		
		// draws the demo screen
		// @param g Graphics
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);
			g.drawOval(132, 161, 115, 115);
			g.drawOval(132, 567, 115, 115);
			g.setColor(Color.BLACK);
			for (byte i = 0; i < 2; i++) {
				g.drawOval(139 + i * 428, 168, 101, 101);
				drawArrow(g, i, 6, 189 + i * 428, 218);
				g.drawOval(139 + i * 428, 38, 101, 101);
				drawArrow(g, i, 6, 189 + i * 428, 88);
				g.drawOval(252 + i * 428, 103, 101, 101);
				drawArrow(g, i, 6, 302 + i * 428, 153);
				g.drawOval(252 + i * 428, 233, 101, 101);
				drawArrow(g, i, 6, 302 + i * 428, 283);
				g.drawOval(139 + i * 428, 298, 101, 101);
				drawArrow(g, i, 6, 189 + i * 428, 348);
				g.drawOval(26 + i * 428, 233, 101, 101);
				drawArrow(g, i, 6, 76 + i * 428, 283);
				g.drawOval(26 + i * 428, 103, 101, 101);
				drawArrow(g, i, 6, 76 + i * 428, 153);
			}
			
			for (byte i = 0; i < 2; i++) {
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 3; k++) {
						g.drawOval(13 + j * 126 + i * 428, k * 126 + 448, 101, 101);
						drawArrow(g, i, 4, 63 + j * 126 + i * 428, k * 126 + 498);
					}
				}
			}
			
			for (int i = 0; i < 2; i++) {
				drawArrow(g, 1, 4, 403, 218 + i * 408);
			}
		}
	}

	// graphics class
	// demonstrates the hint system is displayed
	public class HintDemoPanel extends JPanel {
		
		// draws the hint demo screen
		// @param g Graphics
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);
			g.drawString("2", 186, 157);
			g.drawOval(132, 161, 115, 115);
			g.setColor(Color.BLACK);
			g.drawOval(139, 168, 101, 101);
			drawArrow(g, 4, 6, 189, 218);
			g.drawOval(139, 38, 101, 101);
			drawArrow(g, 4, 6, 189, 88);
			g.drawOval(252, 103, 101, 101);
			drawArrow(g, 4, 6, 302, 153);
			g.drawOval(252, 233, 101, 101);
			drawArrow(g, 4, 6, 302, 283);
			g.drawOval(139, 298, 101, 101);
			drawArrow(g, 4, 6, 189, 348);
			g.drawOval(26, 233, 101, 101);
			drawArrow(g, 4, 6, 76, 283);
			g.drawOval(26, 103, 101, 101);
			drawArrow(g, 4, 6, 76, 153);
		}
	}
	
	// graphics class
	// annyoing pop-up if the hint button is pressed while a hint is being displayed
	public class HintError extends JFrame implements ActionListener, Runnable{
		public JFrame hErrFrame;
		
		public HintError() {
			hErrFrame = new JFrame("ERROR");
			hErrFrame.setPreferredSize(new Dimension(320, 150));

			JPanel hErrPanel = new JPanel();
			hErrPanel.setLayout(new BoxLayout(hErrPanel, BoxLayout.PAGE_AXIS));
			hErrPanel.setBorder(BorderFactory.createEmptyBorder (5, 0, 0, 0));
			hErrPanel.setBackground(Color.WHITE);
			
			suffer = true;
			
			JPanel messagePanel = new JPanel();
			JPanel errButtonPanel = new JPanel();
			JLabel errLabel = new JLabel("<html><center>YOU ALREADY HAVE A HINT ON THE SCREEN<br>I'M ADDING 10 MORE SECONDS JUST FOR THAT</center></html>");
			messagePanel.setBackground(Color.WHITE);
			JButton errButton = new JButton("SUFFER");
			errButton.addActionListener(this);
			errButton.setBackground(Color.WHITE);
			errButtonPanel.setBackground(Color.WHITE);
			messagePanel.add(errLabel);
			errButtonPanel.add(errButton);
			hErrPanel.add(messagePanel);
			hErrPanel.add(errButtonPanel);

			hErrFrame.add(hErrPanel);
			hErrFrame.setResizable(false);
			hErrFrame.pack();
			hErrFrame.setVisible(true);

			Thread thread = new Thread(this);
			thread.start();
		}

		// executes code on button press
		// @param e The ActionEvent from the JButton
		public void actionPerformed(ActionEvent e) {
			suffer = false;
		}

		// brings this JFrame to the front
		public void run() {
			while (suffer) {
				hErrFrame.setVisible(true);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {

				}
			}
			hErrFrame.dispose();
		}
	}

	// executes code on button press
	// @param e The ActionEvent from the JButton
	public void actionPerformed(ActionEvent e) {
		String eName = e.getActionCommand();

		switch (eName) {
		case "square":
			screen = 1;
			screen();
			break;
		case "hex":
			screen = 3;
			screen();
			break;
		case "instruct":
			screen = 5;
			screen();
			break;
		case "lead":
			screen = 6;
			screen();
			break;
		case "exit":
			System.exit(1);
		case "d2":
			d = 2;
			screen();
			break;
		case "d4":
			d = 4;
			screen();
			break;
		case "d6":
			d = 6;
			screen();
			break;
		case "sqConfirm":
			screen = 2;
			game = "sq";
			timer = System.currentTimeMillis();
			for (int i = 0; i < 3; i++) {
				hint[i] = 0;
			}
			sq = new SqGrid(n + 2, d);
			frame.setResizable(true);

			screen();

			CirclePoint.setCentre(126, 126);
			CirclePoint.setGridType(true);
			clickCircles = new HashMap<CirclePoint, int[]>(n * n, 1);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					int[] c = {i + 1, j + 1};
					clickCircles.put(new CirclePoint(76 + i * 126, 76 + j * 126), c);
				}
			}

			do {
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						int clicks = (int) (Math.random() * d);
						for (int k = 0; k < clicks; k++) {
							sq.press(i + 1, j + 1);
						}
					}
				}
			} while (sq.hint()[2] == 0);
			break;
		case "hexConfirm":
			screen = 4;
			game = "hex";
			timer = System.currentTimeMillis();
			for (int i = 0; i < 3; i++) {
				hint[i] = 0;
			}
			h = new HexGrid(n + 1, d);
			frame.setResizable(true);

			screen();

			CirclePoint.setCentre(w / 2, l / 2);
			CirclePoint.setGridType(false);
			clickCircles = new HashMap<CirclePoint, int[]>(n * n * 3 + n * 3 + 1, 1);
			int[] c = {0, 0};
			clickCircles.put(new CirclePoint (w / 2, l / 2), c);
			for (int i = 1; i <= n; i++) {
				int[][] sc = new int[i * 6][2];
				for (int j = 0; j < sc.length; j++) {
					sc[j][0] = i;
					sc[j][1] = j;
				}
				for (int j = 0; j < i; j++) {
					clickCircles.put(new CirclePoint(w / 2 + j * 113, l / 2 - i * 130 + j * 65), sc[j]); 
					clickCircles.put(new CirclePoint(w / 2 + i * 113, l / 2 - i * 65 + j * 130), sc[sc.length / 6 + j]);
					clickCircles.put(new CirclePoint(w / 2 + i * 113 - j * 113, l / 2 + i * 65 + j * 65), sc[sc.length / 3 + j]);
					clickCircles.put(new CirclePoint(w / 2 - j * 113, l / 2 + i * 130 - j * 65), sc[sc.length / 2 + j]);
					clickCircles.put(new CirclePoint(w / 2 - i * 113, l / 2 + i * 65 - j * 130), sc[sc.length * 2 / 3 + j]);
					clickCircles.put(new CirclePoint(w / 2 - i * 113 + j * 113, l / 2 - i * 65 - j * 65), sc[sc.length * 5 / 6 + j]);
				}
			}

			do {
				for (int i = 0; i <= n; i++) {
					for (int j = 0; j < i * 6 || j == 0; j++) {
						int clicks = (int) (Math.random() * d);
						for (int k = 0; k < clicks; k++) {
							h.press(i, j);
						}
					}
				}
			} while (h.hint()[2] == 0);
			break;
		case "back":
			if (!win) {
				screen = 0;
				screen();
				suffer = false;
				frame.setResizable(false);
			}
			break;
		case "hHint":
			if (!win) {
				timer -= 10000;
				if (hint[2] == 0) {
					hint = h.hint();
					aPanel.repaint();
				}
				else {
					if (!suffer) {
						new HintError();
					}
					timer -= 10000;
				}
			}
			break;
		case "sqHint":
			if (!win) {
				timer -= 10000;
				if (hint[2] == 0) {
					hint = sq.hint();
					aPanel.repaint();
				}
				else {
					if (!suffer) {
						new HintError();
					}
					timer -= 10000;
				}
			}
			break;
		case "sqShape":
			game = "sq";
			d = d == 6 ? 4 : d;
			screen();
			break;
		case "hShape":
			game = "hex";
			d = d == 4 ? 6 : d;
			screen();
		}
	}

	// changes the n variable with the JSpinner
	// @param e The ChangeEvent from the JSpinner
	public void stateChanged(ChangeEvent e) {
		n = (int) (((JSpinner) e.getSource()).getValue());
		screen();
	}

	// unused
	// @param e MouseEvent
	public void mouseClicked(MouseEvent e) {

	}

	// unused
	// @param e MouseEvent
	public void mousePressed(MouseEvent e) {

	}

	// handles clicking on the game screen
	// @param e MouseEvent
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (screen == 4 && !win && !suffer) {
				int x = e.getX();
				int y = e.getY();
				CirclePoint c = new CirclePoint(x, y);
				if (clickCircles.containsKey(c)) {
					int[] gc = clickCircles.get(c);
					win = h.press(gc[0], gc[1]);
					if (gc[0] == hint[0] && gc[1] == hint[1] && hint[2] != 0) {
						hint[2]--;
					}
					aPanel.repaint();
				}
			}
			else if (screen == 2 && !win && !suffer) {
				int x = e.getX();
				int y = e.getY();
				CirclePoint c = new CirclePoint(x, y);
				if (clickCircles.containsKey(c)) {
					int[] gc = clickCircles.get(c);
					win = sq.press(gc[0], gc[1]);
					if (gc[0] == hint[0] && gc[1] == hint[1] && hint[2] != 0) {
						hint[2]--;
					}
					aPanel.repaint();
				}
			}
			if (win) {
				new EnterName();
			}
		}
	}

	// unused
	// @param e MouseEvent
	public void mouseEntered(MouseEvent e) {

	}

	// unused
	// @param e MouseEvent
	public void mouseExited(MouseEvent e) {

	}

	// draws arrows at a given coordinate
	// @param g Graphics
	// @param dir The direction the arrow is facing
	// @param maxDir The number of directions the arrow can face
	// @param x The x coordinate
	// @param y The y coordinate
	public void drawArrow(Graphics g, int dir, int i, int x, int y) {
		if (dir == 0) {
			g.drawLine(x, y - 11, x - 39, y + 11);
			g.drawLine(x, y - 11, x + 39, y + 11);
		}
		else if (i == 6) {
			if (dir == 1) {
				g.drawLine(x + 10, y - 5, x - 29, y - 28);
				g.drawLine(x + 10, y - 5, x + 10, y + 40);
			}
			else if (dir == 2) {
				g.drawLine(x + 10, y + 5, x - 29, y + 28);
				g.drawLine(x + 10, y + 5, x + 10, y - 40);
			}
			else if (dir == 3) {
				g.drawLine(x, y + 11, x - 39, y - 11);
				g.drawLine(x, y + 11, x + 39, y - 11);
			}
			else if (dir == 4) {
				g.drawLine(x - 10, y + 5, x + 29, y + 28);
				g.drawLine(x - 10, y + 5, x - 10, y - 40);
			}
			else {
				g.drawLine(x - 10, y - 5, x + 29, y - 28);
				g.drawLine(x - 10, y - 5, x - 10, y + 40);
			}
		}
		else if (i == 4) {
			if (dir == 1) {
				g.drawLine(x + 11, y, x - 11, y + 39);
				g.drawLine(x + 11, y, x - 11, y - 39);
			}
			else if (dir == 2) {
				g.drawLine(x, y + 11, x - 39, y - 11);
				g.drawLine(x, y + 11, x + 39, y - 11);
			}
			else {
				g.drawLine(x - 11, y, x + 11, y - 39);
				g.drawLine(x - 11, y, x + 11, y + 39);
			}
		}
		else {
			g.drawLine(x, y + 11, x - 39, y - 11);
			g.drawLine(x, y + 11, x + 39, y - 11);
		}
	}

	// updates the screen and allows for the changing of screens
	public void screen() {
		removeAll();
		switch (screen) {
		case 0: { // main menu
			win = false;
			
			setPreferredSize(new Dimension(960, 600));
			setLayout(new FlowLayout());
			bgm.stop();

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder (80, 0, 0, 0));
			panel.setBackground(Color.WHITE);

			JPanel titlePanel = new JPanel();
			titlePanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 50, 0));
			titlePanel.setBackground(Color.WHITE);
			JLabel title = new JLabel("Arrow Puzzle");
			title.setFont(titleFont);
			titlePanel.add(title);
			panel.add(titlePanel);

			JPanel squarePanel = new JPanel();
			JPanel hexPanel = new JPanel();
			JPanel instructPanel = new JPanel();
			JPanel leadPanel = new JPanel();
			JPanel exitPanel = new JPanel();
			JButton square = new JButton("Square Grid");
			JButton hex = new JButton("Hex Grid");
			JButton instruct = new JButton("Instructions");
			JButton lead = new JButton("Leaderboards");
			JButton exit = new JButton("Exit");
			square.setActionCommand("square");
			square.addActionListener(this);
			square.setPreferredSize(new Dimension(210, 54));
			square.setBackground(Color.WHITE);
			square.setFont(buttonFont);
			squarePanel.setBackground(Color.WHITE);
			hex.setActionCommand("hex");
			hex.addActionListener(this);
			hex.setPreferredSize(new Dimension(210, 54));
			hex.setBackground(Color.WHITE);
			hex.setFont(buttonFont);
			hexPanel.setBackground(Color.WHITE);
			instruct.setActionCommand("instruct");
			instruct.addActionListener(this);
			instruct.setPreferredSize(new Dimension(210, 54));
			instruct.setBackground(Color.WHITE);
			instruct.setFont(buttonFont);
			instructPanel.setBackground(Color.WHITE);
			lead.setActionCommand("lead");
			lead.addActionListener(this);
			lead.setPreferredSize(new Dimension(210, 54));
			lead.setBackground(Color.WHITE);
			lead.setFont(buttonFont);
			leadPanel.setBackground(Color.WHITE);
			exit.setActionCommand("exit");
			exit.addActionListener(this);
			exit.setPreferredSize(new Dimension(210, 54));
			exit.setBackground(Color.WHITE);
			exit.setFont(buttonFont);
			exitPanel.setBackground(Color.WHITE);
			squarePanel.add(square);
			hexPanel.add(hex);
			instructPanel.add(instruct);
			leadPanel.add(lead);
			exitPanel.add(exit);
			panel.add(squarePanel);
			panel.add(hexPanel);
			panel.add(instructPanel);
			panel.add(leadPanel);
			panel.add(exitPanel);

			add(panel);
			break;
		}
		case 1: { // sqConfig
			d = d == 6 ? 4 : d;
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createEmptyBorder (40, 0, 0, 0));
			panel.setBackground(Color.WHITE);

			JPanel sizePanel = new JPanel();
			sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.PAGE_AXIS));

			JPanel sLabelPanel = new JPanel();
			JPanel sSpinPanel = new JPanel();
			JPanel dLabelPanel = new JPanel();
			JPanel dButtonPanel = new JPanel();
			JPanel sqConfirmPanel = new JPanel();
			JPanel sPreviewPanel = new JPanel();
			JLabel sizeLabel = new JLabel("Size of Board");
			JSpinner sizeSpin = new JSpinner(new SpinnerNumberModel(n, 3, 127, 1));
			dLabel = new JLabel("Directions: " + d);
			JButton dButton1 = new JButton("2");
			JButton dButton2 = new JButton("4");
			JButton sqConfirm = new JButton("Confirm");
			sl = (n) * 21 + (n + 1) * 5;
			sw = sl;
			pPanel = new PreviewPanel();
			sizeLabel.setFont(buttonFont);
			sLabelPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			sLabelPanel.setBackground(Color.WHITE);
			sizeSpin.addChangeListener(this);
			sizeSpin.setFont(buttonFont);
			sizeSpin.setPreferredSize(new Dimension(90, 30));
			sSpinPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			sSpinPanel.setBackground(Color.WHITE);
			dLabel.setFont(buttonFont);
			dLabelPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			dLabelPanel.setBackground(Color.WHITE);
			dButton1.setActionCommand("d2");
			dButton1.addActionListener(this);
			dButton1.setFont(buttonFont);
			dButton1.setBackground(Color.WHITE);
			dButton2.setActionCommand("d4");
			dButton2.addActionListener(this);
			dButton2.setFont(buttonFont);
			dButton2.setBackground(Color.WHITE);
			dButtonPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			dButtonPanel.setBackground(Color.WHITE);
			sqConfirm.setActionCommand("sqConfirm");
			sqConfirm.addActionListener(this);
			sqConfirm.setFont(buttonFont);
			sqConfirm.setBackground(Color.WHITE);
			sqConfirmPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			sqConfirmPanel.setBackground(Color.WHITE);
			pPanel.setPreferredSize(new Dimension(sw - 10, sl - 10));
			pPanel.setBackground(Color.WHITE);
			JScrollPane sScroll = new JScrollPane(pPanel);
			JPanel sScrollPanel = new JPanel();
			sScrollPanel.setLayout(new BorderLayout());
			sScrollPanel.add(sScroll, BorderLayout.CENTER);
			int dsl = sl < 220 ? sl + 5: 240;
			int dsw = sw < 220 ? sw + 5: 240;
			sScrollPanel.setPreferredSize(new Dimension(dsw, dsl));
			sPreviewPanel.setBackground(Color.WHITE);
			sLabelPanel.add(sizeLabel);
			sSpinPanel.add(sizeSpin);
			dLabelPanel.add(dLabel);
			dButtonPanel.add(dButton1);
			dButtonPanel.add(dButton2);
			sqConfirmPanel.add(sqConfirm);
			sPreviewPanel.add(sScrollPanel);
			sizePanel.add(sLabelPanel);
			sizePanel.add(sSpinPanel);
			sizePanel.add(dLabelPanel);
			sizePanel.add(dButtonPanel);
			sizePanel.add(sqConfirmPanel);
			sizePanel.add(sPreviewPanel);

			panel.add(sizePanel, BorderLayout.CENTER);
			add(panel);
			break;
		}
		case 2: { // sqGame
			l = n * 101 + (n + 1) * 25;
			w = l;
			int dl = l < Toolkit.getDefaultToolkit().getScreenSize().height - 180 ? l + 105: Toolkit.getDefaultToolkit().getScreenSize().height - 75;
			int dw = w + 25 < Toolkit.getDefaultToolkit().getScreenSize().width ? w + 25: Toolkit.getDefaultToolkit().getScreenSize().width;
			dw -= l < Toolkit.getDefaultToolkit().getScreenSize().height - 180 ? 20 : 0;

			setLayout(new BorderLayout());

			aPanel = new ArrowPanel();
			aPanel.setPreferredSize(new Dimension(w, l));
			aPanel.setBackground(Color.WHITE);
			aPanel.addMouseListener(this);

			scroll = new JScrollPane(aPanel);
			scroll.getVerticalScrollBar().setUnitIncrement(20);
			add(scroll, BorderLayout.CENTER);

			tPanel = new TimePanel();
			tPanel.setBackground(Color.WHITE);
			tPanel.setPreferredSize(new Dimension(w, 35));
			add(tPanel, BorderLayout.NORTH);

			JPanel sqButtonPanel = new JPanel();
			JButton sqHint = new JButton("Hint");
			JLabel Spacer = new JLabel("");
			JButton exit = new JButton("Back");
			sqHint.setActionCommand("sqHint");
			sqHint.addActionListener(this);
			sqHint.setFont(buttonFont);
			sqHint.setBackground(Color.WHITE);
			Spacer.setPreferredSize(new Dimension(80, 54));
			exit.setActionCommand("back");
			exit.addActionListener(this);
			exit.setFont(buttonFont);
			exit.setBackground(Color.WHITE);
			sqButtonPanel.setBackground(Color.WHITE);
			sqButtonPanel.add(sqHint);
			sqButtonPanel.add(Spacer);
			sqButtonPanel.add(exit);
			add(sqButtonPanel, BorderLayout.SOUTH);

			bgm.stop();
			bgm.flush();
			bgm.setFramePosition(0);
			bgm.loop(bgm.LOOP_CONTINUOUSLY);

			setPreferredSize(new Dimension(dw, dl));
			break;
		}
		case 3: { // hexConfig
			d = d == 4 ? 6 : d;
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createEmptyBorder (40, 0, 0, 0));
			panel.setBackground(Color.WHITE);

			JPanel sizePanel = new JPanel();
			sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.PAGE_AXIS));

			JPanel sLabelPanel = new JPanel();
			JPanel sSpinPanel = new JPanel();
			JPanel dLabelPanel = new JPanel();
			JPanel dButtonPanel = new JPanel();
			JPanel hConfirmPanel = new JPanel();
			JPanel sPreviewPanel = new JPanel();
			JLabel sizeLabel = new JLabel("Size of Board");
			JSpinner sizeSpin = new JSpinner(new SpinnerNumberModel(n, 3, 127, 1));
			dLabel = new JLabel("Directions: " + d);
			JButton dButton1 = new JButton("2");
			JButton dButton2 = new JButton("6");
			JButton hexConfirm = new JButton("Confirm");
			sl = (2 * n + 1) * 18 + (n + 1) * 18;
			sw = (2 * n + 1) * 16 + (n + 1) * 16;
			pPanel = new PreviewPanel();
			sizeLabel.setFont(buttonFont);
			sLabelPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			sLabelPanel.setBackground(Color.WHITE);
			sizeSpin.addChangeListener(this);
			sizeSpin.setFont(buttonFont);
			sizeSpin.setPreferredSize(new Dimension(90, 30));
			sSpinPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			sSpinPanel.setBackground(Color.WHITE);
			dLabel.setFont(buttonFont);
			dLabelPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			dLabelPanel.setBackground(Color.WHITE);
			dButton1.setActionCommand("d2");
			dButton1.addActionListener(this);
			dButton1.setFont(buttonFont);
			dButton1.setBackground(Color.WHITE);
			dButton2.setActionCommand("d6");
			dButton2.addActionListener(this);
			dButton2.setFont(buttonFont);
			dButton2.setBackground(Color.WHITE);
			dButtonPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			dButtonPanel.setBackground(Color.WHITE);
			hexConfirm.setActionCommand("hexConfirm");
			hexConfirm.addActionListener(this);
			hexConfirm.setFont(buttonFont);
			hexConfirm.setBackground(Color.WHITE);
			hConfirmPanel.setBorder(BorderFactory.createEmptyBorder (0, 0, 10, 0));
			hConfirmPanel.setBackground(Color.WHITE);
			pPanel.setPreferredSize(new Dimension(sw - 10, sl - 10));
			pPanel.setBackground(Color.WHITE);
			JScrollPane sScroll = new JScrollPane(pPanel);
			JPanel sScrollPanel = new JPanel();
			sScrollPanel.setLayout(new BorderLayout());
			sScrollPanel.add(sScroll, BorderLayout.CENTER);
			int dsl = sl < 255 ? sl + 3: 265;
			int dsw = sw < 220 ? sw + 3: 230;
			sScrollPanel.setPreferredSize(new Dimension(dsw, dsl));
			sPreviewPanel.setBackground(Color.WHITE);
			sLabelPanel.add(sizeLabel);
			sSpinPanel.add(sizeSpin);
			dLabelPanel.add(dLabel);
			dButtonPanel.add(dButton1);
			dButtonPanel.add(dButton2);
			hConfirmPanel.add(hexConfirm);
			sPreviewPanel.add(sScrollPanel);
			sizePanel.add(sLabelPanel);
			sizePanel.add(sSpinPanel);
			sizePanel.add(dLabelPanel);
			sizePanel.add(dButtonPanel);
			sizePanel.add(hConfirmPanel);
			sizePanel.add(sPreviewPanel);

			panel.add(sizePanel, BorderLayout.CENTER);
			add(panel);
			break;
		}
		case 4: { // hexGame
			l = (2 * n + 1) * 87 + (n + 1) * 87;
			w = (2 * n + 1) * 76 + (n + 1) * 75;
			int dl = l < Toolkit.getDefaultToolkit().getScreenSize().height - 180 ? l + 105 : Toolkit.getDefaultToolkit().getScreenSize().height - 75;
			int dw = w + 25 < Toolkit.getDefaultToolkit().getScreenSize().width ? w  + 25: Toolkit.getDefaultToolkit().getScreenSize().width;
			dw -= l < Toolkit.getDefaultToolkit().getScreenSize().height - 180 ? 20 : 0;

			setLayout(new BorderLayout());

			aPanel = new ArrowPanel();
			aPanel.setPreferredSize(new Dimension(w, l));
			aPanel.setBackground(Color.WHITE);
			aPanel.addMouseListener(this);

			scroll = new JScrollPane(aPanel);
			scroll.getVerticalScrollBar().setUnitIncrement(20);
			add(scroll, BorderLayout.CENTER);

			tPanel = new TimePanel();
			tPanel.setBackground(Color.WHITE);
			tPanel.setPreferredSize(new Dimension(w, 35));
			add(tPanel, BorderLayout.NORTH);

			JPanel hButtonPanel = new JPanel();
			JButton hHint = new JButton("Hint");
			JLabel Spacer = new JLabel("");
			JButton exit = new JButton("Back");
			hHint.setActionCommand("hHint");
			hHint.addActionListener(this);
			hHint.setFont(buttonFont);
			hHint.setBackground(Color.WHITE);
			Spacer.setPreferredSize(new Dimension(80, 54));
			exit.setActionCommand("back");
			exit.addActionListener(this);
			exit.setFont(buttonFont);
			exit.setBackground(Color.WHITE);
			hButtonPanel.setBackground(Color.WHITE);
			hButtonPanel.add(hHint);
			hButtonPanel.add(Spacer);
			hButtonPanel.add(exit);
			add(hButtonPanel, BorderLayout.SOUTH);

			bgm.stop();
			bgm.flush();
			bgm.setFramePosition(0);
			bgm.loop(bgm.LOOP_CONTINUOUSLY);

			setPreferredSize(new Dimension(dw, dl));
			break;
		}
		case 5: { // instructions
			setLayout(new BorderLayout());
			
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			panel.setBorder(BorderFactory.createEmptyBorder (20, 0, 20, 0));
			panel.setBackground(Color.WHITE);

			JPanel exPlanel = new JPanel();
			JLabel textPlain = new JLabel("<html>Make all the arrows face up to win<br>Clicking a circle will rotate itself and all adjacent circles once clockwise</html>");
			textPlain.setFont(instructFont);
			exPlanel.setBackground(Color.WHITE);
			exPlanel.add(textPlain);
			panel.add(exPlanel);

			JPanel demo = new JPanel();
			JPanel dPanel = new DemoPanel();
			dPanel.setPreferredSize(new Dimension(806, 908));
			dPanel.setBackground(Color.WHITE);
			demo.setBackground(Color.WHITE);
			demo.add(dPanel);
			panel.add(demo);
			
			JPanel furtherExPlanel = new JPanel();
			JLabel furtherTextPlain = new JLabel("<html>Hints will tell you how many times to rotate a random circle<br>Hints also add 10 seconds to the timer</html>");
			furtherTextPlain.setFont(instructFont);
			furtherExPlanel.setBackground(Color.WHITE);
			furtherExPlanel.add(furtherTextPlain);
			panel.add(furtherExPlanel);
			
			JPanel hintDemo = new JPanel();
			JPanel hdPanel = new HintDemoPanel();
			hdPanel.setPreferredSize(new Dimension(378, 435));
			hdPanel.setBackground(Color.WHITE);
			hintDemo.setBackground(Color.WHITE);
			hintDemo.add(hdPanel);
			panel.add(hintDemo);
			
			JPanel backPanel = new JPanel();
			JButton back = new JButton("Back");
			back.setActionCommand("back");
			back.addActionListener(this);
			back.setFont(buttonFont);
			back.setBackground(Color.WHITE);
			backPanel.setBackground(Color.WHITE);
			backPanel.add(back);
			panel.add(backPanel);
			
			JScrollPane scroll = new JScrollPane(panel);
			scroll.getVerticalScrollBar().setUnitIncrement(20);
			add(scroll, BorderLayout.CENTER);
			break;
		}
		case 6: { // leaderboard
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder (10, 0, 0, 0));
			
			JPanel navPanel = new JPanel();
			navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.PAGE_AXIS));
			navPanel.setBackground(Color.WHITE);
			JPanel catPanel = new JPanel();
			JPanel buttonPanel = new JPanel();
			JPanel sizePanel = new JPanel();
			String mode = game.equals("hex") ? "Hexagonal" : "Square";
			JLabel catLabel = new JLabel(String.format("%s Grid, %d directions", mode, d));
			JButton sqShapeButton = new JButton("Square");
			JButton hShapeButton = new JButton("Hex");
			JLabel buttonSpacer = new JLabel(" ");
			JButton d2Button = new JButton("2");
			String dir = game.equals("hex") ? "6" : "4";
			JButton d2plusButton = new JButton(dir);
			JLabel sizeLabel = new JLabel("Size: ");
			JSpinner sizeSpin = new JSpinner(new SpinnerNumberModel(n, 3, 127, 1));
			catLabel.setFont(buttonFont);
			catPanel.setBackground(Color.WHITE);
			sqShapeButton.setActionCommand("sqShape");
			sqShapeButton.addActionListener(this);
			sqShapeButton.setFont(buttonFont);
			sqShapeButton.setPreferredSize(new Dimension(125, 45));
			sqShapeButton.setBackground(Color.WHITE);
			buttonSpacer.setPreferredSize(new Dimension(70, 45));
			hShapeButton.setActionCommand("hShape");
			hShapeButton.addActionListener(this);
			hShapeButton.setFont(buttonFont);
			hShapeButton.setPreferredSize(new Dimension(125, 45));
			hShapeButton.setBackground(Color.WHITE);
			d2Button.setActionCommand("d2");
			d2Button.addActionListener(this);
			d2Button.setFont(buttonFont);
			d2Button.setPreferredSize(new Dimension(125, 45));
			d2Button.setBackground(Color.WHITE);
			d2plusButton.setActionCommand("d" + dir);
			d2plusButton.addActionListener(this);
			d2plusButton.setFont(buttonFont);
			d2plusButton.setPreferredSize(new Dimension(125, 45));
			d2plusButton.setBackground(Color.WHITE);
			buttonPanel.setBackground(Color.WHITE);
			sizeLabel.setFont(buttonFont);
			sizeSpin.addChangeListener(this);
			sizeSpin.setFont(buttonFont);
			sizeSpin.setPreferredSize(new Dimension(75, 30));
			sizePanel.setBackground(Color.WHITE);
			catPanel.add(catLabel);
			buttonPanel.add(sqShapeButton);
			buttonPanel.add(hShapeButton);
			buttonPanel.add(buttonSpacer);
			buttonPanel.add(d2Button);
			buttonPanel.add(d2plusButton);
			sizePanel.add(sizeLabel);
			sizePanel.add(sizeSpin);
			navPanel.add(catPanel);
			navPanel.add(buttonPanel);
			navPanel.add(sizePanel);
			add(navPanel, BorderLayout.NORTH);
			
			JPanel panel = new JPanel();
			try {
				BufferedReader input = new BufferedReader(new FileReader(new File(String.format("%s%03d-%d.txt", game, n, d))));
				JTextArea scores = new JTextArea();
				try {
					int numEntries = Integer.parseInt(input.readLine());
					int width = Integer.toString(numEntries).length() > 4 ? Integer.toString(numEntries).length() : 4;
					String header = "Rank     ";
					for (int i = 4; i < width; i++) {
						header += " ";
					}
					scores.append(header + String.format("%-16s     %-8s     %s%n", "Name", "Time", "Date"));
					for (int i = 0; i < numEntries; i++) {
						String digits = "";
						for (int j = Integer.toString(i + 1).length(); j < width - 1; j++) {
							digits += " ";
						}
						String line = input.readLine();
						Ranking score = new Ranking(line.substring(0, 16).trim(), Long.parseLong(line.substring(17, line.lastIndexOf(' '))), Long.parseLong(line.substring(line.lastIndexOf(' ') + 1)));
						scores.append(digits + (i + 1) + ".     " + score.toString() + "\n");
					}
					input.close();
					scores.setFont(instructFont);
					panel.add(scores);
				} catch (NumberFormatException e) {

				}
			} catch (FileNotFoundException e) {
				panel.setBorder(BorderFactory.createEmptyBorder (120, 0, 0, 0));
				JLabel emptyLead = new JLabel("<html><center>This leaderboard is currently empty<br>Be the first to add to this category</center></html>");
				emptyLead.setFont(buttonFont);
				panel.add(emptyLead);
			}
			catch (IOException e) {

			}
			panel.setBackground(Color.WHITE);
			
			JScrollPane scroll = new JScrollPane(panel);
			scroll.getVerticalScrollBar().setUnitIncrement(20);
			add(scroll, BorderLayout.CENTER);
			
			JPanel backPanel = new JPanel();
			JButton back = new JButton("Back");
			back.setActionCommand("back");
			back.addActionListener(this);
			back.setFont(buttonFont);
			back.setBackground(Color.WHITE);
			backPanel.setBackground(Color.WHITE);
			backPanel.add(back);
			
			add(backPanel, BorderLayout.SOUTH);
			break;
		}
		}
		repaint();
		frame.setVisible(true);
		frame.pack();
	}

	public static void main(String[] args) {
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File("bgm.wav"));
			bgm = AudioSystem.getClip();
			bgm.open(sound);
		}
		catch (UnsupportedAudioFileException e) {

		}
		catch (LineUnavailableException e) {

		}
		catch (IOException e) {

		}

		frame = new JFrame("Arrow Puzzle");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JComponent content = new ArrowPuzzle();
		content.setOpaque(true);
		frame.setContentPane(content);
		frame.pack();
		frame.setVisible(true);
	}
}
