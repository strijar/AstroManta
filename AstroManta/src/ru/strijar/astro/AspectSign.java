package ru.strijar.astro;

import java.util.ArrayList;

/**
 * Аспект по эклиптической долготе по знакам, без учета орбиса
 * 
 */

public class AspectSign extends Aspect {
	private ArrayList<String>	items = new ArrayList<String>(); 		

	/**
	 * Добавить информацию
	 *
	 * @param sign количество знаков между точками
	 * @param info информация
	 */
	public void add(int sign, String info) {
		items.add(sign, info);
	}
	
	protected String calc(Spot spot1, Spot spot2) {
		int sign1 = (int) (spot1.getEcliptic().getLon() / 30);
		int sign2 = (int) (spot2.getEcliptic().getLon() / 30);
		int sign = sign1 - sign2;
		
		if (sign < 0) sign = -sign;
		if (sign > 6) sign = 12 - sign;
		
		return items.get(sign);
	}
}
