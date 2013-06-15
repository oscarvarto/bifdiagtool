package umich.gui

import org.scalatest.Matchers
import tagobjects.GUITest

import org.fest.swing.fixture.FrameFixture
import org.fest.swing.edt.{ GuiActionRunner, GuiQuery }

class MainFrameTest extends FestiveFunSuite with Matchers {
  override def beforeEach() {
    val frame = GuiActionRunner.execute(
      new GuiQuery[MainFrame]() {
        protected def executeInEDT() = new MainFrame()
      })
    window = new FrameFixture(frame)
    window.show()
  }
}
