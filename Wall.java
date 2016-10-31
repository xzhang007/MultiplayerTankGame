import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Wall extends Thing {
	private static int width, height;
	private static Image wallImage;
	
	static {
		try {
			wallImage = ImageIO.read(GameWorld.class.getResource("Resources/Blue_wall1.png"));
			
			width = wallImage.getWidth(null);    // 32
			height = wallImage.getHeight(null);   //32
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Wall(int x, int y, GameWorld game) {
		super(x, y, game);
	}
	
	@Override
	public void draw(Graphics g) {
		g.drawImage(wallImage, super.getX(), super.getY(), null);
	}
	
	@Override
	public Rectangle getRec() {
		return new Rectangle(super.getX(), super.getY(), width, height);
	}
}
