package umich.gui

import org.scalatest.matchers.ShouldMatchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

class ConfParamFrameTest extends FestiveFunSuite with ShouldMatchers {
  import umich.parser.{ DynSys, DynSys1 }

  import scalaz.NonEmptyList
  import scalaz.syntax.validation._

  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[ConfParamFrame]() {
        protected def executeInEDT() = new ConfParamFrame(
          new MainFrame(), DynSys1.dynSys)
      })
    window = new FrameFixture(frame)
    window.show()
    //setup
  }

  test("ComboBox with names of parameters should not be editable", GUITest) {
    window.comboBox("ConfParamFr.selectionContainer.combo").requireNotEditable()
  }

  test("Selection container for parameters should show the different " +
    "parameter names", GUITest) {
    window.comboBox("ConfParamFr.selectionContainer.combo").requireItemCount(1)
    window.comboBox("ConfParamFr.selectionContainer.combo").contents() should be(Array("_w"))
  }

  test("From, To and Step entries must be numbers. " +
    "If From entry is not a number, then the user ", GUITest) {

  }
}
