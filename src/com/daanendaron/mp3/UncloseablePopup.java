package com.daanendaron.mp3;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UncloseablePopup extends JFrame {

	private static final long serialVersionUID = -3571221948359873876L;

	public UncloseablePopup(int width, int height, String message) {
		setSize(width, height);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		setLocationRelativeTo(null);

		setLayout(new BorderLayout(0, 10));

		JLabel label = new JLabel(message);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label, BorderLayout.CENTER);

		setVisible(true);
	}

	public void close() {
		dispose();
	}

}
