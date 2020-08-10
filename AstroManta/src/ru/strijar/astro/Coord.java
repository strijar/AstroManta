package ru.strijar.astro;

/**
 * Координаты
 */

public class Coord {
	protected double 	lon;
	protected double 	lat;

	protected double 	lonSpeed;
	protected double 	latSpeed;
	
	private int			age = 0;
	private String		signName[];
	
	public Coord() {
		lon = 0.0;
		lat = 0.0;
		lonSpeed = 0.0;
		latSpeed = 0.0;

		signName = new String[] { 
			"Ar", "Ta", "Ge", "Cn", "Le", "Vi",
			"Li", "Sc", "Sg", "Cp", "Aq", "Pi"
		};
	}

	/**
	 * Получить угловое расстояние по долготе
	 *
	 * @param lon1 долгота первой точки
	 * @param lon2 долгота второй точки
	 * @return угловое расстояние (-180 .. 180)
	 */
	public static double angle(double lon1, double lon2) {
		double res, res_abs;
	
		res = lon2 - lon1;
		res_abs = res < 0.0 ? -res : res;

		if (lon1 > lon2) {
			if (res_abs > 180.0) {
				res = 360.0 - res_abs;
				
				if (lon1 < 180.0) {
					res = -res;
				}
			}
		} else {
			if (res_abs > 180.0) {
				res = 360.0 - res_abs;
				res = -res;
			}
		}
		
		return res;
	}

	/**
	 * Установить массив имен знаков зодиака
	 *
	 * @param names массив имен
	 */
	public void signName(String[] names) {
		signName = names;
	}
	
	protected int age() {
		return age;
	}

	protected void age(int age) {
		this.age = age;
	}
	
	/**
	 * Получить долготу
	 *
	 */
	public double getLon() {
		return lon;
	}
	
	/**
	 * Получить широту
	 *
	 */
	public double getLat() {
		return lat;
	}
	
	/**
	 * Установить долготу
	 *
	 */
	public void setLon(double data) {
		age++;
		
		if (data < 0) {
			lon = data + 360.0;
		} else if (data > 360.0) {
			lon = data - 360.0;
		} else {
			lon = data;
		}
	}
	
	/**
	 * Установить широту
	 *
	 */
	public void setLat(double data) {
		age++;
		lat = data;
	}

	/**
	 * Получить скорость по долготе
	 *
	 */
	public double getLonSpeed() {
		return lonSpeed;
	}
	
	/**
	 * Получить скорость по широте
	 *
	 */
	public double getLatSpeed() {
		return latSpeed;
	}
	
	/**
	 * Установить скорость по долготе
	 *
	 */
	public void setLonSpeed(double data) {
		age++;
		lonSpeed = data;
	}
	
	/**
	 * Установить скорость по широте
	 *
	 */
	public void setLatSpeed(double data) {
		age++;
		latSpeed = data;
	}
	
	/**
	 * Получить долготу в текстовом виде используя массив имен знаков Зодиака
	 *
	 * @param signs массив имен знаков
	 */
	public String getLonStr(Object[] signs) {
		int 	l, g, m, s;
		
		l = (int) (lon * 3600.0);
		s = l % 60;

		l = l / 60;
		m = l % 60;
		
		g = l / 60;
		
		return String.format("%02d %s %02d'%02d\"", g % 30, signs[g / 30].toString(), m, s);
	}

	/**
	 * Получить долготу в текстовом виде
	 *
	 */
	public String getLonStr() {
		return getLonStr(signName);
	}

	/**
	 * Угловое расстояние до указанной долготы
	 *
	 * @param lon долгота (в градусах)
	 * @return угловое расстояние
	 */
	public double angle(double lon) {
		return angle(this.lon, lon);
	}

	/**
	 * Угловое расстояние до указанной координаты
	 *
	 * @param lon объект координаты
	 * @return угловое расстояние
	 */
	public double angle(Coord lon) {
		return angle(this.lon, lon.getLon());
	}
	
	/**
	 * Поиск положения точки среди множества точек 
	 * @since 5.1
	 * @param coords массив координат для поиска
	 * @return номер пары если точка находится внутри пары или -1 если пара не найдена
	 */
	
	public int between(Coord coords[]) {
		for (int i = 0; i < coords.length-1; i++) 
			if (angle(coords[i]) < 0 && angle(coords[i+1]) > 0)
				return i;
		
		return -1;
	}

}
