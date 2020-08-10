package ru.strijar.astro;

import java.util.ArrayList;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта на фиксированные эклиптические положения точки (ингрессии, итп)
 */

public class ChartIngress extends ChartSpotLon {
	private ArrayList<Double>	to = new ArrayList<Double>();

	protected ChartIngress(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	/**
	 * Добавить искомое положение
	 *
	 * @param lon долгота на эктиптике (в градусах)
	 */
	public void addTo(double lon) {
		to.add(lon);
	}
	
	protected double fixedLon() {
		movedSpot.calc(getMoment().getJD());
		
		int		i = 0;
		double 	pos = movedSpot.getEcliptic().getLon();
		int		n = to.size();

		for (i = 0; i < n; i++) {
			if (to.get(i) > pos)
				break;
		}

		if (backward) i--;
		if (i == n) i = 0;

		return to.get(i);
	}

}
