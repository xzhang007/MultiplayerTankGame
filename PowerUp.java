import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.swing.*;

public class PowerUp extends Unit {
	private int width, height;
	protected static BufferedImage [] weaponImage = new BufferedImage[2];
	private static Random generator = new Random();
	private int clock = 400;
	private int index;
	
	static {
		try {
			BufferedImage tempImg1 = ImageIO.read(GameWorld.class.getResource("Resources/Pickup_strip4.png"));
			for (int i = 0, xCoord = 0, yCoord = 0, width = tempImg1.getWidth() /4, height = tempImg1.getHeight(); i < 4; i++) {
				if (i == 1) {
					weaponImage[0] = tempImg1.getSubimage(xCoord, yCoord, width, height);
				} else if (i == 2) {
					weaponImage[1] = tempImg1.getSubimage(xCoord, yCoord, width, height);
				}
				xCoord += width;
				//width = weaponImage.getWidth();
				//height = weaponImage.getHeight();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PowerUp(int x, int y, GameWorld game) {
		super(x, y, game);
		int x0 = generator.nextInt(1280);
		int y0 = generator.nextInt(1280);
		super.setX(x0);
		super.setY(y0);
		width = weaponImage[0].getWidth();
		height = weaponImage[0].getHeight();
		index = generator.nextInt(2);
	}
	
	@Override
	public void draw(Graphics g) {
		if (super.isDamaged()) {
			return;
		}
		
		g.drawImage(weaponImage[index], super.getX(), super.getY(), null);
		clock--;
		
		if (clock == 0) {
			resetPosition();
		}
	}
	
	@Override
	public Rectangle getRec() {
		return new Rectangle(super.getX(), super.getY(), width, height);
	}

	private void resetPosition() {
		int x = generator.nextInt(1280);
		int y = generator.nextInt(1280);
		super.setX(x);
		super.setY(y);
	}
	
	@Override
	public void update(Observable obj, Object arg) {
		GameEvents ge = (GameEvents) arg;
		if (2 == ge.getType() && this == ge.getTarget() && ge.getCaller() instanceof Tank) {
			GameWorld game = super.getGame();
			super.setDamaged(true);
			game.powerUps.remove(this);
		} else if (2 == ge.getType() && this == ge.getCaller()) {
			resetPosition();   // poweUp can't be shown in the Wall
		}
	}

	public int getIndex() {
		return index;
	}
}

