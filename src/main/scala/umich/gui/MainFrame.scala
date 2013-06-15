package umich.gui

import java.awt.Dimension
import javax.swing.{ JFrame, JMenu, JMenuBar }

import actions.NewProjectAction

class MainFrame extends JFrame {
  val DefaultWidth = 800
  val DefaultHeight = 600

  // Menu Bar
  val menubar = new JMenuBar()
  // Project Menu

  val projectMenu = new JMenu("Project") {
    val newProjectItem = this.add(new NewProjectAction())
    newProjectItem.setName("ProjectMenu.newProjectItem")
  }

  menubar.add(projectMenu)
  setJMenuBar(menubar)
  setPreferredSize(new Dimension(DefaultWidth, DefaultHeight))
}
