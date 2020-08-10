package ru.strijar.astromanta.pc;

import javax.swing.JComboBox;

import ru.strijar.astro.ChartEclipse;
import ru.strijar.astro.ChartNatal;
import ru.strijar.astromanta.pc.listener.EditChartListener;

@SuppressWarnings("serial")
public class DialogEditEclipse extends DialogEditNatal {
	private JComboBox<String>	type;
	private JComboBox<String>	find;

	public DialogEditEclipse(Sys parent, ChartEclipse chart, EditChartListener listener) {
		super(parent, chart, listener);
	}
	
	protected void addContent() {	
		super.addContent();
		
		PlaceLabel("Eclipse");

		final String[] typeList = {
			I18n.get("Lunar"),
			I18n.get("Solar")
		};
		
		type = new JComboBox<String>(typeList);

		final String[] findList = {
			I18n.get("Previous"),
			I18n.get("Next")
		};

		find = new JComboBox<String>(findList);
		Place(type, find);
	}
	
	protected void loadContent(ChartNatal chart) {
		super.loadContent(chart);

		ChartEclipse eclipse = (ChartEclipse) chart;
		
		type.setSelectedIndex(eclipse.getLunar() ? 0:1);
		find.setSelectedIndex(eclipse.getBackward() ? 0:1);
	}
	
	protected void saveContent(ChartNatal chart) {
		super.saveContent(chart);

		ChartEclipse eclipse = (ChartEclipse) chart;
		
		eclipse.setLunar(type.getSelectedIndex() == 0);
		eclipse.setBackward(find.getSelectedIndex() == 0);
	}
	
}
