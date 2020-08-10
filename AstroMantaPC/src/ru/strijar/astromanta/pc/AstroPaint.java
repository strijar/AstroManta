package ru.strijar.astromanta.pc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import com.alee.graphics.strokes.ShapeStroke;

/**
 * Тип отрисовки
 */

public class AstroPaint {
	protected Color		color;
	protected Stroke	stroke = new BasicStroke();
	private int			width = 1;
	
	/**
	 * Установить цвет
	 *
	 * @param rgb значение цвета
	 */
	public void setColor(int rgb) {
		color = new Color(rgb);
	}

	/**
	 * Установить цвет и прозрачность
	 *
	 * @param rgb значение цвета
	 * @param alpha значение прозрачности
	 */
	public void setColor(int rgb, int alpha) {
		color = new Color(alpha << 24 | rgb, true);
	}
		
	/**
	 * Установить ширину линии
	 *
	 * @param width ширина (в точках)
	 */
	public void setStrokeWidth(int width) {
		this.width = width;
		stroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}
	
	/**
	 * Установить стиль штриха
	 *
	 * @param on размер штриха (в точках)
	 * @param off размер пропуска между штрихами (в точках)
	 */
	public void setStyle(int on, int off) {
	    float[] dashPattern = { on, off + width };

	    stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
	        1.0f, dashPattern, 0);
	}

	/**
	 * Установить стиль в виде однонаправленной линии прямого направления
	 *
	 * @param width ширина линии (в точках)
	 */
	public void setForwardStyle(float width) {
		Path2D stamp = new Path2D.Double();
		
		stamp.moveTo(0, 0);
		stamp.lineTo(-width, -width);
		stamp.lineTo(width, -width);
		stamp.lineTo(2*width, 0);
		stamp.lineTo(width, width);
		stamp.lineTo(-width, width);
        stamp.closePath();

		stroke = new ShapeStroke(stamp, 2.5f*width);
	}

	/**
	 * Установить стиль в виде однонаправленной линии обратного направления
	 *
	 * @param width ширина линии (в точках)
	 */
	public void setBackwardStyle(float width) {
		Path2D stamp = new Path2D.Double();
		
		stamp.moveTo(0, 0);
		stamp.lineTo(width, -width);
		stamp.lineTo(-width, -width);
		stamp.lineTo(-2*width, 0);
		stamp.lineTo(-width, width);
		stamp.lineTo(width, width);
        stamp.closePath();

		stroke = new ShapeStroke(stamp, 2.5f*width);
	}

	/**
	 * Установить стиль в виде двунаправленной линии
	 *
	 * @param width ширина линии
	 */
	public void setBidirectStyle(float width) {
		Path2D stamp1 = new Path2D.Double();

		stamp1.moveTo(0, 0);
		stamp1.lineTo(width, width);
        stamp1.lineTo(2.0f*width, width);
        stamp1.lineTo(3.0f*width, 0);
        stamp1.lineTo(2.0f*width, -width);
        stamp1.lineTo(width, -width);
        stamp1.closePath();

        Path2D stamp2 = new Path2D.Double();

        stamp1.moveTo(0, 0);
        stamp2.moveTo(3.5f*width, 0);
        stamp2.lineTo(2.5f*width, width);
        stamp2.lineTo(5.5f*width, width);
        stamp2.lineTo(4.5f*width, 0);
        stamp2.lineTo(5.5f*width, -width);
        stamp2.lineTo(2.5f*width, -width);
        stamp2.closePath();

        stroke = new ShapeStroke(new Shape[] { stamp1, stamp2 }, 2.5f*width);
	}
		
	protected void assign(Graphics2D canvas) {
		canvas.setColor(color);
		canvas.setStroke(stroke);
	}
}
