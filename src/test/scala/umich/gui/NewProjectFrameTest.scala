package umich.gui

import org.scalatest.Matchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

class NewProjectFrameTest extends FestiveFunSuite with Matchers {
  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[NewProjectFrame]() {
        protected def executeInEDT() = new NewProjectFrame()
      })
    window = new FrameFixture(frame)
    window.show()
    //setup
  }

  test("Should get some project name when a non-empty name is typed", GUITest) {
    window.textBox("InformationPanel.nameTextField").enterText("Bif1")
    window.button("ConfirmationPanel.okButton").click()
    val fr = window.target.asInstanceOf[NewProjectFrame]
    fr.getProjectName should be(Some("Bif1"))
  }

  test("Should get None for an empty name", GUITest) {
    window.textBox("InformationPanel.nameTextField").enterText("")
    window.button("ConfirmationPanel.okButton").click()
    val fr = window.target.asInstanceOf[NewProjectFrame]
    fr.getProjectName should be(None)
  }
}
