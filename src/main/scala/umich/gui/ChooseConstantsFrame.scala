package umich.gui

import umich.simulation.Project

import java.awt.event.ActionEvent

import javax.swing.{
  AbstractAction,
  BoxLayout,
  JComboBox,
  JFrame,
  JLabel,
  JPanel,
  JTextField
}

import shapeless._
import umich.parser.Names._

class ChooseConstantsFrame(
  val proj: Project,
  val cont: Option[(Unit ⇒ Unit)] = None) extends JFrame {
  val instructions = new JLabel(
    "Choose one variable/parameter to fix its value")

  import umich.bidiatool.polyFuncs.names
  val names2VarOrPar = proj.names2VarOrPar
  val justNames: List[String] = names2VarOrPar.map(names).toList.flatten

  val selectionContainer = new JPanel {
    import java.util.{ Vector ⇒ JVector }
    import collection.JavaConverters._
    val options = new JVector(justNames.asJava)
    val combo = new JComboBox(options)
    combo.setName("ChooseConstsFr.selectionContainer.combo")
    // Layout
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS))

    val constValueJText = new JTextField(ConstValueWidth)
    constValueJText.setName("ChooseConstsFr.selectionContainer.constValueJText")
    add(combo)
    add(constValueJText)

    // Constants
    val ConstValueWidth = 10
  }

  val logPane = LogPane("ChooseConstsFr.logArea")

  val confPanel = new ConfirmationPanel(5, new AbstractAction("Ok") {
    def actionPerformed(event: ActionEvent) {
      import scalaz.Validation
      import scalaz.syntax.std.string._
      import scalaz.syntax.std.option._
      val errormsgOrNum: Validation[String, Double] = getConstValue()
        .parseDouble.leftMap { _.getMessage }
      errormsgOrNum.fold(
        errormsg ⇒ logPane.textArea.setText(errormsg),
        num ⇒ cont.cata(c ⇒ c(()), println("This only happens during testing")))
    }
  })

  val pane = getContentPane()
  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS))
  pane.add(instructions)
  pane.add(selectionContainer)
  pane.add(logPane)
  pane.add(confPanel)

  private def getParamName(): String =
    selectionContainer.combo.getSelectedItem().asInstanceOf[String]

  private def getConstValue(): String =
    selectionContainer.constValueJText.getText()

  def getNewProject(): Project = {
    import scalaz.{ @@, Tags }
    type λ = Option[Name] @@ Tags.First
    import scalaz.syntax.monoid._
    import scalaz.std.option._
    import scalaz.syntax.std.option._
    import umich.bidiatool.polyFuncs.{ lookup, tagOption }
    val ovp = lookup(names2VarOrPar)(getParamName()).
      foldMap(Tags.First(none): λ)(tagOption) {
        (a, b) ⇒ a ⊹ b
      }
    val vp = ovp.asInstanceOf[Option[Name]].err("This should never happen")
    import scalaz.syntax.std.string._
    val num = getConstValue().parseDouble.toOption.err("This should never happen")
    proj.addFixedValue(vp → num)
  }
}
