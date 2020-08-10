package ru.strijar.astro;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта на момент точного транзита (смена скорости движения, пересечения эклиптики или экватора итп)
 */

public class ChartTransit extends ChartNatal {
	protected Date			calcMoment = new Date();
	protected int			ageCalcMoment = 0;
	protected boolean		backward = true; 
	protected Transit		transit;

	protected ChartTransit(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}
	
	/**
	 * Задать транзит
	 *
	 * @param transit объект транзита
	 */
	public void setTransit(Transit transit) {
		this.transit = transit;
		ageCalcMoment = 0;
	}

	/**
	 * Задать направление поиска
	 *
	 * @param backward true если назад и false если вперед
	 */
	public void setBackward(boolean backward) {
		this.backward = backward;
		ageCalcMoment = 0;
	}

	/**
	 * Получить направление поиска
	 *
	 * @return true если назад и false если вперед
	 */
	public boolean getBackward() {
		return backward;
	}

	/**
	 *  Получить момент найденного точного транзита
	 */
	public Date getCalcMoment() {
		if (ageCalcMoment != moment.age()) {
			transit.next(moment.getJD(), backward, calcMoment);

			ageCalcMoment = moment.age();
		}

		return calcMoment;
	}
	
}
