package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;

import ru.strijar.astro.Spot;

/**
 * Отрисовка зоны внутри области карты
 */

public class ViewZone extends ViewSpot {
	private Spot		spot2;
	
	protected ViewZone(Spot from, Spot to) {
		super(from);
		spot2 = to;
	}

	protected void draw(Graphics2D canvas, ViewEcliptic view) {
		if (spot.getVisible() && spot2.getVisible()) {
			double	r = view.size - view.sizeChart*view.scale/2;
			double	start;
			double	sweep;

			paint.setStrokeWidth((int) (view.sizeChart * view.scale));
			
			double x = view.centerX - r;
			double y = view.centerY - r;

			if (view.dir) {
				start = -view.angle(spot2.getEcliptic().getLon());
				sweep = spot.angle(spot2);
			} else {
				start = -view.angle(spot.getEcliptic().getLon());
				sweep = -spot2.angle(spot);
			}
			
			if (sweep < 0) sweep += 360;

			paint.assign(canvas);
			
			canvas.drawArc(
				(int)Math.round(x), (int)Math.round(y), 
				(int)Math.round(r*2), (int)Math.round(r*2), 
				(int)Math.round(start), (int)Math.round(sweep)
			);
		}
	}

}
