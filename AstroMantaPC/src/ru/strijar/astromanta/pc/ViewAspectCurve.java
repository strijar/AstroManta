package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import ru.strijar.astro.AspectData;

/**
 * Отрисовка аспекта в виде дуги между точками
 */

public class ViewAspectCurve extends ViewAspect {
	private float	curve = 0.0F;
	private Point2D	spot3 = new Point2D.Double();
	private Point2D	spot4 = new Point2D.Double();
	private Path2D	path = new Path2D.Double();

	protected ViewAspectCurve(float curve) {
		super();
		
		this.curve = curve;
	}

	protected void draw(AspectData aspect, Graphics2D canvas, ViewEcliptic view) {
		int R = view.size;

		double a1;
		double a2;
		double dist = aspect.getSpot1().angle(aspect.getSpot2());

		if (dist > 0) {
			a1 = aspect.getSpot1().getEcliptic().getLon();
			a2 = aspect.getSpot2().getEcliptic().getLon();
		} else {
			a2 = aspect.getSpot1().getEcliptic().getLon();
			a1 = aspect.getSpot2().getEcliptic().getLon();
		}
		
		view.getXY(a1, R, spot1);
		view.getXY(a2, R, spot4);
		
		double a = a1 + Math.abs(dist) / 2.0;
		double r = R * Math.cos(dist / 180.0 * Math.PI / 2.0) - (R * curve);

		if (r > 0) {
			view.getXY(a - 15, (int) r, spot2);
			view.getXY(a + 15, (int) r, spot3);
		} else {
			view.getXY(a + 15, (int) r, spot2);
			view.getXY(a - 15, (int) r, spot3);
		}

		if (dist > 0) {
			path.moveTo(spot1.getX(), spot1.getY());
			path.curveTo(spot2.getX(), spot2.getY(), spot3.getX(), spot3.getY(), spot4.getX(), spot4.getY());
		} else {
			path.moveTo(spot4.getX(), spot4.getY());
			path.curveTo(spot3.getX(), spot3.getY(), spot2.getX(), spot2.getY(), spot1.getX(), spot1.getY());
		}

		paint.assign(canvas);
		canvas.draw(path);
		path.reset();
	}
}
