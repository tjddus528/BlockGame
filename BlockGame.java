import java.awt.Color;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BlockGame extends JFrame {
	GameTitlePanel sp;
	GamePlayPanel gp;
	GameOverPanel ep;
	int mode = 0;
	boolean change = false;
	
	int highScore = 0;
	int yourScore = 0;

	BlockGame() {

		setTitle("Block Game");
		setSize(800, 800);
		
		sp = new GameTitlePanel(this);
		add(sp);
		sp.setFocusable(true);
		sp.requestFocus();

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);


	}
	
	public void addEndPanel() {
		ep = new GameOverPanel(this);
		getContentPane().removeAll();
		getContentPane().add(ep);
		ep.setFocusable(true);
		ep.requestFocus();
		revalidate();
		repaint();
	}

	public void addGamePanel() {
		gp = new GamePlayPanel(this);
		getContentPane().removeAll();
		getContentPane().add(gp);
		gp.setFocusable(true);
		gp.requestFocus();
		revalidate();
		repaint();
	}
	public void addStartPanel() {
		sp = new GameTitlePanel(this);
		getContentPane().removeAll();
		getContentPane().add(sp);
		sp.setFocusable(true);
		sp.requestFocus();
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		new BlockGame();
	}

}
