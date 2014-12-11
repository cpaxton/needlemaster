package grid.needlegame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
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
	double scale;
	
	int status;
	
	GeneralPath polygon; // total outline of the gate
	GeneralPath top; // top "cap": do not hit it!
	GeneralPath bottom; // bottom "cap": do not hit it!
	
	boolean entered; // have you entered this gate?
	
	public static final int GATE_FAILED = -1;
	public static final int GATE_CLOSED = 0;
	public static final int GATE_ON_DECK = 1;
	public static final int GATE_NEXT = 2;
	public static final int GATE_PASSED = 3;
	
	private static final Color failed = new Color(0.50f, 0.40f, 0.40f);
	private static final Color passed = new Color(0.40f, 0.50f, 0.40f);
	
	private static final Color closed = new Color(0.40f, 0.40f, 0.40f);
	private static final Color onDeck = new Color(0.50f, 0.50f, 0.50f);
	private static final Color next = new Color(0.70f, 0.70f, 0.70f);
	
	private static final Color highlight = new Color(0.40f, 0.90f, 0.40f);
	private static final Color highlightOnDeck = new Color(0.30f, 0.50f, 0.30f);
	private static final Color warning = new Color(1.00f, 0.20f, 0.05f);
	
	public Gate(double x, double y, double w) {
		this.x = x;
		this.y = y;
		this.w = w;
		status = GATE_CLOSED;
		
		screenWidth = 0;
		screenHeight = 0;
		scale = 0;
		rescale(800, 600);
		
		entered = false;
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
	
	/**
	 * Draw the gate on the screen.
	 * @param g
	 */
	public void draw(Graphics2D g) {
		
		if(status == GATE_CLOSED) {
			g.setColor(closed);
		} else if (status == GATE_ON_DECK) {
			g.setColor(onDeck);
		} else if (status == GATE_NEXT) {
			g.setColor(next);
		} else if (status == GATE_PASSED) {
			g.setColor(passed);	
		} else if (status == GATE_FAILED) {
			g.setColor(failed);
		}
		g.fill(polygon);
		
		if(status != GATE_PASSED && status != GATE_FAILED) {
			g.setColor(warning);
			g.fill(top);
			g.fill(bottom);
		}
		
		Stroke s = g.getStroke();
		g.setStroke(new BasicStroke(3.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
		if(status == GATE_NEXT && !entered) {
			g.setColor(highlight);
			g.draw(polygon);
		} else if (status == GATE_ON_DECK) {
			g.setColor(highlightOnDeck);
			g.draw(polygon);
		}
		g.setStroke(s);
	}
	
	void redraw() {

		synchronized(this) {
			double width1 = scale * 0.025 * Math.cos(w) + scale * 0.015 * Math.sin(w);
			double height1 = scale * 0.025 * Math.sin(w) - scale * 0.015 * Math.cos(w);
			double width2 = -1 * scale * 0.025 * Math.cos(w) + scale * 0.015 * Math.sin(w);
			double height2 = -1 * scale * 0.025 * Math.sin(w) - scale * 0.015 * Math.cos(w);
			
			double width1m = (scale * 0.025 - 6.0) * Math.cos(w) + scale * 0.015 * Math.sin(w);
			double height1m = (scale * 0.025 - 6.0) * Math.sin(w) - scale * 0.015 * Math.cos(w);
			double width2m = (-1 * scale * 0.025 + 6.0) * Math.cos(w) + scale * 0.015 * Math.sin(w);
			double height2m = (-1 * scale * 0.025 + 6.0) * Math.sin(w) - scale * 0.015 * Math.cos(w);
			
			double realX = x * screenWidth;
			double realY = (1.0 - y) * screenHeight;
			
			polygon = new GeneralPath();
			polygon.moveTo(realX + width1, realY + height1);
			polygon.lineTo(realX + width2, realY + height2);
			polygon.lineTo(realX - width1, realY - height1);
			polygon.lineTo(realX - width2, realY - height2); 
			polygon.closePath();
			
			top = new GeneralPath();
			top.moveTo(realX + width1, realY + height1);
			top.lineTo(realX + width1m, realY + height1m);
			top.lineTo(realX - width2m, realY - height2m);
			top.lineTo(realX - width2, realY - height2); 
			top.closePath();
			
			bottom = new GeneralPath();
			bottom.moveTo(realX + width2, realY + height2);
			bottom.lineTo(realX + width2m, realY + height2m);
			bottom.lineTo(realX - width1m, realY - height1m);
			bottom.lineTo(realX - width1, realY - height1);
			bottom.closePath();
		}
	}

	public boolean rescale(int width, int height) {
		if (screenWidth != width || screenHeight != height) {
			// scale the needle; regenerate the path creating its image
			
			screenWidth = width;
			screenHeight = height;
			scale = Math.sqrt(width*width + height*height);
			
			redraw();
				
			return true;
		}
		else return false;
	}

	public void update(Needle needle) {
		Point2D.Double pt = new Point2D.Double(needle.getRealX(), needle.getRealY());
		if (top.contains(pt) || bottom.contains(pt)) {
			status = GATE_FAILED;
		} else if (polygon.contains(pt) && status == GATE_NEXT) {
			entered = true;
		} else if (entered == true && status != GATE_FAILED) {
			status = GATE_PASSED;
		}
	}

	public int getStatus() {
		return status;
	}
}
