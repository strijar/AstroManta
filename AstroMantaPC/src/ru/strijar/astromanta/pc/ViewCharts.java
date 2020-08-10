package ru.strijar.astromanta.pc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import ru.strijar.astro.Charts;
import ru.strijar.astro.Spot;
import ru.strijar.astromanta.pc.ViewSpotCollect.HotPoint;

/**
 * Область отрисовки карт
 */

@SuppressWarnings("serial")
public class ViewCharts extends JPanel {
	private ArrayList<ViewChart>	viewChart = new ArrayList<ViewChart>();
	private Sys						sys;
	private Charts					charts;
	
	protected ViewCharts(Sys sys, Charts charts) {
		this.sys = sys;
		this.charts = charts;
		setBackground(Color.WHITE);
        setMinimumSize(new Dimension(640, 640));
		setVisible(true);
	}
	
	/**
	 * Удалить отрисовку всех карт и обработчики событий
	 */
	public void clear() {
		viewChart.clear();

		for (ComponentListener item : getComponentListeners())
			removeComponentListener(item);

		for (MouseMotionListener item : getMouseMotionListeners())
			removeMouseMotionListener(item);

		for (MouseListener item : getMouseListeners())
			removeMouseListener(item);
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

	private void draw(Graphics2D canvas) {
		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (ViewChart item : viewChart)
        	if (item.getVisible()) 
        		item.draw(canvas);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }	
    	
	/**
	 * Сохранить содержимое области в графический файл .png
	 *
	 * @param name имя файла
	 */
	public void screenshot(String name) {
	    BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	    
	    draw((Graphics2D) img.getGraphics());

	    try {
			ImageIO.write(img, "png", new File(name));
		} catch (IOException e) {
		}
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
		
		for (ViewChart viewItem : viewChart ) {
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
	
}
