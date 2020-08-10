package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;

import ru.strijar.astromanta.pc.ViewSpotCollect.HotPoint;

/**
 * Базовый класс для отрисовки карт и аспектов
 */

public class ViewChart {
    protected boolean	visible = true;
	
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

	protected void draw(Graphics2D canvas) {
	}

	protected HotPoint getNear(int x, int y, int max) {
		return null;
	}

}
