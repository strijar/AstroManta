package ru.strijar.astromanta.android;

import ru.strijar.astro.AspectData;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Отрисовка аспекта в виде прямой линии между точками
 */

public class ViewAspect {
	protected Point			spot1 = new Point();
	protected Point			spot2 = new Point();
	protected AstroPaint	paint;
	
	protected ViewAspect() {
		paint = new AstroPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
	}

	/**
	 * Получить объект отрисовки
	 *
	 */
	public AstroPaint getPaint() {
		return paint;
	}

	/**
	 * Установить объект отрисовки
	 *
	 */
	public void setPaint(AstroPaint paint) {
		this.paint = paint;
	}
		
	protected void draw(AspectData aspect, Canvas canvas, ViewEcliptic view) {
		int R = view.size;

		view.getXY(aspect.getSpot1().getEcliptic().getLon(), R, spot1);
		view.getXY(aspect.getSpot2().getEcliptic().getLon(), R, spot2);
			
		canvas.drawLine(spot1.x, spot1.y, spot2.x, spot2.y, paint);
	}
}
