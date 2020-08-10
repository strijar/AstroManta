package ru.strijar.astromanta.android;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import ru.strijar.astro.Charts;
import ru.strijar.astro.Spot;
import ru.strijar.astromanta.android.ViewSpotCollect.HotPoint;
import ru.strijar.astromanta.android.listener.TouchListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Область отрисовки карт
 */

public class ViewCharts extends SurfaceView implements Callback, Runnable {
	private ArrayList<ViewEcliptic>			viewChart = new ArrayList<ViewEcliptic>();
    private TouchListener					touchListener;
    private SurfaceHolder					holder;
    private Sys								sys;
    private Charts							charts;
    private Thread 							drawThread;
  
    private Bitmap							backBitmap;
    private BitmapDrawable					backDrawable;
    private int								backColor = Color.WHITE;
	
	private int								width, height;

	public ViewCharts(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        
        holder = getHolder();
        holder.addCallback(this);
	}

	private void Draw(Canvas canvas) {
        canvas.drawColor(backColor);

        if (backDrawable != null) {
			backDrawable.setBounds(0, 0, width, height);
        	backDrawable.draw(canvas);
        }
                
        for (ViewEcliptic item : viewChart)
        	if (item.getVisible()) 
        		item.draw(canvas);
	}

	public synchronized void run() {
		while (true) {
			try {
				Canvas canvas = holder.lockCanvas(null);

				if (canvas != null) {
					Draw(canvas);
					holder.unlockCanvasAndPost(canvas);
					wait();
				} else {
					wait(10);
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	protected void setCharts(Charts charts) {
		this.charts = charts;
	}

	protected void setSys(Sys sys) {
		this.sys = sys;
	}

	/**
	 * Добавить отрисовку карт на эклиптике
	 *
	 * @return объект отрисовки
	 */
	public ViewEcliptic addViewEcliptic() {
		ViewEcliptic item = new ViewEcliptic(charts, sys);

		viewChart.add(item);
	
		return item;
	}
	
	public boolean onTouchEvent(MotionEvent event) { 
		if (touchListener != null) {
			try {
				return touchListener.onTouchEvent(event);
			} catch (Exception e) {
				sys.onError(e);
			}
		}

		return super.onTouchEvent(event);
	}
	
	/**
	 * Установить обработчик нажатий на экран
	 * @param listener объект обработчика
	 */
	public void setTouchListener(TouchListener listener) {
		touchListener = listener;
	}

	/**
	 * Удалить отрисовку всех карт и обработчики событий
	 */
	public void clear() {
		viewChart.clear();

		touchListener = null;
	}

	/**
	 * Перерисовать область
	 */
	public synchronized void repaint() {
		notify();
	}

	/**
	 * Сохранить содержимое области в графический файл .png
	 *
	 * @param name имя файла
	 */
	public void screenshot(String name) {
    	Bitmap 			bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);                
    	Canvas 			canvas = new Canvas(bmp);
    	OutputStream 	outStream = null;    

    	Draw(canvas);
 
    	File 			path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    	java.io.File 	file = new java.io.File(path, name);

    	try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            Log.e("Screenshot", e.getMessage());
        }
    }

	public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new Thread(this);
		drawThread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.width = width;
		this.height = height;

    	repaint();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
		drawThread.interrupt();
    }

    /**
	 * Найти ближайшую точку на карте
	 * 
	 * @since 5.1
	 * @param x горизонтальная координата
	 * @param y вертикальная координата
	 * @param max максимальное расстояние до точки
	 * @return точка если она ближе чем параметр max, иначе null 
	 */
	public Spot getNear(int x, int y, int max) {
		HotPoint	near = null;
		int			dist = max;
		
		for (ViewEcliptic viewItem : viewChart ) {
			HotPoint hot = viewItem.getNear(x, y, dist);
			
			if (hot != null) {
				dist = hot.dist;
				near = hot;
			}
		}
		
		if (near == null)
			return null;
		
		return near.getSpot();
	}

	/**
	 * Установить фоновый рисунок
	 * @since 5.1
	 * @param file имя файла с рисунком
	 */
	public void setBackground(String file) {
		backBitmap = sys.loadBitmap(file, file);
		
		if (backBitmap != null) {
			backDrawable = new BitmapDrawable(getResources(), backBitmap);
			backDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		}
	}
	
	/**
	 * Установить цвет фона
	 * @since 5.1
	 * @param color цвет фона
	 */
	public void setBackground(int color) {
		backColor = color;
	}

}
