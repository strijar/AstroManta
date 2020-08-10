package ru.strijar.astromanta.pc.gui;

import javax.swing.JMenu;

/**
 * Стандартное Menu с возможностью создания Menu и MenuItem содержащих ID
 */

@SuppressWarnings("serial")
public class AstroMenu extends JMenu {

	public AstroMenu(String label) {
		super(label);
	}
	
	public AstroMenuItem addItem(int id, String label) {
		AstroMenuItem res = new AstroMenuItem(id, label);
		
		add(res);
		
		return res;
	}

	public AstroMenu addMenu(String label) {
		AstroMenu res = new AstroMenu(label);
		
		add(res);
		
		return res;
	}
}
