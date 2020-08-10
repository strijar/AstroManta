package ru.strijar.astromanta.android;

import ru.strijar.astromanta.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

public class ActivityCharts extends Activity implements OnGroupClickListener, OnChildClickListener, android.view.View.OnClickListener {
	private ExpandableListView		list;
	private LinearLayout			toolbar;
	private Button					ok;		
	
	private boolean					saveFlag;
	private Intent					intent;
	
	private ChartDB					chartDB = new ChartDB();
	private SimpleCursorTreeAdapter	adapter;

	private String[]				categoryFromList = new String[] { "name" };
    private int[] 					categoryToList = new int[] { android.R.id.text1 };
	private Cursor					categoryCursor;

    private String[]				chartFromList = new String[] { "info", "date", "time", "place" };
    private int[] 					chartToList = new int[] { R.id.chart_info, R.id.chart_date, R.id.chart_time, R.id.chart_place };

    private boolean					chartSelect = false;
    private boolean					categorySelect = false;
    private long					idSelect;

    final int 						MENU_ADD_CATEGORY = 1;
    final int 						MENU_DELETE_CATEGORY = 2;
    final int 						MENU_RENAME_CATEGORY = 3;
    final int 						MENU_DELETE_CHART = 4;

    class ChartAdapter extends SimpleCursorTreeAdapter {

		public ChartAdapter(Context context, Cursor cursor, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout,
				String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
		}

		protected Cursor getChildrenCursor(Cursor groupCursor) {
			int idColumn = groupCursor.getColumnIndex("_id");
	 
			return chartDB.getChart(groupCursor.getString(idColumn));
	    }
	}

    private void message(int r) {
		Toast.makeText(this, r, Toast.LENGTH_LONG).show();
	}

	private void load() {
		if (categoryCursor != null) {
			categoryCursor.close();
		}
		
		categoryCursor = chartDB.getCategory();

		adapter = new ChartAdapter(
				this, categoryCursor,
		        android.R.layout.simple_expandable_list_item_1, categoryFromList, categoryToList,
		        R.layout.charts_item, chartFromList, chartToList
		);
	
		list.setAdapter(adapter);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_charts);

		list = (ExpandableListView) findViewById(R.id.charts_list);
		list.setOnChildClickListener(this);
		list.setOnGroupClickListener(this);
	
		toolbar = (LinearLayout) findViewById(R.id.toolbar);

		ok = (Button) findViewById(R.id.btn_ok);
		ok.setOnClickListener(this);

		if (chartDB.open()) {
			load();
		} else {
        	message(R.string.db_error);
		}
		
		intent = getIntent();
		saveFlag = intent.getBooleanExtra("save_flag", false);
	}

	protected void onStop() {
		super.onStop();
		
		if (chartDB != null) {
			chartDB.close();
		}

		if (categoryCursor != null) {
			categoryCursor.close();
		}
	}

	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		toolbar.setVisibility(View.VISIBLE);
		ok.setText(saveFlag ? R.string.chart_save_chart : R.string.chart_load);

		chartSelect = true;
		categorySelect = false;
		idSelect = id;
		
		return false;
	}

	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		if (saveFlag) {
			toolbar.setVisibility(View.VISIBLE);
			ok.setText(R.string.chart_save_group);
		} else {
			toolbar.setVisibility(View.GONE);
		}
		chartSelect = false;
		categorySelect = true;
		idSelect = id;

		return false;
	}

	private void selectOk() {
		if (saveFlag) {
			if (categorySelect) {
				if (chartDB.saveToCategory(intent, idSelect)) {
					Toast.makeText(this, R.string.saved_ok, Toast.LENGTH_SHORT).show();
				}
			} else if (chartSelect) {
				if (chartDB.saveToChart(intent, idSelect)) {
					Toast.makeText(this, R.string.saved_ok, Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			Intent intent = new Intent();

			if (chartDB.load(intent, idSelect)) {
				setResult(RESULT_OK, intent);
			}
		}
		finish();
	}
	
	private void selectDeleteCategory() {
		AlertDialog.Builder dialog= new AlertDialog.Builder(this);
		
		dialog.setTitle(R.string.delete_group);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
        		if (chartDB.removeCategory(idSelect)) {
        			categorySelect = false;
        			chartSelect = false;
        			toolbar.setVisibility(View.GONE);
        			load();
        		} else {
        			message(R.string.db_error);
        		}
            }
        });

        dialog.show();
	}

	private void selectDeleteChart() {
		AlertDialog.Builder dialog= new AlertDialog.Builder(this);
		
		dialog.setTitle(R.string.delete_chart);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
        		if (chartDB.removeChart(idSelect)) {
        			categorySelect = false;
        			chartSelect = false;
        			toolbar.setVisibility(View.GONE);
        			load();
        		} else {
        			message(R.string.db_error);
        		}
            }
        });

        dialog.show();
	}
	
	private void selectAddCategory() {
		AlertDialog.Builder dialog= new AlertDialog.Builder(this);
		
		dialog.setTitle(R.string.add_group);

		final EditText input = new EditText(this);

		dialog.setView(input);
		
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (chartDB.insertCategory(input.getText().toString())) {
            		load();
            	} else {
            		message(R.string.db_error);
            	}
            }
        });

        dialog.show();
	}
		
	private void selectRenameCategory() {
		AlertDialog.Builder dialog= new AlertDialog.Builder(this);
		
		dialog.setTitle(R.string.rename_group);

		final EditText input = new EditText(this);

		dialog.setView(input);
		
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (chartDB.renameCategory(idSelect, input.getText().toString())) {
            		load();
            	} else {
                	message(R.string.db_error);
            	}
            }
        });

        dialog.show();
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		menu.add(0, MENU_ADD_CATEGORY, 0, R.string.add_group);

		if (categorySelect) {
			menu.add(0, MENU_DELETE_CATEGORY, 0, R.string.delete_group);
			menu.add(0, MENU_RENAME_CATEGORY, 0, R.string.rename_group);
		} if (chartSelect) {
			menu.add(0, MENU_DELETE_CHART, 0, R.string.delete_chart);
		}

		return super.onPrepareOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ADD_CATEGORY:
				selectAddCategory();
				return true;

			case MENU_DELETE_CATEGORY:
				selectDeleteCategory();
				return true;

			case MENU_RENAME_CATEGORY:
				selectRenameCategory();
				return true;

			case MENU_DELETE_CHART:
				selectDeleteChart();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ok:
				selectOk();
				break;
		}
	}
}
