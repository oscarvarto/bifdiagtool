package umich.gui

import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

import umich.parser.{ DynSys, DynSys1, DynSys2 }
import umich.simulation.Project

import scalaz.NonEmptyList
import scalaz.syntax.validation._

class ConfParamFrameTest1 extends FestiveFunSuite with Matchers {
  val dummyProject = Project("DummyProject", DynSys1.dynSys)

  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[ConfParamFrame]() {
        protected def executeInEDT() = new ConfParamFrame(dummyProject)
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
    window.comboBox("ConfParamFr.selectionContainer.combo").contents() should
      be(Array("_w"))
  }

  test("From, To and Step entries must be numbers. " +
    "If From entry is not a number, then the user should be told", GUITest) {
    window.button("ConfirmationOrSkipPanel.okButton").click()
    window.textBox("ConfParamFr.logArea").text() should be(
      """|From, To and Step entries must be numbers
         |empty String
         |empty String
         |empty String""".stripMargin)
  }

  test("This frame should NOT be followed by another ConfParamFrameTest",
    GUITest) {
      window.textBox("ConfParamFr.paramInfo.rangeFrom").setText("0.0")
      window.textBox("ConfParamFr.paramInfo.rangeTo").setText("1.0")
      window.textBox("ConfParamFr.paramInfo.step").setText("0.1")
      window.button("ConfirmationOrSkipPanel.okButton").click()
      window.textBox("ConfParamFr.logArea").text() should be("")
      pending // NOT TESTING PARAMETER COMBO SELECTION FIRST
    }
}

// class ConfParamFrameTest2 extends FestiveFunSuite
//     with Matchers with GivenWhenThen {
//   val dummyProject = Project("DummyProject", DynSys1.dynSys)

//   test("If force2d is true 3d Radio Button must be disabled", GUITest) {

//     val frame = GuiActionRunner.execute(
//       new GuiQuery[ConfParamFrame]() {
//         protected def executeInEDT() = new ConfParamFrame(dummyProject)
//       })
//     window = new FrameFixture(frame)
//     window.show()

//     window.radioButton("ConfParamFr.choose2dOr3dPanel.2dRadioButton")
//       .requireSelected()
//     window.radioButton("ConfParamFr.choose2dOr3dPanel.3dRadioButton")
//       .requireDisabled()
//     Then("Trying to select 3D Radio Button must produce IllegalStateException")
//     intercept[IllegalStateException] {
//       window.radioButton("ConfParamFr.choose2dOr3dPanel.3dRadioButton").check()
//     }
//   }

//   test("If force2d is false 3d Radio Button must be enabled", GUITest) {

//     val frame = GuiActionRunner.execute(
//       new GuiQuery[ConfParamFrame]() {
//         protected def executeInEDT() = new ConfParamFrame(dummyProject)
//       })
//     window = new FrameFixture(frame)
//     window.show()

//     window.radioButton("ConfParamFr.choose2dOr3dPanel.2dRadioButton")
//       .requireSelected()
//     window.radioButton("ConfParamFr.choose2dOr3dPanel.3dRadioButton")
//       .requireEnabled()

//     Then("3D Radio Button can be selected")
//     window.radioButton("ConfParamFr.choose2dOr3dPanel.3dRadioButton").check()
//   }
// }

class ConfParamFrameTest3 extends FestiveFunSuite with Matchers {
  val dummyProject = Project("DummyProject", DynSys2.dynSys)

  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[ConfParamFrame]() {
        protected def executeInEDT() = new ConfParamFrame(dummyProject, None)
      })
    window = new FrameFixture(frame)
    window.show()
    //setup
  }

  test("Selection container for parameters should show the different " +
    "parameter names", GUITest) {
    window.comboBox("ConfParamFr.selectionContainer.combo").requireItemCount(2)
    window.comboBox("ConfParamFr.selectionContainer.combo").contents() should be(Array("_a", "_b"))
  }

  test("From, To and Step entries must be numbers. " +
    "If From entry is not a number, then the user should be told", GUITest) {
    window.textBox("ConfParamFr.paramInfo.rangeFrom").setText("0.0")
    window.textBox("ConfParamFr.paramInfo.rangeTo").setText("1.0")
    window.textBox("ConfParamFr.paramInfo.step").setText("0.1")
    window.button("ConfirmationOrSkipPanel.okButton").click()
    window.textBox("ConfParamFr.logArea").text() should be("")
    pending // NOT TESTING PARAMETER COMBO SELECTION FIRST
  }
}
