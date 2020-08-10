package ru.strijar.astro;

import java.util.ArrayList;

/**
 * Долгота аспекта по эклиптике
 */

public class AspectEclipticLon extends Hiding {

	private class InfoItem extends Hiding {
		protected String	info;
		protected double	min;
		protected double	max;

		protected InfoItem(String info, double min, double max) {
			this.info = info;
			this.min = min;
			this.max = max;
		}

		protected String get(double d, double a) {
			if (d >= min && d <= max) {
				return info;
			} else {
				return null;
			}
		}		
	}
	
	private class InfoItemAbs extends InfoItem {
		protected InfoItemAbs(String info, double min, double max) {
			super(info, min, max);
		}

		protected String get(double d, double a) {
			if (a >= min && a <= max) {
				return info;
			} else {
				return null;
			}
		}		
	}
	
	private double						lon;
	private AspectEclipticOrbis			orbis;
	private ArrayList<InfoItem>			items = new ArrayList<InfoItem>();
	private ArrayList<CheckSpotItem>	exclude = new ArrayList<CheckSpotItem>();

	protected AspectEclipticLon(double lon) {
		this.lon = lon;
	}
	
	/**
	 * Установить орбис.
	 *
	 * @param orbis объект орбиса
	 */
	public void setOrbis(AspectEclipticOrbis orbis) {
		this.orbis = orbis;
	}

	/**
	 * Установить орбис в градусах
	 * @since 5.1
	 * @param orbis значение в градусах
	 */
	public void setOrbis(Double orbis) {
		this.orbis = new AspectEclipticOrbis(orbis);
	}

	/**
	 * Добавить часть аспекта
	 *
	 * @param info информация
	 * @param min минимальная граница части (-1 .. 1)
	 * @param max максимальная граница части (-1 .. 1)
	 * @return скрываемый объект
	 */
	public Hiding addInfo(String info, double min, double max) {
		InfoItem item = new InfoItem(info, min, max);

		items.add(item);
		
		return item;
	}

	/**
	 * Добавить часть аспекта c абсолютным значением границ
	 * @since 5.1
	 * @param info информация
	 * @param min минимальная граница части (в градусах)
	 * @param max максимальная граница части (в градусах)
	 * @return скрываемый объект
	 */
	public Hiding addInfoAbs(String info, double min, double max) {
		InfoItem item = new InfoItemAbs(info, min, max);

		items.add(item);
		
		return item;
	}
	
	/**
	 * Добавить информацию для всего аспекта
	 *
	 * @since 5.1
	 * @param info информация
	 * @return скрываемый объект
	 */
	public Hiding addInfo(String info) {
		return addInfo(info, -1, 1);
	}
	/**
	 * Исключить из аспекта точки по категории
	 *
	 * @since 5.1
	 * @param category имя категории
	 */

	public void excludeCategory(String category) {
		exclude.add(new CheckSpotCategoryItem(category));
	}
	
	/**
	 * Исключить из аспекта точки по категории
	 *
	 * @since 5.1
	 * @param category1 категория первой точки
	 * @param category2 категория второй точки
	 */
	public void excludeCategory(String category1, String category2) {
		exclude.add(new CheckSpotCategoriesItem(category1, category2));
	}

	/**
	 * Исключить из аспекта точку по имени
	 *
	 * @since 5.1
	 * @param name имя точки
	 */
	public void excludeName(String name) {
		exclude.add(new CheckSpotNameItem(name));
	}	

	/**
	 * Исключить из аспекта точки по именам
	 *
	 * @since 5.1
	 * @param name1 имя первой точки
	 * @param name2 имя второй точки
	 */
	public void excludeName(String name1, String name2) {
		exclude.add(new CheckSpotNamesItem(name1, name2));
	}	

	protected String check(Spot spot1, Spot spot2, double lon, boolean reverse) {
		for (CheckSpotItem item: exclude) {
			if (item.check(spot1, spot2))
				return null;
		}

		Double val = orbis.get(spot1, spot2);
		
		if (val == null) {
			return null;
		}

		double abs = lon - this.lon;
		double delta = abs / val.doubleValue();
	
		if (delta < -1.0 || delta > 1.0) {
			return null;
		}
		
		if (reverse) {
			delta = -delta;
			abs = -abs;
		}

		for (InfoItem item: items) 
			if (item.getVisible()) {
				String res = item.get(delta, abs);
			
				if (res != null) 
					return res;
			}
				
		return null;
	}
	
	public void visibleTag(boolean data, int tag) {
		super.visibleTag(data, tag);

		for (InfoItem item: items)
			item.visibleTag(data, tag);
	}
}
