package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;

import ru.strijar.astro.Spot;
import ru.strijar.astromanta.pc.ViewSpotCollect.HotPoint;

/**
 * Базовый класс отрисовки точки карты
 */

public class ViewSpot {
	protected AstroPaint	paint = new AstroPaint();
	protected Spot			spot;

	protected ViewSpot(Spot spot) {
		this.spot = spot;
	}

	/**
	 * Получить тип отрисовки
	 *
	 * @return объект типа
	 */
	public AstroPaint getPaint() {
		return paint;
	}

	/**
	 * Установить тип отрисовки
	 *
	 * @param paint объект типа
	 */
	public void setPaint(AstroPaint paint) {
		this.paint = paint;
	}

	protected Spot getSpot() {
		return spot;
	}

	protected void preDraw(ViewCharts view) {
	}
	
	protected void draw(Graphics2D canvas, ViewEcliptic view, HotPoint hot) {
	}
				
	protected void draw(Graphics2D canvas, ViewEcliptic view) {
	}

	protected void drawInner(Graphics2D canvas, ViewEcliptic view) {
	}
}
