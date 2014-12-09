package grid.needlegame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Class to store the state of the needle and to draw the needle when necessary
 * @author Chris
 *
 */
public class Needle {

	double x; // left/right position
	double y; // height
	double w; // rotation
	
	double lastMoveX; // where was the last input X?
	double lastMoveY; // where was the last input Y?
	double moveX;
	double moveY;
	
	boolean isMoving;
	
	int screenWidth;
	int screenHeight;
	double scale;
	double length;

	double movementMultiplier;
	double rotationMultiplier;
	
	ArrayList<Point2D> threadPoints;
	
	GeneralPath polygon;
	GeneralPath thread;
	
	Surface current;
	
	private static final Color needleColor = new Color(0.50f, 0.50f, 0.75f);
	private static final Color threadColor = new Color(0.50f, 0.50f, 0.50f);
	
	private static final double MAX_DELTA_XY = 0.025;
	
	private static final double LENGTH_CONST = 0.08;
	
	public Needle(double x, double y, double w) {
		this.x = x;
		this.y = y;
		this.w = w;
		
		threadPoints = new ArrayList<Point2D>();
		//threadPoints.add(new Point2D.Double(x,y));
		
		isMoving = false;
		
		screenWidth = 0;
		screenHeight = 0;
		scale = 0;
		polygon = new GeneralPath();
	
		movementMultiplier = 1.0;
		rotationMultiplier = 1.0;
		
		rescale(800, 600);
	}
	
	/**
	 * Simple setter for the surface, right now.
	 * @param s
	 */
	public void applySurface(Surface s) {
		//if (s.contains(x * screenWidth, (1.0 - y) * screenHeight)) {
		current = s;
		//}
	}
	
	/**
	 * updates the current surface, makes sure we are still in it...
	 */
	private void updateSurface() {
		if (current == null) {
			movementMultiplier = 1.0;
			rotationMultiplier = 1.0;
			return;
		} else if (! current.contains(getRealX(), getRealY())) {
			current = null;
			movementMultiplier = 1.0;
			rotationMultiplier = 1.0;
		} else {
			movementMultiplier = current.getMovementMultiplier();
			rotationMultiplier = current.getRotationMultiplier();
		}
	}
	
	/**
	 * Redraw the needle -- recomputes the lines so that it looks right
	 */
	private void redraw() {

		synchronized(this) {
			
			double realX = x * screenWidth;
			double realY = (1.0 - y) * screenHeight;
			polygon = new GeneralPath();
			polygon.moveTo(realX, realY);
			
			double topW = w + (Math.PI/2);
			double bottomW = w - (Math.PI/2);
	
			length = LENGTH_CONST * scale;
			
			double topX = realX + ((0.01 * scale) * Math.cos(topW)) - (length * Math.cos(w));
			double topY = realY + ((0.01 * scale) * Math.sin(topW)) - (length * Math.sin(w));
			
			double bottomX = realX + ((0.01 * scale) * Math.cos(bottomW)) - (length * Math.cos(w));
			double bottomY = realY + ((0.01 * scale) * Math.sin(bottomW)) - (length * Math.sin(w));
			
			polygon.lineTo(topX, topY);
			polygon.lineTo(bottomX, bottomY);
			polygon.closePath();
			
			thread = new GeneralPath();
			if (threadPoints.size() > 0) {
				thread.moveTo(threadPoints.get(0).getX() * screenWidth,
						(1.0 - threadPoints.get(0).getY()) * screenHeight);
				
				for (int i = 1; i < threadPoints.size(); i++) {
					thread.lineTo(threadPoints.get(i).getX() * screenWidth,
							(1.0 - threadPoints.get(i).getY()) * screenHeight);
				}
			}
			
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
	
	public void draw(Graphics2D g) {
		synchronized(this) {
			g.setColor(needleColor);
			g.fill(polygon);
			g.setColor(threadColor);
			g.draw(thread);
		}
	}
	
	public void updateMove(int x, int y) {
		moveX = (double)x / screenWidth;
		moveY = 1.0 - ((double)y / screenHeight); 
	}
	
	public synchronized void move() {
		
		updateSurface();
		
		if (isMoving) {
			
			// compute speed and change in rotation from this information
			// we want to take the angle FROM the current direction to the new point
			double dx = moveX - lastMoveX;
			double dy = moveY - lastMoveY;
			
			lastMoveX = moveX;
			lastMoveY = moveY;
			
			double dist = Math.sqrt((dx*dx)+(dy*dy));
			
			if (dist > MAX_DELTA_XY) {
				dist = MAX_DELTA_XY;
			}

			if (Math.abs(dy) > 0 || Math.abs(dx) > 0) {
				// creating a triangle: current (x,y), new (x,y), and projected (x,y) onto line from old angle
				// we want to find the x and y of this triangle to compute rotation and movement
				// we also may want to apply a threshold so that this works better
				double dw = w + Math.atan2(dy, dx);
				
				threadPoints.add(new Point2D.Double(this.x, this.y));
				
				System.out.println("movement mult = " + movementMultiplier);
				
				double movement = dist * Math.cos(dw) * movementMultiplier; // x projection of the motion?
				System.out.println("actual = " + movement);
				this.x = this.x + (movement * Math.cos(w));
				this.y = this.y - (movement * Math.sin(w));
				
				double rotation = Math.sin(dw) / 16.0 * rotationMultiplier;
				w += rotation;				
			}
			
			if (w < 0) {
				w += 2*Math.PI;
			} else if (w > 2*Math.PI) {
				w -= 2*Math.PI;
			}
			
			redraw();
		} else {
			return;
		}
	}

	public void startMove(int x2, int y2) {
		moveX = lastMoveX = (double)x2 / screenWidth;
		moveY = lastMoveY = 1.0 - ((double)y2 / screenHeight);
		isMoving = true;
	}

	public void endMove() {
		isMoving = false;
	}

	public synchronized double getRealX() {
		return x * screenWidth;
	}

	public synchronized double getRealY() {
		return (1.0 - y) * screenHeight;
	}
}
