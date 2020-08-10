package ru.strijar.astro;

import java.util.Iterator;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта дирекций по эклиптике
 */

public class ChartEclipticDirect extends ChartDevelop {

	protected ChartEclipticDirect(SwissEph Eph, SwissLib Lib) {
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
				
				to_item.ecliptic.setLon(from_item.ecliptic.getLon() + delta);
				to_item.ecliptic.setLonSpeed(k);
			}
		}
	}

}
