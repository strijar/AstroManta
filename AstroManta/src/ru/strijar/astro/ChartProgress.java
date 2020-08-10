package ru.strijar.astro;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта прогрессий
 * K1/K2 = день прогрессии/год жизни 
 */

public class ChartProgress extends ChartDevelop {
	private Date	calcMoment = new Date();
	
	protected ChartProgress(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	public Date getCalcMoment() {
		double jd0 = parent.getMoment().getJD();
		double jd = jd0 + (moment.getJD() - jd0) * k1 / 365.2422 / k2;
		
		calcMoment.setJD(jd);
		
		return calcMoment;
	}
	
}
