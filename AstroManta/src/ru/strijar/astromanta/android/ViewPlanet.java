package ru.strijar.astromanta.android;

import ru.strijar.astro.Spot;
import ru.strijar.astromanta.android.ViewSpotCollect.HotPoint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Отрисовка планеты
 */

public class ViewPlanet extends ViewSpot {
	private Bitmap			bitmap;
	private Point			point = new Point();
	private AstroPaint		paintText = new AstroPaint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	private float			textSize = (float) 14.0;
	private int				bitmapW, bitmapH;
	private int				size = 4;
	private Rect			bounds = new Rect();
	private Rect			bitmapBounds = new Rect();
	private double			radius = 0;
	private String			str;

	protected double		shift;
	protected ViewPlanet	left;
	protected ViewPlanet	right;
	protected ViewPlanet	next;
	protected ViewPlanet	down;
	
	protected boolean		done;

	protected ViewPlanet(Spot spot) {
		super(spot);

		paintText.setStrokeWidth(0);
		paintText.setStyle(Paint.Style.FILL);   
		paintText.setTextSize(textSize);
		paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
	}

	/**
	 * Установить изображение
	 *
	 * @param pic объект изображения
	 */
	public void setBitmap(Bitmap pic) {
		bitmap = pic;
		
		bitmapW = bitmap.getWidth();
		bitmapH = bitmap.getHeight();
	}

	/**
	 * Получить тип отрисовки текста
	 *
	 * @return объект типа
	 */
	public AstroPaint getPaintText() {
		return paintText;
	}
	
	/**
	 * Установить размер окружности 
	 *
	 * @param size размер (в точках)
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	protected void preDraw(ViewEcliptic view) {
		double	lon = spot.getEcliptic().getLon();

		str = Integer.toString((int) Math.ceil(lon % 30));

		paintText.setTextSize(textSize * view.scale);
		paintText.getTextBounds(str, 0, str.length(), bounds);
		
		double w = bitmapW * view.scale + bounds.right;
		double h = bitmapH * view.scale + bounds.bottom;
		double d = Math.sqrt(w*w + h*h);
		
		radius = 360.0 / (view.size * Math.PI * 2.0) * d / 2.0;
		
		shift = lon;
		left = null;
		right = null;
		next = null;
		down = null;
		done = false;
	}
	
	protected double getRadius() {
		double		r = radius;
		ViewPlanet	item = down;
		
		while (item != null) {
			r += item.radius;
			item = item.down;
		}
		
		return r;
	}
	
	private ViewPlanet getLast() {
		ViewPlanet item = this;

		while (item.down != null) {
			item = item.down;
		}

		return item;
	}
	
	protected void append(ViewPlanet view) {
		ViewPlanet item = this;
		
		while (item.down != null)
			item = item.down;
		
		item.down = view;
	}
	
	protected void shift() {
		if (down != null) {
			double 		lon = getLon() - getRadius() + radius;
			ViewPlanet	cur = this;
			
			while (cur != null) {
				cur.shift = lon;
				lon += cur.radius;
				
				cur = cur.down;
				if (cur != null) lon += cur.radius;
			}
		}
	}
	
	protected void insert(ViewPlanet item) {
		if (item.spot.getEcliptic().getLon() < spot.getEcliptic().getLon()) {
			if (left != null) {
		      	left.insert(item);
		    } else {
		       	left = item;
		    }
		} else {
			if (right != null ) {
				right.insert(item);
			} else {
				right = item;
			}
		}
	}
	
	protected void traverse(ViewPlanetVisitor visitor) {
		if (left != null) left.traverse(visitor);
		
		visitor.visit(this);
		
		if (right != null) right.traverse(visitor);
	}
	
	protected double getLon() {
		if (down != null) {
			return spot.getEcliptic().getLon() + spot.angle(getLast().spot) / 2.0; 
		}
		
		return spot.getEcliptic().getLon();
	}
	
	protected void draw(Canvas canvas, ViewEcliptic view, HotPoint hot) {
		if (bitmap != null) {
			view.getXY(shift, view.size - view.sizeChart*view.scale/2, point);

			hot.setPoint(point.x, point.y);
			
			int w = (int) (bitmapW * view.scale);
			int h = (int) (bitmapH * view.scale);
			
			bitmapBounds.left = point.x - (w + bounds.right)/2;
			bitmapBounds.top = point.y - h/2;

			bitmapBounds.right = bitmapBounds.left + w;
			bitmapBounds.bottom = bitmapBounds.top + h;

		    canvas.drawBitmap(bitmap, null, bitmapBounds, paintText);
			
			bitmapBounds.left += w;
			bitmapBounds.top -= bounds.top/2;
			
			canvas.drawText(str, bitmapBounds.left, bitmapBounds.top, paintText);

			if (spot.getEcliptic().getLonSpeed() < 0) {
				canvas.drawText("R", bitmapBounds.left, bitmapBounds.top + h, paintText);
			}
		}
	}

	protected void drawInner(Canvas canvas, ViewEcliptic view) {
		float size = (float) (this.size * view.scale);
		
		view.getXY(spot.getEcliptic().getLon(), view.size, point);
		canvas.drawCircle(point.x - size/2, point.y - size/2, size, paint);
	}
}
