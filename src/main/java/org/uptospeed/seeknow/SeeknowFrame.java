package org.uptospeed.seeknow;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import static java.awt.event.KeyEvent.*;
import static java.awt.event.WindowEvent.*;

/** image display dummy client */
class SeeknowFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 7468239430795306535L;

	SeeknowFrame(byte[] imageData, int x, int y) {
		addKeyListener(this);
		setContentPane(new JLabel(new ImageIcon(imageData)));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setUndecorated(true);
		// com.sun.awt.AWTUtilities.setWindowOpaque(this, false);

		setLocation(x, y);
		pack();
		setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) { if (e.getKeyCode() == VK_ESCAPE) { close(); } }

	void close() {
		WindowEvent we = new WindowEvent(this, WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(we);
	}
}
