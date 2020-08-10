package ru.strijar.astromanta.pc.gui;

import javax.swing.JMenuBar;

/**
 * Стандартный MenuBar с возможность создания Menu и MenuItem содержащих ID
 */

@SuppressWarnings("serial")
public class AstroMenuBar extends JMenuBar {
	public AstroMenu addMenu(String label) {
		AstroMenu res = new AstroMenu(label);
		
		add(res);

		return res;
	}

	public AstroMenuItem addItem(int id, String label) {
		AstroMenuItem res = new AstroMenuItem(id, label);
		
		add(res);
		
		return res;
	}

}
