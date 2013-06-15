package umich.gui

import java.awt.GridLayout

import javax.swing.{ Action, JButton, JLabel, JPanel }

class ConfirmationOrSkipPanel(numComponents: Int,
                              skipAction: Action, okAction: Action) extends JPanel {
  setLayout(new GridLayout(1, numComponents))
  import java.awt.ComponentOrientation
  setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT)
  val okButton = new JButton(okAction)
  okButton.setName("ConfirmationOrSkipPanel.okButton")
  add(okButton)
  val skipButton = new JButton(skipAction)
  skipButton.setName("ConfirmationOrSkipPanel.skipButton")
  add(skipButton)
  // Add dummy JLabels to get "Right alignment"
  for (i ← 2 until numComponents) {
    this.add(new JLabel())
  }
}
