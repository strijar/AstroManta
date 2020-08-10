package ru.strijar.astromanta.android;

import ru.strijar.astromanta.R;
import android.content.Intent;
import android.widget.Spinner;

public class ActivityEditEclipse extends ActivityEditNatal {
	private Spinner					type;
	private Spinner					find;

	protected void SetView() {
		super.SetView();

		addRow(R.layout.eclipse_row_head);
		addRow(R.layout.eclipse_row_type);
		addRow(R.layout.eclipse_row_find);
		
		type = (Spinner) findViewById(R.id.edit_eclipse_type);
		find = (Spinner) findViewById(R.id.edit_eclipse_find);
	}

	protected void from(Intent intent) {
		super.from(intent);
		
		type.setSelection(intent.getBooleanExtra("lunar", false) ? 0:1);
		find.setSelection(intent.getBooleanExtra("backward", true) ? 0:1);
	}

	protected void to(Intent intent) {
		super.to(intent);

		intent.putExtra("lunar", type.getSelectedItemPosition() == 0);
		intent.putExtra("backward", find.getSelectedItemPosition() == 0);
	}

}
