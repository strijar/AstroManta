package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import ru.strijar.astro.Coord;

/**
 * Отрисовка знака Зодиака
 */

public class ViewSign {
	private Image		bitmap;
	private int 		bitmapW;
	private int 		bitmapH;
	protected double	from, to;
    private Point2D		point = new Point2D.Double();
	private AstroPaint	paint = new AstroPaint();

	protected ViewSign(double from, double to) {
		this.from = from;
		this.to = to;

        paint.setColor(0xD0EFFF, 255);
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
		float	r = view.size - view.sizeBlank*view.scale/2;
		double	start;
		double	sweep;

		paint.setStrokeWidth((int) (view.sizeBlank * view.scale));
		
		double x = view.centerX - r;
		double y = view.centerY - r;

		if (view.dir) {
			start = -view.angle(to);
			sweep = Coord.angle(from, to);
		} else {
			start = -view.angle(from);
			sweep = -Coord.angle(to, from);
		}
		
		if (sweep < 0) sweep += 360;

		paint.assign(canvas);
		
		canvas.drawArc(
			(int)Math.round(x), (int)Math.round(y), 
			(int)Math.round(r*2), (int)Math.round(r*2), 
			(int)Math.round(start), (int)Math.round(sweep)
		);

    	if (bitmap != null) {
        	view.getXY(from + Coord.angle(from, to)/2, r, point);

        	int w = (int) (bitmapW * view.scale);
			int h = (int) (bitmapH * view.scale);

         	x = (point.getX() - w/2);
         	y = (point.getY() - h/2);

         	Image img = bitmap.getScaledInstance(w, h, Image.SCALE_SMOOTH);

			canvas.drawImage(
				img, 
				(int)Math.round(x), (int)Math.round(y), 
				(int)Math.round(w), (int)Math.round(h), 
			null);
    	}
	}

}
