package ru.strijar.astro;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Карта на заданное угловое расстояние между точками (фазы Луны итп)
 */

public class ChartSpotAngle extends ChartNatal {
	protected Date				calcMoment = new Date();
	protected int				ageCalcMoment = 0;
	protected boolean			backward = true; 
	private ArrayList<Double>	to = new ArrayList<Double>();
	private Transit				transit;
	private SwissPlanet			spot1, spot2;

	protected ChartSpotAngle(SwissEph Eph, SwissLib Lib) {
		super(Eph, Lib);
		transit = new Transit(Eph);
	}

	/**
	 * Задать направление поиска
	 *
	 * @param backward true если назад и false если вперед
	 */
	public void setBackward(boolean backward) {
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
	 * Задать подвижные точки
	 *
	 * @param spot1 первая точки
	 * @param spot2 вторая точка
	 */
	public void moved(SwissPlanet spot1, SwissPlanet spot2) {
		this.spot1 = spot1;
		this.spot2 = spot2;
		transit.angle(spot1.id, spot2.id);
	}

	/**
	 * Добавить искомое угловое расстояние
	 *
	 * @param lon угловое расстояние (в градусах) 
	 */
	public void addTo(double lon) {
		to.add(lon);
	}

	private double selectTo(double pos) {
		int		n = to.size();
		int		i;

		for (i = 0; i < n; i++) {
			if (to.get(i) > pos)
				break;
		}

		if (backward) i--;
		if (i == n) i = 0;

		return to.get(i);
	}

	protected void search() {
		double jd = moment.getJD();

		if (spot1 == null || spot2 == null) {
			calcMoment.setJD(jd);
			return;
		}
		
		spot1.calc(jd);
		spot2.calc(jd);

		double angle = spot1.angle(spot2);
		
		if (angle < 0) angle += 360;
		
		angle = selectTo(angle);

		transit.setValue(angle);
		transit.next(jd, backward, calcMoment);
	}

	/**
	 *  Получить момент найденного углового расстояния
	 */
	public Date getCalcMoment() {
		if (ageCalcMoment != moment.age()) {
			search();
			ageCalcMoment = moment.age();
		}

		return calcMoment;
	}

	protected void store(JSONObject obj) {
		super.store(obj);
		
		try {
			obj.put("backward", backward);
		} catch (JSONException e) {
		}
	}

	protected void load(JSONObject obj) {
		super.load(obj);
		
		try {
			backward = obj.getBoolean("backward");
		} catch (JSONException e) {
		}
	}

}
