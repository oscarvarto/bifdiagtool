package umich.gui

import java.awt.{
  Dimension,
  GraphicsEnvironment
}

import java.awt.event.ActionEvent

import javax.swing.{
  AbstractAction,
  BoxLayout,
  JEditorPane,
  JFrame,
  JScrollPane,
  JSplitPane,
  JTextArea,
  ScrollPaneConstants,
  WindowConstants
}

import scalaz.{ NonEmptyList, ValidationNel }
import scalaz.syntax.validation._

import umich.parser.DynSys

class NewDynamicalSystemFrame(
  val mfr: MainFrame,
  var parsingFunc: String => ValidationNel[String, DynSys])
    extends JFrame {

  setName("NewDynamicalSystemFrame")

  // Split pane contents:
  // ed, where the user types the Dynamical System
  // logPane, where the user receives feedback from Bidiatool
  val ed = new JEditorPane()
  ed.setName("NewDynamicalSystemFrame.ed")

  val logPane = LogPane("NewDynamicalSystemFrame.logArea")

  val splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT)
  splitPane.setTopComponent(ed)
  splitPane.setBottomComponent(logPane)
  // Confirmation Panel
  import scalaz.std.string._
  import scalaz.syntax.std.string._
  val logAction = new AbstractAction() {
    def actionPerformed(event: ActionEvent) {
      val mCode: Option[String] = ed.getText().charsNel map { _.list.mkString }
      import NewDynamicalSystemFrame._
      val errorsOrDynSys: ValidationNel[String, DynSys] = mCode.fold {
        NonEmptyList(EMPTY_CODE).failure[DynSys]
      } { parsingFunc }
      // TODO: SUCCESS CASE SHOULD BE TREATED DIFFERENTLY!!
      // DynSys should be passed to next window: ConfigureSimulationFrame
      errorsOrDynSys.fold(loe => logPane.textArea.setText(
        loe.list.mkString("\n")), sys => logPane.textArea.setText("Cool"))
    }
  }
  val confPanel = new ConfirmationPanel(5, logAction)

  val pane = getContentPane()
  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS))
  pane.add(splitPane)
  pane.add(confPanel)

  val b = GraphicsEnvironment.getLocalGraphicsEnvironment.getMaximumWindowBounds
  setPreferredSize(new Dimension(b.width / 2, b.height * 5 / 6))
  splitPane.setDividerLocation(b.height * 2 / 3)
  setLocationRelativeTo(null)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
}

object NewDynamicalSystemFrame {
  val EMPTY_CODE = "You should write a Dynamical System\n"
  val SYNTAX_ERRORS = "Please check your entry for syntax errors\n"
}
