package ru.strijar.astro;

import org.json.JSONException;
import org.json.JSONObject;

import swisseph.SweConst;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта Солнечных и Лунных затмений
 */

public class ChartEclipse extends ChartNatal {
	private double 		prev[] = new double[10];
	private double 		next[] = new double[10];
	private boolean		cached = false;

	private Date		calcMoment = new Date();
	private int			ageCalcMoment = 0;

	private boolean		lunar = false;
	private boolean		backward = true; 

	protected ChartEclipse(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	/**
	 * Задать направление поиска
	 *
	 * @param backward назад от заданного момента (true) или вперед (false)
	 */
	public void setBackward(boolean backward) {
		cached = false;
		this.backward = backward;
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
	 * Установить тип затмения
	 *
	 * @param lunar true если Лунное и false если Солнечное
	 */
	public void setLunar(boolean lunar) {
		cached = false; 
		this.lunar = lunar;
	}

	/**
	 * Получить тип затмения
	 *
	 * @return true если Лунное и false если Солнечное
	 */
	public boolean getLunar() {
		return lunar;
	}
	
	private void cacheCalc(double moment) {
		if (lunar) {
			eph.swe_lun_eclipse_when(moment, SweConst.SEFLG_SWIEPH, 0, prev, 1, null);
			eph.swe_lun_eclipse_when(moment, SweConst.SEFLG_SWIEPH, 0, next, 0, null);
		} else {
			eph.swe_sol_eclipse_when_glob(moment, SweConst.SEFLG_SWIEPH, 0, prev, 1, null);
			eph.swe_sol_eclipse_when_glob(moment, SweConst.SEFLG_SWIEPH, 0, next, 0, null);
		}
		cached = true;
	}
	
	/**
	 *  Получить момент найденного затмения
	 */
	public Date getCalcMoment() {
		if (ageCalcMoment != moment.age()) {
			double jd = moment.getJD();

			if (!cached || prev[0] > jd || next[0] < jd) {
				cacheCalc(jd);
			}

			ageCalcMoment = moment.age();
			calcMoment.setJD(backward ? prev[0] : next[0]);
		}

		return calcMoment;
	}
	
	protected void store(JSONObject obj) {
		super.store(obj);
		
		try {
			obj.put("lunar", lunar);
		} catch (JSONException e) {
		}

		try {
			obj.put("backward", backward);
		} catch (JSONException e) {
		}
	}
	
	protected void load(JSONObject obj) {
		super.load(obj);
		
		try {
			lunar = obj.getBoolean("lunar");
		} catch (JSONException e) {
		}
		
		try {
			backward = obj.getBoolean("backward");
		} catch (JSONException e) {
		}
	}

}
