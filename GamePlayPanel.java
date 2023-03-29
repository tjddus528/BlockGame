import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JPanel;

//조상 object
abstract class MyObject {
	float x;
	float y;
	float width;
	float height;
	Color color;

	MyObject(float _x, float _y, float _w, float _h, Color c) {
		x = _x;
		y = _y;
		width = _w;
		height = _h;
		color = c;
	}

	abstract void draw(Graphics g);

	void update(float dt) {
	}

	abstract void collisionResolution(MyObject o);
}

class MyWall extends MyObject {

	MyWall(float _x, float _y, float _w, float _h, Color c) {
		super(_x, _y, _w, _h, c);
	}

	@Override
	void draw(Graphics g) {
		g.setColor(color);
		g.fillRect((int) x, (int) y, (int) width, (int) height);
	}

	@Override
	void collisionResolution(MyObject o) {

	}
}

class MyBlock extends MyObject {

	boolean crash;
	boolean multi;

	MyBlock(float _x, float _y, float _w, float _h, Color c) {
		super(_x, _y, _w, _h, c);
		crash = false;
		multi = false;
	}

	@Override
	void draw(Graphics g) {
		if (!crash) {
			int e = (int) (height * 0.03);
			g.setColor(Color.white);
			g.fillRect((int) x, (int) y, (int) width, (int) height);
			Color c1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
			Color c2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
			GradientPaint gp = new GradientPaint((int) x, (int) y, c1, (int) (x + width), (int) (y + height), c2);
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(gp);
			g2.fillRect((int) x, (int) y, (int) width, (int) height);
			g.setColor(color);
			g.fillRect((int) x + e, (int) y + e, (int) width - 2 * e, (int) height - 2 * e);
		}
	}

	@Override
	void collisionResolution(MyObject o) {
		if (o instanceof MyBall) {
			color = Color.red;
			crash = true;
			x = 0;
			y = 0;
			width = 0;
			height = 0;
		}
	}
}

class MyLacket extends MyObject {

	float vx;
	float vy;

	MyLacket(float _x, float _y, float _w, float _h, Color c) {
		super(_x, _y, _w, _h, c);

		int speed = 1;
		vx = 20;
		vy = 0;

	}

	@Override
	void draw(Graphics g) {
		int e = (int) (height * 0.06);
		g.setColor(Color.white);
		g.fillRect((int) x, (int) y, (int) width, (int) height);

		Color c1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
		Color c2 = new Color(5, 5, 5, 240);
		GradientPaint gp = new GradientPaint((int) x, (int) y, c1, (int) x, (int) (y + height), c2);
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(gp);
		g2.fillRect((int) x, (int) y, (int) width, (int) height);
		g.setColor(color);
		g.fillRect((int) x + e, (int) y + e, (int) width - 2 * e, (int) height - 2 * e);
	}

	void update(float dt) {
		x += vx;
	}

