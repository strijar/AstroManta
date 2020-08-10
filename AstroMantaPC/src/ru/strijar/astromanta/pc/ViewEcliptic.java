package ru.strijar.astromanta.pc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import ru.strijar.astro.AspectData;
import ru.strijar.astro.AspectTable;
import ru.strijar.astro.Chart;
import ru.strijar.astro.Charts;
import ru.strijar.astro.Spot;
import ru.strijar.astromanta.pc.ViewSpotCollect.HotPoint;

/**
 * Отображение карт и аспектов на эклиптике
 */

public class ViewEcliptic extends ViewChart {
	protected float							scale = (float) 1.0;
    protected int							centerX;
    protected int							centerY;
    protected Spot							start;
    protected boolean 						dir = false;
    private int								radius;

    protected int 							size;
    protected int 							sizeBlank = 30;
    protected int 							sizeChart = 30;

	private Sys								sys;
	private Charts							charts;
    private AspectTable						aspectTable = new AspectTable();
    private HashMap<Chart, ViewSpotCollect>	viewSpotCollect = new HashMap<Chart, ViewSpotCollect>();
    private HashMap<String, ViewAspect>		viewAspect = new HashMap<String, ViewAspect>();

	private ArrayList<ViewSign>				viewSign = new ArrayList<ViewSign>();
    private ArrayList<String>				listSign = new ArrayList<String>();
	private Point2D 						point = new Point2D.Double();
	private AstroPaint 						backPaint = new AstroPaint();

	protected ViewEcliptic(Charts charts, Sys sys) {
		this.charts = charts;
		this.sys = sys;
		
		backPaint.setColor(0xFFFFFF, 192);
	}

	/**
	 * Установить центр
	 *
	 */
	public void setCenter(int x, int y) {
    	centerX = x;
    	centerY = y;
    }

	/**
	 * Получить объект отрисовки фона
	 *
	 */
	public AstroPaint getBackPaint() {
		return backPaint;
	}
	
	private ViewSpotCollect checkCollection(Chart chart) {
		ViewSpotCollect	collect = getCollection(chart);

		if (collect == null) {
			collect = new ViewSpotCollect();
			viewSpotCollect.put(chart, collect);
		}
		
		return collect;
	}

    private ViewSpotCollect getCollection(Chart chart) {
		return viewSpotCollect.get(chart);
	}
	
    /**
     * Клонировать отрисовку карты из другого объекта
     *
     * @param view оригинал объекта отрисовки
     * @param chart выбранная карта
     */
    public void cloneCollection(ViewEcliptic view, Chart chart) {
    	ViewSpotCollect orig = view.getCollection(chart);
    	
    	if (orig != null) {
    		ViewSpotCollect collect = new ViewSpotCollect(orig);
   
    		viewSpotCollect.put(chart, collect);
    	}
    }
    
	/**
	 * Очистить отрисовку карт и аспектов
	 */
	public void clear() {
		viewSpotCollect.clear();
		viewAspect.clear();
		aspectTable.clear();
		start = null;
	}

	/**
	 * Очистить отрисовку знаков Зодиака
	 */
	public void clearSign() {
		viewSign.clear();
		listSign.clear();
	}

	/**
	 * Добавить отрисовку знака Зодиака
	 *
	 * @param name имя знака
	 * @param from начальная долгота знака
	 * @param to конечная долгота знака
	 * @param file имя файла с изображением
	 * @return объект отрисовки знака
	 */
	public ViewSign addSign(String name, double from, double to, String file) {
		ViewSign sign = new ViewSign(from, to);
		
		viewSign.add(sign);
		
	   	Image bitmap = sys.loadBitmap(name, file);

		if (bitmap != null) {
			sign.setBitmap(bitmap);
		}

		return sign;
	}
	
	/**
	 * Добавить метку знака Зодиака<br>
	 * Используется для {@link ru.strijar.astro.Coord#getLonStr(Object[])}
	 *
	 * @param label символьная метка
	 */
	public void addSignLabel(String label) {
		listSign.add(label);
	}

	/**
	 * Добавить отрисовку планеты
	 *
	 * @param spot объект точки карты
	 * @param file имя файла с изображением
	 * @return объект отрисовки
	 */
	public ViewPlanet addPlanet(Spot spot, String file) {
		ViewPlanet		view = new ViewPlanet(spot);

		checkCollection(spot.getChart()).Add(view);
			
	   	Image pic = sys.loadBitmap(spot.getName(), file);
	   	
	   	if (pic != null) {
	    	view.setBitmap(pic);
	    }
		
		return view;
	}

	/**
	 * Добавить отрисовку линии с внешней стороны Знаков
	 *
	 * @param spot объект точки карты
	 * @return объект отрисовки
	 */
	public ViewOutside addOutside(Spot spot) {
		ViewOutside view = new ViewOutside(spot);

		checkCollection(spot.getChart()).Add(view);
		
		return view;
	}

