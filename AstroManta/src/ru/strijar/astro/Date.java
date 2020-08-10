package ru.strijar.astro;

import java.util.Calendar;

import swisseph.SweDate;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Момент времени
 */

@SuppressWarnings("serial")
public class Date extends SweDate {
	private int				day;
	private int				month;
	private int				year;
	private int				time;		// В секундах
	private DateTimeZone	timeZoneId;

	private long			timeZone;	// В секундах
	private int				age = 0;
	private boolean			jdReady = false;
	
	private void updateTimeZone() {
		if (timeZoneId != null) {
			int			minute = time / 60;
			DateTime	dt = new DateTime(year, month, day, minute / 60, minute % 60);

			timeZone = timeZoneId.getOffsetFromLocal(dt.getMillis()) / 1000;
		}
	}

	protected int age() {
		return age;
	}
	
	/**
	 * Получить Юлианскую дату
	 *
	 */
	public double getJD() {
		if (!jdReady) {
			updateTimeZone();
			jdReady = true;
		}
		
		return getJulDay() - timeZone / (24.0 * 3600.0);
	}
	
	/**
	 * Установить Юлианскую дату
	 *
	 */
	public void setJD(double jd) {
		setJulDay(jd);

		day = getDay();
		month = getMonth();
		year = getYear();
		time = (int) (getHour() * 3600.0);

		jdReady = false;
		updateTimeZone();
		
		age++;
	}
	
	/**
	 * Установить текущее время
	 */
	public void now() {
		Calendar cal = Calendar.getInstance();
	 
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH) + 1;
		year = cal.get(Calendar.YEAR);
		time = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60;
		
		setDate(year, month, day, time / 3600.0);
		jdReady = false;
		age++;
	}
	
	/**
	 * Изменить время 
	 *
	 * @param sec изменение (в секундах)
	 */
	public void rotate(long sec) {
		setHour((time + sec) / 3600.0);
		makeValidDate();
		
		day = getDay();
		month = getMonth();
		year = getYear();
		time = (int) (getHour() * 3600.0);
		jdReady = false;

		age++;
	}
	
	/**
	 * Установть дату из текста
	 *
	 * @param str дата (в виде ДД/ММ/ГГГГ)
	 */
	public void setDateStr(String str) {
		String part[] = str.split("/");
		
		if (part.length != 3) {
			return;
		}
		
		day = Integer.parseInt(part[0]);
		month = Integer.parseInt(part[1]);
		year = Integer.parseInt(part[2]);

		setDate(year, month, day, time / 3600.0);
		jdReady = false;
		age++;
	}

	/**
	 * Установить время из текста
	 *
	 * @param str время (в виде ММ:ЧЧ или ММ:ЧЧ:СС)
	 */
	public void setTimeStr(String str) {
		String part[] = str.split(":");
		
		if (part.length < 2) {
			return;
		} else if (part.length == 2) {
			time = Integer.parseInt(part[0]) * 3600 + Integer.parseInt(part[1]) * 60;
		} else {
			time = Integer.parseInt(part[0]) * 3600 + Integer.parseInt(part[1]) * 60 + Integer.parseInt(part[2]); 
		}
		setDate(year, month, day, time / 3600.0);
		jdReady = false;
		age++;
	}
	
	/**
	 * Получить дату
	 *
	 * @return дата (в виде ДД/ММ/ГГГГ)
	 */
	public String getDateStr() {
		return String.format("%02d/%02d/%04d", day, month, year);
	}
	
	/**
	 * Получить время
	 *
	 * @return время (в виде ММ:ЧЧ)
	 */
	public String getTimeStr() {
		return String.format("%02d:%02d", (time / 3600) % 60, (time / 60) % 60);
	}
	
	/**
	 * Получить временной сдвиг
	 *
	 * @return сдвиг (в часах)
	 */
	public double getTimeZone() {
		return timeZone / 3600.0;
	}
	
	/**
	 * Установить временной сдвиг
	 *
	 * @param x сдвиг (в часах)
	 */
	public void setTimeZone(double x) {
		timeZone = (int) x * 3600;
		jdReady = false;
		age++;
	}
	
	/**
	 * Установить id временной зоны
	 *
	 * @param id зона (согласно zoneinfo)
	 */
	public void setTimeZoneId(String id) {
		if (id != null && id.length() > 0) { 
			timeZoneId = DateTimeZone.forID(id);
		} else {
			timeZoneId = null;
		}
		jdReady = false;
		age++;
	}

	/**
	 * Получить id временной зоны
	 *
	 */
	public String getTimeZoneId() {
		if (timeZoneId == null) {
			return null;
		}

		return timeZoneId.getID();
	}
	
	/**
	 * Получить временной сдвиг в текстовом виде
	 *
	 * @return в текстовом виде +ЧЧ:ММ или -ЧЧ:ММ
	 */
	public String getTimeZoneStr() {
		int x = (int) Math.abs(timeZone);
		
		return String.format("%s%02d:%02d", timeZone < 0 ? "-" : "+", (x / 3600) % 60, (x / 60) % 60);
	}
	
	/**
	 * Установить временной сдвиг из текста
	 *
	 * @param x в виде +ЧЧ:ММ или -ЧЧ:ММ
	 */
	public void setTimeZoneStr(String x) {
		String part[] = x.replace("+", "").split(":");
		
		if (part.length >= 2) {
			int h = Integer.parseInt(part[0]) * 3600;
			int m = Integer.parseInt(part[1]) * 60;
			
			timeZone = (h < 0) ? h - m : h + m;
			jdReady = false;
		}
	}

	protected void store(JSONObject obj) {
		try {
			obj.put("date", getDateStr());
		} catch (JSONException e) {
		}

		try {
			obj.put("time", getTimeStr());
		} catch (JSONException e) {
		}

		try {
			obj.put("timezoneid", getTimeZoneId());
		} catch (JSONException e) {
		}

		try {
			obj.put("timezone", getTimeZone());
		} catch (JSONException e) {
		}
	}

	protected void load(JSONObject obj) {
		try {
			setDateStr(obj.getString("date"));
		} catch (JSONException e) {
		}

		try {
			setTimeStr(obj.getString("time"));
		} catch (JSONException e) {
		}

		try {
			setTimeZoneId(obj.getString("timezoneid"));
		} catch (JSONException e) {
		}

		try {
			setTimeZone(obj.getDouble("timezone"));
		} catch (JSONException e) {
		}
	}

}
