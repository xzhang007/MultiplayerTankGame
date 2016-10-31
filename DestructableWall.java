import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.swing.*;

import javax.imageio.ImageIO;

public class DestructableWall extends Unit {
	private int width, height;
	private static int oldWidth, oldHeight;
	private int clock = 0;
	private static Image wallImage;
	
	static {
		try {
			wallImage = ImageIO.read(GameWorld.class.getResource("Resources/Blue_wall2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DestructableWall(int x, int y, GameWorld game) {
		super(x, y, game);
		width = wallImage.getWidth(null);
		height = wallImage.getHeight(null);
		oldWidth = width;
		oldHeight = height;
	}
	
	@Override
	public void draw(Graphics g) {
		if (super.isDamaged()) {
			clock--;
			if (clock != 0) {
				return;
			}
			super.setDamaged(false);
			width = oldWidth;
			height = oldHeight;
		}
		g.drawImage(wallImage, super.getX(), super.getY(), null);
	}
	
	
	@Override
	public Rectangle getRec() {
		return new Rectangle(super.getX(), super.getY(), width, height);
	}
	
	@Override
	public void update(Observable obj, Object arg) {
		GameEvents ge = (GameEvents) arg;
		if (2 == ge.getType() && this == ge.getTarget()) {
			if (ge.getCaller() instanceof Bullet) {
				super.setDamaged(true);
				width = 0;
				height = 0;
				clock = 400;
			}
		}
	}
}
