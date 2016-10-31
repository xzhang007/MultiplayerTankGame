import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;

public class Tank extends Player {
	//private static final int WIDTH = 30, HEIGHT = 30;
	private static final int INITIALLIFE = 100;
	private static final int INITIALSCORE = 0;
	private static int WIDTH, HEIGHT;
	private boolean good;
	private BloodBar bloodBar = new BloodBar();
	//private static BufferedImage [] tankImg = new BufferedImage[60];
	private BufferedImage [] tankImg = new BufferedImage[60];
	private boolean dirU = false, dirD = false, dirL = false, dirR = false;  // only for S, W, A, D control
	private int clock = 10;
	private boolean shield = false;
	private static Image [] shieldImage = new Image[2];
	private static final int INITIALSPEED = 5;
	private boolean superF = false;
	
	static {
		try {
			shieldImage[0] = ImageIO.read(GameWorld.class.getResource("Resources/Shield1.png"));
			shieldImage[1] = ImageIO.read(GameWorld.class.getResource("Resources/Shield2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Tank(int x, int y, boolean good, GameWorld game) {
		super(x, y, game);
		super.setSpeed(INITIALSPEED);
		this.good = good;
		super.setOldX(x);
		super.setOldY(y);
		super.setLife(INITIALLIFE);
		super.setScore(INITIALSCORE);
		
		try {
			BufferedImage tempImg = good ? ImageIO.read(GameWorld.class.getResource("Resources/Tank_blue_basic_strip60.png")) : ImageIO.read(GameWorld.class.getResource("Resources/Tank_red_basic_strip60.png"));
			for (int i = 0, xCoord = 0, yCoord = 0, width = tempImg.getWidth() / 60, height = tempImg.getHeight(); i < 60; i++) {
				tankImg[i] = tempImg.getSubimage(xCoord, yCoord, width, height);
				xCoord += width;
			}
			WIDTH = tankImg[0].getWidth();
			HEIGHT = tankImg[0].getHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override 
	public void draw(Graphics g) {
		if (super.isDamaged())  {
			clock--;
			if (clock == 0) {
				clock = 10;
				super.setDamaged(false);
				super.setLife(INITIALLIFE);
				int x = good ? 230 : 1060;
				super.setX(x);
				int y = good ? 220 : 1020;
				super.setY(y);
				super.setOldX(x);
				super.setOldY(y);
				super.setDirectionIndex(0);
			}
			return;
		}
		
		move();
		bloodBar.draw(g);
	
		int directionIndex = super.getDirectionIndex();
		if (directionIndex < 0) {
			super.setDirectionIndex(directionIndex + 60);
		}
		
		g.drawImage(tankImg[super.getDirectionIndex()], super.getX(), super.getY(), null);
		
		if (shield) {
			g.drawImage(good ? shieldImage[0] : shieldImage[1], super.getX(), super.getY(), null);
		}
		
		//move();
	}
	
	
	
	@Override
	public void update(Observable obj, Object arg) {
		GameEvents ge = (GameEvents) arg;
		if (1 == ge.getType()) {    // pressed
			KeyEvent e = (KeyEvent) ge.getEvent();
			int directionIndex = super.getDirectionIndex();
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (good) {
					super.setDirectionIndex((++directionIndex) % 60);
				}
			break;
			case KeyEvent.VK_RIGHT:
				if (good) {
					super.setDirectionIndex((--directionIndex) % 60);
				}
			break;
			case KeyEvent.VK_UP:
				if (good) {
					int speed = super.getSpeed();
					int x = super.getX();
					x += Math.cos(Math.toRadians(directionIndex * 6)) * speed;
					super.setX(x);
					
					int y = super.getY();
					y -= Math.sin(Math.toRadians(directionIndex * 6)) * speed;
					super.setY(y);
				}
			break;
			case KeyEvent.VK_DOWN:
				if (good) {
					int speed = super.getSpeed();
					int x = super.getX();
					x -= Math.cos(Math.toRadians(directionIndex * 6)) * speed;
					super.setX(x);
					
					int y = super.getY();
					y += Math.sin(Math.toRadians(directionIndex * 6)) * speed;
					super.setY(y);
				}
			break;
			case KeyEvent.VK_A:
				if (!good) {
					dirL = true;
					//super.setDirectionIndex((++directionIndex) % 60);
				}
			break;
			case KeyEvent.VK_D:
				if (!good) {
					dirR = true;
					//super.setDirectionIndex((--directionIndex) % 60);
				}
			break;
			case KeyEvent.VK_W:
				if (!good) {
					dirU = true;
				}
			break;
			case KeyEvent.VK_S:
				if (!good) {
					dirD = true;
				}
			break;
			}
		} else if (3 == ge.getType()) {   // released
			KeyEvent e = (KeyEvent) ge.getEvent();
			int directionIndex = super.getDirectionIndex();
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				if (!good) {
					dirL = false;
				}
			break;
			case KeyEvent.VK_D:
				if (!good) {
					dirR = false;
				}
			break;
			case KeyEvent.VK_W:
				if (!good) {
					dirU = false;
				}
			break;
			case KeyEvent.VK_S:
				if (!good) {
					dirD = false;
				}
			break;
			case KeyEvent.VK_ENTER:
				if (!good) {
					fire();
				}
			break;

			case KeyEvent.VK_C:
				if (!good) {
					superFire();
				}
				
			break;
			case KeyEvent.VK_SPACE:
				if (good) {
					fire();
				}
			break;

			case KeyEvent.VK_CONTROL:
				if (good) {
					superFire();
				}
			break;
			}
		} else if (2 == ge.getType() 
				&& (this == ge.getCaller() && (ge.getTarget() instanceof Wall || ge.getTarget() instanceof DestructableWall)
				|| (ge.getCaller() instanceof Tank && this == ge.getTarget())
				)) { // tank collide with wall or tank
			stay();
		} else if (2 == ge.getType() && this == ge.getTarget() && ge.getCaller() instanceof Bullet && !shield) { // shoot by the bullet
			int life = super.getLife();
			super.setLife(life - 20);
			if (super.getLife() <= 0) {
				super.setDamaged(true);
				GameWorld game = super.getGame();
				game.explosions.add(new Explosion(super.getX(), super.getY(), game, true));
				Tank tank = good ? game.enemy : game.myTank;
				int score = tank.getScore();
				tank.setScore(score + 1);
			}
		} else if (2 == ge.getType() && this == ge.getCaller() && ge.getTarget() instanceof PowerUp) {
			int index = ge.getNumber()[0];
			if (0 == index) {
				superF = true;
			} else if (1 == index) {
				shield = true;
			}
		}
	}
	
	private void stay() {
		super.setX(super.getOldX());
		super.setY(super.getOldY());
	}
	
	@Override
	protected void move() { 
		int x = super.getX();
		int y = super.getY();
		super.setOldX(x);
		super.setOldY(y);
		
		int directionIndex = super.getDirectionIndex();
		int speed = super.getSpeed();
		if (!good) {	// only for A, D, S, W control
			if (dirU) {
				x += Math.cos(Math.toRadians(directionIndex * 6)) * speed;
				y -= Math.sin(Math.toRadians(directionIndex * 6)) * speed;
			}
			if (dirD) {
				x -= Math.cos(Math.toRadians(directionIndex * 6)) * speed;
				y += Math.sin(Math.toRadians(directionIndex * 6)) * speed;
			}
			super.setX(x);
			super.setY(y);
			
			if (dirL) {
				super.setDirectionIndex((++directionIndex) % 60);
			}
			if (dirR) {
				super.setDirectionIndex((--directionIndex) % 60);
			}
		}
	    
		x = super.getX();
		y = super.getY();
		GameWorld game = super.getGame();
	    if (x < 0) {
	    	super.setX(0);
	    }
	    if (y < 30) {
	    	super.setY(30);
	    }
	    if (x + WIDTH > game.getGamewidth()) {
	    	super.setX(game.getGamewidth() - WIDTH);
	    }
	    if (y + HEIGHT > game.getGameheight()) {
	    	super.setY(game.getGameheight() - HEIGHT);
	    }
	}
	
	private void fire() {
		if (super.isDamaged()) return;
		int bx = super.getX() + WIDTH /2 - Bullet.getWIDTH() / 2;
		int by = super.getY() + HEIGHT / 2 - Bullet.getHEIGHT() / 2;
		GameWorld game = super.getGame();
		game.bullets.add(new Bullet(bx, by, super.getDirectionIndex(), good, game));
	}
	
	@Override
	public Rectangle getRec() {
		return new Rectangle(super.getX(), super.getY(), WIDTH, HEIGHT);
	}
	
	@Override
	public void setDamaged(boolean damaged) {
		super.setDamaged(damaged);
		GameWorld game = super.getGame();
		if (good) {
			int score = game.enemy.getScore();
			game.enemy.setScore(score + 1);
		} else {
			int score = game.myTank.getScore();
			game.myTank.setScore(score + 1);
		}
	}
	
	public boolean isGood() {
		return good;
	}
	
	public void superFire() {
		if (superF) {
			if (super.isDamaged()) return;
			int bx = super.getX() + WIDTH /2 - Bullet.getWIDTH() / 2;
			int by = super.getY() + HEIGHT / 2 - Bullet.getHEIGHT() / 2;
			GameWorld game = super.getGame();
			game.bullets.add(new Bullet(bx, by, super.getDirectionIndex(), good, game, true));
		}
	}

	private class BloodBar {
		private void draw(Graphics g) {
			Color color = g.getColor();
			g.setColor(Color.GREEN);
			g.drawRect(Tank.super.getX(), Tank.super.getY() - 4, WIDTH, 3);
			int w = WIDTH * Tank.super.getLife() / 100;
			g.fillRect(Tank.super.getX(), Tank.super.getY() - 4, w, 3);
			g.setColor(color);
		}
	}
	
}

