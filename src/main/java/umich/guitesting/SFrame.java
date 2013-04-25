package umich.guitesting;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.Container;

import fj.data.Option;

public class SFrame extends JFrame {    
    public SFrame() {
	infoPanel = new InfoPanel(this);
	okPanel = new ConfPanel(this);

	Container pane = this.getContentPane();
	pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
	pane.add(infoPanel);
	pane.add(okPanel);
	
    }

    public void checkName() {
	Option<String> optionName = infoPanel.name();
	if (optionName.isSome()) {
	    String aName = optionName.some();
	    infoPanel.showNameShouldNotBeEmpty(false);
	    okPanel.setNameTyped(aName);  
	} else {
	    okPanel.setNameTyped("");
	    infoPanel.showNameShouldNotBeEmpty(true);
	}
    }
    
    private InfoPanel infoPanel;
    private ConfPanel okPanel;
}
