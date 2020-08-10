package ru.strijar.astromanta.android;

import ru.strijar.astro.AspectData;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;

/**
 * Отрисовка аспекта в виде дуги между точками
 */

public class ViewAspectCurve extends ViewAspect {
	private float	curve = 0.0F;
	private Path	path;
	private Point	spot3;
	private Point	spot4;

	protected ViewAspectCurve(float curve) {
		super();
		
		this.curve = curve;
		path = new Path();
		spot3 = new Point();
		spot4 = new Point();
	}

	protected void draw(AspectData aspect, Canvas canvas, ViewEcliptic view) {
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
			path.moveTo(spot1.x, spot1.y);
			path.cubicTo(spot2.x, spot2.y, spot3.x, spot3.y, spot4.x, spot4.y);
		} else {
			path.moveTo(spot4.x, spot4.y);
			path.cubicTo(spot3.x, spot3.y, spot2.x, spot2.y, spot1.x, spot1.y);
		}

		canvas.drawPath(path, paint);
		path.reset();
	}
}
