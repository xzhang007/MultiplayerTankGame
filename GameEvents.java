import java.awt.event.KeyEvent;
import java.util.Observable;

public class GameEvents extends Observable {
	private int type;
	private static final int KEY = 1;
	private static final int COLLISION = 2;
	private static final int RELEASED = 3;
	private Object event;
	private Object caller, target;
	private int [] number = new int[2];
	
	public void setValue(KeyEvent e) {
		type = KEY; // let's assume this means key input. 
		event = e;
		setChanged();
		// trigger notification
		notifyObservers(this);
	}
	
	public void setKeys(KeyEvent e) {
		type = RELEASED; // let's assume this means key input. 
		event = e;
		setChanged();
		// trigger notification
		notifyObservers(this);
	}
	
	public void setValue(Thing caller, Thing target) {
		type = COLLISION;
		//event = msg;
		this.caller = caller;
		this.target = target;
		setChanged();
		// trigger notification
		notifyObservers(this);
	}
	
	public void setValue(Thing caller, Thing target, int number) { 
		this.number[0] = number;
		setValue(caller, target);
	}
	
	public void setValue(Thing caller, Thing target, int number1, int number2) {
		number[0] = number1;
		number[1] = number2;
		setValue(caller, target);
	}
	
	public int getType() {
		return type;
	}
	
	public Object getEvent() {
		return event;
	}
	
	public Object getCaller() {
		return caller;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public int [] getNumber() {
		return number;
	}
}