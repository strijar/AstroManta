package ru.strijar.astromanta.pc;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import ru.strijar.astro.Coord;
import ru.strijar.astro.Spot;

public class ViewSpotCollect implements ViewPlanetVisitor {
	protected ArrayList<ViewSpot>		spot;
    protected ArrayList<ViewPlanet>		spotWide;
    protected HashMap<String, HotPoint>	hotPoint = new HashMap<String, HotPoint>();

    private boolean						visible = true;
    private	ViewPlanet					tree;
    private ViewPlanet					head, tail;

    protected class HotPoint {
		private int			x, y;
		private Spot		spot;
		protected int		dist = Integer.MAX_VALUE;
		
		private HotPoint(Spot spot) {
			this.spot = spot;
		}
				
		protected Spot getSpot() {
			return spot;
		}
		
		protected boolean getVisible() {
			return spot.getVisible();
		}

		protected int getDist(int x, int y) {
			dist = (int) Math.sqrt((this.x-x)*(this.x-x) + (this.y-y)*(this.y-y));
			
			return dist;
		}

		protected void setPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	protected ViewSpotCollect() {
    	spot = new ArrayList<ViewSpot>();
    	spotWide = new ArrayList<ViewPlanet>();
	}

	public ViewSpotCollect(ViewSpotCollect orig) {
    	spot = orig.spot;
    	spotWide = orig.spotWide;

    	for (ViewSpot view : spot) {
    		String name = view.getSpot().getName();
    		
    		if (orig.hotPoint.containsKey(name))
    	    	hotPoint.put(name, new HotPoint(view.getSpot()));
    	}
	}

	public void Add(ViewPlanet view) {
    	spot.add(view);
    	spotWide.add(view);
    	
    	hotPoint.put(view.getSpot().getName(), new HotPoint(view.getSpot()));
    }

    public void Add(ViewOutside view) {
    	spot.add(0, view);
	}

    public void Add(ViewInside view) {
		spot.add(0, view);
    }

    public void Add(ViewZone view) {
		spot.add(0, view);
    }

	public void Visible(boolean data) {
    	visible = data;
    }
    
    public boolean Visible() {
    	return visible;
    }

    private void Grouping() {
    	ViewPlanet	cur = head;
    	ViewPlanet	next = cur.next;
    	int 		limit = spotWide.size() * 2; 

    	while (limit > 0) {
    		if (cur == null || next == null) break;

    		double d = Math.abs(Coord.angle(cur.getLon(), next.getLon()));
    		double r = cur.radius() + next.radius();

        	if (d < r) {
        		cur.append(next);
       
        		cur.done = false;

        		next = next.next;
        		cur.next = next;
        	} else {
        		if (cur.done) break;

        		cur.shift();
        		cur.done = true;
        		
       			cur = next;
       			next = cur.next;
        	}

        	limit--;
    	}
    }

	protected void DrawChart(ViewEcliptic view, Graphics2D canvas) {
    	tree = null;

    	for (ViewPlanet item: spotWide) 
			if (item.getSpot().getVisible()) {
				item.preDraw(canvas, view);
				
				if (tree == null) {
					tree = item;
				} else {
					tree.insert(item);
				}
			}

    	if (tree != null) {
    		tail = null;

    		tree.traverse(this);
       		tail.next = head;

       		Grouping();
    	}

    	for (ViewSpot item: spot) {
			Spot spot = item.getSpot();
			
			if (spot.getVisible()) {
				HotPoint hot = hotPoint.get(spot.getName());
				
				if (hot != null) {
					item.draw(canvas, view, hot);
				} else {
					item.draw(canvas, view);
				}
			}
		}
	}

	protected void DrawInner(ViewEcliptic view, Graphics2D canvas) {
		for (ViewSpot item: spot) {
			if (item.getSpot().getVisible()) {
				item.drawInner(canvas, view);
			}
		}
	}

	public void Visit(ViewPlanet item) {
		if (tail == null) {
			tail = item;
			head = item;
		} else {
			tail.next = item;
			tail = item;
		}
	}

	protected HotPoint getNear(int x, int y, int max) {
		HotPoint	near = null;
		int			dist = max;

		Iterator<Entry<String, HotPoint>> it = hotPoint.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, HotPoint>	item = it.next();
			HotPoint				val = item.getValue();
			
			if (val.getVisible()) {
				int d = val.getDist(x, y);
				
				if (d < dist) {
					dist = d;
					near = val;
				}
			}
		}
		
		return near;
	}

}
