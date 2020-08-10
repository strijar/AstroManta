package ru.strijar.astro;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта на подвижную точку эклиптики (возвращения, итп)
 */

public class ChartRevolution extends ChartSpotLon {
	private Coord	to;
	private int		ageTo = 0;

	protected ChartRevolution(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	/**
	 * Задать положение на эктиптике
	 *
	 * @param coord объект положения
	 */
	public void setTo(Coord coord) {
		to = coord;
		ageTo = coord.age();
	}

	protected double fixedLon() {
		return to.getLon();
	}

	/**
	 * Получить найденный момент положения
	 */
	public Date getCalcMoment() {
		if (to != null) {
			if (ageCalcMoment != moment.age()) {
				search();
				ageCalcMoment = moment.age();
			} else if (ageTo != to.age()) {
				search();
				ageTo = to.age();
			}
		}

		return calcMoment;
	}

}
