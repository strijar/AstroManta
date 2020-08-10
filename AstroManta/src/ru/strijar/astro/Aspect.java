package ru.strijar.astro;

import java.util.ArrayList;

/**
 * Базовый класс аспектов
 */

public class Aspect extends Hiding {
	private ArrayList<CheckSpotItem> exclude = new ArrayList<CheckSpotItem>();

	protected AspectData data(Spot spot1, Spot spot2) {
		for (CheckSpotItem item: exclude) {
			if (item.check(spot1, spot2))
				return null;
		}
		
		AspectData data = new AspectData(spot1, spot2);
		
		return data;
	}
	
	protected String calc(Spot spot1, Spot spot2) {
		return null;
	}

	/**
	 * Исключить из аспектов точки по категории
	 *
	 * @param category имя категории
	 */
	public void excludeCategory(String category) {
		exclude.add(new CheckSpotCategoryItem(category));
	}
	
	/**
	 * Исключить из аспектов точки по категории
	 *
	 * @param category1 категория первой точки
	 * @param category2 категория второй точки
	 */
	public void excludeCategory(String category1, String category2) {
		exclude.add(new CheckSpotCategoriesItem(category1, category2));
	}

	/**
	 * Исключить из аспектов точку по имени
	 *
	 * @param name имя точки
	 */
	public void excludeName(String name) {
		exclude.add(new CheckSpotNameItem(name));
	}	

	/**
	 * Исключить из аспектов точки по именам
	 *
	 * @param name1 имя первой точки
	 * @param name2 имя второй точки
	 */
	public void excludeName(String name1, String name2) {
		exclude.add(new CheckSpotNamesItem(name1, name2));
	}	
}
