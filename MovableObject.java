
abstract public class MovableObject extends Unit {
	private int SPEED;
	private int oldX, oldY;
	private int directionIndex;
	
	protected MovableObject(int x, int y, GameWorld game) {
		super(x, y, game);
		oldX = super.getX();
		oldY = super.getY();
	}
	
	abstract protected void move();
	
	protected int getSpeed() {
		return SPEED;
	}
	
	protected void setSpeed(int speed) {
		this.SPEED = speed;
	}

	protected int getOldX() {
		return oldX;
	}

	protected void setOldX(int oldX) {
		this.oldX = oldX;
	}

	protected int getOldY() {
		return oldY;
	}

	protected void setOldY(int oldY) {
		this.oldY = oldY;
	}

	protected int getDirectionIndex() {
		return directionIndex;
	}

	protected void setDirectionIndex(int directionIndex) {
		this.directionIndex = directionIndex;
	}
}

