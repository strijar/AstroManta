package ru.strijar.astro;

import swisseph.SweConst;
import swisseph.SwissEph;
import swisseph.TCPlanet;
import swisseph.TCPlanetPlanet;
import swisseph.TransitCalculator;

/**
 * Транзит
 */

public class Transit {
	protected TransitCalculator tc;
	private SwissEph 			eph;

	protected Transit(SwissEph eph) {
		this.eph = eph;
	}
	
	protected void speed(int spot) {
		tc = new TCPlanet(
			eph, 
			spot, 
			SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRANSIT_LONGITUDE | SweConst.SEFLG_TRANSIT_SPEED, 
			0
		);
	}

	protected void lon(int spot) {
		tc = new TCPlanet(
			eph, 
			spot, 
			SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRANSIT_LONGITUDE, 
			0
		);
	}

	protected void lat(int spot) {
		tc = new TCPlanet(
			eph, 
			spot, 
			SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRANSIT_LATITUDE, 
			0
		);
	}

	protected void dec(int spot) {
		tc = new TCPlanet(
			eph, 
			spot, 
			SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRANSIT_LATITUDE | SweConst.SEFLG_EQUATORIAL, 
			0
		);
	}

	protected void angle(int spot1, int spot2) {
		tc = new TCPlanetPlanet(
			eph, 
			spot2, spot1,
			SweConst.SEFLG_SWIEPH | SweConst.SEFLG_TRANSIT_LONGITUDE, 
			0
		);
	}

	/**
	 * Найти следующий момент точного транзита
	 *
	 * @param jd момент в Юлианской дате
	 * @param back направление поиска
	 * @return расчитанный момент в Юлианской дате
	 */
	public double nextJD(double jd, boolean back) {
		return eph.getTransitET(tc, jd, back);
	}

	/**
	 * Найти следующий момент точного транзита
	 *
	 * @param jd момент в Юлианской дате
	 * @param back направление поиска
	 * @param res объект для результата
	 */
	public void next(double jd, boolean back, Date res) {
		res.setJD(nextJD(jd, back));
	}

	/**
	 * Найти следующий момент точного транзита
	 *
	 * @param jd момент в Юлианской дате
	 * @param back направление поиска
	 * @return объект с моментом
	 */
	public Date next(double jd, boolean back) {
		Date res = new Date();
		
		res.setJD(nextJD(jd, back));

		return res;
	}

	/**
	 * Установить значения искомого транзита
	 *
	 * @param data данные
	 */
	public void setValue(double data) {
		tc.setOffset(data);
	}

	/**
	 * Получить значение найденного транзита
	 *
	 * @return данные
	 */
	public double getValue() {
		return tc.getOffset();
	}
}
