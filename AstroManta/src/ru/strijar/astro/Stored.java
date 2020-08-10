package ru.strijar.astro;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Сохраняемый объект 
 */

public class Stored {
	protected void store(JSONObject obj) {
	}
	
	protected void load(JSONObject obj) {
	}

	/**
	 * Сохранить состояние объекта в текст
	 *
	 */
	public String store() {
		JSONObject obj = new JSONObject();
		
		store(obj);

		try {
			return obj.toString(4);
		} catch (JSONException e) {
			return "";
		}
	}

	/**
	 * Загрузить состояние объекта из текста
	 *
	 */
	public void load(String str) {
		JSONTokener pars = new JSONTokener(str);
		
		JSONObject obj;

		try {
			obj = (JSONObject) pars.nextValue();
			load(obj);
		} catch (JSONException e) {
		}
	}

}
