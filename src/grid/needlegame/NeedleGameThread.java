package grid.needlegame;

import grid.ThreadTheNeedleGame;

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
		//long t00 = System.currentTimeMillis();
		while(running) {
			long t0 = System.currentTimeMillis();
			
			Surface s = game.checkNeedleLocation(needle.getRealX(), needle.getRealY());
			//if (s != null) {
			needle.applySurface(s);
			//}
			needle.move();
			
			long dt = System.currentTimeMillis() - t0;
			//System.out.println("time taken = " + dt);
			if (dt < TICK_LENGTH) {
				try {
					//System.out.println(System.currentTimeMillis() + " -- " + dt + " -- sleeping for " + (TICK_LENGTH - dt));
					Thread.sleep(TICK_LENGTH - dt);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//long ttmp = System.currentTimeMillis();
			//System.out.println(ttmp - t00); t00 = ttmp;
		}
	}
}
