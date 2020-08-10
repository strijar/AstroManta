package ru.strijar.astro;

import java.util.ArrayList;
import java.util.Iterator;

import ru.strijar.astro.listener.AspectCalcListener;

/**
 * Таблица аспектов
 */

public class AspectTable {
	private	ArrayList<AspectData>	items = new ArrayList<AspectData>();
	private ArrayList<Aspect>		aspects = new ArrayList<Aspect>();
	
	private class AspectIterator implements Iterator<AspectData> {
		private Iterator<AspectData>	iterator;
		private AspectData				item;
		
		AspectIterator() {
			iterator = items.iterator();
		}

		public boolean hasNext() {
			while (iterator.hasNext()) {
				item = iterator.next();

				if (item.getInfo() != null)
					return true;
			}

			return false;
		}

		public AspectData next() {
			return item;
		}

		public void remove() {
		}
		
	}
	
	/**
	 * Очистить все аспекты
	 */
	public void clear() {
		items.clear();
		aspects.clear();
	}
	
	/**
	 * Получить расчитанные данные аспектов
	 * 
	 * @deprecated начиная с 5.1, используйте {@link #iterator()}
	 * @return массив данных
	 */
	public ArrayList<AspectData> getItems() {
		return items;
	}

	/**
	 * Получить итератор по данным аспектов
	 * 
	 * @since 5.1
	 * @return итератор
	 */
	public Iterator<AspectData> iterator() {
		return new AspectIterator();
	}
	
	/**
	 * Получить аспекты загруженные в таблицу
	 *
	 * @return массив аспектов
	 */
	public ArrayList<Aspect> getAspects() {
		return aspects;
	}
	
	/**
	 * Добавить аспекты по эклиптической долготе
	 *
	 * @return объект аспектов
	 */
	public AspectEcliptic addEcliptic() {
		AspectEcliptic aspect = new AspectEcliptic();
		
		aspects.add(aspect);
		
		return aspect;
	}

	/**
	 * Добавить аспекты по эклиптической долготе, по знакам без учета орбиса
	 *
	 * @return объект аспектов
	 */
	public AspectSign addSign() {
		AspectSign aspect = new AspectSign();
		
		aspects.add(aspect);
		
		return aspect;
	}

	/**
	 * Добавить аспекты по склонению
	 *
	 * @param orbis орбис аспектов (в градусах)
	 * @return объект аспектов
	 */
	public AspectEquator addEquator(double orbis) {
		AspectEquator aspect = new AspectEquator(orbis);
		
		aspects.add(aspect);
		
		return aspect;
	}

	/**
	 * Добавить аспекты по приему
	 *
	 * @return объект аспектов
	 */
	public AspectReception addReception() {
		AspectReception aspect = new AspectReception();
		
		aspects.add(aspect);
		
		return aspect;
	}

	/**
	 * Добавить аспект вычисляемый по произвольному алгоритму
	 * 
	 * @since 5.1
	 * @param listener объект обработчика для вычисления
	 * @return объект аспекта
	 */
	public AspectCalc addCalc(AspectCalcListener listener) {
		AspectCalc aspect = new AspectCalc(listener);

		aspects.add(aspect);
		
		return aspect;
	}

	/**
	 * Задать аспектацию точек внутри одной карты
	 *
	 * @param chart аспектируемая карта
	 */
	public void aspected(Chart chart) {
		items.clear();
		
		int n = chart.getSpots().size();
		
		for (int x = 0; x < n; x++) {
			Spot spot1 = chart.getSpots().get(x); 
	
			for (int y = x+1; y < n; y++) {
				Spot spot2 = chart.getSpots().get(y);

				for (Aspect aspect: aspects) {
					AspectData item = aspect.data(spot1, spot2);
					
					if (item != null) {
						item.setAspect(aspect);

						items.add(item);
					}
				}
			}
		}
	}

	/**
	 * Задать аспектацию между точками двух карт
	 *
	 * @param chart1 первая карта
	 * @param chart2 вторая карта
	 */
	public void aspected(Chart chart1, Chart chart2) {
		items.clear();
		
		for (Spot spot1: chart1.getSpots()) {
			for (Spot spot2: chart2.getSpots()) {
				for (Aspect aspect: aspects) {
					AspectData item = aspect.data(spot1, spot2);
					
					if (item != null) {
						item.setAspect(aspect);

						items.add(item);
					}
				}
			}
		}
	}
	
	/**
	 * Расчитать все аспекты
	 */
	public void calc() {
		for (AspectData item: items) {
			item.calc();
		}
	}
	
	/**
	 * Скрыть/показать аспекты заданного тэга
	 *
	 * @param on видимость
	 * @param tag приминить только к объектам тэга
	 */
	public void visibleTag(boolean on, int tag) {
		for (Aspect aspect: aspects)
			aspect.visibleTag(on, tag);
	}

	/**
	 * Скрыть/показать аспекты заданного тэга
	 *
	 * @param on видимость
	 * @param tag приминить только к объектам тэга
	 */
	public void visibleTag(boolean on, String tag) {
		visibleTag(on, tag.hashCode());
	}
}
