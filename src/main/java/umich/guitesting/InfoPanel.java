package umich.guitesting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import fj.data.Option;

public class InfoPanel extends JPanel {
    public InfoPanel(SFrame aMainFrame) {
	mainFrame = aMainFrame;
	
	setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 0;
	c.gridy = 0;
	nameLabel = new JLabel("Name");
	add(nameLabel, c);

	c.gridx = 1;
	c.gridy = 0;
	nameTextField = new JTextField(MAX_NAME_LENGTH);
	nameTextField.setName("InfoPanel.nameTextField");
	add(nameTextField, c);

	c.gridx = 1;
	c.gridy = 1;
	c.gridwidth = 2;
	c.gridheight = 1;
	nameShouldNotBeEmpty = new JLabel(NAME_ERROR);
	nameShouldNotBeEmpty.setForeground(Color.RED);
	nameShouldNotBeEmpty.setVisible(false);
	add(nameShouldNotBeEmpty, c);
	setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public Option<String> name() {
	return Option.fromString(nameTextField.getText());
    }

    public void showNameShouldNotBeEmpty(boolean b) {
	nameShouldNotBeEmpty.setVisible(b);
    }
    
    // InformationPanel components 
    private JLabel nameLabel; 
    private JTextField nameTextField;
    private JLabel nameShouldNotBeEmpty;
    // Association to Main Frame's Application
    private SFrame mainFrame;

    // Constants
    private static final String NAME_ERROR = "Name should not be empty";
    private static final int MAX_NAME_LENGTH = 20;

    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 70;
}
