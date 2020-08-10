package ru.strijar.astromanta.pc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ChartsModel implements TreeModel {
	private	ArrayList<CategoryNode>		root = new ArrayList<CategoryNode>();
	private ChartDB						db;
	
	interface Node {
		public boolean 		isChart();
		public int 			childCount();
		public ChartNode 	getChild(int index);
		public long 		getId();
		public String		getInfo();
	}
	
	class CategoryNode implements Node {
		protected int					id;
		protected String				info;
		private	ArrayList<ChartNode>	charts = new ArrayList<ChartNode>();
		
		public CategoryNode(ResultSet category) {
			try {
				id = category.getInt("_id");
				info = category.getString("name");

				ResultSet res = db.getChart(id);
				
				while (res.next()) {
					ChartNode item = new ChartNode(res);

					charts.add(item);
				}
				
				res.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public boolean isChart() {
			return false;
		}
		
		public String toString() {
			return String.format("<html><b>%s</b></html>", info);
		}

		public int childCount() {
			return charts.size();
		}

		public ChartNode getChild(int index) {
			return charts.get(index);
		}

		public long getId() {
			return id;
		}

		public String getInfo() {
			return info;
		}
	};
	
	class ChartNode implements Node {
		protected int		id;
		protected String	info;

		public ChartNode(ResultSet chart) throws SQLException {
			id = chart.getInt("_id");

			info = String.format(
				"<html><b>%s</b><br>%s %s %s</html>",
				chart.getString("info"),
				chart.getString("date"),
				chart.getString("time"),
				chart.getString("place")
			);
		}

		public boolean isChart() {
			return true;
		}

		public String toString() {
			return info;
		}

		public int childCount() {
			return 0;
		}

		public ChartNode getChild(int index) {
			return null;
		}

		public long getId() {
			return id;
		}

		public String getInfo() {
			return null;
		}
	};
	
	private void load() {
		ResultSet category = db.getCategory();
		
		try {
			while (category.next()) {
				CategoryNode item = new CategoryNode(category);
				
				root.add(item);
			}
			
			category.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ChartsModel(ChartDB db) {
		this.db = db;
		load();
	}
	
	public Object getChild(Object node, int index) {
		if (node == root) {
			return root.get(index);
		} else {
			return ((Node)node).getChild(index);
		}
	}

	public int getChildCount(Object node) {
		if (node == root) {
			return root.size();
		} else {
			return ((Node)node).childCount();
		}
	}

	public int getIndexOfChild(Object arg0, Object arg1) {
		return 0;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		if (node == root) {
			return false;
		} else {
			return ((Node)node).isChart();
		}
	}

	public void removeTreeModelListener(TreeModelListener arg0) {
	}

	public void addTreeModelListener(TreeModelListener arg0) {
	}

	public void valueForPathChanged(TreePath arg0, Object arg1) {
	}

}
