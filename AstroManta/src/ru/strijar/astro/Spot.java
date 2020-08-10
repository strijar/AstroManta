package ru.strijar.astro;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Базовый класс точки карты
 */

public class Spot extends Hiding {
	protected Coord		ecliptic = new Coord();
	protected Coord		equator = null;
	protected Coord		horizont = null;
	protected Coord		inMundo = null;

	protected Chart		chart;

	private String		name;
	private String		category;

	private int			nameHash;
	private Object		bag;

	private boolean		aspected = true;
	private boolean		aspecting = true;
		
	/**
	 * Установить имя
	 *
	 */
	public void setName(String name) {
		this.name = name;
		nameHash = name.hashCode();
	}
	
	/**
	 * Получить имя
	 *
	 */
	public String getName() {
		return name;
	}

	protected int hashName() {
		return nameHash;
	}

	/**
	 * Установить категорию
	 *
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * Получить категорию
	 *
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Установить хранилище дополнительных данных
	 *
	 */
	public void setBag(Object bag) {
		this.bag = bag;
	}

	/**
	 * Получить хранилище дополнительных данных
	 *
	 */
	public Object getBag() {
		return bag;
	}
	
	/**
	 * Расчитать
	 */
	public void calc() {
	}
	
	protected void setChart(Chart chart) {
		this.chart = chart;
	}
	
	/**
	 * Получить карту которой принадлежит точка
	 *
	 */
	public Chart getChart() {
		return chart;
	}
		
	public void setVisible(boolean on) {
		if (!on) {
			aspected = false;
			aspecting = false;
		}
		
		super.setVisible(on);
	}
	
	/**
	 * Аспектируется ли эта точка
	 *
	 */
	public boolean getAspected() {
		return aspected;
	}
	
	/**
	 * Установить аспектацию этой точки
	 *
	 */
	public void setAspected(boolean on) {
		if (on) super.setVisible(true);
		
		aspected = on;
	}

	/**
	 * Аспектирует ли эта точка
	 *
	 */
	public boolean getAspecting() {
		return aspecting;
	}
	
	/**
	 * Установить аспектирование этой точкой
	 *
	 */
	public void setAspecting(boolean on) {
		if (on) super.setVisible(true);

		aspecting = on;
	}
	
	/**
	 * Получить эклиптические координаты
	 *
	 */
	public Coord getEcliptic() {
		return ecliptic;
	}

	/**
	 * Получить экваториальные координаты
	 *
	 */
	public Coord getEquator() {
		if (equator == null) {
			equator = new Coord();
		}
		
		if (equator.age() != ecliptic.age()) {
			chart.toEquator(ecliptic, equator);
		}

		return equator;
	}

	/**
	 * Получить горизонтальные координаты
	 *
	 */
	public Coord getHorizont() {
		if (horizont == null) {
			horizont = new Coord();
		}
		
		if (horizont.age() != ecliptic.age()) {
			chart.toHorizont(ecliptic, horizont);
		}

		return horizont;
	}

	/**
	 * Получить координаты in mundo
	 * @since 5.1
	 */
	public Coord getInMundo() {
		if (inMundo == null) {
			inMundo = new Coord();
		}
		
		if (inMundo.age() != ecliptic.age()) {
			chart.toInMundo(ecliptic, inMundo);
		}

		return inMundo;
	}
	
	/**
	 * Получить угловое расстояние до указанной точки
	 *
	 */
	public double angle(Spot spot) {
		return ecliptic.angle(spot.getEcliptic());
	}

	/**
	 * Получить угловое расстояние до указанной долготы
	 * 
	 */
	public double angle(double lon) {
		return ecliptic.angle(lon);
	}

	protected void store(JSONObject obj) {
		super.store(obj);

		try {
			obj.put("aspected", aspected);
		} catch (JSONException e) {
		}

		try {
			obj.put("aspecting", aspecting);
		} catch (JSONException e) {
		}
	}
	
	protected void load(JSONObject obj) {
		super.load(obj);
		
		try {
			aspected = obj.getBoolean("aspected");
		} catch (JSONException e) {
		}

		try {
			aspecting = obj.getBoolean("aspecting");
		} catch (JSONException e) {
		}
	}

}
