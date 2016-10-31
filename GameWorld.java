import java.awt.*;
import java.awt.event.*;
import java.awt.Image.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;


public class GameWorld extends JPanel implements Runnable {
	private Thread thread;
	// GameWorld is a singleton class!
    private static final GameWorld game = new GameWorld();
    
	static final int GAMEWIDTH = 1280;
	static final int GAMEHEIGHT = 1280;
	private BufferedImage bimg = null;
	private Graphics2D g2;
	private MapLoader mapLoader = MapLoader.getInstance();    // singleton
	private GameEvents gameEvents = new GameEvents();
	private CollisionDetector collisionDetector = new CollisionDetector(this);
	
	Tank myTank = new Tank(230, 220, true, this);
	Tank enemy = new Tank(1060, 1020, false, this);
	List<Bullet> bullets = new ArrayList<Bullet>();
	List<Explosion> explosions = new ArrayList<Explosion>();
	List<Thing> walls = new ArrayList<Thing>();
	List<PowerUp> powerUps = new ArrayList<PowerUp>();
	private Image background;
	private BufferedImage leftView, rightView, miniMap;
	private SoundPlayer sp;
	
	
	public void init() {
		//addKeyListener(new KeyControl());
		try {
			background = ImageIO.read(GameWorld.class.getResource("Resources/Background.png"));
			sp = new SoundPlayer(1, "Resources/Music.wav");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 4; i ++) {
			powerUps.add(new PowerUp(0, 0, this));
		}
	}
	
	private void drawBackGroundWithTileImage(Graphics g) {
		int tileWidth = background.getWidth(this);
		int tileHeight = background.getHeight(this);
		
		int numberX = (int) (GAMEWIDTH / tileWidth);
		int numberY = (int) (GAMEHEIGHT / tileHeight);
		
		for (int i = 0; i <= numberY; i++) {	// use 0 instead of -1
			for (int j = 0; j <= numberX; j++) {
				g.drawImage(background, j * tileWidth, i * tileHeight, tileWidth, tileHeight,  this);
			}
		}
	}
	
	private void addToWallList(List<Thing> walls, List<Location> wallMap) {
		int size = wallMap.size();
		for (int i = 0; i < size; i++) {
			Location location = wallMap.get(i);
			int x = location.getX() * 32;
			int y = location.getY() * 32;
			Thing singleWall = (location.getThing().equals("Wall")) ? new Wall(x, y, this) : new DestructableWall(x, y, this);
			walls.add(singleWall);
		}
 	}
	
	private void getWallList() {
		mapLoader.read();
		List<Location> wallMap = mapLoader.getWallMap();
		addToWallList(walls, wallMap);
	}
	
	private void drawWall(Graphics g) {
		if (walls.isEmpty()) {
			getWallList();
		}
		
		for(int i = 0; i < walls.size(); i++) {
			walls.get(i).draw(g);
		}
	}
	
	private void drawDemo(Graphics g) {
		drawBackGroundWithTileImage(g);
		drawWall(g);
		
		for (int i = 0; i < explosions.size(); i++) {
			explosions.get(i).draw(g);
		}
		
		for (int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			bullet.draw(g);
			collisionDetector.collideWith(bullet, myTank);
			collisionDetector.collideWith(bullet, enemy);
			collisionDetector.collideWith(bullet, walls);
		}
		
		for (int i = 0; i < powerUps.size(); i++) {
			PowerUp powerUp = powerUps.get(i);
			collisionDetector.collideWith(powerUp, walls);  // powerUps can't be shown in the wall
			powerUps.get(i).draw(g);
		}
		
		collisionDetector.collideWith(myTank, enemy);
		collisionDetector.collideWith(myTank, walls);
		collisionDetector.collideWith(myTank, powerUps);
		myTank.draw(g);
		
		enemy.draw(g);
	}
	
	public void paint(Graphics g) {
		if (bimg == null) {
			//Dimension windowSize = getSize();
			//bimg = (BufferedImage) createImage(windowSize.width, windowSize.height);
			bimg = (BufferedImage) createImage(GAMEWIDTH, GAMEHEIGHT);
			g2 = bimg.createGraphics();
		}
		drawDemo(g2);
		try {
			int leftX = (myTank.getX() - 200 <= 0) ? 0 : (myTank.getX() - 200);
			if (leftX + 400 > GAMEWIDTH) {
				leftX = GAMEWIDTH - 401;
			}
			int leftY = (myTank.getY() - 240 <= 0) ? 0 : (myTank.getY() - 240);
			if (leftY + 480 > GAMEHEIGHT) {
				leftY = GAMEHEIGHT - 481;
			}
			leftView = bimg.getSubimage(leftX,leftY, 400, 480);
			
			collisionDetector.collideWith(enemy, myTank);   // important to put here. in case of rightView thrashing
			collisionDetector.collideWith(enemy, walls);
			collisionDetector.collideWith(enemy, powerUps);
			int rightX = (enemy.getX() - 200 <= 0) ? 0 : (enemy.getX() - 200);
			if (rightX + 400 > GAMEWIDTH) {
				rightX = GAMEWIDTH - 401;
			}
			int rightY = (enemy.getY() - 240 <= 0) ? 0 : (enemy.getY() - 240);
			if (rightY + 480 > GAMEHEIGHT) {
				rightY = GAMEHEIGHT - 481;
			}
			//rightView = bimg.getSubimage(879, 799, 400, 480);
			rightView = bimg.getSubimage(rightX, rightY, 400, 480);
			miniMap = bimg;
		} catch (RasterFormatException e) {
			e.printStackTrace();
		}
		g.drawImage(leftView, 0, 0, null);
		g.drawImage(rightView, 420, 0, null);
		Color color = g.getColor();
		g.setColor(Color.BLACK);
		g.fillRect(400, 0, 20, 480);
		
		Font font = g.getFont();
		g.setFont(new Font("Dialog", Font.BOLD, 50));
		g.setColor(Color.RED);
		g.drawString("" + myTank.getScore(), 350, 50);
		g.setColor(Color.BLUE);
		g.drawString("" + enemy.getScore(), 440, 50);
		g.setFont(font);
		
		g.setColor(color);
		g.drawImage(miniMap, 350, 335, 120, 120, null);
	}
	
	public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
	
	public void run() {
        Thread me = Thread.currentThread();
        while (thread == me) {
            repaint();  
          try {
                thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
	
	public static GameWorld getInstance(){
    	return game;
    }

	public GameEvents getObservable() {
		return gameEvents;
	}

	public static int getGamewidth() {
		return GAMEWIDTH;
	}

	public static int getGameheight() {
		return GAMEHEIGHT;
	}
}



