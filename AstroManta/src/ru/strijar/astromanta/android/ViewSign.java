package ru.strijar.astromanta.android;

import ru.strijar.astro.Coord;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Отрисовка знака Зодиака
 */

public class ViewSign {
	private Bitmap		bitmap;
	private AstroPaint	paint;
	private RectF		rect = new RectF();
    private Point		point = new Point();

	protected double	from, to;

	protected ViewSign(double from, double to) {
		paint = new AstroPaint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		
		paint.setStrokeCap(Paint.Cap.BUTT);
	    paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xD0EFFF);
        paint.setAlpha(255);
	    
	    this.from = from;
	    this.to = to;
	}

	/**
	 * Установить изображение
	 *
	 * @param bitmap объект изображения
	 */
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	/**
	 * Получить тип отрисовки
	 *
	 * @return объект типа
	 */
	public AstroPaint getPaint() {
		return paint;
	}

	/**
	 * Установить тип отрисовки
	 *
	 * @param paint объект типа
	 */
	public void setPaint(AstroPaint paint) {
		this.paint = paint;
	}
	
	protected void draw(Canvas canvas, ViewEcliptic view) {
		float	r = view.size - view.sizeBlank*view.scale/2;
		double	start;
		double	sweep;

		paint.setStrokeWidth(view.sizeBlank * view.scale);
		rect.set(view.centerX - r, view.centerY - r, view.centerX + r, view.centerY + r);
		
		if (view.dir) {
			start = view.angle(from);
			sweep = Coord.angle(from, to);
		} else {
			start = view.angle(to);
			sweep = -Coord.angle(to, from);
		}
		
		if (sweep < 0) sweep += 360;

		canvas.drawArc(rect, (float)start, (float)sweep, false, paint);
		
    	if (bitmap != null) {
        	view.getXY(from + Coord.angle(from, to)/2, r, point);

        	int w = (int) (bitmap.getWidth() * view.scale);
			int h = (int) (bitmap.getHeight() * view.scale);

         	rect.left = point.x - w/2;
         	rect.top = point.y - h/2;
			rect.right = rect.left + w;
			rect.bottom = rect.top + h;

			canvas.drawBitmap(bitmap, null, rect, paint);
    	}
	}
}
