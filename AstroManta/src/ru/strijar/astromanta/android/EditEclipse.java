package ru.strijar.astromanta.android;

import android.app.Activity;
import android.content.Intent;
import ru.strijar.astro.ChartEclipse;

public class EditEclipse extends EditNatal {
	private ChartEclipse chart;
	
	public EditEclipse(ChartEclipse chart) {
		super(chart);
		this.chart = chart;
	}

	public void to(Intent intent) {
		super.to(intent);
		
    	intent.putExtra("lunar", chart.getLunar());
    	intent.putExtra("backward", chart.getBackward());
	}

	public void from(Intent intent) {
		super.from(intent);

		chart.setLunar(intent.getBooleanExtra("lunar", false));
		chart.setBackward(intent.getBooleanExtra("backward", false));
	}

	public void edit(Activity activity, int code) {
    	Intent intent = new Intent(activity, ActivityEditEclipse.class);

    	to(intent);
    	activity.startActivityForResult(intent, code);
	}
	
}
