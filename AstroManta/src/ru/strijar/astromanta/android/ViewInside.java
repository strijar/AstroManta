package ru.strijar.astromanta.android;

import ru.strijar.astro.Spot;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint;

/**
 * Отрисовка линии внутри поля карт
 */

public class ViewInside extends ViewSpot {
	protected Point			point = new Point();
	private Bitmap			bitmap;
	private int				bitmapW, bitmapH;
	private Rect			rect = new Rect();
	private Paint			paintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	public ViewInside(Spot spot) {
		super(spot);
	}

	/**
	 * Установить изображение
	 *
	 * @param bitmap объект изображения
	 */
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		bitmapW = bitmap.getWidth();
		bitmapH = bitmap.getHeight();
	}

	protected void draw(Canvas canvas, ViewEcliptic view) {
		if (spot.getVisible()) {
			int 	x, y;
			double	lon = spot.getEcliptic().getLon();

			view.getXY(lon, view.size, point);
        	
        	x = point.x;
        	y = point.y;

        	float size = view.sizeChart * view.scale;
        	
        	view.getXY(lon, view.size - size, point);			
			view.getXY(spot.getEcliptic().getLon(), view.size - size, point);
			canvas.drawLine(point.x, point.y, x, y, paint);

			if (bitmap != null) {
				view.getXY(spot.getEcliptic().getLon(), view.size - view.sizeChart * view.scale/2, point);

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
