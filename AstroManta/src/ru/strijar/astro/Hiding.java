package ru.strijar.astro;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Скрываемый объект
 */

public class Hiding extends Stored {
	private boolean	visible = true;
	private int		tag = 0;

	/**
	 * Установть тэг
	 *
	 */
	public void setTag(int data) {
		tag = data;
	}
	
	/**
	 * Установить тэг
	 *
	 */
	public void setTag(String data) {
		tag = data.hashCode();
	}
	
	/**
	 * Установить видимость
	 *
	 */
	public void setVisible(boolean on) {
		visible = on;
	}
	
	/**
	 * Получить видимость
	 *
	 */
	public boolean getVisible() {
		return visible;
	}

	/**
	 * Установить видимость если тэг совпадает
	 *
	 * @param on видимость
	 * @param tag тэг
	 */
	public void visibleTag(boolean on, int tag) {
		if (tag == this.tag)
			visible = on;
	}

	/**
	 * Установить видимость если тэг совпадает
	 *
	 * @param on видимость
	 * @param tag тэг
	 */
	public void visibleTag(boolean data, String tag) {
		visibleTag(data, tag.hashCode());
	}
	
	protected void store(JSONObject obj) {
		super.store(obj);

		try {
			obj.put("visible", visible);
		} catch (JSONException e) {
		}
	}
	
	protected void load(JSONObject obj) {
		super.load(obj);
		
		if (obj.has("visible")) {
			try {
				visible = obj.getBoolean("visible");
			} catch (JSONException e) {
			}
		}
	}

}
