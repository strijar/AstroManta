package ru.strijar.astromanta.android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class ChartDB {
	private SQLiteDatabase		db;

	private void CreateDB(String path) {
		db = SQLiteDatabase.openOrCreateDatabase(path, null);
	}
	
	private void createCategory() {
		db.execSQL(
			"CREATE TABLE category (" +
				"_id	INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name	TEXT" +
            ")"
		);
	}

	private void createChart() {
		db.execSQL(
				"CREATE TABLE chart (" +
					"_id		INTEGER PRIMARY KEY AUTOINCREMENT," +
					"category	INT," +
	                "info		TEXT," +
	                "date		TEXT," +
	                "time		TEXT," +
	                "time_zone	TEXT," +
	                "zone_id	TEXT," +
	                "place		TEXT," +
	                "lon		TEXT," +
	                "lat		TEXT" +
	            ")"
			);
	}

	private void createVar() {
		db.execSQL(
				"CREATE TABLE var (" +
					"_id		INTEGER PRIMARY KEY AUTOINCREMENT," +
					"chart_id	INT," +
	                "key		TEXT," +
	                "val		TEXT" +
	            ")"
			);
	}
	
	public boolean open() {
		String path = Sys.pathDB + "chart.db";
		
		try {
			db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException e) {
			try {
				CreateDB(path);
			} catch (SQLiteException e_db) {
				return false;
			}
		}

		try {
			Cursor cursor = db.rawQuery("SELECT * FROM category LIMIT 1", null);
			cursor.close();
		} catch (SQLiteException e) {
			try {
				createCategory();
				insertCategory("Charts");
			} catch (SQLiteException e_category) {
				return false;
			}
		}

		try {
			Cursor cursor = db.rawQuery("SELECT * FROM chart LIMIT 1", null);
			cursor.close();
		} catch (SQLiteException e) {
			try {
				createChart();
			} catch (SQLiteException e_chart) {
				return false;
			}
		}

		try {
			Cursor cursor = db.rawQuery("SELECT * FROM var LIMIT 1", null);
			cursor.close();
		} catch (SQLiteException e) {
			try {
				createVar();
			} catch (SQLiteException e_chart) {
				return false;
			}
		}

		return true;
	}
	
	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
			db = null;
		}
	}
	
	public Cursor getCategory() {
		Cursor cursor = db.rawQuery(
			"SELECT _id, name FROM category ORDER BY name", 
			null
		);
		
		return cursor;
	}
	
	public Cursor getChart(String category) {
		Cursor cursor = db.rawQuery(
			"SELECT * FROM chart WHERE category = ? ORDER BY info", 
			new String[]{ category }
		);
		
		return cursor;
	}
	
	public boolean insertCategory(String category) {
		if (db == null || db.isOpen() == false) {
			return false;
		}

		db.execSQL("INSERT INTO category (name) VALUES (?)", new String[]{ category });
		return true;
	}

	public boolean renameCategory(long id, String category) {
		if (db == null || db.isOpen() == false) {
			return false;
		}

		db.execSQL("UPDATE category SET name = ? WHERE _id = ?", new String[]{ category, Long.toString(id) });
		return true;
	}

	private void saveVar(Intent intent, long id) {
		String	key[] = intent.getStringArrayExtra("var_key");
		String	val[] = intent.getStringArrayExtra("var_val");
		String	id_str = Long.toString(id);

		try {
			db.beginTransaction();
			db.execSQL("DELETE FROM var WHERE chart_id=?", new String[] { id_str });
		
			for (int i = 0; i < key.length; i++)
				db.execSQL(
					"INSERT INTO var (chart_id, key, val) VALUES (?, ?, ?)", 
					new String[] { id_str, key[i], val[i] }
				);

			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			Log.e("SaveVar", e.getMessage());
		} finally {
			db.endTransaction();
		}
	}
	
	private void loadVar(Intent intent, long id) {
		if (db == null || db.isOpen() == false) {
			return;
		}

		Cursor cursor = db.rawQuery(
			"SELECT key,val FROM var WHERE chart_id = ?", 
			new String[]{ Long.toString(id) }
		);
		
		if (cursor.moveToFirst()) {
			int		i = cursor.getCount();
			String 	val[] = new String[i];
			String 	key[] = new String[i];

			i = 0;
			
			while (true) {
				key[i] = cursor.getString(0);
				val[i] = cursor.getString(1);

				i++;
				
				if (!cursor.moveToNext()) break;
			}
			
			intent.putExtra("var_key", key);
			intent.putExtra("var_val", val);
		}

		cursor.close();
	}
	
	public boolean saveToCategory(Intent intent, long id) {
		if (db == null || db.isOpen() == false) {
			return false;
		}
		
		try {
			ContentValues vals = new ContentValues();
			
			vals.put("category", id);
			vals.put("info", intent.getStringExtra("info"));
			vals.put("date", intent.getStringExtra("date"));
			vals.put("time", intent.getStringExtra("time"));
			vals.put("time_zone", intent.getStringExtra("time_zone"));
			vals.put("zone_id", intent.getStringExtra("zone_id"));
			vals.put("place", intent.getStringExtra("place"));
			vals.put("lon", intent.getStringExtra("lon"));
			vals.put("lat", intent.getStringExtra("lat"));

			long chart_id = db.insert("chart", null, vals);

			if (chart_id >= 0 && intent.hasExtra("var_key")) {
				saveVar(intent, chart_id);
			}
		} catch (SQLiteException e) {
			Log.e("Charts", e.getMessage());
			return false;
		}
		
		return true;
	}

	public boolean saveToChart(Intent intent, long id) {
		if (db == null || db.isOpen() == false) {
			return false;
		}

		try {
			db.execSQL(
				"UPDATE chart SET info=?, date=?, time=?, time_zone=?, zone_id=?, place=?, lon=?, lat=? WHERE _id=?",
				new String[] {
					intent.getStringExtra("info"),
					intent.getStringExtra("date"),
					intent.getStringExtra("time"),
					intent.getStringExtra("time_zone"),
					intent.getStringExtra("zone_id"),
					intent.getStringExtra("place"),
					intent.getStringExtra("lon"),
					intent.getStringExtra("lat"),
					Long.toString(id),
				}
			);
		} catch (SQLiteException e) {
			Log.e("Charts", e.getMessage());
			return false;
		}
		
		if (intent.hasExtra("var_key")) {
			saveVar(intent, id);
		}
		
		return true;
	}
	
	public boolean load(Intent intent, long id) {
		try {
			Cursor cursor = db.rawQuery(
				"SELECT * FROM chart WHERE _id = ?",
				new String[]{ Long.toString(id) }	
			);
		
			if (cursor.moveToFirst()) {
				intent.putExtra("info", cursor.getString(cursor.getColumnIndex("info")));
				intent.putExtra("date", cursor.getString(cursor.getColumnIndex("date")));
				intent.putExtra("time", cursor.getString(cursor.getColumnIndex("time")));
				intent.putExtra("time_zone", cursor.getString(cursor.getColumnIndex("time_zone")));
				intent.putExtra("zone_id", cursor.getString(cursor.getColumnIndex("zone_id")));
				intent.putExtra("place", cursor.getString(cursor.getColumnIndex("place")));
				intent.putExtra("lon", cursor.getString(cursor.getColumnIndex("lon")));
				intent.putExtra("lat", cursor.getString(cursor.getColumnIndex("lat")));
			}
			
			loadVar(intent, id);
		} catch (SQLiteException e) {
			Log.e("Charts", e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	public boolean removeCategory(long id) {
		if (db == null || db.isOpen() == false) {
			return false;
		}

		try {
			db.execSQL("DELETE FROM category WHERE _id=?", new String[] { Long.toString(id) });
			db.execSQL("DELETE FROM chart WHERE category=?", new String[] { Long.toString(id) });
		} catch (SQLiteException e) {
			Log.e("Charts", e.getMessage());
			return false;
		}
		return true;
	}

	public boolean removeChart(long id) {
		if (db == null || db.isOpen() == false) {
			return false;
		}

		try {
			String[] param = new String[] { Long.toString(id) };
			
			db.execSQL("DELETE FROM chart WHERE _id=?", param);
			db.execSQL("DELETE FROM var WHERE chart_id=?", param);
		} catch (SQLiteException e) {
			Log.e("Charts", e.getMessage());
			return false;
		}
		return true;
	}
}
