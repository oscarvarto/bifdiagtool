package umich.gui

import org.scalatest.Matchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

class NewDynamicalSystemFrameTest extends FestiveFunSuite with Matchers {
  import umich.parser.{ DynSys, DynSys1 }
  import NewDynamicalSystemFrame._

  import scalaz.NonEmptyList
  import scalaz.syntax.validation._

  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[NewDynamicalSystemFrame]() {
        protected def executeInEDT() = new NewDynamicalSystemFrame(
          None, s ⇒ NonEmptyList("This is a dummy parser function").failure[DynSys])
      })
    window = new FrameFixture(frame)
    window.show()
    //setup
  }

  test("If a Dynamical System is not typed " +
    "the user should see simple error messages at the Log Area", GUITest) {
    window.textBox("NewDynamicalSystemFrame.ed").setText("")
    window.button("ConfirmationPanel.okButton").click()
    window.textBox("NewDynamicalSystemFrame.logArea").requireText(EmptyCode)
  }

  test("If parsing of a Dynamical System fails " +
    "the user should see simple error messages at the Log Area", GUITest) {
    window.textBox("NewDynamicalSystemFrame.ed").setText("this is not a DynSys")
    window.button("ConfirmationPanel.okButton").click()
    import java.util.regex.Pattern
    window.textBox("NewDynamicalSystemFrame.logArea").requireText(Pattern.compile(".+"))
    pending
    // TODO: better error logging for parsing errors or Dynamical Systems
  }

  // test("If parsing of a Dynamical System succeeds " +
  //   "the user should see a ConfigureSimulationFrame window", GUITest) {
  //   pending
  // }
}
