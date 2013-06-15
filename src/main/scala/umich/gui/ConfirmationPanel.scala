package umich.gui

import java.awt.GridLayout

import javax.swing.{ Action, JButton, JLabel, JPanel }

class ConfirmationPanel(numComponents: Int, action: Action) extends JPanel {
  setLayout(new GridLayout(1, numComponents))
  import java.awt.ComponentOrientation
  setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT)
  val okButton = new JButton(action)
  okButton.setName("ConfirmationPanel.okButton")
  add(okButton)
  // Add dummy JLabels to get "Right alignment"
  for (i ← 1 until numComponents) {
    this.add(new JLabel())
  }
}
