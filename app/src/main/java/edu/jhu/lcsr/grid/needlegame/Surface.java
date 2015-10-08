package edu.jhu.lcsr.grid.needlegame;

import android.graphics.Canvas;
import android.graphics.Color;
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

    final static int tissue = Color.argb(255, 232, 146, 124);
    final static int deepTissue = Color.argb(255, 207, 69, 32);

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

    double damage;

	double movementMultiplier;
    boolean isDeepTissue;

	int width;
	int height;

    Paint myPaint;
    Region myRegion;

    /**
     * Basic surface constructor.
     * @param isDeepTissue what the surface looks like
     * @param x locations of surface boundaries (x)
     * @param y locations of surface boundaries (y)
     */
	public Surface(boolean isDeepTissue, double[] x, double[] y) {
        if (isDeepTissue) {
            mycolor = deepTissue;
        } else {
            mycolor = tissue;
        }
        this.isDeepTissue = isDeepTissue;
		this.x = x;
		this.y = y;

        myPaint = new Paint();
        myPaint.setColor(mycolor);
        damage = 0;
		
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
        myRegion.setPath(scaledLine, new Region((int)tmp.left, (int)tmp.top, (int)tmp.right, (int)tmp.bottom));
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
		return scaledLine != null && myRegion.contains((int)x, (int)y);
	}


    public String toString() {
        String str = "";
        if (isDeepTissue) {
            str += "IsDeepTissue: true\n";
        } else {
            str += "IsDeepTissue: false\n";
        }
        str += "SurfaceX: ";
        for (int i = 0; i < x.length; i++) {
            str += x[i] * width;
            if (i < x.length - 1) {
                str += ",";
            }
        }
        str += "\nSurfaceY: ";
        for (int i = 0; i < y.length; i++) {
            str += y[i] * height;
            if (i < y.length - 1) {
                str += ",";
            }
        }
        str += "\n";
        return str;
    }

    private void updateDamage() {
        System.out.println(damage);

        if(damage > 100) damage = 100.;

        int r = (int)((232) + ((207.0 - 232.0) * damage / 100.0));
        int g = (int)((146) + ((69.0 - 146.0) * damage / 100.0));
        int b = (int)((142) + ((32.0 - 142.0) * damage / 100.0));

        myPaint.setColor(Color.argb(255, r, g, b));
    }

    public double applyMovement(double movement) {
        //movement /= 2.0;
        if(!isDeepTissue) {
            if (false && Math.abs(movement) > 25.0) {
                //damage += Math.abs(movement) - 25.0;

                //updateDamage();

                if (movement > 0) {
                    return 25.0;
                } else {
                    return -25.0;
                }
            } else {
                return movement;
            }
        } else {
            return 0;
        }
    }

    public double applyRotation(double rotation) {
        rotation /= 2.0;
        System.out.println(rotation);
        if(!isDeepTissue) {
            if (Math.abs(rotation) > 0.01) {
                damage += (Math.abs(rotation) - 0.01) * 100;

                updateDamage();

                if (rotation > 0) {
                    return 0.02;
                } else {
                    return -0.02;
                }
            } else {
                return rotation;
            }
        } else {
            return 0;
        }
    }

    public boolean destroyed() {
        return damage >= 100.0;
    }

    public double getDamage() {
        if(this.isDeepTissue) return 0;
        else return damage;
    }
}
