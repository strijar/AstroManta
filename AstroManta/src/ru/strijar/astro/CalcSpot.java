package ru.strijar.astro;

import ru.strijar.astro.listener.SpotCalcListener;

/**
 * Точка карты с задаваемым расчетом
 */

public class CalcSpot extends Spot {
	private SpotCalcListener	calcListener;
	private Object				args;
	
	public void calc() {
		if (calcListener != null)
			calcListener.Calc(this, args);
	}
	
	/**
	 * Задать объект алгоритма расчета
	 *
	 * @param listener расчитывающий объект
	 * @param args аргументы передаваемые в расчетный объект
	 */
	public void setCalcListener(SpotCalcListener listener, Object args) {
		calcListener = listener;
		this.args = args;
	}

	/**
	 * Задать объект алгоритма расчета
	 *
	 * @param listener расчитывающий объект
	 */
	public void setCalcListener(SpotCalcListener listener) {
		calcListener = listener;
		args = null;
	}
	
}
