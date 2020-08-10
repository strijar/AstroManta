package ru.strijar.astromanta.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import ru.strijar.astro.Spot;

/**
 * Отрисовка зоны внутри области карты
 */

public class ViewZone extends ViewSpot {
	private Spot		spot2;
	private RectF		rect = new RectF();
	
	protected ViewZone(Spot from, Spot to) {
		super(from);
		spot2 = to;

		paint.setStrokeCap(Paint.Cap.BUTT);
	    paint.setStyle(Paint.Style.STROKE);
	}

	protected void draw(Canvas canvas, ViewEcliptic view) {
		if (spot.getVisible() && spot2.getVisible()) {
			float	r = view.size - view.sizeChart*view.scale/2;
			double	start;
			double	sweep;

			paint.setStrokeWidth(view.sizeChart * view.scale);
			rect.set(view.centerX - r, view.centerY - r, view.centerX + r, view.centerY + r);
			
			if (view.dir) {
				start = view.angle(spot.getEcliptic().getLon());
				sweep = spot.angle(spot2);
			} else {
				start = view.angle(spot2.getEcliptic().getLon());
				sweep = -spot2.angle(spot);
			}
			
			if (sweep < 0) sweep += 360;

			canvas.drawArc(rect, (float)start, (float)sweep, false, paint);
		}
	}
}
