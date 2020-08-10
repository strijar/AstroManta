package ru.strijar.astromanta.pc;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import ru.strijar.astro.Spot;
import ru.strijar.astromanta.pc.ViewSpotCollect.HotPoint;

/**
 * Отрисовка планеты
 */

public class ViewPlanet extends ViewSpot {
	private Image			bitmap;
	private int				bitmapW, bitmapH;
	private int				size = 4;
	private double			radius = 0;
	private String			str;
	private float			textSize = (float) 14.0;
	private Font 			font;
	private Point2D			point = new Point2D.Double();
	
	protected double		shift;
	protected ViewPlanet	left;
	protected ViewPlanet	right;
	protected ViewPlanet	next;
	protected ViewPlanet	down;
	
	protected boolean		done;
	private float 			textW;
	private int 			textH;
	private AstroPaint		paintText = new AstroPaint();

	protected ViewPlanet(Spot spot) {
		super(spot);
	}

	/**
	 * Установить изображение
	 *
	 * @param pic объект изображения
	 */
	public void setBitmap(Image pic) {
		bitmap = pic;
		
		bitmapW = bitmap.getWidth(null);
		bitmapH = bitmap.getHeight(null);
	}

	/**
	 * Установить размер окружности 
	 *
	 * @param size размер (в точках)
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Получить тип отрисовки текста
	 *
	 * @return объект типа
	 */
	public AstroPaint getPaintText() {
		return paintText;
	}

	protected void preDraw(Graphics2D canvas, ViewEcliptic view) {
		double	lon = spot.getEcliptic().getLon();

		str = Integer.toString((int) Math.ceil(lon % 30));
		font = new Font(Font.MONOSPACED, Font.BOLD, (int) (textSize * view.scale));

		canvas.setFont(font);
		
		FontMetrics fontMetrics = canvas.getFontMetrics();
	
		textH = fontMetrics.getHeight();
		textW = fontMetrics.stringWidth(str);
		
		double w = bitmapW * view.scale + textW;
		double h = bitmapH * view.scale + textH;
		double d = Math.sqrt(w*w + h*h);
		
		radius = 360.0 / (view.size * Math.PI * 2.0) * d / 2.0;
		
		shift = lon;
		left = null;
		right = null;
		next = null;
		down = null;
		done = false;
	}

	protected double radius() {
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
			double 		lon = getLon() - radius() + radius;
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
		
		visitor.Visit(this);
		
		if (right != null) right.traverse(visitor);
	}
	
	protected double getLon() {
		if (down != null) {
			return spot.getEcliptic().getLon() + spot.angle(getLast().spot) / 2.0; 
		}
		
		return spot.getEcliptic().getLon();
	}

	protected void draw(Graphics2D canvas, ViewEcliptic view, HotPoint hot) {
		if (bitmap != null) {
			view.getXY(shift, view.size - view.sizeChart*view.scale/2, point);

			hot.setPoint((int)point.getX(), (int)point.getY());

			int w = (int) (bitmapW * view.scale);
			int h = (int) (bitmapH * view.scale);
			
			int x = (int) (point.getX() - (w + textW)/2);
			int y = (int) (point.getY() - h/2);

			Image img = bitmap.getScaledInstance(w, h, Image.SCALE_SMOOTH);

		    canvas.drawImage(img, x, y, null);
		    
			x += w;
			y += textH/2;

			paintText.assign(canvas);
			canvas.setFont(font);

		    canvas.drawString(str, x, y);

			if (spot.getEcliptic().getLonSpeed() < 0) {
				canvas.drawString("R", x, y+h-textH/2);
			}
		}
	}

	protected void drawInner(Graphics2D canvas, ViewEcliptic view) {
		int size = (int) (this.size * view.scale);
		
		view.getXY(spot.getEcliptic().getLon(), view.size, point);
		paint.assign(canvas);
		canvas.fillOval((int) point.getX() - size, (int) point.getY() - size, size*2, size*2);
	}

}
