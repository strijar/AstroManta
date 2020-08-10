package ru.strijar.astromanta.android;

import android.graphics.Canvas;

/**
 * Базовый класс для отрисовки карт и аспектов
 */

public class ViewChart {
    private boolean	visible = true;

    protected ViewChart() {
    }

    /**
	 * Установить видимость
	 *
	 * @param on видимость
	 */
    public void setVisible(boolean on) {
		visible = on;
	}
	
	/**
	 * Получить видимость
	 *
	 * @return видимость
	 */
	public boolean getVisible() {
		return visible;
	}

	protected void draw(Canvas canvas) {
		
	}

}
