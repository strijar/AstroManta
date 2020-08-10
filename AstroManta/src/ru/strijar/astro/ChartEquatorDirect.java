package ru.strijar.astro;

import java.util.Iterator;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта дирекций по экватору
 */

public class ChartEquatorDirect extends ChartDevelop {

	protected ChartEquatorDirect(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	public void calc() {
		double k = k1 / k2;
		double delta = (getCalcMoment().getJD() - parent.getCalcMoment().getJD()) * k1 / 365.2422 / k2;
		
		Iterator<Spot> to = spots.iterator();
		
		while (to.hasNext()) {
			Spot to_item = to.next();
			
			if (to_item.getVisible()) {
				Spot from_item = parent.getSpot(to_item.getName());

				if (!from_item.getVisible())
					from_item.calc();

				parent.shiftEquator(from_item.ecliptic, to_item.ecliptic, delta);
				to_item.ecliptic.setLonSpeed(k);
			}
		}
	}

}
