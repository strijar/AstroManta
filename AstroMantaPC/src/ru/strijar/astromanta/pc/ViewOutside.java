package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import ru.strijar.astro.Spot;

/**
 * Отрисовка линии с внешней стороны знаков Зодиака
 */
public class ViewOutside extends ViewSpot {
	protected Point2D		point = new Point2D.Double();
	protected int			size = 10;
	private Image			bitmap;
	private int				bitmapW, bitmapH;
	
	protected ViewOutside(Spot spot) {
		super(spot);
	}

	/**
	 * Размер линии
	 *
	 * @param size размер (в точках)
	 */
	public void setSize(int size) {
		this.size = size;
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

			float r = view.getRadius();
			
			view.getXY(lon, r, point);
        	
        	int x = (int) point.getX();
        	int y = (int) point.getY();

        	r += size * view.scale;
        	
        	view.getXY(lon, r, point);			
			
			paint.assign(canvas);

			canvas.drawLine(
				(int)Math.round(x), (int)Math.round(y),
				(int)Math.round(point.getX()), (int)Math.round(point.getY()) 
			);
			
			if (bitmap != null) {
				view.getXY(spot.getEcliptic().getLon(), view.getRadius() + r * view.scale / 2, point);

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
