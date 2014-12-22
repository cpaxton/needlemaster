package edu.jhu.lcsr.grid.needlegame;

import edu.jhu.lcsr.grid.ThreadTheNeedleGame;

/**
 * Core game loop.
 * Updates things, calls 
 * @author Chris
 *
 */
public class NeedleGameThread extends Thread {
	
	boolean running;
	Needle needle;
	ThreadTheNeedleGame game;
	
	private static final long TICK_LENGTH = 20;
	
	public NeedleGameThread(Needle needle, ThreadTheNeedleGame game) {
		running = true;
		this.needle = needle;
		this.game = game;
	}
	
	public void run() {

		while(running) {
			long t0 = System.currentTimeMillis();

			Surface s = game.checkNeedleLocation(needle.getRealX(), needle.getRealY());

			needle.applySurface(s);
			needle.humanMove();
            game.redraw();

            if (needle.isOffscreen()) {
                game.end();
            } else if (game.checkFailureSurfaces()) {
                game.end();
            }
			
			long dt = System.currentTimeMillis() - t0;

			if (dt < TICK_LENGTH) {
				try {
					Thread.sleep(TICK_LENGTH - dt);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			running = game.isRunning();
			
		}
	}
}
