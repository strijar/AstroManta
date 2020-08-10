package ru.strijar.astromanta.pc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

import ru.strijar.astro.ChartNatal;
import ru.strijar.astro.Date;
import ru.strijar.astro.Place;

public class ChartDB {
	private Connection	db = null;

	private void createCategory() throws SQLException {
		Statement stat = db.createStatement();
		
		stat.execute(
			"CREATE TABLE category (" +
				"_id	INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name	TEXT" +
            ")"
		);
	}

	private void createChart() throws SQLException {
		Statement stat = db.createStatement();

		stat.execute(
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

	private void createVar() throws SQLException {
		Statement stat = db.createStatement();

		stat.execute(
				"CREATE TABLE var (" +
					"_id		INTEGER PRIMARY KEY AUTOINCREMENT," +
					"chart_id	INT," +
	                "key		TEXT," +
	                "val		TEXT" +
	            ")"
			);
	}

	public boolean open() {
		db = null;
				
		try {
			db = DriverManager.getConnection("jdbc:sqlite:chart.db");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		ResultSet res = null;
		
		try {
			Statement stat = db.createStatement();

			res = stat.executeQuery("SELECT * FROM category LIMIT 1");
			res.close();
			stat.close();
		} catch (SQLException e) {
			try {
				createCategory();
				insertCategory(I18n.get("Charts"));
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
			}
		}

		try {
			Statement stat = db.createStatement();

			res = stat.executeQuery("SELECT * FROM chart LIMIT 1");
			res.close();
			stat.close();
		} catch (SQLException e) {
			try {
				createChart();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
			}
		}

		try {
			Statement stat = db.createStatement();

			res = stat.executeQuery("SELECT * FROM var LIMIT 1");
			res.close();
			stat.close();
		} catch (SQLException e) {
			try {
				createVar();
			} catch (SQLException e1) {
				e1.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public void close() {
		try {
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertCategory(String category) throws SQLException {
		if (db == null) {
			return false;
		}

		Statement stat = db.createStatement();

		stat.execute(String.format("INSERT INTO category (name) VALUES ('%s')", category ));
		stat.close();
		return true;
	}

	public ResultSet getCategory() {
		try {
			Statement stat = db.createStatement();

			return stat.executeQuery("SELECT _id, name FROM category ORDER BY name");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResultSet getChart(int category) {
		try {
			Statement stat = db.createStatement();

			return  stat.executeQuery(
				String.format("SELECT * FROM chart WHERE category = %d ORDER BY info", category)
			);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean load(ChartNatal chart, long id) {
		try {
			Statement stat = db.createStatement();

			ResultSet res = stat.executeQuery(String.format("SELECT * FROM chart WHERE _id = %d", id));
			
			Date	moment = chart.getMoment();
			Place	place = chart.getPlace();
			
			chart.setInfo(res.getString("info"));

			moment.setDateStr(res.getString("date"));
			moment.setTimeStr(res.getString("time"));
			moment.setTimeZoneStr(res.getString("time_zone"));
			moment.setTimeZoneId(res.getString("zone_id"));
			
			place.setInfo(res.getString("place"));
			place.setLonStr(res.getString("lon"));
			place.setLatStr(res.getString("lat"));

			res.close();
			stat.close();
			loadVar(chart, id);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean renameCategory(long id, String category) {
		if (db == null) {
			return false;
		}

		try {
			Statement stat = db.createStatement();

			stat.execute(String.format("UPDATE category SET name = '%s' WHERE _id = %d",  category, id));
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeCategory(long id) {
		if (db == null) {
			return false;
		}

		try {
			Statement stat = db.createStatement();

			stat.execute(String.format("DELETE FROM category WHERE _id=%d", id));
			stat.execute(String.format("DELETE FROM chart WHERE category=%d", id));
			
			stat.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveToCategory(ChartNatal chart, long id) {
		if (db == null) {
			return false;
		}

		try {
			Date		moment = chart.getMoment();
			Place		place = chart.getPlace();

			PreparedStatement	stat = db.prepareStatement( 
				"INSERT INTO chart (category, info, date, time, time_zone, zone_id, place, lon, lat) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
			);
			
			stat.setLong(1, id);
			stat.setString(2, chart.getInfo());
			stat.setString(3, moment.getDateStr());
			stat.setString(4, moment.getTimeStr());
			stat.setString(5, moment.getTimeZoneStr());
			stat.setString(6, moment.getTimeZoneId());
			stat.setString(7, place.getInfo());
			stat.setString(8, place.getLonStr());
			stat.setString(9, place.getLatStr());
			
			stat.executeUpdate();
			ResultSet keys = stat.getGeneratedKeys();
			
			if (keys.next()) {
                saveVar(chart, keys.getLong(1));
            }
			
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean saveToChart(ChartNatal chart, long id) {
		if (db == null) {
			return false;
		}

		try {
			Date		moment = chart.getMoment();
			Place		place = chart.getPlace();
			
			PreparedStatement	stat = db.prepareStatement( 
				"UPDATE chart SET info=?, date=?, time=?, time_zone=?, zone_id=?, place=?, lon=?, lat=? WHERE _id=?" 
			);

			stat.setString(1, chart.getInfo());
			stat.setString(2, moment.getDateStr());
			stat.setString(3, moment.getTimeStr());
			stat.setString(4, moment.getTimeZoneStr());
			stat.setString(5, moment.getTimeZoneId());
			stat.setString(6, place.getInfo());
			stat.setString(7, place.getLonStr());
			stat.setString(8, place.getLatStr());
			stat.setLong(9, id);
			
			stat.executeUpdate();
			stat.close();
			
			saveVar(chart, id);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean removeChart(long id) {
		if (db == null) {
			return false;
		}

		try {
			Statement stat = db.createStatement();

			stat.execute(String.format("DELETE FROM chart WHERE _id=%d", id));
			stat.execute(String.format("DELETE FROM var WHERE chart_id=%d", id));
			
			stat.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void saveVar(ChartNatal chart, long id) throws SQLException {
		PreparedStatement	stat;

		db.setAutoCommit(false);

		stat = db.prepareStatement("DELETE FROM var WHERE chart_id=?"); 

		stat.setLong(1, id);
		stat.execute();
		stat.close();

		stat = db.prepareStatement("INSERT INTO var (chart_id, key, val) VALUES (?, ?, ?)"); 
		
		for (Entry<String, Object> entry : chart.getVars().entrySet()) {
			stat.setLong(1, id);
			stat.setString(2, entry.getKey());
			stat.setString(3, entry.getValue().toString());
			
			stat.execute();
		}

		stat.close();
		db.commit();
		db.setAutoCommit(true);
	}
		
	private void loadVar(ChartNatal chart, long id) throws SQLException {
		PreparedStatement	stat;
		
		stat = db.prepareStatement("SELECT key,val FROM var WHERE chart_id = ?"); 
		stat.setLong(1, id);
		
		ResultSet res = stat.executeQuery();
		
		chart.clearVars();
		
		while (res.next()) {
			chart.setVar(res.getString("key"), res.getString("val"));
		}
		
		stat.close();
	}

}
