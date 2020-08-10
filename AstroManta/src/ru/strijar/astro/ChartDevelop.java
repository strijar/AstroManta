package ru.strijar.astro;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Базовый класс для прогрессий, дирекций и  проч
 */

public class ChartDevelop extends ChartNatal {
	protected double 		k1 = 1.0;
	protected double 		k2 = 1.0;
	protected ChartNatal	parent;
	
	protected ChartDevelop(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	/**
	 * Получить делимое коэффициента
	 */
	public double getK1() {
		return k1;
	}

	/**
	 * Получить делитель коэффициента
	 */
	public double getK2() {
		return k2;
	}
	
	/**
	 * Установить делимое коэффициента
	 */
	public void setK1(double data) {
		k1 = data;
	}

	/**
	 * Установить делитель коэффициента
	 */
	public void setK2(double data) {
		k2 = data;
	}
	
	/**
	 * Установить родительскую карту (натал)
	 *
	 * @param chart объект карты
	 */
	public void setParent(ChartNatal chart) {
		parent = chart;
	}
}
