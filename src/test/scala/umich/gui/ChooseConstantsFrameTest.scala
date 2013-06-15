package umich.gui

import org.scalatest.Matchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

import umich.parser.{ DynSys, DynSys2 }
import umich.simulation.Project

class ChooseConstantsFrameTest1 extends FestiveFunSuite with Matchers {
  val dummyProject = Project("DummyProject", DynSys2.dynSys)

  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[ChooseConstantsFrame]() {
        protected def executeInEDT() = new ChooseConstantsFrame(dummyProject)
      })
    window = new FrameFixture(frame)
    window.show()
    //setup
  }

  test("ComboBox with names of parameters should not be editable", GUITest) {
    window.comboBox("ChooseConstsFr.selectionContainer.combo")
      .requireNotEditable()
  }

  test("Selection container for parameters should show available " +
    "variables and parameter names", GUITest) {
    window.comboBox("ChooseConstsFr.selectionContainer.combo")
      .requireItemCount(2)
    window.comboBox("ChooseConstsFr.selectionContainer.combo").contents() should
      be(Array("_a", "_b"))
  }

  test("Assigned value must be a number" +
    "Otherwise the user should be told", GUITest) {
    window.comboBox("ChooseConstsFr.selectionContainer.combo")
      .selectItem("_b")
    window.textBox("ChooseConstsFr.selectionContainer.constValueJText").setText("0.1")
    window.button("ConfirmationPanel.okButton").click()
    window.textBox("ChooseConstsFr.logArea").text() should be("")
  }
}

class ChooseConstantsFrameTest2 extends FestiveFunSuite with Matchers {
  import shapeless._
  import umich.parser.Names._
  import collection.immutable.TreeMap

  val alreadyChosen =
    Map.empty[VarName, Double] :: Map(ParamName("_a") → 0.5) :: HNil
  val dummyProject = Project("DummyProject",
    DynSys2.dynSys, TreeMap.empty, alreadyChosen)

  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[ChooseConstantsFrame]() {
        protected def executeInEDT() = new ChooseConstantsFrame(dummyProject)
      })
    window = new FrameFixture(frame)
    window.show()
    //setup
  }

  test("Selection container for parameters should show available " +
    "variables and parameter names", GUITest) {
    window.comboBox("ChooseConstsFr.selectionContainer.combo")
      .requireItemCount(1)
    window.comboBox("ChooseConstsFr.selectionContainer.combo").contents() should
      be(Array("_b"))
  }
}