	/**
	 * Добавить отрисовку линии с внешней стороны Знаков
	 *
	 * @param spot объект точки карты
	 * @param file имя файла с изображением
	 * @return объект отрисовки
	 */
	public ViewOutside addOutside(Spot spot, String file) {
		ViewOutside view = new ViewOutside(spot);

		checkCollection(spot.getChart()).Add(view);

		Image pic = sys.loadBitmap(spot.getName(), file);
	    
	    if (pic != null) {
			view.setBitmap(pic);
		}
		
		return view;
	}

	/**
	 * Добавить отрисовку линии с внешней стороны Знаков с именем точки
	 *
	 * @param spot объект точки карты
	 * @return объект отрисовки
	 */
	public ViewOutsideInfo addOutsideInfo(Spot spot) {
		ViewOutsideInfo view = new ViewOutsideInfo(spot);

		checkCollection(spot.getChart()).Add(view);
		
		return view;
	}

	/**
	 * Добавить отрисовку линии внутри области карты
	 *
	 * @param spot объект точки карты
	 * @return объект отрисовки
	 */
	public ViewInside addInside(Spot spot) {
		ViewInside view = new ViewInside(spot);

		checkCollection(spot.getChart()).Add(view);

		return view;
	}

	/**
	 * Добавить отрисовку линии внутри области карты
	 *
	 * @param spot объект точки карты
	 * @param file имя файла с изображением
	 * @return объект отрисовки
	 */
	public ViewInside addInside(Spot spot, String file) {
		ViewInside view = new ViewInside(spot);

		checkCollection(spot.getChart()).Add(view);

	    Image pic = sys.loadBitmap(spot.getName(), file);
	    
	    if (pic != null) {
			view.setBitmap(pic);
		}
		
		return view;
	}

	/**
	 * Добавить отрисовку зоны внутри области карты
	 *
	 * @param from точка карты начала зоны
	 * @param to точка карты конца зоны
	 * @return объект отрисовки
	 */
	public ViewZone addZone(Spot from, Spot to) {
		ViewZone view = new ViewZone(from, to);

		checkCollection(from.getChart()).Add(view);
		
		return view;
	}

	/**
	 * Добавить отрисовку аспекта в виде прямой линии
	 *
	 * @param info информация аспекта
	 * @return объект отрисовки
	 */
	public ViewAspect addAspect(String info) {
		ViewAspect item =  new ViewAspect();
		
		viewAspect.put(info, item);
		
		return item;
	}

	/**
	 * Добавить отрисовку аспекта в виде дуги
	 *
	 * @param info информация аспекта
	 * @param curve величина искривления дуги (0.0 .. 1.0)
	 * @return объект отрисовки
	 */
	public ViewAspect addAspectCurve(String info, float curve) {
		ViewAspect item =  new ViewAspectCurve(curve);
		
		viewAspect.put(info, item);
		
		return item;
	}

	/**
	 * Получить массив меток знаков Зодиака<br>
	 * Используется для {@link ru.strijar.astro.Coord#getLonStr(Object[])}
	 */
	public Object[] getSignLabels() {
		return listSign.toArray();
	}

	/**
	 * Установить направление отображения Зодиака
	 *
	 * @param cw true если по часовой стрелке и false если против
	 */
	public void setDir(boolean cw) {
		this.dir = cw;
	}

	/**
	 * Получить направление отображения Зодиака
	 *
	 * @return the dir
	 */
	public boolean getDir() {
		return dir;
	}
	
	/**
	 * Установить размер (ширину) поля знаков Зодиака
	 *
	 * @param size размер (в точках)
	 */
	public void setSizeBlank(int size) {
		sizeBlank = size;
	}

	/**
	 * Получить размер поля знаков Зодиака
	 *
	 * @return the size blank
	 */
	public int getSizeBlank() {
		return sizeBlank;
	}
	
	/**
	 * Установить размер (ширину) поля карты
	 *
	 * @param size размер (в точках)
	 */
	public void setSizeChart(int size) {
		sizeChart = size;
	}

	/**
	 * Получить размер поля карты
	 *
	 * @return the size chart
	 */
	public int getSizeChart() {
		return sizeChart;
	}
	
	/**
	 * Установить точку карты для начала отсчета отрисовки
	 *
	 * @param start объект точки или null если отрисовывать от Овна
	 */
	public void setStart(Spot start) {
		this.start = start;
	}
	
	/**
	 * Получить начало отсчета отрисовки
	 *
	 * @return объект точки карты или null если отрисовка от Овна
	 */
	public Spot getStart() {
		return start;
	}

