package umich.guitesting;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
    
public class ConfPanel extends JPanel implements ActionListener {

    public ConfPanel(SFrame aMainFrame) {
	mainFrame = aMainFrame;
	
	// Add Button and dummy JLabels to get "Right alignment"
	setLayout(new GridLayout(1, NUM_COMPONENTS));
	this.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	
	okButton = new JButton("Ok");
	okButton.setActionCommand(OK_ACTION);
	okButton.addActionListener(this);
	okButton.setName("ConfPanel.okButton");
	add(okButton);
	
	nameTyped = new JLabel();
	nameTyped.setName("ConfPanel.nameTyped");
	add(nameTyped);
	for (int i = 0; i <  NUM_COMPONENTS - 2; ++i) {
	    add(new JLabel());
	}
    }

    public void actionPerformed(ActionEvent evt) {
	String command = evt.getActionCommand();
	if ( command.equals(OK_ACTION) ) {
	    mainFrame.checkName();
	}
    }

    public void setNameTyped(String aName) {
	nameTyped.setText(aName);
    }

    // ConfirmationPanel components
    private JLabel nameTyped;
    private JButton okButton;
    // Association to Main Frame's Application
    private SFrame mainFrame;

    // Constants
    private final static String OK_ACTION = "Ok";
    private final static int NUM_COMPONENTS = 4;
}
