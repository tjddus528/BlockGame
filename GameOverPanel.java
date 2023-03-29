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

class TitleLabel2 extends JLabel implements Runnable {
	Font font;
	int x, y;
	int width, height;
	Color color;
	Thread t;
	String text;
	boolean b = false;

	TitleLabel2(String str, Font f, int _x, int _y, int w, int h, Color c) {
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

public class GameOverPanel extends JPanel {
	ArrayList<TitleLabel2> labels;
	BlockGame frame;

	Clip gameDead;

	GameOverPanel(BlockGame f) {
		frame = f;
		try {
			gameDead = AudioSystem.getClip();
			URL url = getClass().getResource("Pow-2.wav");
			AudioInputStream audioInputStream;
			audioInputStream = AudioSystem.getAudioInputStream(url);
			gameDead.open(audioInputStream);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		gameDead.start();

		updateScore();

		setLayout(null);

		labels = new ArrayList<>();

		Font BodoniFont = new Font("Bodoni MT Black", Font.BOLD, 100);
		Font BritanicFont = new Font("Britannic Bold", Font.BOLD, 30);
		Font BritanicFont2 = new Font("Britannic Bold", Font.BOLD, 60);
		Font SegoePrintFont = new Font("Segoe Print", Font.PLAIN, 20);

		labels.add(new TitleLabel2("GAME OVER", BodoniFont, 0, 200, 800, 100, Color.white));
		for (int i = 0; i < 10; i++)
			labels.add(new TitleLabel2("GAME OVER", BodoniFont, 0, 200 + i, 800, 100, new Color(255, 190, 0, 240)));
		labels.add(new TitleLabel2("PRESS SPACE TO RETURN START", BritanicFont, 0, 600, 800, 30, Color.red));
		labels.add(new TitleLabel2("HIGH SCORE : " + frame.highScore, BritanicFont2, 0, 400, 800, 60, Color.gray));
		labels.add(new TitleLabel2("YOUR SCORE : " + frame.yourScore, BritanicFont2, 0, 450, 800, 60, Color.gray));
		for (var l : labels) {
			if (l.getText().equals("PRESS SPACE TO RETURN START")) {
				l.t.start();
			}
			add(l);
		}

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					gameDead.stop();
					gameDead.setFramePosition(0);
					frame.addStartPanel();
					return;
				}
			}
		});
	}

	public void updateScore() {
		if (frame.highScore < frame.yourScore)
			frame.highScore = frame.yourScore;
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

		labels.get(labels.size() - 2).setText("HIGH SCORE : " + frame.highScore);
		labels.get(labels.size() - 1).setText("YOUR SCORE : " + frame.yourScore);
	}

}
