package ru.strijar.astromanta.pc;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Коллекция для локализациии
 */

public class I18n {
	static private HashMap<String, String>	items = new HashMap<String, String>();
	
	/**
	 * Добавить сообщение
	 *
	 * @param lang язык сообщения
	 * @param key ключ сообщения (на Английском)
	 * @param msg сообщение
	 */
	static public void add(String lang, String key, String msg) {
		if (lang.equals(Locale.getDefault().getLanguage())) {
			items.put(key, msg);
		}
	}
	
	/**
	 * Получить сообщение
	 *
	 * @param key ключ сообщения (на Английском)
	 * @return сообщение на текущем языке
	 */
	static public String get(String key) {
		String res = items.get(key);
		
		if (res == null) {
			System.out.println("\""+ key + "\" : \"\",");
			return key;
		} else {
			return res;
		}
	}
	
	static protected void load(InputStream input) {
		if (input == null)
			return;

		JSONTokener	pars = new JSONTokener(input);
		String		lang = Locale.getDefault().getLanguage();

		try {
			JSONObject	root = (JSONObject) pars.nextValue();
			JSONObject	current = root.getJSONObject(lang);

			if (current != null) {
				Iterator<String> keys = current.keys();

				while (keys.hasNext()) {
					String key = keys.next();
					
					items.put(key, current.getString(key));
				}
			}
		} catch (JSONException e) {
		}
	}
}
