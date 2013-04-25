package umich.gui

import org.scalatest.{
  BeforeAndAfterAll,
  BeforeAndAfterEach,
  FunSuite
}
import org.fest.swing.edt.FailOnThreadViolationRepaintManager
import org.fest.swing.fixture.FrameFixture

import umich.gui.tagobjects.GUITest

class FestiveFunSuite extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll {

  protected var window: FrameFixture = _

  override def beforeAll() {
    FailOnThreadViolationRepaintManager.install();
    //setUpOnce()
  }

  override def afterEach() {
    window.cleanUp()
    //tearDown()
  }
}

