package grid.needlegame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

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
	
	private static final Color closed = Color.DARK_GRAY;
	private static final Color onDeck = Color.LIGHT_GRAY;
	private static final Color next = Color.CYAN;
	private static final Color passed = Color.GREEN;
	
	public Gate(double x, double y, double w) {
		this.x = x;
		this.y = y;
		this.w = w;
		status = GATE_CLOSED;
		
		screenWidth = 0;
		screenHeight = 0;
		rescale(800, 600);
	}

	/**
	 * Update this gate's status.
	 * @param status
	 * @return true if the status was updated; false otherwise.
	 */
	public boolean setStatus(int status) {
		if (status < 0 || status > 3) {
			System.err.println("Status not recognized: " + status);
			return false;
		} else {
			this.status = status;
			return true;
		}
	}
	
	public void draw(Graphics2D g) {
		
		if(status == GATE_CLOSED) {
			g.setColor(closed);
		} else if (status == GATE_ON_DECK) {
			g.setColor(onDeck);
		} else if (status == GATE_NEXT) {
			g.setColor(next);
		} else {
			g.setColor(passed);	
		}
		g.fill(polygon);
		
	}
	
	void redraw() {

		synchronized(this) {
			//double width1 = screenWidth * 0.05 * Math.cos(w + (0.1 * Math.PI));
			//double height1 = screenHeight * 0.05 * Math.sin(w + (0.1 * Math.PI));
			//double width2 = screenWidth * 0.05 * Math.cos(w - (0.1 * Math.PI));
			//double height2 = screenHeight * 0.05 * Math.sin(w - (0.1 * Math.PI));
			double width1 = screenWidth * 0.05 * Math.cos(w) + screenHeight * 0.03 * Math.sin(w);
			double height1 = screenHeight * 0.05 * Math.sin(w) - screenHeight * 0.03 * Math.cos(w);
			double width2 = -1 * screenWidth * 0.05 * Math.cos(w) + screenHeight * 0.03 * Math.sin(w);
			double height2 = -1 * screenHeight * 0.05 * Math.sin(w) - screenHeight * 0.03 * Math.cos(w);
			
			double realX = x * screenWidth;
			double realY = (1.0 - y) * screenHeight;
			
			System.out.println("Drawing gate:");
			
			System.out.println(width1);
			System.out.println(height1);
			System.out.println(width2);
			System.out.println(height2);
			
			polygon = new GeneralPath();
			polygon.moveTo(realX + width1, realY + height1);
			System.out.println((realX + width1) + ", " + (realY + height1));
			polygon.lineTo(realX + width2, realY + height2);
			System.out.println((realX + width2) + ", " + (realY + height2));
			polygon.lineTo(realX - width1, realY - height1);
			System.out.println((realX - width1) + ", " + (realY - height2));
			polygon.lineTo(realX - width2, realY - height2);
			System.out.println((realX - width2) + ", " + (realY - height2));
			polygon.closePath();
		}
	}

	public boolean rescale(int width, int height) {
		if (screenWidth != width || screenHeight != height) {
			// scale the needle; regenerate the path creating its image
			
			screenWidth = width;
			screenHeight = height;
			
			redraw();
				
			return true;
		}
		else return false;
	}

	public void update(Needle needle) {
		if (polygon.contains(new Point2D.Double(needle.getRealX(), needle.getRealY())) && status == GATE_NEXT) {
			status = GATE_PASSED;
		}
	}

	public int getStatus() {
		return status;
	}
}
