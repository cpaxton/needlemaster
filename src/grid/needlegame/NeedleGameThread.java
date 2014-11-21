package grid.needlegame;

/**
 * Core game loop.
 * Updates things, calls 
 * @author Chris
 *
 */
public class NeedleGameThread extends Thread {
	
	boolean running;
	Needle needle;
	
	private static final long TICK_LENGTH = 20;
	
	public NeedleGameThread(Needle needle) {
		running = true;
		this.needle = needle;
	}
	
	public void run() {
		
		while(running) {
			long t0 = System.currentTimeMillis();
			
			needle.move();
			
			long dt = System.currentTimeMillis() - t0;
			//System.out.println("time taken = " + dt);
			if (dt < TICK_LENGTH) {
				try {
					Thread.sleep(TICK_LENGTH - dt);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
