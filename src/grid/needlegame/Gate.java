package grid.needlegame;

import java.awt.Color;
import java.awt.geom.GeneralPath;

/**
 * This class represents a gate that must be passed through.
 * @author Chris
 *
 */
public class Gate {
	double x;
	double y;
	double w;
	

	int screenWidth;
	int screenHeight;
	
	int status;
	
	GeneralPath polygon;
	
	public static final int GATE_CLOSED = 0;
	public static final int GATE_ON_DECK = 1;
	public static final int GATE_NEXT = 2;
	public static final int GATE_PASSED = 3;
	
	private static final Color closed = Color.RED;
	private static final Color onDeck = Color.ORANGE;
	private static final Color next = Color.YELLOW;
	private static final Color passed = Color.GREEN;
	
	Gate(double x, double y, double w) {
		this.x = x;
		this.y = y;
		this.w = w;
		status = GATE_CLOSED;
	}

	/**
	 * Update this gate's status.
	 * @param status
	 * @return true if the status was updated; false otherwise.
	 */
	boolean setStatus(int status) {
		if (status < 0 || status > 3) {
			System.err.println("Status not recognized: " + status);
			return false;
		} else {
			this.status = status;
			return true;
		}
	}
}
