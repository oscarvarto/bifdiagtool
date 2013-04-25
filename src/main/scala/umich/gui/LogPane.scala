package umich.gui

import javax.swing.{ JScrollPane, JTextArea }
import javax.swing.ScrollPaneConstants._

class LogPane private (val textArea: JTextArea, val name: String) extends JScrollPane(
  textArea,
  VERTICAL_SCROLLBAR_ALWAYS,
  HORIZONTAL_SCROLLBAR_NEVER) {
  textArea.setEditable(false)
  textArea.setName(name)
}

object LogPane {
  def apply(name: String): LogPane = new LogPane(new JTextArea(), name)
}
