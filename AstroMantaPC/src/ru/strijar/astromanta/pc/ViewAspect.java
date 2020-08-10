package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ru.strijar.astro.AspectData;

/**
 * Отрисовка аспекта в виде прямой линии между точками
 */

public class ViewAspect {
	protected Point2D		spot1 = new Point2D.Double();
	protected Point2D		spot2 = new Point2D.Double();
	protected AstroPaint	paint;
	
	protected ViewAspect() {
		paint = new AstroPaint();
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

	protected void draw(AspectData aspect, Graphics2D canvas, ViewEcliptic view) {
		int R = view.size;

		view.getXY(aspect.getSpot1().getEcliptic().getLon(), R, spot1);
		view.getXY(aspect.getSpot2().getEcliptic().getLon(), R, spot2);
		
		paint.assign(canvas);
		canvas.drawLine((int)spot1.getX(), (int)spot1.getY(), (int)spot2.getX(), (int)spot2.getY());
	}

}
