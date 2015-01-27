package edu.jhu.lcsr.grid.needlegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Class to store the state of the needle and to draw the needle when necessary
 * @author Chris
 *
 */
public class Needle {

    File file;
    FileOutputStream fo;
    OutputStreamWriter fow;

	double x; // left/right position
	double y; // height
	double w; // rotation

    double fx;
    double fy;
	
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

    long startTime;

	private static final int needleColor = Color.argb(255, 134, 200, 188);
	private static final int threadColor = Color.argb(255, 167, 188, 214);
	
	private static final double MAX_DELTA_XY = 75.0;
	
	private static final double LENGTH_CONST = 0.08;
	
	public Needle(double x, double y, double w) {
		this.fx = x;
		this.fy = y;
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
        threadPaint.setStrokeWidth(4.0f);
        needlePaint = new Paint();
        needlePaint.setColor(needleColor);
        needlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        offscreen = false;

        startTime = 0;

		rescale(800, 600);
	}

    /**
     * Create a file pointer for recording demonstrations
     * @param dir the directory the file will end up in
     * @param filename the name of the file
     * @return true if file was opened; false if already opened
     */
    public boolean startFileOutput(File dir, String filename) {
        if (file == null) {
            file = new File(dir, filename);
            try {
                fo = new FileOutputStream(file);
                fow = new OutputStreamWriter(fo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean endFileOutput() {
        if (file != null) {
            try {
                fow.close();
                fo.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

	/**
	 * Simple setter for the surface, right now.
	 * @param s surface that this needle is now inside of
	 */
	public void applySurface(Surface s) {
		//if (s.contains(x * screenWidth, (1.0 - y) * screenHeight)) {
		current = s;
		//}
	}

    /**
     * is the needle on the screen or not?
     * @return true or false; is the needle on the screen or did it go off the right side?
     */
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
			
			double realX = x;// * screenWidth;
			double realY = y;//(1.0 - y) * screenHeight;
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
				//thread.moveTo(threadPoints.get(0).x, threadPoints.get(0).y);

				for (int i = 1; i < threadPoints.size(); i++) {
					thread.lineTo(threadPoints.get(i).x * screenWidth,
							(1.0f - threadPoints.get(i).y) * screenHeight);
                    //thread.moveTo(threadPoints.get(i).x, threadPoints.get(i).y);
				}
			}

            if (x > screenWidth && topX > screenWidth && bottomX > screenHeight) {
                offscreen = true;
            } else {
                offscreen = false;
            }
			
		}
	}
	
	public boolean rescale(int width, int height) {
		if (screenWidth != width || screenHeight != height) {
			// scale the needle; regenerate the path creating its image

			screenWidth = width;
			screenHeight = height;
			scale = Math.sqrt(width*width + height*height);

            x = screenWidth * fx;
            y = (1 - fy) * screenHeight;

            redraw();

			return true;
		}
		else return false;
	}
	
	public void draw(Canvas c) {
		synchronized(this) {
			c.drawPath(polygon, needlePaint);
            if(threadPoints.size() > 0) {
                c.drawPath(thread, threadPaint);
            }
		}
	}
	
	public void updateMove(float x, float y) {
		moveX = (double)x; // / screenWidth;
		moveY = (double)(screenHeight - y); //1.0 - ((double)y / screenHeight);
		//System.out.println("move = " + moveX + "," + moveY);
	}

    /**
     * Call to move the needle
     * @param movement -- distance to move, x/y
     * @param rotation -- change in angle (applied after x/y changes are computed)
     */
    public void move(double movement, double rotation) {

        //System.out.println("m=" + movement + ", r=" + rotation);

        try {
            if (fow != null) {
                fow.write((System.currentTimeMillis() - startTime) + "," + getRealX() + "," + getRealY() + "," + w + "," + movement + "," + rotation + "\n");
                fow.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(current != null) {
            movement = current.applyMovement(movement);
            rotation = current.applyRotation(rotation);
        } else {
            if(Math.abs(movement) > MAX_DELTA_XY) {
                if (movement > 0) movement = MAX_DELTA_XY;
                else movement = -1*MAX_DELTA_XY;
            }
        }

        this.w += rotation;

        this.x = this.x + (movement * Math.cos(w));
        this.y = this.y + (movement * Math.sin(w));
    }

    /**
     * The human version of the move.
     * Processes old commands and uses them to order the needle around.
     */
	public void humanMove() {
		
		updateSurface();

		if (isMoving) {
			
			// compute speed and change in rotation from this information
			// we want to take the angle FROM the current direction to the new point
			double dx = moveX - lastMoveX;
			double dy = moveY - lastMoveY;


            //System.out.println(moveX + "-->" + lastMoveX);
            //System.out.println(moveY + "-->" + lastMoveY);

			lastMoveX = moveX;
			lastMoveY = moveY;

			
			double dist = Math.sqrt((dx*dx)+(dy*dy));
			//if (dist > MAX_DELTA_XY) {
			//	dist = MAX_DELTA_XY;
			//}

			if (Math.abs(dy) > 0 || Math.abs(dx) > 0) {
				// creating a triangle: current (x,y), new (x,y), and projected (x,y) onto line from old angle
				// we want to find the x and y of this triangle to compute rotation and movement
				// we also may want to apply a threshold so that this works better
				double dw = w + Math.atan2(dy, dx);
				
				threadPoints.add(new PointF((float)this.x/screenWidth, (float)1.0f - ((float)y / screenHeight)));
				
				double movement =  dist * Math.cos(dw); // x projection of the motion?
				double rotation = Math.sin(dw) / 15.0;

                move(movement, rotation);

			}
			
			if (w < 0) {
				w += 2*Math.PI;
			} else if (w > 2*Math.PI) {
				w -= 2*Math.PI;
			}

			redraw();
        }
	}

	public void startMove(float x2, float y2) {
		moveX = lastMoveX = (double)(x2); // / screenWidth;
		moveY = lastMoveY = (double)(screenHeight - y2); //1.0 - ((double)y2 / screenHeight);
		isMoving = true;
	}

	public void endMove() {
		isMoving = false;
	}

	public synchronized float getRealX() {
		return (float) x; // * screenWidth;
	}

	public synchronized float getRealY() {
		return (float) y; //(1.0 - y) * screenHeight;
	}

    public double getPathLength() {
        double len = 0.0;
        for (int i = 1; i < threadPoints.size(); i++) {
            PointF pt0 = threadPoints.get(i-1);
            PointF pt1 = threadPoints.get(i);

            float dx = pt1.x - pt0.x;
            float dy = pt1.y - pt0.y;

            len += Math.sqrt((dx*dx) + (dy*dy));
        }

        return len;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
