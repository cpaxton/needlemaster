package grid;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import grid.needlegame.Gate;
import grid.needlegame.Needle;
import grid.needlegame.NeedleGameThread;
import grid.needlegame.Surface;

public class ThreadTheNeedleGame extends JPanel {
	
    final static Color bg = new Color(0.85f, 0.90f, 1.00f);
    final static Color fg = new Color(0.00f, 0.00f, 0.00f, 0.50f);
    final static Color tissue = new Color(0.98f, 0.85f, 0.50f);
    final static Color deepTissue = new Color(0.90f, 0.25f, 0.15f);
    final static Color white = Color.white;
    final static Color outlines = new Color(1.0f, 0.80f, 0.0f);
    
    Needle needle;
    ArrayList<Surface> surfaces;
    ArrayList<Gate> gates;
    
    int index;
    
    NeedleGameThread thread;
    
    long startTime;
    boolean running;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -3307855543895436008L;

	ThreadTheNeedleGame(int width, int height, int preset) {		
		
		super();
		
		startTime = 0;
		running = false;
		
		index = 0;
		
		surfaces = new ArrayList<Surface>();
		gates = new ArrayList<Gate>();
		needle = new Needle(0.05, 0.90, 0);
		
		setBackground(Color.white);
		setForeground(Color.black);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	System.out.println("Starting motion at: " + e.getX() + ", " + e.getY());
                needle.startMove(e.getX(), e.getY());
                needle.updateMove(e.getX(), e.getY());
            }
            
            public void mouseReleased(MouseEvent e) {
            	System.out.println("Ending motion at: " + e.getX() + ", " + e.getY());
                needle.updateMove(e.getX(), e.getY());
            	needle.endMove();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                needle.updateMove(e.getX(), e.getY());
            }
        });
		
		initialize(preset);
		
		thread = new NeedleGameThread(needle, this);
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
	}
	
	public synchronized boolean isRunning() {
		return running;
	}
	
	
	/**
	 * Paint function for the game
	 * Note: some example code taken from the shape demo online
	 */
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getSize();
        
        if (needle.rescale(d.width, d.height)) {
        	repaint();
        }
        
        g.setColor(bg);
        g.fillRect(0, 0, d.width, d.height);
        
        for (Surface s: surfaces) {
        	s.rescaleLine(d.width, d.height);
        	s.draw(g2);
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
        	gt.rescale(d.width, d.height);
        	gt.update(needle);
        	gt.draw(g2);
        }
        
        needle.draw(g2);
        
        g.setColor(fg);
        int fontSize = (int) Math.min(d.height, d.width) / 10;
        g.setFont(new Font("Geneva",Font.PLAIN,fontSize));
        
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
        
        g.drawString("Time: " + String.format("%02d", mins)
        		+ ":" + String.format("%02d", secs)
        		+ ":" + String.format("%02d", millis / 10),
        		50, fontSize + 10); // how to print out time remaining
        
        //int widthRange = d.width / 5;
        //int heightRange = d.height / 5;
        //repaint((int)needle.getRealX() - widthRange, (int)needle.getRealY() - heightRange, 2 * widthRange, 2 * heightRange);
        repaint();
	}
	
	/**
	 * Set up the needle game with a specific preset list of information
	 * @param preset
	 */
	private void initialize(int preset) {
		if (preset == 0) {
			double[] s1x = {0, 0.25, 0.5, 1, 1, 0};
			double[] s1y = {0.6, 0.3, 0.35, 0.4, 0, 0};
			Surface s1 = new Surface(tissue, true, 45, s1x, s1y, false);
			surfaces.add(s1);
			
			double[] s2x = {0, 0.23, 0.45, 1, 1, 0};
			double[] s2y = {0.25, 0.12, 0.17, 0.26, 0, 0};
			Surface s2 = new Surface(deepTissue, true, 45, s2x, s2y, false);
			surfaces.add(s2);
			
		} else if (preset == 1) {
			double[] s1x = {0, 0.4, 0.5, 0.6, 1, 1, 0};
			double[] s1y = {0.4, 0.6, 0.25, 0.6, 0.4, 0, 0};
			Surface s1 = new Surface(tissue, true, 45, s1x, s1y, false);
			s1.setMovementMultiplier(0.5);
			s1.setRotationMultiplier(0.3);
			surfaces.add(s1);
			
			double[] s2x = {0, 0.38, 0.5, 0.61, 1, 1, 0};
			double[] s2y = {0.21, 0.34, 0.13, 0.32, 0.26, 0, 0};
			Surface s2 = new Surface(deepTissue, true, 45, s2x, s2y, false);
			surfaces.add(s2);
			
			double[] outsidex = {0, 0.4, 0.6, 1, 1, 0};
			double[] outsidey = {0.4, 0.6, 0.6, 0.4, 0, 0};
			Surface outside = new Surface(outlines, true, 45, outsidex, outsidey, true);
			surfaces.add(outside);
			
			//Gate g1 = new Gate(0.4, 0.5, - Math.PI * 3 / 4);
			//Gate g2 = new Gate(0.6, 0.5, Math.PI * 3 / 4);
			gates.add(new Gate(0.2, 0.7, 0.3));
			gates.add(new Gate(0.4, 0.5, Math.PI / 2));
			gates.add(new Gate(0.6, 0.5, Math.PI / 2));
			gates.add(new Gate(0.8, 0.7, 1.5));
			
		}
	}
	
	// curve for the depth of the surface
	// curve for the depth of the 
	Surface tough;
	Surface danger;
	
	/**
	 * Starts a demo instance of the game
	 * With its own 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Demo");
		ThreadTheNeedleGame game = new ThreadTheNeedleGame(800, 600, 1);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(new Dimension(800, 600));
		
		frame.getContentPane().add(game, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		game.start();
	}


	/**
	 * Check to see if the needle is in any surfaces of interest
	 * @param realX -- the real X location of the needle
	 * @param realY -- the real Y location of the needle
	 * @return
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

}
