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

class NewProjectFrame(var cont: Option[(Unit ⇒ Unit)] = None) extends JFrame {
  val infoPanel = new JPanel {
    val NameError = "Name should not be empty"
    val MaxNameLenght = 15
    val DefaultWidth = 50
    val DefaultHeight = 70

    // InformationPanel components
    val nameLabel: JLabel = new JLabel("Project's name")
    val nameTextField: JTextField = new JTextField("", MaxNameLenght)
    val nameShouldNotBeEmpty = new JLabel(NameError)
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
    setPreferredSize(new Dimension(DefaultWidth, DefaultHeight))
  }

  def getProjectName(): Option[String] =
    infoPanel.nameTextField.getText().charsNel map { _.list.mkString }

  val okAction = new AbstractAction("Ok") {
    def actionPerformed(event: ActionEvent) {
      showNameShouldNotBeEmpty(false)
      val mProjName = getProjectName()
      import scalaz.std.option._
      import scalaz.syntax.std.option._
      mProjName.cata(
        name ⇒ cont.cata(
          some ⇒ some(()), println("This only happens during testing")),
        showNameShouldNotBeEmpty(true))
    }
  }

  val confPanel = new ConfirmationPanel(4, okAction)

  setName("NewProjectFrame")
  val pane = getContentPane()
  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS))
  pane.add(infoPanel)
  pane.add(confPanel)

  def showNameShouldNotBeEmpty(aFlag: Boolean): Unit =
    infoPanel.nameShouldNotBeEmpty.setVisible(aFlag)
}
