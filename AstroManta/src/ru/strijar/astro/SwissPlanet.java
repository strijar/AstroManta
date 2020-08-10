package ru.strijar.astro;

import swisseph.DblObj;
import swisseph.SweConst;
import swisseph.SwissEph;

/**
 * Планета расчитываемая по Швейцарским эффемеридам
 */

public class SwissPlanet extends Spot {
	protected int			id;
	private SwissEph 		eph;
	private Date			moment;
	private double			res[] = new double[6];
	private StringBuffer	err = new StringBuffer();

	private void transTime(Place place, double jd, Date res, int flag) {
		DblObj 	time = new DblObj();
		
		eph.swe_rise_trans(
				jd, 
				id, 
				null,
				SweConst.SEFLG_SWIEPH, 
				flag | SweConst.SE_BIT_NO_REFRACTION,
				new double[]{ place.getLon(), place.getLat(), 0.0 },
				0.0,
				0.0,
				time,
				null
		);
		
		res.setJD(time.val);
	}
	
	protected SwissPlanet(SwissEph eph, int id, Date moment) {
		this.eph = eph;
		this.id = id;
		this.moment = moment;
	}
	
	protected void setMoment(Date moment) {
		this.moment = moment;
	}
	
	/**
	 * Расчитать на заданный момент 
	 *
	 * @param moment время (Юлианская дата)
	 * @param ecliptic объект для результата
	 */
	public void calc(double moment, Coord ecliptic) {
		eph.swe_calc(moment, id, SweConst.SEFLG_SPEED, res, err);
		
		ecliptic.setLon(res[0]);
		ecliptic.setLat(res[1]);
		
		ecliptic.setLonSpeed(res[3]);
		ecliptic.setLatSpeed(res[4]);
	}

	/**
	 * Расчитать на заданный момент
	 *
	 * @param moment время (Юлианская дата)
	 */
	public void calc(double moment) {
		calc(moment, ecliptic);
	}

	public void calc() {
		calc(moment.getJD(), ecliptic);
	}
	
	/**
	 * Найти время восхода
	 *
	 * @param place место расчета
	 * @param jd время расчета (Юлианская дата)
	 * @param res объект для результата
	 */
	public void calcRiseTime(Place place, double jd, Date res) {
		transTime(place, jd, res, SweConst.SE_CALC_RISE);
	}

	/**
	 * Найти время захода
	 *
	 * @param place место расчета
	 * @param jd время расчета (Юлианская дата)
	 * @param res объект для результата
	 */
	public void calcSetTime(Place place, double jd, Date res) {
		transTime(place, jd, res, SweConst.SE_CALC_SET);
	}

	/**
	 * Найти время восхода
	 *
	 * @param place место расчета
	 * @param jd время расчета (Юлианская дата)
	 */
	public Date calcRiseTime(Place place, double jd) {
		Date res = new Date();
		
		transTime(place, jd, res, SweConst.SE_CALC_RISE);
		
		return res;
	}

	/**
	 * Найти время захода
	 *
	 * @param place место расчета
	 * @param jd время расчета (Юлианская дата)
	 */
	public Date calcSetTime(Place place, double jd) {
		Date res = new Date();

		transTime(place, jd, res, SweConst.SE_CALC_SET);
		
		return res;
	}

	/**
	 * Создать транзит на момент изменения скорости
	 *
	 * @return объект транзита
	 */
	public Transit transitSpeed() {
		Transit transit = new Transit(eph);

		transit.speed(id);
		
		return transit;
	}

	/**
	 * Создать транзит на точное положение по долготе эклиптики
	 *
	 * @return объект транзита
	 */
	public Transit transitLon() {
		Transit transit = new Transit(eph);

		transit.lon(id);
		
		return transit;
	}

	/**
	 * Создать транзит на точное положение по широте эклиптики
	 *
	 * @return объект транзита
	 */
	public Transit transitLat() {
		Transit transit = new Transit(eph);

		transit.lat(id);
		
		return transit;
	}

	/**
	 * Создать транзит на точное угловое расстяние по долготе
	 *
	 * @param to вторая точка
	 * @return объект транзита
	 */
	public Transit transitAngle(SwissPlanet to) {
		Transit transit = new Transit(eph);

		transit.angle(id, to.id);
		
		return transit;
	}

	/**
	 * Создать транзит на пересечение экватора (по склонению)
	 *
	 * @return объект транзита
	 */
	public Transit transitDec() {
		Transit transit = new Transit(eph);

		transit.dec(id);
		
		return transit;
	}
	
}
