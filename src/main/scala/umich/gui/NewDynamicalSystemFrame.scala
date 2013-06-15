package umich.gui

import java.awt.{
  BorderLayout,
  Dimension,
  GraphicsEnvironment
}

import java.awt.event.ActionEvent

import javax.swing.{
  AbstractAction,
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
  var cont: Option[(Unit ⇒ Unit)] = None,
  val parsingFunc: String ⇒ ValidationNel[String, DynSys])
  extends JFrame {

  setName("NewDynamicalSystemFrame")

  // Split pane contents:
  // ed, where the user types the Dynamical System
  // logPane, where the user receives feedback from Bidiatool
  val ed = new JEditorPane()
  ed.setName("NewDynamicalSystemFrame.ed")

  val logPane = LogPane("NewDynamicalSystemFrame.logArea")

  val splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ed, logPane)
  // Confirmation Panel
  import scalaz.std.string._
  import scalaz.syntax.std.string._
  val confPanel = new ConfirmationPanel(5, new AbstractAction("Ok") {
    def actionPerformed(event: ActionEvent) {
      val mCode: Option[String] = ed.getText().charsNel map { _.list.mkString }
      import NewDynamicalSystemFrame._
      import scalaz.std.option._
      import scalaz.syntax.std.option._
      val errorsOrDynSys: ValidationNel[String, DynSys] = mCode.cata(
        parsingFunc(_), NonEmptyList(EmptyCode).failure[DynSys])

      errorsOrDynSys.fold(
        loe ⇒ logPane.textArea.setText(loe.list.mkString("\n")),
        dynSys ⇒ cont.cata(
          c ⇒ c(()), println("This happens only during testing")))
    }
  })

  val pane = getContentPane()
  pane.setLayout(new BorderLayout())
  pane.add(splitPane, BorderLayout.CENTER)
  pane.add(confPanel, BorderLayout.SOUTH)

  val b = GraphicsEnvironment.getLocalGraphicsEnvironment.getMaximumWindowBounds
  setPreferredSize(new Dimension(b.width / 2, b.height * 5 / 6))
  splitPane.setDividerLocation(b.height * 2 / 3)
  setLocationRelativeTo(null)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  def getDynSys(): Option[DynSys] = parsingFunc(ed.getText()).toOption
}

object NewDynamicalSystemFrame {
  val EmptyCode = "You should write a Dynamical System"
  val SyntaxErrors = "Please check your entry for syntax errors"
}
