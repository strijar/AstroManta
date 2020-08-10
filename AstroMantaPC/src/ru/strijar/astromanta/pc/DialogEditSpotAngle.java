package ru.strijar.astromanta.pc;

import javax.swing.JComboBox;

import ru.strijar.astro.ChartNatal;
import ru.strijar.astro.ChartSpotAngle;
import ru.strijar.astromanta.pc.listener.EditChartListener;

@SuppressWarnings("serial")
public class DialogEditSpotAngle extends DialogEditNatal {
	private JComboBox<String>	find;

	public DialogEditSpotAngle(Sys parent, ChartNatal chart, EditChartListener listener) {
		super(parent, chart, listener);
	}

	protected void addContent() {	
		super.addContent();
		
		PlaceLabel("Find");

		final String[] findList = {
			I18n.get("Previous"),
			I18n.get("Next")
		};

		find = new JComboBox<String>(findList);
		Place(find);
	}
	
	protected void loadContent(ChartNatal chart) {
		super.loadContent(chart);

		ChartSpotAngle angle = (ChartSpotAngle) chart;
		
		find.setSelectedIndex(angle.getBackward() ? 0:1);
	}
	
	protected void saveContent(ChartNatal chart) {
		super.saveContent(chart);

		ChartSpotAngle angle = (ChartSpotAngle) chart;
		
		angle.setBackward(find.getSelectedIndex() == 0);
	}

}
