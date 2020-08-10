package ru.strijar.astromanta.android;

import ru.strijar.astro.Spot;
import ru.strijar.astromanta.android.ViewSpotCollect.HotPoint;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Базовый класс отрисовки точки карты
 */

public class ViewSpot {
	protected Spot			spot;
	protected AstroPaint	paint = new AstroPaint(Paint.ANTI_ALIAS_FLAG);

	protected ViewSpot(Spot spot) {
		this.spot = spot;
	}

	/**
	 * Получить точку отрисовки
	 * 
	 * @since 5.1
	 * @return объект точки
	 */
	public Spot getSpot() {
		return spot;
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

	protected void preDraw(ViewCharts view) {
	}
		
	protected void draw(Canvas canvas, ViewEcliptic view, HotPoint hot) {
	}

	protected void draw(Canvas canvas, ViewEcliptic view) {
	}

	protected void drawInner(Canvas canvas, ViewEcliptic view) {
	}
}
