package ru.strijar.astro;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Местоположение
 */

public class Place extends Coord {
	private String	info;
	
	private double fromStr(String str) {
		double	res = 0;
		int		sign = 1;
		String	data = "";
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			switch (c) {
				case 'S':
				case 's':
				case 'W':
				case 'w':
					res += Double.parseDouble(data);
					data = "";
					sign = -1;
					break;

				case 'N':
				case 'n':
				case 'E':
				case 'e':
					res += Double.parseDouble(data);
					data = "";
					sign = 1;
					break;

				case '\'':
					res += Double.parseDouble(data) / 60.0;
					data = "";
					break;

				case '"':
					res += Double.parseDouble(data) / 3600.0;
					data = "";
					break;

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '.':
					data = data + c;
					break;

				case ',':
					data = data + '.';
					break;
			}
		}
		
		return sign * res;
	}

	private String toStr(double x, String neg, String pos) {
		int g, m, s;
		int c = (int) Math.abs(x * 3600.0);
		
		g = c / 3600;
		m = c % 3600 / 60;
		s = c % 60;
		
		return String.format("%02d%s%02d'%02d\"", g, (x < 0 ? neg : pos), m, s);
	}

	/**
	 * Установить широту из текста
	 *
	 */
	public void setLatStr(String str) {
		if (str != null) {
			lat = fromStr(str);
		}
	}
	
	/**
	 * Установить долготу из текста
	 *
	 */
	public void setLonStr(String str) {
		if (str != null) {
			lon = fromStr(str);
		}
	}

	/**
	 * Получить широту в тексте
	 *
	 */
	public String getLatStr() {
		return toStr(lat, "S", "N");
	}

	/**
	 * Получить долготу в тексте
	 *
	 */
	public String getLonStr() {
		return toStr(lon, "W", "E");
	}

	/**
	 * Установить информацию
	 *
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * Получить информацию
	 *
	 */
	public String getInfo() {
		return info;
	}

	protected void store(JSONObject obj) {
		try {
			obj.put("info", getInfo());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			obj.put("lon", getLon());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			obj.put("lat", getLat());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void load(JSONObject obj) {
		try {
			setInfo(obj.getString("info"));
		} catch (JSONException e) {
		}

		try {
			setLon(obj.getDouble("lon"));
		} catch (JSONException e) {
		}

		try {
			setLat(obj.getDouble("lat"));
		} catch (JSONException e) {
		}
	}
}
