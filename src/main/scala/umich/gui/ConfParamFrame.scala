package umich.gui

import java.awt.{
  GridBagConstraints,
  GridBagLayout
}

import java.awt.event.ActionEvent

import javax.swing.{
  AbstractAction,
  BoxLayout,
  ButtonGroup,
  JComboBox,
  JLabel,
  JFrame,
  JPanel,
  JRadioButton,
  JTextField
}

import java.util.{ List ⇒ JList }

import umich.parser._
import umich.parser.Names._
import umich.simulation.Project
import umich.simulation.ParameterConfig

//val force2d: Boolean = false
class ConfParamFrame(
  val proj: Project,
  var cont: Option[(Unit ⇒ Unit)] = None) extends JFrame { fr ⇒
  val dynSys = proj.dynSys
  val alreadyChosen = proj.paramConfs.headOption map { _._1 }
  val selectionContainer = new JPanel {
    // Components
    val instructions = new JLabel("Choose one parameter, its range, and a step")
    import java.util.{ Vector ⇒ JVector }

    import collection.JavaConverters._

    val allNames: List[String] = dynSys.namesParams.toList.map { _.name }
    import scalaz.std.option._
    val paramNames: List[String] = fold(alreadyChosen)(
      some ⇒ allNames.filterNot(some.name contains),
      allNames)
    val options = new JVector(paramNames.asJava)
    val combo = new JComboBox(options)
    combo.setName("ConfParamFr.selectionContainer.combo")
    combo.setEditable(false)
    // Layout
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    add(instructions)
    add(combo)
  }

  val paramInfo = new JPanel {
    // Components
    val rangeFrom = new JTextField("", RangeFromColumns)
    rangeFrom.setName("ConfParamFr.paramInfo.rangeFrom")
    val rangeTo = new JTextField("", RangeToColumns)
    rangeTo.setName("ConfParamFr.paramInfo.rangeTo")
    val step = new JTextField("", StepColumns)
    step.setName("ConfParamFr.paramInfo.step")

    //Layout
    setLayout(new GridBagLayout())
    val c = new GridBagConstraints()

    c.fill = GridBagConstraints.HORIZONTAL

    c.weightx = 0.0
    c.gridx = 0
    c.gridy = 0
    add(new JLabel("From"), c)

    c.weightx = 0.35
    c.gridx = 1
    c.gridy = 0
    add(rangeFrom, c)

    c.weightx = 0.1
    c.gridx = 2
    c.gridy = 0
    add(new JLabel("To"), c)

    c.weightx = 0.35
    c.gridx = 3
    c.gridy = 0
    add(rangeTo, c)

    c.weightx = 0.0
    c.gridx = 0
    c.gridy = 1
    c.gridwidth = 1
    add(new JLabel("Step"), c)

    c.weightx = 1.0
    c.gridx = 1
    c.gridy = 1
    c.gridwidth = 3
    add(step, c)

    // Constants
    val RangeFromColumns = 10
    val RangeToColumns = 10
    val StepColumns = 20
  }

  // val choose2dOr3dPanel = new JPanel {
  //   // Components
  //   val buttonGroup = new ButtonGroup()
  //   val twoDButton = new JRadioButton("2D")
  //   twoDButton.setName("ConfParamFr.choose2dOr3dPanel.2dRadioButton")
  //   val threeDButton = new JRadioButton("3D")
  //   threeDButton.setName("ConfParamFr.choose2dOr3dPanel.3dRadioButton")
  //   twoDButton.setSelected(true)
  //   threeDButton.setEnabled(!force2d)
  //   buttonGroup.add(twoDButton)
  //   buttonGroup.add(threeDButton)
  //   setLayout(new BoxLayout(this, BoxLayout.X_AXIS))
  //   add(twoDButton)
  //   add(threeDButton)
  // }

  val logPane = LogPane("ConfParamFr.logArea")

  val skipAction = new AbstractAction("I will vary one parameter only") {
    def actionPerformed(event: ActionEvent) {
      import scalaz.std.option._
      import scalaz.syntax.std.option._
      cont.cata(
        c ⇒ c(()), println("This should happen during testing only"))
    }
  }
  val okAction = new AbstractAction("Ok") {
    def actionPerformed(event: ActionEvent) {
      getErrorsOrParamConfig().fold(
        errorsNel ⇒
          {
            // Show errors to user
            val errorMessages: List[String] =
              EntriesMustBeNumbers :: errorsNel.list
            logPane.textArea.setText(errorMessages.mkString("\n"))
          },
        parConf ⇒
          {
            import scalaz.std.option._
            import scalaz.syntax.std.option._
            cont.cata(
              c ⇒ c(()), println("This should happen during testing only"))
          })
    }
  }
  val confPanel = new ConfirmationOrSkipPanel(3, skipAction, okAction)
  if (alreadyChosen.isEmpty) confPanel.skipButton.setVisible(false)

  val pane = getContentPane()
  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS))
  pane.add(selectionContainer)
  pane.add(paramInfo)
  //pane.add(choose2dOr3dPanel)
  pane.add(logPane)
  pane.add(confPanel)

  def name(): String = selectionContainer.combo.getSelectedItem().
    asInstanceOf[String]
  def from(): String = paramInfo.rangeFrom.getText()
  def to(): String = paramInfo.rangeTo.getText()
  def step(): String = paramInfo.step.getText()

  import scalaz.ValidationNel
  def getErrorsOrParamConfig(): ValidationNel[String, ParameterConfig] = {
    import scalaz.syntax.std.string._
    import scalaz.syntax.traverse._
    import scalaz.std.list._
    import scalaz.{ NonEmptyList, Validation }

    val maybeStringNums = List(from(), to(), step())
    type λ[α] = Validation[NonEmptyList[NumberFormatException], α]
    val nfeNelOrNums: λ[List[Double]] = maybeStringNums.traverse[λ, Double](
      _.parseDouble.toValidationNel)
    val errorMsgsOrNums: ValidationNel[String, List[Double]] = nfeNelOrNums.
      leftMap { _.map(_.getMessage) }
    errorMsgsOrNums flatMap { ns ⇒
      val nums = ns.toArray
      ParameterConfig(nums(0), nums(1), nums(2))
    }
  }

  def getParamConfig(): Option[(ParamName, ParameterConfig)] = {
    import scalaz.syntax.std.option._
    val paramConf = getErrorsOrParamConfig().toOption
    paramConf.flatMap { conf ⇒ Some(ParamName(name()) → conf) }
  }

  // Constants
  val EntriesMustBeNumbers = "From, To and Step entries must be numbers"
}
