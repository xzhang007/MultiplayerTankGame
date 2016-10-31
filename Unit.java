import java.util.Observer;

abstract public class Unit extends Thing implements Observer {
	private boolean damaged;
	
	protected Unit(int x, int y, GameWorld game) {
		super(x, y, game);
		game.getObservable().addObserver(this);
	}
	
	protected boolean isDamaged() {
		return damaged;
	}

	protected void setDamaged(boolean damaged) {
		this.damaged = damaged;
	}
}

