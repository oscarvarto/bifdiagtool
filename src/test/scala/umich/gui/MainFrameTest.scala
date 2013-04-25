package umich.gui

import org.scalatest.matchers.ShouldMatchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

class MainFrameTest extends FestiveFunSuite with ShouldMatchers {
  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[MainFrame]() {
        protected def executeInEDT() = new MainFrame()
      })
    window = new FrameFixture(frame)
    window.show()
  }

  test("Should open the New Project Dialog Window when selecting this option " +
    "from the New Project Menu", GUITest) {
    window.menuItem("ProjectMenu.newProjectItem").click()
    val npf = window.target.asInstanceOf[MainFrame].newProjFr
    npf.isVisible should be(true)
  }
}
