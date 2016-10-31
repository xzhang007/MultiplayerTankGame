import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Observable;

import javax.imageio.ImageIO;

public class Bullet extends MovableObject {
	//private static final int XSPEED = 10, YSPEED = 10;
	//private static final int SPEED = 10;
	private static final int SPEED = 10;
	public static int WIDTH, HEIGHT;
	private boolean good;
	private static BufferedImage [] bulletImg = new BufferedImage[60];
	private static SoundPlayer sp;
	private static BufferedImage [] bigBulletImg = new BufferedImage[60];
	private boolean big = false; // big bullet
	
	static {
		try {
			BufferedImage tempImg = ImageIO.read(GameWorld.class.getResource("Resources/Shell_light_strip60.png"));
			for (int i = 0, xCoord = 0, yCoord = 0, width = tempImg.getWidth() / 60, height = tempImg.getHeight(); i < 60; i++) {
				bulletImg[i] = tempImg.getSubimage(xCoord, yCoord, width, height);
				xCoord += width;
			}
			WIDTH = bulletImg[0].getWidth();    // 24
			HEIGHT = bulletImg[0].getHeight();   // 24
			
			tempImg = ImageIO.read(GameWorld.class.getResource("Resources/Shell_basic_strip60.png"));
			for (int i = 0, xCoord = 0, yCoord = 0, width = tempImg.getWidth() / 60, height = tempImg.getHeight(); i < 60; i++) {
				bigBulletImg[i] = tempImg.getSubimage(xCoord, yCoord, width, height);
				xCoord += width;
			}
			
			sp = new SoundPlayer(2, "Resources/Explosion_small.wav");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Bullet(int x, int y, int directionIndex, boolean good, GameWorld game) {
		super(x, y, game);
		super.setSpeed(SPEED);
		this.good = good;
		super.setDirectionIndex(directionIndex);
		sp = new SoundPlayer(2, "Resources/Explosion_small.wav");   // can't be removed
	}
	
	public Bullet(int x, int y, int directionIndex, boolean good, GameWorld game, boolean big) {
		this(x, y, directionIndex, good, game);
		this.big = big;
	}
	
	@Override
	public void draw(Graphics g) {
		/*if (damaged) {
			tc.bullets.remove(this);
			return;
		}*/
		g.drawImage(big ? bigBulletImg[super.getDirectionIndex()] : bulletImg[super.getDirectionIndex()], super.getX(), super.getY(), null);
		
		move();
	}
	
	@Override
	protected void move() {    // update()
		int speed = super.getSpeed();
		int x = super.getX();
		x += Math.cos(Math.toRadians(super.getDirectionIndex() * 6)) * speed;
		super.setX(x);
		
		int y = super.getY();
		y -= Math.sin(Math.toRadians(super.getDirectionIndex() * 6)) * speed;
		super.setY(y);
	    
		x = super.getX();
		y = super.getY();
	    if (x < 0 || y < 0 || x > super.getGame().getGamewidth() || y > super.getGame().getGameheight()) {
	    	super.setDamaged(true);
	    }
	}
	
	@Override
	public Rectangle getRec() {
		return new Rectangle(super.getX(), super.getY(), WIDTH, HEIGHT);
	}
	
	@Override
	public void update(Observable obj, Object arg) {
		GameEvents ge = (GameEvents) arg;
		if (2 == ge.getType() && this == ge.getCaller()) {
			sp.play();
			GameWorld game = super.getGame();
			boolean b = big ? true : false;  // big bullet generate big explosion or normal bullet generate small explosion
			game.explosions.add(new Explosion(super.getX(), super.getY(), game, b));
			super.setDamaged(true);
			super.getGame().bullets.remove(this);
		}
	}

	public static int getWIDTH() {
		return WIDTH;
	}

	public static int getHEIGHT() {
		return HEIGHT;
	}

	public boolean isGood() {
		return good;
	}
	
	
	
	/*public boolean collideWithTank(MyTank tank) {
		if (getRec().intersects(tank.getRec()) && !tank.isDamaged() && good != tank.isGood()) {
			//if (tank.isGood()) {
				tank.setLife(tank.getLife() - 20);
				if (tank.getLife() <= 0) {
					//tank.setDamaged(true);
					tc.explosions.add(new Explosion(tank.getX(), tank.getY(), tc, true));
					tank.setDamaged(true);
				}
			//} else {
				//tank.setDamaged(true);
			//}
			sp.play();
			damaged = true;
			tc.explosions.add(new Explosion(x, y, tc, false));
			return true;
		}
		return false;
	}
	
	
	public boolean collideWithWall(Wall wall) {
		if (!damaged && getRec().intersects(wall.getRec())) {
			//System.out.println("222 yes");
			if (wall.getClass().getName().equals("DestructableWall")) {
				//System.out.println("333 yes");
				((DestructableWall) wall).collision();
			}
			sp.play();
			damaged = true;
			tc.explosions.add(new Explosion(x, y, tc, false));
			return true;
		}
		return false;
	}
	
	public void collideWithWall(List<Wall> wall) {
		int size = wall.size();
		for (int i = 0; i < size; i++) {
			collideWithWall(wall.get(i));
		}
	}*/
	
}
