package ru.strijar.astromanta.pc;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import ru.strijar.astro.Spot;

/**
 * Отрисовка линии с внешней стороны Знаков с именем точки
 */

public class ViewOutsideInfo extends ViewOutside {
	private float			textSize = (float) 16.0;
	private Font 			font;
	private AstroPaint		paintText;
	private String			str;

	protected ViewOutsideInfo(Spot spot) {
		super(spot);

		paintText = new AstroPaint();
		str = spot.getName();
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
	 * Установить тип отрисовки текста
	 *
	 * @param paint тип отрисовки
	 */
	public void setPaintText(AstroPaint paint) {
		paintText = paint;
	}
	
	/**
	 * Установить размер текста
	 *
	 * @param size размер
	 */
	public void setTextSize(int size) {
		textSize = size;
	}
	
	protected void draw(Graphics2D canvas, ViewEcliptic view) {
		super.draw(canvas, view);
		
		if (spot.getVisible()) {
			font = new Font(Font.MONOSPACED, Font.BOLD, (int) (textSize * view.scale));

			canvas.setFont(font);
			
			FontMetrics fontMetrics = canvas.getFontMetrics();

			int		h = fontMetrics.getHeight();
			int		w = fontMetrics.stringWidth(str);
			float	d = (float) Math.sqrt(w*w + h*h) / 2.0f;

			view.getXY(spot.getEcliptic().getLon(), view.getRadius() + size * view.scale + d, point);

			int		x = (int) (point.getX() - w/2);
			int		y = (int) (point.getY() + h/2);

			paintText.assign(canvas);
			canvas.drawString(str, x, y);
		}
	}
}
