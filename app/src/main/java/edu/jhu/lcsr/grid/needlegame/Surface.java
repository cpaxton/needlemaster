package edu.jhu.lcsr.grid.needlegame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * This class describes a line delimiting a surface where the needle behaves differently.
 * @author Chris
 *
 */
public class Surface {

	int mycolor; // what color does this surface show up as?

	Path scaledLine;
	double [] x;
	double [] y;
	
	double rotationMultiplier;
	public double getRotationMultiplier() {
		return rotationMultiplier;
	}

	public void setRotationMultiplier(double rotationMultiplier) {
		this.rotationMultiplier = rotationMultiplier;
	}

	public double getMovementMultiplier() {
		return movementMultiplier;
	}

	public void setMovementMultiplier(double movementMultiplier) {
		this.movementMultiplier = movementMultiplier;
	}

	double movementMultiplier;
	
	boolean isVirtual;
	
	int width;
	int height;

    Paint myPaint;
    Region myRegion;

    /**
     * Basic surface constructor.
     * @param color what the surface looks like
     * @param x locations of surface boundaries (x)
     * @param y locations of surface boundaries (y)
     * @param isVirtual is this a real surface, that influences the needle?
     */
	public Surface(int color, double[] x, double[] y, boolean isVirtual) {
		mycolor = color;
		this.x = x;
		this.y = y;
		this.isVirtual = isVirtual;

        myPaint = new Paint();
        myPaint.setColor(mycolor);

        if(isVirtual) {
            myPaint.setStyle(Paint.Style.STROKE);
        } else {
            myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
		
		width = 1;
		height = 1;

        rescaleLine(800, 600);
	}
	
	/**
	 * recompute the scaled line for a window of a certain height and width
	 * @param width of the window
	 * @param height of the window
	 */
	public void rescaleLine(int width, int height) {
		if (width != this.width || height != this.height) {
			this.width = width;
			this.height = height;
			scaledLine = new Path(); //GeneralPath.WIND_NON_ZERO, x.length);
			scaledLine.moveTo((float)(x[0] * width), (float)((1.0 - y[0]) * height));
			for (int i = 1; i < x.length; i ++) {
				scaledLine.lineTo((float)(x[i] * width), (float)((1.0 - y[i]) * height));
			}
			scaledLine.close();
		}

        RectF tmp = new RectF();
        scaledLine.computeBounds(tmp, true);
        myRegion = new Region();
        myRegion.setPath(scaledLine, new Region((int)tmp.left, (int)tmp.top, (int)tmp.left, (int)tmp.right));
	}
	
	/**
	 * Draw the filled-in surface in its chosen color.
	 * @param c canvas to draw on
	 */
	public void draw(Canvas c) {
        c.drawPath(scaledLine, myPaint);
	}

	/**
	 * Does the surface contain anything
	 * @param x real X location
	 * @param y real Y location
	 * @return true if contained and non-virtual
	 */
	public boolean contains(double x, double y) {
		return scaledLine != null && (!isVirtual) && myRegion.contains((int)x, (int)y);
	}
	
	/**
     * Does this surface actually do anything or does it just exist to create useful predicates?
     * @return true if it is a virtual surface (aka, does not affect anything); false otherwise.
	 */
	public boolean isVirtual() { return isVirtual; }
}
