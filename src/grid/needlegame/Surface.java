package grid.needlegame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 * This class describes a line delimiting a surface where the needle behaves differently.
 * @author Chris
 *
 */
public class Surface {
	Color mycolor; // what color does this surface show up as?
	boolean angleRestricted; // is this restricted by angle?
	double angle; // angle on which the needle is allowed to enter
	GeneralPath scaledLine; 
	double [] x;
	double [] y;
	
	int width;
	int height;
	
	public Surface(Color color, boolean isAngleRestricted, double entryAngle, double[] x, double[] y) {
		mycolor = color;
		angleRestricted = isAngleRestricted;
		angle = entryAngle;
		this.x = x;
		this.y = y;
		
		width = 1;
		height = 1;
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
			scaledLine = new GeneralPath(GeneralPath.WIND_NON_ZERO, x.length);
			scaledLine.moveTo(x[0] * width, (1.0 - y[0]) * height);
			for (int i = 1; i < x.length; i ++) {
				scaledLine.lineTo(x[i] * width, (1.0 - y[i]) * height);
			}
			scaledLine.closePath();
		}
	}
	
	/**
	 * Draw the filled-in surface in its chosen color.
	 * @param g2
	 */
	public void draw(Graphics2D g2) {
		g2.setPaint(mycolor);
		g2.fill(scaledLine);
	}
}
