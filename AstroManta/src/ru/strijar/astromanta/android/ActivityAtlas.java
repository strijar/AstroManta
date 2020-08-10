package ru.strijar.astromanta.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.strijar.astromanta.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ActivityAtlas extends Activity implements TextWatcher, OnItemClickListener, OnClickListener, Runnable {
	private EditText			place;
	private ListView			list;
	private LinearLayout		toolbar;
	private Button				ok; 
	private ProgressBar			wait;

	private SQLiteDatabase		atlasDB;
	private String[]			fromList = new String[] { "name", "reg" };
    private int[] 				toList = new int[] { R.id.atlas_name, R.id.atlas_reg, };
    private SimpleCursorAdapter	adapter;
    private Cursor				cursor;
    private Handler 			handler = new Handler();
    
    private String				name;
    private double				lon;
    private double				lat;
    private String				zone;

    static private String		atlasFile = "atlas.db";
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_atlas);

		wait = (ProgressBar) findViewById(R.id.waitBar);

		place = (EditText) findViewById(R.id.atlas_find);
		place.addTextChangedListener(this);

		list = (ListView) findViewById(R.id.atlas_list);
		list.setOnItemClickListener(this);

		toolbar = (LinearLayout) findViewById(R.id.toolbar);

		ok = (Button) findViewById(R.id.btn_ok);
		ok.setOnClickListener(this);

		try {
			atlasDB = SQLiteDatabase.openDatabase(
				Sys.pathDB + atlasFile,
				null,
				SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS
			);
		} catch (SQLiteException e) {
			atlasDB = null;
			Toast.makeText(this, R.string.atlas_not_found, Toast.LENGTH_LONG).show();
			
			if (CopyDefault()) {
				Toast.makeText(this, R.string.atlas_installed, Toast.LENGTH_LONG).show();

				try {
					atlasDB = SQLiteDatabase.openDatabase(
							Sys.pathDB + atlasFile,
							null,
							SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS
						);
				} catch (SQLiteException e1) {
					
				}
			} else {
				Toast.makeText(this, R.string.atlas_not_installed, Toast.LENGTH_LONG).show();
			}
		}

		adapter = new SimpleCursorAdapter(this, R.layout.atlas_item, null, fromList, toList, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    list.setAdapter(adapter);
	}
	
	private boolean CopyDefault() {
		InputStream		in = null;
		OutputStream	out = null;
		AssetManager	assetManager = getAssets();

		wait.setVisibility(View.VISIBLE);

		try {
			in = assetManager.open("db/" + atlasFile);

	        File outFile = new File(Sys.pathDB + atlasFile);
	      
	        out = new FileOutputStream(outFile);
	        
	        byte[]	buffer = new byte[1024];
	        int		read;

	        while ((read = in.read(buffer)) != -1) {
	        	out.write(buffer, 0, read);
	        }

	        in.close();
	        out.flush();
	        out.close();
		} catch(IOException e) {
			wait.setVisibility(View.INVISIBLE);
			return false;
		}

		wait.setVisibility(View.INVISIBLE);
		return true;
	}
	
	protected void onStop() {
		super.onStop();
		
		if (atlasDB != null) {
			atlasDB.close();
		}

    	if (cursor != null) {
    		cursor.close();
    	}
	}

	public void afterTextChanged(Editable s) {
		if (atlasDB != null) {
			handler.removeCallbacks(this);

			if (s.length() > 0) {
				wait.setVisibility(View.VISIBLE);
				handler.postDelayed(this, 1000);
			} else {
				wait.setVisibility(View.INVISIBLE);
				adapter.changeCursor(null);
				adapter.notifyDataSetChanged();
			}
		}
		toolbar.setVisibility(View.GONE);
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		Cursor	cursor = (Cursor) list.getItemAtPosition(position);
	
		name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		lon = cursor.getDouble(cursor.getColumnIndexOrThrow("lon"));
		lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"));
		zone = cursor.getString(cursor.getColumnIndexOrThrow("zone"));
		 
		place.setText(name);
		toolbar.setVisibility(View.VISIBLE);
	}

	public void onClick(View v) {
		Intent intent = new Intent();

		intent.putExtra("name", name);
		intent.putExtra("lon", lon);
		intent.putExtra("lat", lat);
		intent.putExtra("zone", zone);

		setResult(RESULT_OK, intent);
	    finish();
	}

	public void run() {
    	if (cursor != null) {
    		cursor.close();
    	}
    	
    	try {
    		cursor = atlasDB.rawQuery(
				"SELECT city.rowid AS _id, city.name AS name, reg.name AS reg, lat, lon, time_zone.name AS zone" +
	            " FROM city,reg,time_zone" +
	            " WHERE city.name LIKE ? AND city.reg = reg.id AND city.time_zone = time_zone.id" +
	            " ORDER BY city.name LIMIT 25",
				new String[] { place.getText().toString() + "%" });
    	} catch (SQLiteException e) {
    		Toast.makeText(this, R.string.atlas_incorrect, Toast.LENGTH_LONG).show();
    	}

    	wait.setVisibility(View.INVISIBLE);
		adapter.changeCursor(cursor);
		adapter.notifyDataSetChanged();
	}

}