	/**
	 * Установить масштаб
	 *
	 * @param scale масштаб (0.25 .. 2.0)
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	/**
	 * Получить масштаб
	 *
	 * @return масштаб (0.25 .. 2.0)
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Установить значение внешнего радиуса области отрисовки
	 *
	 * @param radius радиус (в точках)
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Получить внешний радиус области отрисовки
	 *
	 * @return радиус (в точках)
	 */
	public int getRadius() {
		return radius;
	}
	
	/**
	 * Установить видимость карты
	 *
	 * @param on видимость
	 * @param chart выбранная карта
	 */
	public void setVisible(boolean on, Chart chart) {
		ViewSpotCollect collection = getCollection(chart);
		
		if (collection != null)
			collection.Visible(on);
	}
	
	/**
	 * Получить объект таблицы аспектов
	 *
	 */
	public AspectTable getAspectTable() {
		return aspectTable;
	}

	/**
	 * Установить объект таблицы аспектов
	 *
	 */
	public void setAspectTable(AspectTable aspects) {
		this.aspectTable = aspects;
	}

	protected void draw(Graphics2D canvas) {
		if (charts == null) return;
		
		size = radius;
		
		if (!charts.getCharts().isEmpty()) {
			drawBlank(canvas);
			drawCharts(canvas);
		}
	}

	protected double angle(double Lon) {
		double res;

		if (dir) {
			res = Lon;
		} else {
			res = 180.0 - Lon;
		}

		if (start != null) {
			if (dir) {
				res = res - start.getEcliptic().getLon();				
			} else {
				res = res + start.getEcliptic().getLon();
			}
		}

		return res;
	}

	protected void getXY(double Lon, float r, Point2D res) {
		double lon = angle(Lon);

		double y = Math.sin(Math.PI * lon / 180.0) * r;
	    double x = Math.cos(Math.PI * lon / 180.0) * r;

	    res.setLocation(centerX + (int)x, centerY + (int)y);
	}

	protected void drawBlank(Graphics2D canvas) {
		backPaint.assign(canvas);
		canvas.fillOval(centerX-radius, centerY-radius, radius*2, radius*2);
		
		Stroke stroke = new BasicStroke(scale);

		canvas.setColor(Color.BLACK);
		canvas.setStroke(stroke);
				
        for (ViewSign item: viewSign)
        	item.draw(canvas, this);

        canvas.setColor(Color.BLACK);
		canvas.setStroke(stroke);
        
        for (ViewSign item: viewSign) {
        	getXY(item.from, size, point);
        	
        	double x = point.getX();
        	double y = point.getY();

        	getXY(item.from, size - sizeBlank * scale, point);

        	canvas.drawLine(
        		(int) Math.round(point.getX()), (int) Math.round(point.getY()), 
        		(int) Math.round(x), (int) Math.round(y));
        }

        int r = size;

        canvas.setColor(Color.BLACK);
        canvas.setStroke(new BasicStroke(3 * scale));
        canvas.drawOval(centerX-r, centerY-r, r*2, r*2);

        r = (int)(size - sizeBlank * scale);

        canvas.setStroke(new BasicStroke(2 * scale));
        canvas.drawOval(centerX-r, centerY-r, r*2, r*2);
	}

	protected void drawCharts(Graphics2D canvas) {
		ViewSpotCollect collection;
		Stroke	stroke = new BasicStroke(1);

		size -= sizeBlank * scale;

		canvas.setStroke(stroke);

		if (charts == null) return;
		
    	for (Chart item: charts.getCharts())
			if (item.getVisible()) { 
				collection = getCollection(item);
				
				if (collection != null && collection.Visible()) {
					collection.DrawChart(this, canvas);

					size -= sizeChart * scale;

					canvas.setColor(Color.BLACK);
					canvas.setStroke(stroke);
					canvas.drawOval(centerX - size, centerY - size, size*2, size*2);
				}
			}

    	Iterator<AspectData> aspects = aspectTable.iterator();
    	
    	while (aspects.hasNext()) {
    		AspectData item = aspects.next();
			ViewAspect view = viewAspect.get(item.getInfo());
			
			if (view != null) {
				view.draw(item, canvas, this);
			}
    	}
    	    	
    	for (Chart item: charts.getCharts())
			if (item.getVisible()) { 
				collection = getCollection(item);
				
				if (collection != null && collection.Visible()) 
					collection.DrawInner(this, canvas);
			}
	}

	protected HotPoint getNear(int x, int y, int max) {
		HotPoint	near = null;
		int			dist = max;
		
		Iterator<Entry<Chart, ViewSpotCollect>> it = viewSpotCollect.entrySet().iterator();

		while (it.hasNext()) {
			Entry<Chart, ViewSpotCollect>	item = it.next();
			HotPoint	hot = item.getValue().getNear(x, y, dist);
			
			if (hot != null) {
				dist = hot.dist;
				near = hot;
			}

		}
		
		return near;
	}

}
