package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import ru.strijar.astro.Spot;

/**
 * Отрисовка линии внутри поля карт
 */

public class ViewInside extends ViewSpot {
	protected Point2D		point = new Point2D.Double();
	private Image			bitmap;
	private int				bitmapW, bitmapH;

	protected ViewInside(Spot spot) {
		super(spot);
	}

	/**
	 * Установить изображение
	 *
	 * @param bitmap объект изображения
	 */
	public void setBitmap(Image bitmap) {
		this.bitmap = bitmap;
		bitmapW = bitmap.getWidth(null);
		bitmapH = bitmap.getHeight(null);
	}

	protected void draw(Graphics2D canvas, ViewEcliptic view) {
		if (spot.getVisible()) {
			double	lon = spot.getEcliptic().getLon();

			view.getXY(lon, view.size, point);
        	
        	int x = (int) point.getX();
        	int y = (int) point.getY();

        	float size = view.sizeChart * view.scale;
        	
        	view.getXY(lon, view.size - size, point);			
			
			paint.assign(canvas);

			canvas.drawLine(
				(int)Math.round(point.getX()), (int)Math.round(point.getY()), 
				(int)Math.round(x), (int)Math.round(y)
			);

			if (bitmap != null) {
				view.getXY(spot.getEcliptic().getLon(), view.size - view.sizeChart * view.scale/2, point);

				int w = (int) (bitmapW * view.scale);
				int h = (int) (bitmapH * view.scale);

				x = (int) (point.getX() - w/2);
				y = (int) (point.getY() - h/2);

				Image img = bitmap.getScaledInstance(w, h, Image.SCALE_SMOOTH);

				canvas.drawImage(img, x,y, null);
			}
		}
	}

}
