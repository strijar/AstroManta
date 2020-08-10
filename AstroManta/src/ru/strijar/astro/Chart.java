package ru.strijar.astro;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import ru.strijar.astro.listener.CalcListener;

/**
 * Базовый класс карты
 */

public class Chart extends Hiding {
	private HashMap<String, Object>	vars = new HashMap<String, Object>();
	protected ArrayList<Spot>		spots = new ArrayList<Spot>();
	private String					info;
	private CalcListener			iAfterCalc;
	private Object					iAfterCalcArg;

	protected Chart() {
	}
	
	protected void onAfterCalc() {
		if (iAfterCalc != null)
			iAfterCalc.onCalc(iAfterCalcArg);
	}
	
	/**
	 * Расчитать карту
	 */
	public void calc() {
		for (Spot spot: spots) {
			if (spot != null && spot.getVisible()) { 
				spot.calc();
			}
		}
		onAfterCalc();
	}
	
	/**
	 * Удалить все дополнительные переменные
	 */
	public void clearVars() {
		vars.clear();
	}
	
	/**
	 * Получить массив дополнительных переменных
	 *
	 * @return массив переменных
	 */
	public HashMap<String, Object> getVars() {
		return vars;
	}

	/**
	 * Задать массив дополнительных переменных
	 *
	 * @param vars массив переменных
	 */
	public void setVars(HashMap<String, Object> vars) {
		this.vars = vars;
	}

	/**
	 * Задать значение дополнительной переменной
	 *
	 * @param name имя переменной
	 * @param val значение
	 */
	public void setVar(String name, Object val) {
		vars.put(name, val);
	}

	/**
	 * Получить значение дополнительной переменной
	 *
	 * @param name имя переменной
	 * @return значение переменной или null если не существует
	 */
	public Object getVar(String name) {
		return vars.get(name);
	}

	/**
	 * Получить массив всех точек карты
	 *
	 * @return массив точек
	 */
	public ArrayList<Spot> getSpots() {
		return spots;
	}
	
	/**
	 * Добавить точку
	 *
	 * @param spot объект точки
	 */
	public void addSpot(Spot spot) {
		spot.setChart(this);
		spots.add(spot);
	}

	/**
	 * Добавить точку с задаваемым расчетом
	 *
	 * @return объект точки
	 */
	public CalcSpot addCalcSpot() {
		CalcSpot spot = new CalcSpot();
		
		addSpot(spot);
		
		return spot;
	}

	/**
	 * Установить обработчик запускаемый автоматически после расчета карты
	 *
	 * @param listener объект обработчика
	 * @param arg параметр передаваемый в обработчик
	 */
	public void setAfterCalcListener(CalcListener listener, Object arg) {
		iAfterCalc = listener;
		iAfterCalcArg = arg;
	}

	/**
	 * Установить обработчик запускаемый автоматически после расчета карты
	 *
	 * @param listener объект обработчика
	 */
	public void setAfterCalcListener(CalcListener listener) {
		iAfterCalc = listener;
		iAfterCalcArg = null;
	}

	public void visibleTag(boolean data, int tag) {
		super.visibleTag(data, tag);

		for (Spot item: spots)
			item.visibleTag(data, tag);
	}

	/**
	 * Получить точку карты по ее имени
	 *
	 * @param name имя точки
	 * @return объект точки или null если не существует
	 */
	public Spot getSpot(String name) {
		int hash = name.hashCode();
		
		for (Spot item: spots)
			if (item.hashName() == hash)
				return item;
		
		return null;
	}
	
	/**
	 * Задать информацию о карты
	 *
	 * @param info информация
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * Получить информацию карты
	 *
	 * @return информация
	 */
	public String getInfo() {
		return info;
	}
		
	/**
	 * Пересчитать эклиптические координаты в экваториальные
	 *
	 * @param ecliptic эклиптические координаты
	 * @param equator объект для получения результата
	 */
	public void toEquator(Coord ecliptic, Coord equator) {
		
	}

	/**
	 * Пересчитать эклиптические координаты в горизонтальные
	 *
	 * @param ecliptic эклиптические координаты
	 * @param horizont объект для получения результата
	 */
	public void toHorizont(Coord ecliptic, Coord horizont) {
		
	}

	/**
	 * Пересчитать эклиптические координаты в in mundo
	 *
	 * @param ecliptic эклиптические координаты
	 * @param inMundo объект для получения результата
	 */
	public void toInMundo(Coord ecliptic, Coord inMundo) {
		
	}

	/**
	 * Установить видимость
	 *
	 */
	public void setVisible(boolean on) {
		super.setVisible(on);
		
		if (on) calc();
	}

	protected void store(JSONObject obj) {
		try {
			obj.put("info", info);
		} catch (JSONException e) {
		}
		
		JSONObject json_spots = new JSONObject();
		
		for (Spot item: spots) {
			JSONObject spot = new JSONObject();
			
			item.store(spot);

			try {
				json_spots.put(item.getName(), spot);
			} catch (JSONException e) {
			}
		}
		
		try {
			obj.put("spots", json_spots);
		} catch (JSONException e) {
		}
	}

	protected void load(JSONObject obj) {
		try {
			setInfo(obj.getString("info"));
		} catch (JSONException e) {
		}

		try {
			JSONObject json_spots = obj.getJSONObject("spots");

			for (Spot item: spots) {
				JSONObject spot = json_spots.getJSONObject(item.getName());
				
				if (spot != null)
					item.load(spot);
			}
		} catch (JSONException e) {
		}
	}
}
