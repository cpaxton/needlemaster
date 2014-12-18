package edu.jhu.lcsr.grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import edu.jhu.lcsr.grid.needlegame.Gate;
import edu.jhu.lcsr.grid.needlegame.Needle;
import edu.jhu.lcsr.grid.needlegame.NeedleGameThread;
import edu.jhu.lcsr.grid.needlegame.Surface;

public class ThreadTheNeedleGame extends View {

    //final static int bg = Color.argb(255, 99, 153, 174);
    final static int fg = Color.argb(125, 0, 0, 0);
    final static int tissue = Color.argb(255, 232, 146, 124);
    final static int deepTissue = Color.argb(255, 207, 69, 32);
    final static int outlines = Color.argb(255, 255, 200, 0);
    
    Needle needle;
    ArrayList<Surface> surfaces;
    ArrayList<Gate> gates;
    
    int index;
    
    NeedleGameThread thread;
    
    long startTime;
    boolean running;
    boolean finished;

    Paint textPaint;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -3307855543895436008L;

	public ThreadTheNeedleGame(Context context, AttributeSet attrs) {
        //int width, int height, int preset
        super(context, attrs);

		startTime = 0;
		running = false;
        finished = false;
		
		index = 0;
		
		surfaces = new ArrayList<Surface>();
		gates = new ArrayList<Gate>();
		needle = new Needle(0.05, 0.90, 0);

        textPaint = new Paint();
        textPaint.setColor(fg);

		thread = new NeedleGameThread(needle, this);

        System.out.println("Starting game");
        start();
	}
	
	public void start() {
		synchronized(this) {
			running = true;
		}
		thread.start();
		startTime = System.currentTimeMillis();
	}
	
	public void end() {
		synchronized(this) {
			running = false;
		}
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Level over!");
        finished = true;
	}

    public boolean isFinished() {
        return finished;
    }
	
	public synchronized boolean isRunning() {
		return running;
	}

    public void onSizeChanged(int w, int h, int oldw, int oldh) {

        textPaint.setTextSize((Math.min(h, w) / 10) + 10);

        needle.rescale(w, h);
        for (Surface s: surfaces) {
            s.rescaleLine(w, h);
        }

        for (Gate gt: gates) {
            gt.rescale(w, h);
        }
    }

    public void startMove(float x, float y) {

        if(!running && !finished) {
            start();
        }

        needle.startMove(x, y);
    }

    public void updateMove(float x, float y) {
        needle.updateMove(x, y);
    }

    public void endMove() {
        needle.endMove();
    }
	
	/**
	 * Paint function for the game
	 * Note: some example code taken from the shape demo online
	 */
	public void onDraw(Canvas c) {

        //g.setColor(bg);
        //g.fillRect(0, 0, d.width, d.height);
        
        for (Surface s: surfaces) {
        	s.draw(c);
        }
        
		while (index < gates.size()
				&& (gates.get(index).getStatus() == Gate.GATE_PASSED
				|| gates.get(index).getStatus() == Gate.GATE_FAILED))
		{
			index++;
		}

		if (index < gates.size()) {
			gates.get(index).setStatus(Gate.GATE_NEXT);
		}

		if (index + 1 < gates.size() && gates.get(index + 1).getStatus() != Gate.GATE_FAILED) {
			gates.get(index + 1).setStatus(Gate.GATE_ON_DECK);
		}
        
        for (Gate gt: gates) {
        	gt.update(needle);
        	gt.draw(c);
        }
        
        needle.draw(c);
        
        //g.setColor(fg);
        //int fontSize = (int) Math.min(d.height, d.width) / 10;
        //g.setFont(new Font("Geneva",Font.PLAIN,fontSize));
        
        // compute time remaining
        long time = 30000 + startTime - System.currentTimeMillis();
        if(time < 0) {
        	time = 0;
        	if (isRunning()) {
        		end();
        	}
        }
        
        long mins = time / 60000;
        long secs = (time - (60000 * mins)) / 1000;
        long millis = time - (60000 * mins) - (1000 * secs);

        c.drawText("Time: " + String.format("%02d", mins)
        		+ ":" + String.format("%02d", secs)
        		+ ":" + String.format("%02d", millis / 10),
        		50, textPaint.getFontSpacing() + 10, textPaint); // how to print out time remaining
	}
	
	/**
	 * Set up the needle game with a specific preset list of information
     * 0 == largely empty world
     * 1 == two peaks, four gates
	 * @param preset which one of the preset levels we want to use
	 */
	public void initialize(int preset) {
		if (preset == 0) {
			double[] s1x = {0, 0.25, 0.5, 1, 1, 0};
			double[] s1y = {0.6, 0.3, 0.35, 0.4, 0, 0};
			Surface s1 = new Surface(tissue, s1x, s1y, false);
			surfaces.add(s1);
			
			double[] s2x = {0, 0.23, 0.45, 1, 1, 0};
			double[] s2y = {0.25, 0.12, 0.17, 0.26, 0, 0};
			Surface s2 = new Surface(deepTissue, s2x, s2y, false);
			surfaces.add(s2);
			
		} else if (preset == 1) {
			double[] s1x = {0, 0.4, 0.5, 0.6, 1, 1, 0};
			double[] s1y = {0.4, 0.6, 0.25, 0.6, 0.4, 0, 0};
			Surface s1 = new Surface(tissue, s1x, s1y, false);
			s1.setMovementMultiplier(0.5);
			s1.setRotationMultiplier(0.3);
			surfaces.add(s1);
			
			double[] s2x = {0, 0.38, 0.5, 0.61, 1, 1, 0};
			double[] s2y = {0.21, 0.34, 0.13, 0.32, 0.26, 0, 0};
			Surface s2 = new Surface(deepTissue, s2x, s2y, false);
			surfaces.add(s2);
			
			double[] outsidex = {0, 0.4, 0.6, 1, 1, 0};
			double[] outsidey = {0.4, 0.6, 0.6, 0.4, 0, 0};
			Surface outside = new Surface(outlines, outsidex, outsidey, true);
			surfaces.add(outside);
			
			//Gate g1 = new Gate(0.4, 0.5, - Math.PI * 3 / 4);
			//Gate g2 = new Gate(0.6, 0.5, Math.PI * 3 / 4);
			gates.add(new Gate(0.2, 0.7, 0.3));
			gates.add(new Gate(0.4, 0.5, Math.PI / 2));
			gates.add(new Gate(0.6, 0.5, Math.PI / 2));
			gates.add(new Gate(0.8, 0.7, 1.5));
			
		}
	}

	/**
	 * Check to see if the needle is in any surfaces of interest
	 * @param realX -- the real X location of the needle
	 * @param realY -- the real Y location of the needle
	 * @return which surface this needle point is located within
	 */
	public Surface checkNeedleLocation(double realX, double realY) {
		Surface in = null;
		for(Surface s: surfaces) {
			if (s.contains(realX, realY)) {
				in = s;
			}
		}
		return in;
	}

    public void redraw() {
        postInvalidate();
    }
}
