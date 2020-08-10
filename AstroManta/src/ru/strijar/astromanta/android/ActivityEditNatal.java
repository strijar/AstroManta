package ru.strijar.astromanta.android;

import java.util.Set;

import org.joda.time.DateTimeZone;

import ru.strijar.astro.Date;
import ru.strijar.astro.Place;
import ru.strijar.astromanta.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

public class ActivityEditNatal extends Activity implements android.view.View.OnClickListener {
	private TableLayout				table;
	
	private Button					ok;
	private Button					now;
	private Button					atlas;
	private Button					load;
	private Button					save;
	private Button					points;

	private EditText				info;
	private EditText				date;
	private EditText				time;
	private EditText				timeZone;
	private EditText				place;
	private EditText				lon;
	private EditText				lat;
	private Spinner					houses;
	private AutoCompleteTextView	zones;

	private	Set<String> 			zoneSet;
	private String[]				zoneArray = new String[]{};

	private String					spotName[];
	private String					spotCategory[];
	private byte					spotVisible[];

	private String					varKey[];
	private String					varVal[];
	
	final int 						ATLAS = 1;
	final int 						LOAD = 2;
	final int						POINTS = 3;

	protected void SetView() {
		setContentView(R.layout.dialog_natal);
		table = (TableLayout) findViewById(R.id.edit_table);
	}
	
	protected void addRow(int id) {
		table.addView(View.inflate(this, id, null));
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SetView();
		
		now = (Button) findViewById(R.id.btn_now);
		now.setOnClickListener((OnClickListener) this);

		atlas = (Button) findViewById(R.id.btn_atlas);
		atlas.setOnClickListener((OnClickListener) this);

		load = (Button) findViewById(R.id.btn_load);
		load.setOnClickListener((OnClickListener) this);

		save = (Button) findViewById(R.id.btn_save);
		save.setOnClickListener((OnClickListener) this);

		points = (Button) findViewById(R.id.btn_points);
		points.setOnClickListener((OnClickListener) this);

		ok = (Button) findViewById(R.id.btn_ok);
		ok.setOnClickListener((OnClickListener) this);

		info = (EditText) findViewById(R.id.edit_info);
		date = (EditText) findViewById(R.id.edit_date);
		time = (EditText) findViewById(R.id.edit_time);
		timeZone = (EditText) findViewById(R.id.edit_time_zone);

		place = (EditText) findViewById(R.id.edit_place);

		lat = (EditText) findViewById(R.id.edit_lat);
		lon = (EditText) findViewById(R.id.edit_lon);
		
		houses = (Spinner) findViewById(R.id.edit_houses);
		zones = (AutoCompleteTextView) findViewById(R.id.edit_zone);

		zoneSet = DateTimeZone.getAvailableIDs();
		zoneArray = new String[]{};

		ArrayAdapter<String>	adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, zoneSet.toArray(zoneArray));
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		zones.setAdapter(adapter);
		
		Intent intent = getIntent();

		if (!intent.hasExtra("house")) {
			findViewById(R.id.row_coord).setVisibility(View.GONE);
			findViewById(R.id.row_houses).setVisibility(View.GONE);
		}

		from(intent);
	}

	protected void from(Intent intent) {
		info.setText(intent.getStringExtra("info"));
		date.setText(intent.getStringExtra("date"));
		time.setText(intent.getStringExtra("time"));
		timeZone.setText(intent.getStringExtra("time_zone"));
		zones.setText(intent.getStringExtra("zone_id"));
		place.setText(intent.getStringExtra("place"));

		lat.setText(intent.getStringExtra("lat"));
		lon.setText(intent.getStringExtra("lon"));

		if (intent.hasExtra("house")) {
			houses.setSelection(intent.getIntExtra("house", 0));
		}
		
		if (intent.hasExtra("point_name")) {
			spotName = intent.getStringArrayExtra("point_name");
			spotCategory = intent.getStringArrayExtra("point_category");
			spotVisible = intent.getByteArrayExtra("point_visible");
		}

		if (intent.hasExtra("var_key")) {
			varKey = intent.getStringArrayExtra("var_key");
			varVal = intent.getStringArrayExtra("var_val");
		}
	}
	
	protected void to(Intent intent) {
		intent.putExtra("info", info.getText().toString());
		intent.putExtra("date", date.getText().toString());
		intent.putExtra("time", time.getText().toString());
		intent.putExtra("time_zone", timeZone.getText().toString());
		intent.putExtra("place", place.getText().toString());
		intent.putExtra("lat", lat.getText().toString());
		intent.putExtra("lon", lon.getText().toString());
		intent.putExtra("house", houses.getSelectedItemPosition());
		intent.putExtra("zone_id", zones.getText().toString());
		
		if (varKey != null) {
			intent.putExtra("var_key", varKey);
			intent.putExtra("var_val", varVal);
		}
	}
	
	private void now() {
		Date moment = new Date();
		
		moment.now();
		
		date.setText(moment.getDateStr());
		time.setText(moment.getTimeStr());
	}
	
	private void atlas() {
    	Intent intent = new Intent(this, ActivityAtlas.class);

    	startActivityForResult(intent, ATLAS);
	}

	private void activityLoad() {
    	Intent intent = new Intent(this, ActivityCharts.class);

    	startActivityForResult(intent, LOAD);
	}

	private void activitySave() {
    	Intent intent = new Intent(this, ActivityCharts.class);

    	to(intent);
    	intent.putExtra("save_flag", true);
 
    	startActivity(intent);
	}

	private void activityPoints() {
    	Intent intent = new Intent(this, ActivityPoints.class);

    	intent.putExtra("point_name", spotName);
    	intent.putExtra("point_category", spotCategory);
    	intent.putExtra("point_visible", spotVisible);

    	startActivityForResult(intent, POINTS);
	}
	
	public void onClick(View v) {
		Intent intent = new Intent();

		switch (v.getId()) {
			case R.id.btn_save:
				activitySave();
				break;

			case R.id.btn_load:
				activityLoad();
				break;

			case R.id.btn_ok:
				to(intent);
				intent.putExtra("point_name", spotName);
		    	intent.putExtra("point_category", spotCategory);
		    	intent.putExtra("point_visible", spotVisible);

				setResult(RESULT_OK, intent);
			    finish();
				break;

			case R.id.btn_now:
				now();
				break;

			case R.id.btn_atlas:
				atlas();
				break;

			case R.id.btn_points:
				activityPoints();
				break;

		    default:
		    	break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		switch (requestCode) {
    	    	case ATLAS:
    	    		Place atlas = new Place();
    	    		
    	    		atlas.setLon(data.getDoubleExtra("lon", 0.0));
    	    		atlas.setLat(data.getDoubleExtra("lat", 0.0));
    	    		
    	    		lon.setText(atlas.getLonStr());
    	    		lat.setText(atlas.getLatStr());
    	    		
    	    		String zone = data.getStringExtra("zone");
    	    		
    	    		if (zone == null || zone.length() == 0) {
    	    			Toast.makeText(this, R.string.unknow_zone, Toast.LENGTH_LONG).show();
    	    		}
    	    		
    	    		zones.setText(zone);
    	    		place.setText(data.getStringExtra("name"));
    	    		break;
    	   
    	    	case LOAD:
    	    		from(data);
    	    		break;
    	    		
    	    	case POINTS:
    	    		spotName = data.getStringArrayExtra("point_name");
    	    		spotCategory = data.getStringArrayExtra("point_category");
    	    		spotVisible = data.getByteArrayExtra("point_visible");
    	    		break;
    	    }
    	}
    }
}
