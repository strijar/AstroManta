package ru.strijar.astro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Деление Зодиака
 */

public class Division implements Comparator<DivisionItem> {
	private List<DivisionItem>	items = new ArrayList<DivisionItem>();
	private double				prevTo = 0;
	private double				prevSize = 0;
	private DivisionItem		search = new DivisionItem();
	
	/**
	 * Добавить часть деления
	 *
	 * @param from стартовая долгота
	 * @param to конечная долгота
	 * @param info информация
	 */
	public void addFromTo(double from, double to, String info) {
		DivisionItem item = new DivisionItem(from, to, info);
		
		prevTo = to;
		prevSize = to-from;

		items.add(item);
		Collections.sort(items, this);
	}

	/**
	 * Добавить часть деления с размером предыдущей части
	 *
	 * @param from стартовая долгота
	 * @param info информация
	 */
	public void addFrom(double from, String info) {
		addFromTo(from, from+prevSize, info);
	}

	/**
	 * Добавить часть деления используя начало и размер
	 *
	 * @param from стартовая долгота
	 * @param size размер части
	 * @param info информация
	 */
	public void addFromSize(double from, double size, String info) {
		addFromTo(from, from+size, info);
	}

	/**
	 * Добавить часть деления используя размер и окончание предыдущей части
	 *
	 * @param size размер части
	 * @param info информация
	 */
	public void addSize(double size, String info) {
		addFromTo(prevTo, prevTo+size, info);
	}

	/**
	 * Получить информацию части
	 *
	 * @param lon искомая долгота
	 * @return иноформация части
	 */
	public String get(double lon) {
		search.point(lon);
		
		int res = Collections.binarySearch(items, search, this);
		
		if (res >= 0) {
			return items.get(res).getInfo();
		}
		
		return "";
	}

	protected int getHash(double data) {
		search.point(data);
		
		int res = Collections.binarySearch(items, search, this);
		
		if (res >= 0) {
			return items.get(res).hash();
		}
		
		return -1;
	}
	
	public int compare(DivisionItem lhs, DivisionItem rhs) {
		return lhs.compare(rhs);
	}
}
