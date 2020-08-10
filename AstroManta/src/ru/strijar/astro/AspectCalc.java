package ru.strijar.astro;

import ru.strijar.astro.listener.AspectCalcListener;

/**
 * Аспект вычисляемый по произвольному алгоритму
 *
 */

public class AspectCalc extends Aspect {
	private AspectCalcListener	listener;
	
	protected AspectCalc(AspectCalcListener listener) {
		this.listener = listener;
	}
	
	protected String calc(Spot spot1, Spot spot2) {
		return listener.calc(spot1, spot2);
	}
}
