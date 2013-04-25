package umich.gui.actions

import java.awt.event.ActionEvent
import javax.swing.AbstractAction

import umich.gui.NewProjectFrame

class NewProjectAction(projFr: NewProjectFrame) extends AbstractAction("New Project") {
  def actionPerformed(event: ActionEvent) {
    projFr.setVisible(true)
  }
}
