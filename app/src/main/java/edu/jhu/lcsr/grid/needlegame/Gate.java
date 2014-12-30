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
	float scale;

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

	private static final int closed = Color.argb(255, 251, 216, 114);
	private static final int onDeck = Color.argb(255, 251, 216, 114);
	private static final int next = Color.argb(255, 251, 216, 114);

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
        warningPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        warningPaint.setStrokeWidth(3.0f);

		screenWidth = 0;
		screenHeight = 0;
		scale = 1.0f;
		rescale(800, 600);

		entered = false;
	}

    public void setScale(float scale) {
        this.scale = scale;
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
        gatePaint.setStrokeWidth(0.0f);
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
        gatePaint.setStrokeWidth(5.0f);
		if(status == GATE_NEXT && !entered) {
            gatePaint.setColor(highlight);
		} else if (status == GATE_ON_DECK) {
            gatePaint.setColor(highlightOnDeck);
		}
        c.drawPath(polygon, gatePaint);
	}

    float width1, height1, width2, height2, width1m, height1m, width2m, height2m, realX, realY;

	void redraw() {

		synchronized(this) {
            double warningWidth = screenHeight / 100;

			width1 = scale * (float)(screenHeight * 0.05 * Math.cos(w) + screenHeight * 0.03 * Math.sin(w));
			height1 = scale * (float)(screenHeight * 0.05 * Math.sin(w) - screenHeight * 0.03 * Math.cos(w));
			width2 = scale * (float)(-1 * screenHeight * 0.05 * Math.cos(w) + screenHeight * 0.03 * Math.sin(w));
			height2 = scale * (float)(-1 * screenHeight * 0.05 * Math.sin(w) - screenHeight * 0.03 * Math.cos(w));

            width1m = scale * (float)((screenHeight * 0.05 - warningWidth) * Math.cos(w) + screenHeight * 0.03 * Math.sin(w));
            height1m = scale * (float)((screenHeight * 0.05 - warningWidth) * Math.sin(w) - screenHeight * 0.03 * Math.cos(w));
            width2m = scale * (float)((-1 * screenHeight * 0.05 + warningWidth) * Math.cos(w) + screenHeight * 0.03 * Math.sin(w));
            height2m = scale * (float)((-1 * screenHeight * 0.05 + warningWidth) * Math.sin(w) - screenHeight * 0.03 * Math.cos(w));

            realX = (float)(x * screenWidth);
            realY = (float)((1.0 - y) * screenHeight);

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
            gateRegion.setPath(polygon, new Region((int)tmp.left, (int)tmp.top, (int)tmp.right, (int)tmp.bottom));

            top.computeBounds(tmp, true);
            topRegion = new Region();
            topRegion.setPath(top, new Region((int)tmp.left, (int)tmp.top, (int)tmp.right, (int)tmp.bottom));

            bottom.computeBounds(tmp, true);
            bottomRegion = new Region();
            bottomRegion.setPath(bottom, new Region((int)tmp.left, (int)tmp.top, (int)tmp.right, (int)tmp.bottom));
		}
	}

	public boolean rescale(int width, int height) {
		if (screenWidth != width || screenHeight != height) {
			// scale the needle; regenerate the path creating its image

			screenWidth = width;
			screenHeight = height;
			//scale = (float)Math.sqrt(width*width + height*height);

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
        } else if (gateRegion.contains((int)pt.x, (int)pt.y)) {
            status = GATE_FAILED;
		} else if (entered == true && status != GATE_FAILED) {
			status = GATE_PASSED;
		}
	}

	public int getStatus() {
		return status;
	}

    public String toString() {
        String str = "";

        str += "GatePos: " + x + "," + y + "," + w + "\n";
        str += "GateX: " + (realX + width1) + "," + (realX + width2) + "," + (realX - width1) + "," + (realX - width2) + "\n";
        str += "GateY: " + (realY + height1) + "," + (realY + height2) + "," + (realY - height1) + "," + (realY - height2) + "\n";
        str += "TopX: " + (realX + width1m) + "," + (realX + width1m) + "," + (realX - width2m) + "," + (realX - width2) + "\n";
        str += "TopY: " + (realY + height1) + "," + (realY + height1m) + "," + (realY - height2m) + "," + (realY - height2) + "\n";
        str += "BottomX: " + (realX + width2) + "," + (realX + width2m) + "," + (realX - width1m) + "," + (realX - width1) + "\n";
        str += "BottomY: " + (realY + height2) + "," + (realY + height2m) + "," + (realY - height1m) + "," + (realY - height1) + "\n";

        return str;
    }
}
