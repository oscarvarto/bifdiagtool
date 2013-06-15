package umich.gui

import javax.swing.{ JFrame, SwingUtilities }

object mainApp extends App {
  // Schedule a job for the event dispatch thread
  // creating and showing this application's GUI
  SwingUtilities.invokeLater(new Runnable() {
    def run() {
      val mainFrame = new MainFrame()
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.pack();
      mainFrame.setVisible(true);
    }
  })
}
