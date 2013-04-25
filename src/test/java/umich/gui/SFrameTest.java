package umich.guitesting;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Ignore;
import org.junit.Test;
 
public class SFrameTest extends FestSwingJUnitTestCase {
 
  private FrameFixture window;
 
  protected void onSetUp() {
    SFrame frame = GuiActionRunner.execute(new GuiQuery<SFrame>() {
        protected SFrame executeInEDT() {
          return new SFrame(); 
        }
    });
    // IMPORTANT: note the call to 'robot()'
    // we must use the Robot from FestSwingTestngTestCase
    window = new FrameFixture(robot(), frame);
    window.show(); // shows the frame to test
  }
 
  @Ignore("Just to learn to use Fest-Swing. Real gui-tests are written with Scala, ScalaTest and Fest-swing")
  @Test 
  public void shouldCopyTextInLabelWhenClickingButton() {
    window.textBox("InfoPanel.nameTextField").enterText("Oscar");
    window.button("ConfPanel.okButton").click();
    window.label("ConfPanel.nameTyped").requireText("Oscar");
  }
}
