package ru.strijar.astro;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Средняя карта Девисона. 
 * Расчитывается на средний момент времени между любым колличеством карт.
 */
public class ChartAverage extends ChartNatal {
	private ArrayList<ChartNatal>		charts = new ArrayList<ChartNatal>();

	protected ChartAverage(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
	}

	/**
	 * Получить расчитанный средний момент
	 */
	public Date getCalcMoment() {
		if (!charts.isEmpty()) {;
			double jd = 0;
		
			for (ChartNatal chart : charts) {
				jd += chart.getMoment().getJD();
			}
		
			moment.setJD(jd / charts.size());
		}
		
		return moment;
	}
	
	/**
	 * Получить расчитанное среднее место
	 */
	public Place getCalcPlace() {
		if (!charts.isEmpty()) {
			double lon = 0;
			double lat = 0;

			for (ChartNatal chart : charts) {
				lon += chart.getPlace().getLon();
				lat += chart.getPlace().getLat();
			}
		
			place.setLon(lon / charts.size());
			place.setLat(lat / charts.size());
		}

		return place;
	}
	
	/**
	 * Добавить карту в расчет
	 *
	 * @param chart объект карты
	 */
	public void add(ChartNatal chart) {
		charts.add(chart);
	}
	
	/**
	 * Удалить все карты из расчета
	 */
	public void clear() {
    	charts.clear();
	}
	
	protected void store(JSONObject obj) {
		try {
			obj.put("info", getInfo());
		} catch (JSONException e) {
		}
	}

	protected void load(JSONObject obj) {
		try {
			setInfo(obj.getString("info"));
		} catch (JSONException e) {
		}
	}
}
