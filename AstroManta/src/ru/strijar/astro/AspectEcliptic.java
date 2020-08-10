package ru.strijar.astro;

import java.util.ArrayList;

/**
 * Аспекты по эклиптической долготе
 */

public class AspectEcliptic extends Aspect {
	private boolean							fixed1 = false;
	private boolean							fixed2 = false;
	private ArrayList<AspectEclipticLon>	items = new ArrayList<AspectEclipticLon>();
	
	protected String calc(Spot spot1, Spot spot2) {
		double dist = spot1.angle(spot2);
		double dist_abs = dist < 0.0 ? -dist : dist;
		double v = (fixed1 ? 0 : spot1.ecliptic.getLonSpeed()) - (fixed2 ? 0.0 : spot2.ecliptic.getLonSpeed());
		boolean revers;
		
		if (v > 0.0) {
			revers = dist > 0.0;
		} else if (v < 0.0) {
			revers = dist < 0.0;
		} else {
			revers = false;
		}
		
		for (AspectEclipticLon item: items) 
			if (item.getVisible()) {
				String info = item.check(spot1, spot2, dist_abs, revers);
			
				if (info != null)
					return info;
			}
		
		return null;
	}
		
	/**
	 * Добавить долготу без учета пересечения границы знака.
	 *
	 * @param lon долгота
	 * @return объект долготы
	 */
	public AspectEclipticLon addLon(double lon) {
		AspectEclipticLon item = new AspectEclipticLon(lon);
		
		items.add(item);
		
		return item;
	}

	/**
	 * Добавить долготу с учетом пересечения границы знака.
	 *
	 * @param lon долгота
	 * @param inside только внутри (true) или только снаружи (false) границ по знакам
	 * @return объект долготы
	 */
	public AspectEclipticLonSign addLon(double lon, boolean inside) {
		AspectEclipticLonSign item = new AspectEclipticLonSign(lon, inside);
		
		items.add(item);
		
		return item;
	}

	/**
	 * Создать орбис
	 * @since 5.1
	 * @return объект орбиса
	 */
	public AspectEclipticOrbis newOrbis() {
		AspectEclipticOrbis item = new AspectEclipticOrbis();
		
		return item;
	}
	
	/**
	 * Создать орбис с значение по умолчанию.
	 *
	 * @param orbis значение орбиса по умолчанию
	 * @return объект орбиса
	 */
	public AspectEclipticOrbis newOrbis(double orbis) {
		AspectEclipticOrbis item = new AspectEclipticOrbis(orbis);
		
		return item;
	}

	/**
	 * Зафиксировать точки.
	 *
	 * @param fixed1 принимать скорость первой точки за ноль
	 * @param fixed2 принимать скорость второй точки за ноль
	 */
	public void fixed(boolean fixed1, boolean fixed2) {
		this.fixed1 = fixed1;
		this.fixed2 = fixed2;
	}
	
	public void visibleTag(boolean data, int tag) {
		super.visibleTag(data, tag);
		
		for (AspectEclipticLon item: items)
			item.visibleTag(data, tag);
	}
}
