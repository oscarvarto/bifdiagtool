package umich.gui

import java.awt.{
  GridBagConstraints,
  GridBagLayout
}

import java.awt.event.ActionEvent

import javax.swing.{
  AbstractAction,
  BoxLayout,
  JComboBox,
  JLabel,
  JFrame,
  JPanel,
  JTextField
}

import umich.parser.DynSys

class ConfParamFrame(val mainFr: MainFrame, val dynSys: DynSys) extends JFrame {
  val selectionContainer = new JPanel {
    // Components
    val instructions = new JLabel("Choose one parameter, its range, and a step")
    import java.util.{ Vector => JVector }
    import collection.JavaConverters._
    val combo = new JComboBox(
      new JVector(dynSys.namesParams.toList.map { _.name }.asJava))
    combo.setName("ConfParamFr.selectionContainer.combo")
    combo.setEditable(false)
    // Layout
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    add(instructions)
    add(combo)
  }

  val paramInfo = new JPanel {
    // Components
    val rangeFrom = new JTextField("", RANGE_FROM_COLUMNS)
    rangeFrom.setName("ConfParamFr.paramInfo.rangeFrom")
    val rangeTo = new JTextField("", RANGE_TO_COLUMNS)
    rangeTo.setName("ConfParamFr.paramInfo.rangeTo")
    val step = new JTextField("", STEP_COLUMNS)
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
    val RANGE_FROM_COLUMNS = 10
    val RANGE_TO_COLUMNS = 10
    val STEP_COLUMNS = 20
  }

  val logPane = LogPane("ConfParamFrame.logArea")
  val confPanel = new ConfirmationPanel(5, new AbstractAction("Next") {
    import scalaz.syntax.std.string._
    import scalaz.syntax.traverse._
    import scalaz.std.list._
    import scalaz.{ NonEmptyList, Validation }

    def actionPerformed(event: ActionEvent) {
      val name = selectionContainer.combo.getSelectedItem().asInstanceOf[String]
      val maybeStringNums = List(
        paramInfo.rangeFrom.getText(),
        paramInfo.rangeTo.getText(),
        paramInfo.step.getText())

      type λ[α] = Validation[NonEmptyList[NumberFormatException], α]
      val errorsOrNums = maybeStringNums.traverse[λ, Double](
        _.parseDouble.toValidationNel)

      errorsOrNums.fold(errorsNel =>
        {
          // Show errors to user
          val errorMessages = ENTRIES_MUST_BE_NUMBERS :: errorsNel.toList
          logPane.textArea.setText(errorMessages.mkString("\n"))
        }, nums =>
        {
          // Do something with
          // nums, validate: from < to, 0 < step <=  (to - from)
          // name
        }
      )
    }
  })

  val pane = getContentPane()
  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS))
  pane.add(selectionContainer)
  pane.add(paramInfo)
  pane.add(logPane)
  pane.add(confPanel)

  // Constants
  val ENTRIES_MUST_BE_NUMBERS = "From, To and Step entries must be numbers"
}
