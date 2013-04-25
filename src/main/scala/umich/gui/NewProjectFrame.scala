package umich.gui

import java.awt.{
  Color,
  Dimension,
  GridBagConstraints,
  GridBagLayout,
  GridLayout
}
import java.awt.event.ActionEvent
import javax.swing.{
  AbstractAction,
  BoxLayout,
  JButton,
  JFrame,
  JLabel,
  JPanel,
  JTextField
}

import scalaz.std.string._
import scalaz.syntax.std.string._

class NewProjectFrame(mainFr: MainFrame) extends JFrame { npf =>
  var mName: Option[String] = None

  val infoPanel = new JPanel {
    val NAME_ERROR = "Name should not be empty"
    val MAX_NAME_LENGTH = 15
    val DEFAULT_WIDTH = 50
    val DEFAULT_HEIGHT = 70

    // InformationPanel components
    val nameLabel: JLabel = new JLabel("Project's name")
    val nameTextField: JTextField = new JTextField("", MAX_NAME_LENGTH)
    val nameShouldNotBeEmpty = new JLabel(NAME_ERROR)
    // Adding components
    setLayout(new GridBagLayout())
    val c = new GridBagConstraints()

    c.fill = GridBagConstraints.HORIZONTAL
    c.gridx = 0
    c.gridy = 0
    add(nameLabel, c)

    c.gridx = 1
    c.gridy = 0
    c.gridwidth = 2
    c.gridheight = 1
    nameTextField.setName("InformationPanel.nameTextField")
    add(nameTextField, c)

    c.gridx = 1
    c.gridy = 1
    c.gridwidth = 2
    c.gridheight = 1
    nameShouldNotBeEmpty.setForeground(Color.RED)
    nameShouldNotBeEmpty.setVisible(false)
    add(nameShouldNotBeEmpty, c)
    setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT))
  }
  val okAction = new AbstractAction("Ok") {
    def actionPerformed(event: ActionEvent) {
      mName = infoPanel.nameTextField.getText().charsNel map { _.list.mkString }
      if (mName.isDefined) {
        mainFr.newProj(mName.get)
      }
    }
  }

  val confPanel = new ConfirmationPanel(4, okAction)

  setName("NewProjectFrame")
  val pane = getContentPane()
  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS))
  pane.add(infoPanel)
  pane.add(confPanel)

  def showNameShouldNotBeEmpty(aFlag: Boolean): Unit = infoPanel.nameShouldNotBeEmpty.setVisible(aFlag)
}

