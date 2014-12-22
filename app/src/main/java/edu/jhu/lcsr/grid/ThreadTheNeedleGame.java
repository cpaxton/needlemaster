package edu.jhu.lcsr.grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import edu.jhu.lcsr.grid.needlegame.Gate;
import edu.jhu.lcsr.grid.needlegame.Needle;
import edu.jhu.lcsr.grid.needlegame.NeedleGameThread;
import edu.jhu.lcsr.grid.needlegame.Surface;

public class ThreadTheNeedleGame extends View {

    final static int fg = Color.argb(125, 0, 0, 0);
    final static int tissue = Color.argb(255, 232, 146, 124);
    final static int deepTissue = Color.argb(255, 207, 69, 32);
    final static int outlines = Color.argb(255, 255, 200, 0);
    
    Needle needle;
    ArrayList<Surface> surfaces;
    ArrayList<Surface> failureSurfaces;
    ArrayList<Gate> gates;
    
    int index;
    
    NeedleGameThread thread;
    
    long startTime;
    boolean running;
    boolean finished;

    Paint textPaint;
    Paint endTextPaint;

    long time;

	public ThreadTheNeedleGame(Context context, AttributeSet attrs) {
        //int width, int height, int preset
        super(context, attrs);

		startTime = 0;
		running = false;
        finished = false;
		
		index = 0;

        time = 0;

		surfaces = new ArrayList<>();
        failureSurfaces = new ArrayList<>();
		gates = new ArrayList<>();
		needle = new Needle(0.05, 0.90, -1.0*Math.PI);

        textPaint = new Paint();
        textPaint.setColor(fg);

        endTextPaint = new Paint();
        endTextPaint.setColor(fg);

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
            finished = true;
		}
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Level over!");
	}

    public boolean isFinished() {
        return finished;
    }
	
	public synchronized boolean isRunning() {
		return running;
	}

    public void onSizeChanged(int w, int h, int oldw, int oldh) {

        textPaint.setTextSize((Math.min(h, w) / 10) + 10);
        endTextPaint.setTextSize(w / 6);

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

        // compute time remaining
        if(running) {
            // time is set here!
            time = 10000 + startTime - System.currentTimeMillis();
            if (time < 0) {
                time = 0;
                if (isRunning()) {
                    end();
                }
            }
        }
        
        long mins = time / 60000;
        long secs = (time - (60000 * mins)) / 1000;
        long millis = time - (60000 * mins) - (1000 * secs);

        c.drawText("Time: " + String.format("%02d", mins)
        		+ ":" + String.format("%02d", secs)
        		+ ":" + String.format("%02d", millis / 10),
        		50, textPaint.getFontSpacing() + 10, textPaint); // how to print out time remaining

        if(checkPassedLevel()) {
            c.drawText("LEVEL", 25, 1.2f * endTextPaint.getFontSpacing(), endTextPaint);
            c.drawText("COMPLETE", 25, 2.1f * endTextPaint.getFontSpacing(), endTextPaint);
        } else if (finished) {
            c.drawText("LEVEL", 25, 1.2f * endTextPaint.getFontSpacing(), endTextPaint);
            c.drawText("FAILED", 25, 2.1f * endTextPaint.getFontSpacing(), endTextPaint);
        }
	}

    public boolean checkPassedLevel() {
        if (finished && needle.isOffscreen()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks to see if the needle has entered any failure areas
     * @return true if failed
     */
    public boolean checkFailureSurfaces() {
        for (Surface s: failureSurfaces) {
            if (s.contains(needle.getRealX(), needle.getRealY())) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Set up the needle game with a specific preset list of information
     * 0 == largely empty world
     * 1 == two peaks, four gates
	 * @param preset which one of the preset levels we want to use
	 */
	public String initialize(int preset) {
        if (preset == 0) {
            return "Swipe in the direction you want to move! Go off the right edge of the screen.";
        } else if (preset == 1) {
            Gate g = new Gate(0.5, 0.5, -1.0*Math.PI / 4);
            g.setScale(5.0f);

            gates.add(g);

            return "Go through the gate without hitting the red top or bottom!";

        } else if (preset == 2) {
            Gate g1 = new Gate(0.25, 0.5, -1.0 * Math.PI / 4);
            g1.setScale(2.0f);

            Gate g2 = new Gate(0.5, 0.5, Math.PI / 2);
            g2.setScale(2.0f);

            Gate g3 = new Gate(0.75, 0.5, Math.PI / 4);
            g3.setScale(2.0f);

            gates.add(g1);
            gates.add(g2);
            gates.add(g3);

            return "Hit gates in the correct order! Pay attention to their borders!";
        } else if (preset == 3) {

            Gate g1 = new Gate(0.3, 0.5, Math.PI / 2);
            g1.setScale(4.0f);

            Gate g2 = new Gate(0.6, 0.3, Math.PI / 2);
            g2.setScale(1.5f);

            Gate g3 = new Gate(0.6, 0.7, Math.PI / 2);
            g3.setScale(1.5f);

            gates.add(g1);
            gates.add(g2);
            gates.add(g3);

            return "Make sure you choose to hit the gates in the right order, or you will break them!";
        } else if (preset == 4) {

            double[] s1x = {0, 0.25, 0.5, 0.75, 1, 1, 0};
            double[] s1y = {0.1, 0.25, 0.75, 0.25, 1, 0, 0};

            gates.add(new Gate(0.5, 0.5, Math.PI / 2));
            gates.get(0).setScale(3.0f);

            Surface s1 = new Surface(tissue, s1x, s1y, false);
            s1.setMovementMultiplier(0.5);
            s1.setRotationMultiplier(0.15);
            surfaces.add(s1);

            return "Tissue effects how easily you can steer your needle, so plan your movements carefully!";
        } else if (preset == 5) {

            gates.add(new Gate(0.6, 0.5, Math.PI / 2));
            gates.get(0).setScale(3.0f);

            double[] s2x = {0, 0.2, 0.21, 0.4, 0.41, 1, 1, 0};
            double[] s2y = {0.2, 0.3, 0.8, 0.8, 0.3, 0.2, 0, 0};
            Surface s2 = new Surface(deepTissue, s2x, s2y, false);
            surfaces.add(s2);
            failureSurfaces.add(s2);

            return "Touching dark-colored tissue will immediately end the level!";
        } else if (preset == 6) {
            double[] s1x = {0, 0.25, 0.5, 1, 1, 0};
            double[] s1y = {0.6, 0.3, 0.35, 0.4, 0, 0};
            Surface s1 = new Surface(tissue, s1x, s1y, false);
            s1.setMovementMultiplier(0.5);
            s1.setRotationMultiplier(0.15);
            surfaces.add(s1);

            double[] s2x = {0, 0.23, 0.45, 1, 1, 0};
            double[] s2y = {0.25, 0.12, 0.17, 0.26, 0, 0};
            Surface s2 = new Surface(deepTissue, s2x, s2y, false);
            surfaces.add(s2);
            failureSurfaces.add(s2);

            gates.add(new Gate(0.2, 0.7, 0.3));
            gates.get(0).setScale(3.0f);

            return "Go from left to right without hitting the dark tissue!";
        } else if (preset == 7) {
			double[] s1x = {0, 0.4, 0.5, 0.6, 1, 1, 0};
			double[] s1y = {0.4, 0.6, 0.25, 0.6, 0.4, 0, 0};
			Surface s1 = new Surface(tissue, s1x, s1y, false);
			s1.setMovementMultiplier(0.5);
			s1.setRotationMultiplier(0.15);
			surfaces.add(s1);
			
			double[] s2x = {0, 0.38, 0.5, 0.61, 1, 1, 0};
			double[] s2y = {0.21, 0.34, 0.13, 0.32, 0.26, 0, 0};
			Surface s2 = new Surface(deepTissue, s2x, s2y, false);
			surfaces.add(s2);
            failureSurfaces.add(s2);

			gates.add(new Gate(0.2, 0.7, 0.3));
			gates.add(new Gate(0.4, 0.5, Math.PI / 2));
			gates.add(new Gate(0.6, 0.5, Math.PI / 2));
			gates.add(new Gate(0.8, 0.7, 1.5));

            return "Hit all of the gates!";
		}

        return "Unknown level! Have fun!";
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

    public boolean gatesDone() {
        int numDone = 0;
        for (Gate gt: gates) {
            if (gt.getStatus() == Gate.GATE_FAILED || gt.getStatus() == Gate.GATE_PASSED) {
                numDone++;
            }
        }

        return numDone == gates.size();
    }

    public void redraw() {
        postInvalidate();
    }


    public int getPassedGates() {
        int passed = 0;
        for (Gate gt : gates) {
            if(gt.getStatus() == Gate.GATE_PASSED) {
                passed++;
            }
        }
        return passed;
    }

    public int getNumGates() {
        return gates.size();
    }

    public double getPathLength() {
        return needle.getPathLength();
    }

    public long getTimeRemaining() {
        return time;
    }
}
