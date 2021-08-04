package eu.michelegiorgio.sysipscan.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogNewLocation extends JDialog {

	private JTextField textFieldLocation;
	private String locationValue;

	public DialogNewLocation(JFrame owner, String title, boolean modal) {
		super(owner, title, modal);

		this.setSize(400, 80);
		this.setLocationRelativeTo(null);

		Container container = this.getContentPane();
		container.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel labelLocation = new JLabel("Location");
		textFieldLocation = new JTextField();
		textFieldLocation.setColumns(22);
		JButton buttonConfirm = new JButton("Confirm");
		buttonConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				locationValue = textFieldLocation.getText();
				Component component = (Component) actionEvent.getSource();
				JDialog dialog = (JDialog) SwingUtilities.getRoot(component);
				dialog.dispose();
			}
		});

		container.add(labelLocation);
		container.add(textFieldLocation);
		container.add(buttonConfirm);
		init();
	}

	public void init() {
		locationValue = "";
		textFieldLocation.setText("");
	}

	public String getValue() {
		return locationValue;
	}
}
