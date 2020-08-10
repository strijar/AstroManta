package ru.strijar.astro;

import java.util.Iterator;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта истиных дирекций
 */

public class ChartTrueDirect extends ChartDevelop {
	private Date	calcMoment = new Date();

	protected ChartTrueDirect(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	public Date getCalcMoment() {
		double jd0 = parent.getMoment().getJD();
		double delta = (moment.getJD() - jd0) * k1 / 365.2422 / k2;
		double jd = jd0 + delta / 359.017043904;
		
		calcMoment.setJD(jd);
		
		return calcMoment;
	}

	public void calc() {
		double k = (k1 / k2) / 365.2422;
		double delta = (getMoment().getJD() - parent.getMoment().getJD()) * k1 / 365.2422 / k2;
		
		Iterator<Spot> to = spots.iterator();
		
		while (to.hasNext()) {
			Spot to_item = to.next();
			
			if (to_item.getVisible()) {
				Spot from_item = parent.getSpot(to_item.getName());

				if (!from_item.getVisible())
					from_item.calc();

				shiftEquator(from_item.ecliptic, to_item.ecliptic, delta);
				to_item.ecliptic.setLonSpeed(k * from_item.ecliptic.lonSpeed);
			}
		}
	}

}
