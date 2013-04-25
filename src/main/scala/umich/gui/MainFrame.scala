package umich.gui

import java.awt.Dimension
import javax.swing.{ JFrame, JMenu, JMenuBar }

import actions.NewProjectAction

class MainFrame extends JFrame {
  val DEFAULT_WIDTH = 75
  val DEFAULT_HEIGHT = 150

  setName("MenuFrame")
  // Menu Bar
  val menubar = new JMenuBar()
  // Project Menu
  val newProjFr = new NewProjectFrame(this)
  val projectMenu = new JMenu("Project") {
    val newProjectItem = this.add(new NewProjectAction(newProjFr))
    newProjectItem.setName("ProjectMenu.newProjectItem")
  }
  menubar.add(projectMenu)
  setJMenuBar(menubar)
  setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))

  def newProj(name: String) = {
    newProjFr.dispose()
    // Open NewDynamicalSystemFrame
  }
}
