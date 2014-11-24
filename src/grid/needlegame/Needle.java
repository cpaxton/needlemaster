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
	double length;
	
	ArrayList<Point2D> threadPoints;
	
	GeneralPath polygon;
	GeneralPath thread;
	
	private static final Color needleColor = Color.BLUE;
	private static final Color threadColor = Color.BLUE;
	
	private static final double MAX_DELTA_W = 0.050;
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
		polygon = new GeneralPath();
		
		rescale(800, 600);
	}
	
	void applySurfaceEffects(Surface s) {
		
		
	}
	
	void redraw() {

		synchronized(this) {
			
			double realX = x * screenWidth;
			double realY = (1.0 - y) * screenHeight;
			polygon = new GeneralPath();
			polygon.moveTo(realX, realY);
			
			double topW = w + (Math.PI/2);
			double bottomW = w - (Math.PI/2);
	
			length = LENGTH_CONST * screenWidth;
			
			double topX = realX + ((0.01 * screenHeight) * Math.cos(topW)) - (length * Math.cos(w));
			double topY = realY + ((0.01 * screenHeight) * Math.sin(topW)) - (length * Math.sin(w));
			
			double bottomX = realX + ((0.01 * screenHeight) * Math.cos(bottomW)) - (length * Math.cos(w));
			double bottomY = realY + ((0.01 * screenHeight) * Math.sin(bottomW)) - (length * Math.sin(w));
			
			polygon.lineTo(topX, topY);
			polygon.lineTo(bottomX, bottomY);
			polygon.closePath();
			
			thread = new GeneralPath();
			if (threadPoints.size() > 0) {
				thread.moveTo(threadPoints.get(0).getX() * screenWidth,
						(1.0 - threadPoints.get(0).getX()) * screenHeight);
				
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
	
	public void move() {
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
				
				threadPoints.add(new Point2D.Double(this.x - (LENGTH_CONST*Math.cos(w)),
						this.y + (LENGTH_CONST * Math.sin(w))));
				//System.out.println(this.x - (LENGTH_CONST*Math.cos(w)) + ", " + (this.y + (LENGTH_CONST * Math.sin(w))));
				
				double movement = dist * Math.cos(dw); // x projection of the motion?
				this.x = this.x + (movement * Math.cos(w));
				this.y = this.y - (movement * Math.sin(w));
				
				double rotation = Math.sin(dw) / 12.0;
				w += rotation;
				
				//System.out.println("Location:" + (this.x * screenWidth) + ", " + (this.y * screenHeight) + "; " + this.w);	
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
}
