package ru.strijar.astromanta.android;

import java.util.Map;

import ru.strijar.astro.Chart;
import ru.strijar.astro.ChartNatal;
import ru.strijar.astro.Spot;
import android.app.Activity;
import android.content.Intent;

public class EditNatal implements EditChart {
	private ChartNatal chart;

	public EditNatal(ChartNatal chart) {
		this.chart = chart;
	}
	
	public void to(Intent intent) {
    	intent.putExtra("info", chart.getInfo());
    	intent.putExtra("date", chart.getMoment().getDateStr());
    	intent.putExtra("time", chart.getMoment().getTimeStr());
    	intent.putExtra("time_zone", chart.getMoment().getTimeZoneStr());
    	intent.putExtra("zone_id", chart.getMoment().getTimeZoneId());
		intent.putExtra("place", chart.getPlace().getInfo());

    	if (chart.hasHouse()) {
    		intent.putExtra("lat", chart.getPlace().getLatStr());
    		intent.putExtra("lon", chart.getPlace().getLonStr());
    		intent.putExtra("house", chart.getHouse());
    	}
    	
    	int 		spots = chart.getSpots().size();
    	String[]	spot_name = new String[spots];
    	String[]	spot_category = new String[spots];
    	byte[]		spot_visible = new byte[spots];
    	int			i = 0;
    	
    	for (Spot spot : chart.getSpots()) {
    		spot_name[i] = spot.getName();
    		spot_category[i] = spot.getCategory();
    		
    		byte visible = 0;

    		if (spot.getVisible()) {
    			if (spot.getAspected()) {
    				visible = (byte) (spot.getAspecting() ? 4 : 2);
    			} else {
    				visible = (byte) (spot.getAspecting() ? 3 : 1);
    			}
    		}
    		
    		spot_visible[i] = visible;
   
    		i++;
    	}
    	
    	intent.putExtra("point_name", spot_name);
    	intent.putExtra("point_category", spot_category);
    	intent.putExtra("point_visible", spot_visible);
    	
    	int	vars = chart.getVars().size();
    	
    	if (vars > 0) {
    		String[]	key = new String[vars];
    		String[]	val = new String[vars];
   
    		i = 0;
    	
    		for (Map.Entry<String, Object> entry : chart.getVars().entrySet()) {
    			key[i] = entry.getKey();
    			val[i] = entry.getValue().toString();
    			
    			i++;
    		}

    		intent.putExtra("var_key", key);
    		intent.putExtra("var_val", val);
    	}
	}

	public void from(Intent intent) {
		chart.setInfo(intent.getStringExtra("info"));
		chart.getMoment().setDateStr(intent.getStringExtra("date"));
		chart.getMoment().setTimeStr(intent.getStringExtra("time"));
		chart.getMoment().setTimeZoneStr(intent.getStringExtra("time_zone"));
		chart.getMoment().setTimeZoneId(intent.getStringExtra("zone_id"));

		chart.getPlace().setInfo(intent.getStringExtra("place"));
		chart.getPlace().setLonStr(intent.getStringExtra("lon"));
		chart.getPlace().setLatStr(intent.getStringExtra("lat"));
		
		int house = intent.getIntExtra("house", -1);
		
		if (house >= 0) {
			chart.setHouse(house);
		}
		
		if (intent.hasExtra("point_name")) {
			String	spot_name[] = intent.getStringArrayExtra("point_name");
			byte	spot_visible[] = intent.getByteArrayExtra("point_visible");
			
			for (int i = 0; i < spot_name.length; i++) {
				Spot spot = chart.getSpot(spot_name[i]);
				
				switch (spot_visible[i]) {
					case 0:
						spot.setVisible(false);
						break;
	
					case 1:
						spot.setVisible(true);
						spot.setAspected(false);
						spot.setAspecting(false);
						break;
	
					case 2:
						spot.setAspected(true);
						spot.setAspecting(false);
						break;
	
					case 3:
						spot.setAspected(false);
						spot.setAspecting(true);
						break;
	
					case 4:
						spot.setAspected(true);
						spot.setAspecting(true);
						break;
				}
			}
		}

		chart.clearVars();
		
		if (intent.hasExtra("var_key")) {
			String	key[] = intent.getStringArrayExtra("var_key");
			String	val[] = intent.getStringArrayExtra("var_val");
			
			for (int i = 0; i < key.length; i++)
				chart.setVar(key[i], val[i]);
		}
	}

	public void edit(Activity activity, int code) {
    	Intent intent = new Intent(activity, ActivityEditNatal.class);

    	to(intent);
    	activity.startActivityForResult(intent, code);
	}

	public void load(Activity activity, int code) {
    	Intent intent = new Intent(activity, ActivityCharts.class);

    	to(intent);
        activity.startActivityForResult(intent, code);
	}

	public void save(Activity activity, int code) {
    	Intent intent = new Intent(activity, ActivityCharts.class);

    	to(intent);
    	intent.putExtra("save_flag", true);
        activity.startActivityForResult(intent, code);
	}

	public Chart getChart() {
		return chart;
	}

}
