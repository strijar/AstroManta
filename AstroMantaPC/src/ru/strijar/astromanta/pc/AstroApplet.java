package ru.strijar.astromanta.pc;

import javax.swing.JApplet;

@SuppressWarnings("serial")
public class AstroApplet extends JApplet {
	public void init() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			}
		});
	}
}
