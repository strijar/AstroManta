package ru.strijar.astromanta.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Отрисовка линии с внешней стороны Знаков с именем точки
 */

public class ViewOutsideInfo extends ViewOutside {
	private float			textSize = (float) 16.0;
	private AstroPaint		paintText;
	private String			str;
	private Rect			bounds = new Rect();
	private Point			point = new Point();

	protected ViewOutsideInfo(ru.strijar.astro.Spot spot) {
		super(spot);

		paintText = new AstroPaint(Paint.ANTI_ALIAS_FLAG);
		str = spot.getName();
	}

	/**
	 * Получить тип отрисовки текста
	 *
	 * @return объект типа
	 */
	public AstroPaint getPaintText() {
		return paintText;
	}

	/**
	 * Установить тип отрисовки текста
	 *
	 * @param paint тип отрисовки
	 */
	public void setPaintText(AstroPaint paint) {
		paintText = paint;
	}

	/**
	 * Установить размер текста
	 *
	 * @param size размер
	 */
	public void setTextSize(int size) {
		textSize = size;
	}
	
	protected void draw(Canvas canvas, ViewEcliptic view) {
		super.draw(canvas, view);

		if (spot.getVisible()) {
			paintText.setTextSize(textSize * view.scale);
			paintText.getTextBounds(str, 0, str.length(), bounds);

			double w = bounds.width();
			double h = bounds.height();
			double d = Math.sqrt(w*w + h*h) / 2.0;

			view.getXY(spot.getEcliptic().getLon(), (float) (view.getRadius() + size * view.scale + d), point);

			canvas.drawText(str, (float) (point.x - w/2), (float) (point.y + h/2), paintText);
		}
	}
}
