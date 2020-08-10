package ru.strijar.astromanta.android;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;

/**
 * Тип отрисовки
 */

public class AstroPaint extends Paint {
	protected AstroPaint(int antiAliasFlag) {
		super(antiAliasFlag);
		setStrokeCap(Cap.ROUND);
	}

	/**
	 * Установить цвет и прозрачность
	 *
	 * @param rgb значение цвета
	 * @param alpha значение прозрачности
	 */

	public void setColor(int color, int alpha) {
		setColor(color);
		setAlpha(alpha);
	}

	/**
	 * Установить стиль штриха
	 *
	 * @param on размер штриха (в точках)
	 * @param off размер пропуска между штрихами (в точках)
	 */
	public void setStyle(int on, int off) {
		setPathEffect(new DashPathEffect(new float[] { on, off + getStrokeWidth() }, 0));
	}
	
	public void setFill(boolean on) {
		setStyle(on ? Paint.Style.FILL : Paint.Style.STROKE);
	}

	/**
	 * Установить стиль в виде однонаправленной линии прямого направления
	 *
	 * @param width ширина линии (в точках)
	 */
	public void setForwardStyle(float width) {
		Path stamp = new Path();
	
		stamp.lineTo(-width, -width);
		stamp.lineTo(width, -width);
		stamp.lineTo(2*width, 0);
		stamp.lineTo(width, width);
		stamp.lineTo(-width, width);
        stamp.close();
        
        setPathEffect(new PathDashPathEffect(stamp, (float) (2.5*width), 0, PathDashPathEffect.Style.MORPH));
	}

	/**
	 * Установить стиль в виде однонаправленной линии обратного направления
	 *
	 * @param width ширина линии (в точках)
	 */
	public void setBackwardStyle(float width) {
		Path stamp = new Path();
	
		stamp.lineTo(width, -width);
		stamp.lineTo(-width, -width);
		stamp.lineTo(-2*width, 0);
		stamp.lineTo(-width, width);
		stamp.lineTo(width, width);
        stamp.close();
        
        setPathEffect(new PathDashPathEffect(stamp, (float) (2.5*width), 0, PathDashPathEffect.Style.MORPH));
	}

	/**
	 * Установить стиль в виде двунаправленной линии
	 *
	 * @param width ширина линии
	 */
	public void setBidirectStyle(float width) {
		Path stamp = new Path();
	
        stamp.lineTo(width, width);
        stamp.lineTo(2.0f*width, width);
        stamp.lineTo(3.0f*width, 0);
        stamp.lineTo(2.0f*width, -width);
        stamp.lineTo(width, -width);
        stamp.close();

        stamp.moveTo(3.5f*width, 0);
        stamp.lineTo(2.5f*width, width);
        stamp.lineTo(5.5f*width, width);
        stamp.lineTo(4.5f*width, 0);
        stamp.lineTo(5.5f*width, -width);
        stamp.lineTo(2.5f*width, -width);
        stamp.close();
        
        setPathEffect(new PathDashPathEffect(stamp, 5.0f*width, 0, PathDashPathEffect.Style.MORPH));
	}

}
