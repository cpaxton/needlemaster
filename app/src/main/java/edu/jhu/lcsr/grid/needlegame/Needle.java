package edu.jhu.lcsr.grid.needlegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

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

    Paint needlePaint;
    Paint threadPaint;

	double movementMultiplier;
	double rotationMultiplier;
	
	ArrayList<PointF> threadPoints;

    boolean offscreen;

	Path polygon;
	Path thread;
	
	Surface current;
	
	private static final int needleColor = Color.argb(255, 134, 200, 188);
	private static final int threadColor = Color.argb(255, 167, 188, 214);
	
	private static final double MAX_DELTA_XY = 0.025;
	
	private static final double LENGTH_CONST = 0.08;
	
	public Needle(double x, double y, double w) {
		this.x = x;
		this.y = y;
		this.w = w;
		
		threadPoints = new ArrayList<PointF>();
		//threadPoints.add(new Point2D.Double(x,y));
		
		isMoving = false;
		
		screenWidth = 0;
		screenHeight = 0;
		scale = 0;
		polygon = new Path();
	
		movementMultiplier = 1.0;
		rotationMultiplier = 1.0;

        threadPaint = new Paint();
        threadPaint.setStyle(Paint.Style.STROKE);
        threadPaint.setColor(threadColor);
        threadPaint.setStrokeWidth(2.0f);
        needlePaint = new Paint();
        needlePaint.setColor(needleColor);
        needlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        offscreen = false;

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

    public boolean isOffscreen() {
        return offscreen;
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
			polygon = new Path();
			polygon.moveTo((float)realX, (float)realY);
			
			double topW = w - (Math.PI/2);
			double bottomW = w + (Math.PI/2);
	
			length = LENGTH_CONST * scale;
			
			double topX = realX - ((0.01 * scale) * Math.cos(topW)) + (length * Math.cos(w));
			double topY = realY - ((0.01 * scale) * Math.sin(topW)) + (length * Math.sin(w));
			
			double bottomX = realX - ((0.01 * scale) * Math.cos(bottomW)) + (length * Math.cos(w));
			double bottomY = realY - ((0.01 * scale) * Math.sin(bottomW)) + (length * Math.sin(w));
			
			polygon.lineTo((float)topX, (float)topY);
			polygon.lineTo((float)bottomX, (float)bottomY);
			polygon.close();
			
			thread = new Path();
			if (threadPoints.size() > 0) {
				thread.moveTo(threadPoints.get(0).x * screenWidth,
						(1.0f - threadPoints.get(0).y) * screenHeight);
				
				for (int i = 1; i < threadPoints.size(); i++) {
					thread.lineTo(threadPoints.get(i).x * screenWidth,
							(1.0f - threadPoints.get(i).y) * screenHeight);
				}
			}

            if (x > 1.0 && topX > screenWidth && bottomX > screenHeight) {
                offscreen = true;
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
	
	public void draw(Canvas c) {
		synchronized(this) {
			c.drawPath(polygon, needlePaint);
			c.drawPath(thread, threadPaint);
		}
	}
	
	public void updateMove(float x, float y) {
		moveX = (double)x / screenWidth;
		moveY = 1.0 - ((double)y / screenHeight); 
		//System.out.println("move = " + moveX + "," + moveY);
	}
	
	public void move() {
		
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
				
				threadPoints.add(new PointF((float)this.x, (float)this.y));
				
				double movement = dist * Math.cos(dw) * movementMultiplier; // x projection of the motion?
				
				this.x = this.x + (movement * Math.cos(w));
				this.y = this.y - (movement * Math.sin(w));
				
				double rotation = Math.sin(dw) / 15.0 * rotationMultiplier;
                if (Math.abs(rotation) > 0.01)
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

	public void startMove(float x2, float y2) {
		moveX = lastMoveX = (double)x2 / screenWidth;
		moveY = lastMoveY = 1.0 - ((double)y2 / screenHeight);
		isMoving = true;
	}

	public void endMove() {
		isMoving = false;
	}

	public synchronized float getRealX() {
		return (float) x * screenWidth;
	}

	public synchronized float getRealY() {
		return (float)(1.0 - y) * screenHeight;
	}
}
