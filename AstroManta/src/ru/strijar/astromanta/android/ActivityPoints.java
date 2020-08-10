package ru.strijar.astromanta.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ru.strijar.astromanta.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;

public class ActivityPoints extends Activity implements OnGroupClickListener, OnChildClickListener, android.view.View.OnClickListener, OnClickListener  {
	private ExpandableListView					list;
	private LinearLayout						toolbar;
	private Button								ok;		

	private Intent								intent;

	private String								spotName[];
	private String								spotCategory[];
	private byte								spotVisible[];
	private String								visible[];

	private List<Map<String, String>>			categoryList;
    private List<List<Map<String, Object>>>		spotList;
	
    private int									categoryId;
    private int									spotId;
    private SimpleExpandableListAdapter 		adapter;
    
	private static final String 				NAME = "NAME";
    private static final String 				INFO = "INFO";
    private static final String 				VISIBLE = "VISIBLE";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_points);

		list = (ExpandableListView) findViewById(R.id.points_list);
		list.setOnChildClickListener(this);
		list.setOnGroupClickListener(this);
		
		toolbar = (LinearLayout) findViewById(R.id.toolbar);

		ok = (Button) findViewById(R.id.btn_ok);
		ok.setOnClickListener(this);

		intent = getIntent();

		spotName = intent.getStringArrayExtra("point_name");
		spotCategory = intent.getStringArrayExtra("point_category");
		spotVisible = intent.getByteArrayExtra("point_visible");
		
		visible = getResources().getStringArray(R.array.visible_type);
		
		initList();
	}

	private void initList() {
		Map<String, List<Map<String, Object>>>		tree = new LinkedHashMap<String, List<Map<String, Object>>>();
		
		categoryList = new ArrayList<Map<String, String>>();
        spotList = new ArrayList<List<Map<String, Object>>>();

		for (int i = 0; i < spotName.length; i++) {
			String 						name = spotName[i];
			String 						category = spotCategory[i];
			List<Map<String, Object>>	tree_item = tree.get(category);
			
			if (tree_item == null) {
				tree_item = new ArrayList<Map<String, Object>>();
				tree.put(category, tree_item);
			}

			Map<String, Object> point_item = new HashMap<String, Object>();
            point_item.put(NAME, name);
            point_item.put(INFO, visible[spotVisible[i]]);
            point_item.put(VISIBLE, Byte.valueOf(spotVisible[i]));
  
            tree_item.add(point_item);
		}
		
		Iterator tree_items = tree.entrySet().iterator();
		
		while (tree_items.hasNext()) {
			Entry						tree_item = (Entry) tree_items.next();
			String						key = (String) tree_item.getKey();
			List<Map<String, Object>>	val = (List<Map<String, Object>>) tree_item.getValue();

			Map<String, String> 		category_item = new HashMap<String, String>();

            category_item.put(NAME, key);
            categoryList.add(category_item);
			
            spotList.add(val);
		}

        adapter = new SimpleExpandableListAdapter(
                this,
                categoryList,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { NAME },
                new int[] { android.R.id.text1 },
                spotList,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME, INFO },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        
        list.setAdapter(adapter);
        
        for (int i = 0; i < adapter.getGroupCount(); i++)
            list.expandGroup(i);
	}
	
	private void selectOk() {
		List<Map<String, Object>>	category_item;
		String 						category_name;
		Map<String, Object> 		point_item;
		String						point_name;
		Byte						point_visible;
		int							index = 0;

		Intent intent = new Intent();

		for(int i = 0; i < spotList.size(); i++) {
			category_item = spotList.get(i);
			category_name = categoryList.get(i).get(NAME);
			
			for (int n = 0; n < category_item.size(); n++) {
				point_item = category_item.get(n);
				
				point_name = (String) point_item.get(NAME);
				point_visible = (Byte) point_item.get(VISIBLE);
				
				spotCategory[index] = category_name;
				spotName[index] = point_name;
				spotVisible[index] = point_visible.byteValue();
				
				index++;
			}
		}

		intent.putExtra("point_name", spotName);
		intent.putExtra("point_category", spotCategory);
		intent.putExtra("point_visible", spotVisible);
		
		setResult(RESULT_OK, intent);
		finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ok:
				selectOk();
				break;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		
		Map<String, Object> item;
		
		if (spotId < 0) {
			for (int i = 0; i < spotList.get(categoryId).size(); i++) {
				item = spotList.get(categoryId).get(i);

				item.put(VISIBLE, Byte.valueOf((byte) which));
				item.put(INFO, visible[which]);
			}
		} else {
			item = spotList.get(categoryId).get(spotId);

			item.put(VISIBLE, Byte.valueOf((byte) which));
			item.put(INFO, visible[which]);
		}
		
		adapter.notifyDataSetChanged();
		
		toolbar.setVisibility(View.VISIBLE);
	}

	private void showDialog(String title, int select) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		
		adb.setTitle(title);
		adb.setSingleChoiceItems(visible, select, this);

		Dialog dialog = adb.create();

		dialog.show();
	}
	
	public boolean onChildClick(ExpandableListView list, View v, int category, int point, long id) {
		categoryId = category;
		spotId = point;
		
		String 	title = (String) spotList.get(category).get(point).get(NAME);
		Byte	select = (Byte) spotList.get(category).get(point).get(VISIBLE);
		
		showDialog(title, select.byteValue());

		return false;
	}

	public boolean onGroupClick(ExpandableListView list, View v, int category, long id) {
		categoryId = category;
		spotId = -1;

		showDialog(categoryList.get(category).get(NAME), -1);
		
		return false;
	}

}
