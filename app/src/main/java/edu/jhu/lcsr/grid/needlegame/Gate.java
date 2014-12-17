package edu.jhu.lcsr.grid.needlegame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

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

	Path polygon; // total outline of the gate
	Path top; // top "cap": do not hit it!
	Path bottom; // bottom "cap": do not hit it!

	boolean entered; // have you entered this gate?

	public static final int GATE_FAILED = -1;
	public static final int GATE_CLOSED = 0;
	public static final int GATE_ON_DECK = 1;
	public static final int GATE_NEXT = 2;
	public static final int GATE_PASSED = 3;

	private static final int failed = Color.argb(255, 175, 100, 100);
	private static final int passed = Color.argb(255, 100, 175, 100);

	private static final int closed = Color.argb(255, 175, 175, 175);
	private static final int onDeck = Color.argb(255, 175, 175, 175);
	private static final int next = Color.argb(255, 175, 175, 175);

	private static final int highlight = Color.argb(255, 100, 230, 100);
	private static final int highlightOnDeck = Color.argb(255, 75, 125, 75);
	private static final int warning = Color.argb(255, 255, 50, 12);

    Paint warningPaint;

    Region topRegion;
    Region bottomRegion;
    Region gateRegion;

	public Gate(double x, double y, double w) {
		this.x = x;
		this.y = y;
		this.w = w;
		status = GATE_CLOSED;

        warningPaint = new Paint();
        warningPaint.setColor(warning);

		screenWidth = 0;
		screenHeight = 0;
		scale = 0;
		rescale(800, 600);

		entered = false;
	}

	/**
	 * Update this gate's status.
	 * @param status status of the gate
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
	 * @param c canvas on which to draw
	 */
	public void draw(Canvas c) {

        Paint gatePaint = new Paint();
        gatePaint.setStyle(Paint.Style.FILL);
		if(status == GATE_CLOSED) {
            gatePaint.setColor(closed);
		} else if (status == GATE_ON_DECK) {
            gatePaint.setColor(onDeck);
		} else if (status == GATE_NEXT) {
			gatePaint.setColor(next);
		} else if (status == GATE_PASSED) {
            gatePaint.setColor(passed);
		} else if (status == GATE_FAILED) {
            gatePaint.setColor(failed);
		}
		c.drawPath(polygon, gatePaint);

		if(status != GATE_PASSED && status != GATE_FAILED) {
			c.drawPath(top, warningPaint);
			c.drawPath(bottom, warningPaint);
		}

        gatePaint.setStyle(Paint.Style.STROKE);
		if(status == GATE_NEXT && !entered) {
            gatePaint.setColor(highlight);
		} else if (status == GATE_ON_DECK) {
            gatePaint.setColor(highlightOnDeck);
		}
        c.drawPath(polygon, gatePaint);
	}

	void redraw() {

		synchronized(this) {
			float width1 = (float)(scale * 0.025 * Math.cos(w) + scale * 0.015 * Math.sin(w));
			float height1 = (float)(scale * 0.025 * Math.sin(w) - scale * 0.015 * Math.cos(w));
			float width2 = (float)(-1 * scale * 0.025 * Math.cos(w) + scale * 0.015 * Math.sin(w));
			float height2 = (float)(-1 * scale * 0.025 * Math.sin(w) - scale * 0.015 * Math.cos(w));

            float width1m = (float)((scale * 0.025 - 6.0) * Math.cos(w) + scale * 0.015 * Math.sin(w));
            float height1m = (float)((scale * 0.025 - 6.0) * Math.sin(w) - scale * 0.015 * Math.cos(w));
            float width2m = (float)((-1 * scale * 0.025 + 6.0) * Math.cos(w) + scale * 0.015 * Math.sin(w));
            float height2m = (float)((-1 * scale * 0.025 + 6.0) * Math.sin(w) - scale * 0.015 * Math.cos(w));

            float realX = (float)(x * screenWidth);
            float realY = (float)((1.0 - y) * screenHeight);

			polygon = new Path();
            polygon.setFillType(Path.FillType.EVEN_ODD);
			polygon.moveTo(realX + width1, realY + height1);
			polygon.lineTo(realX + width2, realY + height2);
			polygon.lineTo(realX - width1, realY - height1);
			polygon.lineTo(realX - width2, realY - height2);
			polygon.close();

			top = new Path();
            top.setFillType(Path.FillType.EVEN_ODD);
			top.moveTo(realX + width1, realY + height1);
			top.lineTo(realX + width1m, realY + height1m);
			top.lineTo(realX - width2m, realY - height2m);
			top.lineTo(realX - width2, realY - height2);
			top.close();

			bottom = new Path();
            bottom.setFillType(Path.FillType.EVEN_ODD);
			bottom.moveTo(realX + width2, realY + height2);
			bottom.lineTo(realX + width2m, realY + height2m);
			bottom.lineTo(realX - width1m, realY - height1m);
			bottom.lineTo(realX - width1, realY - height1);
			bottom.close();

            RectF tmp = new RectF();
            polygon.computeBounds(tmp, true);
            gateRegion = new Region();
            gateRegion.setPath(polygon, new Region((int)tmp.left, (int)tmp.top, (int)tmp.left, (int)tmp.right));

            top.computeBounds(tmp, true);
            topRegion = new Region();
            topRegion.setPath(top, new Region((int)tmp.left, (int)tmp.top, (int)tmp.left, (int)tmp.right));

            bottom.computeBounds(tmp, true);
            bottomRegion = new Region();
            bottomRegion.setPath(bottom, new Region((int)tmp.left, (int)tmp.top, (int)tmp.left, (int)tmp.right));
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
		PointF pt = new PointF(needle.getRealX(), needle.getRealY());
		if (topRegion.contains((int)pt.x, (int)pt.y) || bottomRegion.contains((int)pt.x, (int)pt.y)) {
			status = GATE_FAILED;
		} else if (gateRegion.contains((int)pt.x, (int)pt.y) && status == GATE_NEXT) {
			entered = true;
		} else if (entered == true && status != GATE_FAILED) {
			status = GATE_PASSED;
		}
	}

	public int getStatus() {
		return status;
	}
}
