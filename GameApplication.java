import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/* Runnable game application */
public class GameApplication {
	private GameWorld game;
	
	public static void main(String argv[]) {
	    final GameWorld game = GameWorld.getInstance();
	    JFrame f = new JFrame("TANK WAR");
	    f.addKeyListener(new KeyControl(game.getObservable()));
	    f.getContentPane().add("Center", game);
	    f.pack();
	    f.setSize(new Dimension(800, 480));
	    game.init();
	    f.setVisible(true);
	    f.setResizable(false);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.start();
	}

}
