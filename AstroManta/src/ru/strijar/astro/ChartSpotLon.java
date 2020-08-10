package ru.strijar.astro;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Базовый класс карт на заданное положение точки
 */

public class ChartSpotLon extends ChartNatal {
	protected Date			calcMoment = new Date();
	protected int			ageCalcMoment = 0;
	protected boolean		backward = true; 
	protected Transit		transit;
	protected SwissPlanet	movedSpot;

	protected ChartSpotLon(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);

		transit = new Transit(Eph);
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
	 */
	public boolean getBackward() {
		return backward;
	}
	
	/**
	 * Задать двигаемую точку
	 *
	 * @param spot объект точки
	 */
	public void moved(SwissPlanet spot) {
		movedSpot = spot;
		transit.lon(spot.id);
		ageCalcMoment = 0;
	}

	protected double fixedLon() {
		return 0;
	}
	
	protected void search() {
		double	jd = moment.getJD();
		double	lon = fixedLon();

		transit.setValue(lon);
		transit.next(jd, backward, calcMoment);
	}

	/**
	 * Получить найденный момент
	 */
	public Date getCalcMoment() {
		if (ageCalcMoment != moment.age()) {
			search();
			ageCalcMoment = moment.age();
		}

		return calcMoment;
	}

}
