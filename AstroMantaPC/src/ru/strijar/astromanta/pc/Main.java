package ru.strijar.astromanta.pc;

import javax.swing.SwingUtilities;

import com.alee.laf.WebLookAndFeel;

public class Main {
	static private Sys	sys;
	
	public static void main(String[] args) {
		WebLookAndFeel.install();
		WebLookAndFeel.setDecorateDialogs(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sys = new Sys();
				sys.bshStart();
			}
		});
	}
}
