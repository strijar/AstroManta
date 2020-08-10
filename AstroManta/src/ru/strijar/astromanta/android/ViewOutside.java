package ru.strijar.astromanta.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Отрисовка линии с внешней стороны знаков Зодиака
 */

public class ViewOutside extends ViewSpot {
	protected Point			point = new Point();

	private Bitmap			bitmap;
	private int				bitmapW, bitmapH;
	private Rect			rect = new Rect();
	private Paint			paintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	protected int			size = 10;

	protected ViewOutside(ru.strijar.astro.Spot spot) {
		super(spot);
	}

	/**
	 * Установить изображение
	 *
	 * @param bitmap объект изображения
	 */
	public void setBitmap(Bitmap pic) {
		this.bitmap = pic;
		bitmapW = pic.getWidth();
		bitmapH = pic.getHeight();
	}

	/**
	 * Размер линии
	 *
	 * @param size размер (в точках)
	 */
	public void setSize(int size) {
		this.size = size;
	}

	protected void draw(Canvas canvas, ViewEcliptic view) {
		if (spot.getVisible()) {
			int 	x, y;
			double	lon = spot.getEcliptic().getLon();
        	float	r = view.getRadius();

			view.getXY(lon, r, point);
        	
        	x = point.x;
        	y = point.y;

        	r += size * view.scale;

        	view.getXY(lon, r, point);			
			canvas.drawLine(point.x, point.y, x, y, paint);

			if (bitmap != null) {
				view.getXY(spot.getEcliptic().getLon(), view.getRadius() + size * view.scale/2, point);

				int w = (int) (bitmapW * view.scale);
				int h = (int) (bitmapH * view.scale);

				rect.left = point.x - w/2;
				rect.top = point.y - h/2;

				rect.right = rect.left + w;
				rect.bottom = rect.top + h;
				
				canvas.drawBitmap(bitmap, null, rect, paintBitmap);
			}
		}
	}

}
