package ru.strijar.astromanta.pc.gui;

import javax.swing.JCheckBox;

/**
 * Стандартный CheckBox с возможностью задания ID
 */

@SuppressWarnings("serial")
public class AstroCheckBox extends JCheckBox {
	private Object	iId;

	public AstroCheckBox(Object id, String label) {
		super(label);
		iId = id;
	}

	public Object getItemId() {
		return iId;
	}

}
