import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

// title BGM

class TitleLabel extends JLabel implements Runnable {
	Font font;
	int x, y;
	int width, height;
	Color color;
	Thread t;
	String text;
	boolean b = false;

	TitleLabel(String str, Font f, int _x, int _y, int w, int h, Color c) {
		super(str);
		text = str;
		font = f;
		x = _x;
		y = _y;
		width = w;
		height = h;
		color = c;
		t = new Thread(this);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSize(width, height);
		setLocation(x, y);
		setFont(font);
		setForeground(color);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(500);
				b = !b;
				if (b)
					setForeground(new Color(0, 0, 0, 0));
				else {
					setForeground(color);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class GameTitlePanel extends JPanel {
	BlockGame frame;

	ArrayList<TitleLabel> labels;
	JLabel spaceLabel;
	Clip gameStart;

	GameTitlePanel(BlockGame f) {
		frame = f;
		setLayout(null);
		frame.yourScore = 0;

		try {
			gameStart = AudioSystem.getClip();
			URL url = getClass().getResource("Victoria-Forest.wav"); // bytecode 위치 찾음
			AudioInputStream audioInputStream;
			audioInputStream = AudioSystem.getAudioInputStream(url);
			gameStart.open(audioInputStream);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		gameStart.start();
		
		Font BodoniFont = new Font("Bodoni MT Black", Font.BOLD, 70);
		Font BritanicFont = new Font("Britannic Bold", Font.BOLD, 30);
		Font SegoePrintFont = new Font("Segoe Print", Font.PLAIN, 20);

		labels = new ArrayList<>();
		labels.add(new TitleLabel("BLOCK BREAKER", BodoniFont, 0, 320, 800, 70, new Color(255, 190, 0, 250)));
		labels.add(new TitleLabel("BLOCK BREAKER", BodoniFont, 1, 321, 800, 70, Color.white));
		labels.add(new TitleLabel("PRESS SPACE TO PLAY", BritanicFont, 0, 500, 800, 40, Color.red));
		labels.add(new TitleLabel("Java Programming", SegoePrintFont, 0,680, 800, 30, Color.white));
		labels.add(new TitleLabel("Homework #5", SegoePrintFont, 0,710, 800, 30, Color.white));
		for (var l : labels) {
			if (l.getText().equals("PRESS SPACE TO PLAY"))
				l.t.start();
			add(l);
		}

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					gameStart.stop();
					frame.addGamePanel();
					return;
				}
			}
		});

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Color startColor = new Color(0, 0, 0);
		Color endColor = new Color(30, 30, 30);
		GradientPaint gp = new GradientPaint(400, 0, startColor, 400, 800, endColor);
		g2.setPaint(gp);
		g2.fillRect(0, 0, 800, 800);

		int gap = 5;
		int blockWidthCnt = 10;
		int blockHeightCnt = 8;
		int totalBlockWidth = 785 - 5 * (blockWidthCnt + 2);
		int blockW = totalBlockWidth / blockWidthCnt;
		int blockH = (int) (blockW * 0.33);
		for (int i = 0; i < blockWidthCnt; i++) {
			for (int j = 0; j < blockHeightCnt; j++) {
				if (i == 1 && j > blockHeightCnt - 3)
					continue;
				if (i == 2 && j > blockHeightCnt - 4)
					continue;
				if (i == 3 && j > blockHeightCnt - 2)
					continue;
				if (i == 5 && j > blockHeightCnt - 2)
					continue;
				if (i == 6 && j > blockHeightCnt - 4)
					continue;
				if (i == 7 && j > blockHeightCnt - 3)
					continue;
				if (i == 9 && j > blockHeightCnt - 4)
					continue;

				Color color = new Color(255, 200, 0);

				g.setColor(new Color(230,230,230));
				g.fillRoundRect(i + gap * (i + 1) + blockW * i - 1, j + gap * (j + 1) + blockH * j - 1, blockW+1,
						blockH+1, 1, 1);
				g.setColor(new Color(180, 175, 180));
				if (i == 0 && (j == 3 || j == 4))
					g.setColor(color);
				if (i == 1 && (j == 2 || j == 5))
					g.setColor(color);
				if (i == 2 && (j == 1 || j == 3))
					g.setColor(color);
				if (i == 3 && (j == 4))
					g.setColor(color);
				if (i == 5 && (j == 0 || j == 2 || j == 8))
					g.setColor(color);
				if (i == 6 && (j == 3 || j == 6))
					g.setColor(color);
				if (i == 7 && (j == 2))
					g.setColor(color);
				if (i == 8 && (j == 1 || j == 4))
					g.setColor(color);
				if (i == 9 && (j == 3))
					g.setColor(color);

				g.fillRect(i + gap * (i + 1) + blockW * i, j + gap * (j + 1) + blockH * j, blockW, blockH);
			}
		}

	}

}