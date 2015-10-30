package com.daanendaron.mp3;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UncloseablePopup extends JFrame {

	private static final long serialVersionUID = -3571221948359873876L;

	private JLabel lblMessage;

	public UncloseablePopup(int width, int height, String message) {
		setSize(width, height);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		// Zet de frame in het midden van het scherm
		setLocationRelativeTo(null);

		lblMessage = new JLabel(message);
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

		add(lblMessage);

		setVisible(true);
	}

	public void closePopup() {
		dispose();
	}

}
