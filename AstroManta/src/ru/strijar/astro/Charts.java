package ru.strijar.astro;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.strijar.astro.listener.CalcListener;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Набор карт.
 */

public class Charts extends Stored {
	private ArrayList<Chart>			charts = new ArrayList<Chart>();
	private HashMap<String, Division>	iDivision = new HashMap<String, Division>();
	private CalcListener				iAfterCalc;
	private Object						iAfterCalcArg;

    private SwissEph					iEph;
    private SwissLib					iLib;
		
    public Charts(SwissEph eph) {
    	iEph = eph;
    	iLib = new SwissLib();
    }
    
    /**
     * Получить массив карт
     *
     */
    public ArrayList<Chart> getCharts() {
    	return charts;
    }
    
	/**
	 * Скрыть/показать карты и точки карт заданного тэга
	 *
	 * @param on видимость
	 * @param tag приминить только к объектам тэга
	 */
	public void visibleTag(boolean on, int tag) {
		for (Chart item: charts)
			item.visibleTag(on, tag);
	}

	/**
	 * Скрыть/показать карты и точки карт заданного тэга
	 *
	 * @param on видимость
	 * @param tag приминить только к объектам тэга
	 */
	public void visibleTag(boolean on, String tag) {
		visibleTag(on, tag.hashCode());
	}

    /**
     * Удалить все карты и обработчики
     */
    public void clear() {
    	charts.clear();
    	iAfterCalc = null;
    	iAfterCalcArg = null;
    }

    /**
     * Удалить все деления зодиака
     */
    public void clearDivision() {
    	iDivision.clear();
    }
    
	private void addChart(Chart chart) {
		charts.add(chart);
	}
	
	/**
	 * Расчитать все карты
	 */
	public synchronized void calc() {
		for (Chart item: charts)
			if (item.getVisible())
				item.calc();
		
		if (iAfterCalc != null)
			iAfterCalc.onCalc(iAfterCalcArg);
	}

	/**
	 * Добавить одиночную карту (натал)
	 *
	 * @return объект карты
	 */
	public ChartNatal addNatal() {
		ChartNatal chart = new ChartNatal(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту затмений
	 *
	 * @return объект карты
	 */
	public ChartEclipse addEclipse() {
		ChartEclipse chart = new ChartEclipse(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту на фиксированные эклиптические положения точки
	 *
	 * @return объект карты
	 */
	public ChartIngress addIngress() {
		ChartIngress chart = new ChartIngress(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту на подвижную точку эклиптики
	 *
	 * @return объект карты
	 */
	public ChartRevolution addRevolution() {
		ChartRevolution chart = new ChartRevolution(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту на заданное угловое расстояние между точками
	 *
	 * @return объект карты
	 */
	public ChartSpotAngle addAngle() {
		ChartSpotAngle chart = new ChartSpotAngle(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту на средний момент и среднее место
	 *
	 * @return объект карты
	 */
	public ChartAverage addAverage() {
		ChartAverage chart = new ChartAverage(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту дирекций по эклиптике
	 *
	 * @return объект карты
	 */
	public ChartEclipticDirect addEclipticDirect() {
		ChartEclipticDirect chart = new ChartEclipticDirect(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту дирекций по экватору
	 *
	 * @return объект карты
	 */
	public ChartEquatorDirect addEquatorDirect() {
		ChartEquatorDirect chart = new ChartEquatorDirect(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту истиных дирекций
	 * @since 5.1
	 * @return объект карты
	 */
	public ChartTrueDirect addTrueDirect() {
		ChartTrueDirect chart = new ChartTrueDirect(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карту прогрессий
	 *
	 * @return объект карты
	 */
	public ChartProgress addProgress() {
		ChartProgress chart = new ChartProgress(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}

	/**
	 * Добавить карта на момент точного транзита
	 *
	 * @return объект карты
	 */
	public ChartTransit addTransit() {
		ChartTransit chart = new ChartTransit(iEph, iLib);
        
		addChart(chart);
		
		return chart;
	}
	
	/**
	 * Добавить деление зодиака
	 *
	 * @param имя деления
	 * @return объект деления
	 */
	public Division addDivision(String name) {
		Division div = new Division();
		
		iDivision.put(name, div);
		
		return div;
	}

	/**
	 * Получить деление зодиака по имени
	 *
	 * @param name имя
	 * @return объект деления
	 */
	public Division getDivision(String name) {
		return iDivision.get(name);
	}
	
	/**
	 * Установить обработчик автоматически запускаемый после расчета всех карт
	 *
	 * @param listener объект обработчика
	 * @param arg параметр передаваемый обработчику
	 */
	public void setAfterCalcListener(CalcListener listener, Object arg) {
		iAfterCalc = listener;
		iAfterCalcArg = arg;
	}

	/**
	 * Установить обработчик автоматически запускаемый после расчета всех карт
	 *
	 * @param listener объект обработчика
	 */
	public void setAfterCalcListener(CalcListener listener) {
		iAfterCalc = listener;
		iAfterCalcArg = null;
	}

	protected void store(JSONObject obj) {
		super.store(obj);
		
		JSONArray json_charts = new JSONArray();
		
		for (Chart item: charts) {
			JSONObject json_chart = new JSONObject();
			
			item.store(json_chart);
			json_charts.put(json_chart);
		}
		
		try {
			obj.put("charts", json_charts);
		} catch (JSONException e) {
		}
	}
	
	protected void load(JSONObject obj) {
		super.load(obj);

		int 		i = 0;
		JSONArray	json_charts = null;

		try {
			json_charts = obj.getJSONArray("charts");
		} catch (JSONException e) {
			return;
		}

		for (Chart item: charts) {
			try {
				JSONObject json_chart = json_charts.getJSONObject(i);
				item.load(json_chart);
			} catch (JSONException e) {
			}
				
			i++;
		}
	}
}
