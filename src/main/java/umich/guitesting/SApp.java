package umich.guitesting;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;

public class SApp {
    SApp(String[] args) {
	// Invoked on the event dispatch thread.
	// Do any initialization here.
	mainFrame = new SFrame();
    }

    public void show() {
	// Show the UI.
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	mainFrame.pack();
	mainFrame.setVisible(true);
    }
    
    public static void main(final String[] args) {
	// Schedule a job for the event dispatch thread
	// Creating and showing this application's GUI
	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    new SApp(args).show();
		}
	    });
    }

    private SFrame mainFrame;
}