	boolean isCollide(MyObject o) {
		if (o instanceof MyWall) {
			MyWall w = (MyWall) o;
			if (w.width == 10) {
				if (w.x == 0 && x <= w.width) {
					return true;
				}
				if (w.x > 0 && x + width > w.x) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	void collisionResolution(MyObject o) {
		if (o instanceof MyWall) {
			MyWall w = (MyWall) o;
			if (w.width == 10) {

				if (w.x == 0 && x < w.width)
					x = w.width;
				if (w.x > 0 && x + width > w.x)
					x = w.x - width;
			}
		}
	}

}

class MyBall extends MyObject {
	float radius;
	float vx;
	float vy;
	float angle;
	float speed;

	// 0.016초 전 위치
	float prev_x;
	float prev_y;

	boolean dead = false;

	MyBall(float _x, float _y, float r, Color c) {
		super(_x, _y, 2 * r, 2 * r, c);
		radius = r;

		// 시작할 때에는 이전 위치는 현재 위치와 동일 -> update에서 변화됨
		prev_x = _x;
		prev_y = _y;

		// 속도
		speed = 300.0f; // 초당 300pixel 움직임

		// 방향은 각도 (360 = 2ㅠ)
		// 시작은 위로
		angle = 1.8f * 3.141592f;
		vx = speed * (float) (Math.cos(angle));
		vy = speed * (float) (Math.sin(angle));

	}

	@Override
	void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
	}

	void setVelocity(float _angle) {
		angle = _angle;
		// 속도
		vx = speed * (float) (Math.cos(angle));
		vy = -speed * (float) (Math.sin(angle));
	}

	@Override
	void update(float dt) {
		// x, y 변화하기 전에 위치 저장
		prev_x = x;
		prev_y = y;

		x += vx * dt;
		y += vy * dt;

	}

	boolean isCollide(MyObject o) {
		if (x > (o.x - radius) && x < (o.x + o.width + radius)) {
			if (y > (o.y - radius) && y < (o.y + o.height + radius)) {
				return true;
			}
		}

		return false;
	}

	@Override
	void collisionResolution(MyObject o) {
		if (o instanceof MyWall || o instanceof MyBlock) {
			MyObject w = o;
			// 벽의 왼쪽에 있었다면
			if (prev_x < w.x - radius) {
				// 뚫고 들어가지 않게 벽에 붙여놓고
				x = w.x - radius;
				vx = -vx;
			}
			// 벽의 오른쪽에 있었다면
			if (prev_x > w.x + w.width + radius) {
				// 뚫고 들어가지 않게 벽에 붙여놓고
				x = w.x + w.width + radius;
				vx = -vx;
			}
			// 벽의 위쪽에 있었다면
			if (prev_y < w.y - radius) {
				// 뚫고 들어가지 않게 벽에 붙여놓고
				y = w.y - radius;
				vy = -vy;
			}
			// 벽의 아래쪽에 있었다면
			if (prev_y > w.y + w.height + radius) {
				// 뚫고 들어가지 않게 벽에 붙여놓고
				y = w.y + w.height + radius;
				vy = -vy;
			}
		}
		if (o instanceof MyLacket) {
			MyLacket l = (MyLacket) o;
			// 위쪽
			if (prev_y < l.y - radius) {
				y = l.y - radius;
				angle = Math.abs((x - (l.x + l.width / 2)) / (l.width / 2)) * 3.141592f;
				vx = (float) (speed * Math.cos(angle));
				vy = -vy;
			}
			// 왼쪽
			if (prev_x < l.x - radius) {
				// 뚫고 들어가지 않게 벽에 붙여놓고
				x = l.x - radius;
				vx = -vx;
			}
			// 벽의 오른쪽에 있었다면
			if (prev_x > l.x + l.width + radius) {
				// 뚫고 들어가지 않게 벽에 붙여놓고
				x = l.x + l.width + radius;
				vx = -vx;
			}
		}
	}
}

class GamePlayPanel extends JPanel implements Runnable {
	LinkedList<MyObject> objs = new LinkedList<>();
	LinkedList<MyBlock> blocks;
	int numOfBall = -1;
	int aliveBlockCnt = -1;

	boolean multi = false;
	int stage;
	boolean clear = false;
	boolean gameOver = false;
	MyLacket lacket;
	boolean lacketMove = false;
	MyBall curBall;

	BlockGame frame;

	Clip breakBlock;
	Clip reflectLacket;
	Clip stageUp;

	GamePlayPanel(BlockGame f) {
		frame = f;
		stage = 1;
		setBackground(new Color(90, 90, 90));

		try {
			// stage up 효과음
			stageUp = AudioSystem.getClip();
			URL url = getClass().getResource("Ascending-1.wav"); // bytecode 위치 찾음
			AudioInputStream audioInputStream;
			audioInputStream = AudioSystem.getAudioInputStream(url);
			stageUp.open(audioInputStream);

			// block 효과음
			breakBlock = AudioSystem.getClip();
			url = getClass().getResource("Ting-Sound.wav");
			audioInputStream = AudioSystem.getAudioInputStream(url);
			breakBlock.open(audioInputStream);

			// lacket 효과음
			reflectLacket = AudioSystem.getClip();
			url = getClass().getResource("Ding-4.wav");
			audioInputStream = AudioSystem.getAudioInputStream(url);
			reflectLacket.open(audioInputStream);

		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// wall
		initWall();

		// block
		initBlock(1);

		// lacket
		initLacket();

		// ball
		initBall();

		// Thread
		Thread t = new Thread(this);
		t.start();
	}

	public void initWall() {
		// wall
		int wallWidth = 10;
		int wallHeight = 800;
		objs.add(new MyWall(0, 0, 790, 10, Color.darkGray));
		objs.add(new MyWall(0, 0, 10, 800, Color.darkGray));
		objs.add(new MyWall(780, 0, 10, 800, Color.darkGray));
	}

	public void initLacket() {
		lacket = new MyLacket(350, 700, 170, 40, new Color(50, 50, 250));
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					lacket.x -= lacket.vx;
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					lacket.x += lacket.vx;
				}
			}
		});

	}

	public void initBlock(int _stage) {
		blocks = new LinkedList<>();

		int wallWidth = 10;
		int wallHeight = 800;
		int e = 9 - _stage;
		if (e < 0)
			e = 0;
		int blockN = 3 * _stage;
		float p = 0.5f;
		int multiBlockN = 1;
		for (int i = 0; i < stage; i++) {
			multiBlockN *= 2;
		}
		int gap = 6;
		int totalGap = 0;
		for (int i = 0; i < blockN; i++) {
			totalGap += gap;
		}
		int blockWidth = (780 - gap * (blockN + 1) - e) / blockN;
		int blockHeight = (int) (blockWidth * 0.33);

		Color specialColor = new Color(255, 200, 0);
		Color blockColor = new Color(180, 175, 180);
		for (int i = 0; i < blockN; i++) {
			for (int j = 0; j < blockN; j++) {
				int x = wallWidth + gap * (i + 1) + blockWidth * i;
				int y = wallWidth + gap * (j + 1) + blockHeight * j;
				MyBlock b = new MyBlock(x, y, blockWidth, blockHeight, blockColor);
				if (Math.random() <= p && multiBlockN >= 1) {
					multiBlockN--;
					b.color = specialColor;
					b.multi = true;
				}
				blocks.add(b);
			}
		}
	}

	public void initBall() {
		curBall = new MyBall(400, 400, 5, Color.white);
		objs.add(curBall);
		numOfBall = 1;
	}

	public void removeDeadBall() {
		for (int i = 0; i < objs.size(); i++) {
			if (objs.get(i) instanceof MyBall) {
				MyBall ball = (MyBall) objs.get(i);
				if (ball.dead)
					objs.remove(ball);
			}
		}
	}

	public int countBall() {
		int number = 0;
		for (var o : objs) {
			if (o instanceof MyBall) {
				number++;
			}
		}
		return number;
	}

	public void multiBall(MyBall ball) {

		objs.remove(ball);

		for (int i = 0; i < 3; i++) {
			float f = 0.1f;
			float a = ball.angle - f;
			MyBall b1 = new MyBall(ball.prev_x, ball.prev_y, ball.radius, Color.white);
			b1.setVelocity((float) ((a + f * i) * 3.141592));
			objs.add(b1);
			numOfBall++;
		}

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Color startColor = new Color(0, 0, 0);
		Color endColor = new Color(60, 60, 60);
		GradientPaint gp = new GradientPaint(400, 0, startColor, 400, 800, endColor);
		g2.setPaint(gp);
		g2.fillRect(0, 0, 800, 800);

		for (var b : blocks) {
			b.draw(g);
		}
		for (var o : objs) {
			o.draw(g);
		}
		lacket.draw(g);

		repaint();
	}

	@Override
	public void run() {
		try {
			while (true) {
				// key input
				if (multi) {
					multiBall(curBall);
					multi = false;
				}
				removeDeadBall();
				if (countBall() == 0) {
					gameOver = true;
				}
				if (gameOver) {
					frame.addEndPanel();
					gameOver = false;
					return;
				}

				// update
				for (var o : objs)
					o.update(0.016f);

				// collision
				for (var o : objs) {
					if (o instanceof MyBall) {
						MyBall ball = (MyBall) o;
						if (ball.y + ball.radius > 800) {
							ball.dead = true;
						}
						for (var w : objs) {
							if (!(w instanceof MyWall))
								continue;
							MyWall wall = (MyWall) w;
							if (ball.isCollide(wall))
								ball.collisionResolution(wall);
						}
						for (var block : blocks) {
							if (ball.isCollide(block)) {
								breakBlock.setFramePosition(0);
								breakBlock.start();
								frame.yourScore += 10;
								ball.collisionResolution(block);
								block.collisionResolution(ball);
								curBall = ball;
								if (block.multi) {
									multi = true;
								}
								aliveBlockCnt = 0;
								for (var b : blocks) {
									if (!b.crash) {
										aliveBlockCnt++;
									}
								}
							}

						}
						if (ball.isCollide(lacket)) {
							reflectLacket.setFramePosition(0);
							reflectLacket.start();
							ball.collisionResolution(lacket);
						}
					} else {
						if (!(o instanceof MyWall))
							continue;
						MyWall wall = (MyWall) o;
						if (lacket.isCollide(wall))
							lacket.collisionResolution(wall);
					}
				}

				if (aliveBlockCnt == 0) {
					// prev item clear
					stageUp.setFramePosition(0);
					stageUp.start();
					blocks.clear();
					objs.clear();

					// new item init
					stage++;
					initWall();
					initBlock(stage);
					initLacket();
					initBall();
					multi = false;

					// aliveCnt init
					aliveBlockCnt = -1;

					Thread.sleep(2000);
				}

				// repaint
				repaint();

				Thread.sleep(16);
			}
		} catch (

		InterruptedException e) {
			e.printStackTrace();
		}
	}

}