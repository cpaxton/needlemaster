package grid;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import grid.needlegame.Gate;
import grid.needlegame.Needle;
import grid.needlegame.NeedleGameThread;
import grid.needlegame.Surface;

public class ThreadTheNeedleGame extends JPanel {
	
    final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static Color tissue = Color.yellow;
    final static Color deepTissue = Color.red;
    final static Color white = Color.white;
    
    Needle needle;
    ArrayList<Surface> surfaces;
    ArrayList<Gate> gates;
    
    NeedleGameThread thread;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -3307855543895436008L;

	ThreadTheNeedleGame(int width, int height, int preset) {		
		
		super();
		
		surfaces = new ArrayList<Surface>();
		needle = new Needle(0.05, 0.90, 0);
		
		setBackground(Color.white);
		setForeground(Color.black);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	System.out.println("Starting motion at: " + e.getX() + ", " + e.getY());
                needle.startMove(e.getX(), e.getY());
                
            }
            
            public void mouseReleased(MouseEvent e) {
            	System.out.println("Ending motion at: " + e.getX() + ", " + e.getY());
            	needle.endMove();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                needle.updateMove(e.getX(), e.getY());
            }
        });
		
		initialize(preset);
		
		thread = new NeedleGameThread(needle);
		thread.start();
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
        
        for (Surface s: surfaces) {
        	s.rescaleLine(d.width, d.height);
        	s.draw(g2);
        }
        
        needle.draw(g2);
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
			surfaces.add(s1);
			
			double[] s2x = {0, 0.38, 0.5, 0.61, 1, 1, 0};
			double[] s2y = {0.21, 0.34, 0.13, 0.32, 0.26, 0, 0};
			Surface s2 = new Surface(deepTissue, true, 45, s2x, s2y, false);
			surfaces.add(s2);
			
			double[] outsidex = {0, 0.4, 0.6, 1, 1, 0};
			double[] outsidey = {0.4, 0.6, 0.6, 0.4, 0, 0};
			Surface outside = new Surface(Color.ORANGE, true, 45, outsidex, outsidey, true);
			surfaces.add(outside);
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
		
	}

}
