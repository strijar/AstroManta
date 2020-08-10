package ru.strijar.astromanta.pc.gui;

import javax.swing.JMenuItem;

/**
 * Стандартный MenuItem меню содержащий ID
 */

@SuppressWarnings("serial")
public class AstroMenuItem extends JMenuItem {
	private Object	iId;

	public AstroMenuItem(Object id, String label) {
		super(label);
		iId = id;
	}
	
	public Object getItemId() {
		return iId;
	}
}
