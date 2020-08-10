package ru.strijar.astromanta.pc;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;

import ru.strijar.astro.*;
import ru.strijar.astromanta.pc.listener.AtlasListener;
import ru.strijar.astromanta.pc.listener.EditChartListener;

@SuppressWarnings("serial")
public class DialogEditNatal extends JDialog implements AtlasListener, EditChartListener {
	private JTextField				info;
	private WebDateField			date;
	private JTextField				time;
	private JTextField				timeZone;
	private JTextField				timeId;
	private JTextField				place;
	private JTextField				lat;
	private JTextField				lon;
	private JComboBox<String>		houses;
	
	private JButton					now;
	private JButton					atlas;
	
	private WebButton				ok;
	private WebButton				save;
	private WebButton				load;

	private int						row = 0;
	private int						col = 0;
	
	private JTabbedPane				tabs;
	private JPanel 					mainPanel;
	private EditPoints				pointsPanel;	
	private GridBagLayout			grid;
	
	private Sys						sys;
	protected ChartNatal			chart;
	private EditChartListener		listener;
	private HashMap<String, Object>	vars = null;
	
	public DialogEditNatal(Sys parent, ChartNatal chart, EditChartListener listener) {
		super(parent, I18n.get("Edit"), true);
		
		sys = parent;
		this.chart = chart;
		this.listener = listener;

		tabs = new JTabbedPane();
		
		grid = new GridBagLayout();
		mainPanel = new JPanel();
		mainPanel.setLayout(grid);
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		addContent();
		addButtons();

		loadContent(chart);
		houses.setSelectedIndex(chart.getHouse());
		
		tabs.addTab(I18n.get("Main"), mainPanel);
		getContentPane().add(tabs, BorderLayout.CENTER);
		pack();

		pointsPanel = new EditPoints(chart.getSpots());

		JScrollPane	scroll = new JScrollPane(pointsPanel.tree);
		tabs.addTab(I18n.get("Points"), scroll);

		setLocationRelativeTo(parent);
		setResizable(false);
	}
	
	private void Place(int w, JComponent obj) {
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = w;
		c.weightx = 1.0;
	
		c.insets = new Insets(2, 2, 2, 2);
		
		grid.setConstraints(obj, c);
		mainPanel.add(obj);
		
		col++;
	}
	
	protected void Place(JComponent obj) {
		Place(2, obj);
	}

	protected void Place(JComponent obj1, JComponent obj2) {
		Place(1, obj1);
		Place(1, obj2);
	}
	
	protected void PlaceLabel(String text) {
		GridBagConstraints c = new GridBagConstraints();

		JLabel label = new JLabel(I18n.get(text));

		row++;
		col = 1;
		
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = row;
		c.weightx = 0.0;

		c.insets = new Insets(2, 0, 2, 4);

		grid.setConstraints(label, c);
		mainPanel.add(label);
	}

	protected void loadContent(ChartNatal chart) {
		info.setText(chart.getInfo());

		Date m = chart.getMoment();
		
		date.setText(m.getDateStr());
		time.setText(m.getTimeStr());
		timeZone.setText(m.getTimeZoneStr());
		timeId.setText(m.getTimeZoneId());

		Place p = chart.getPlace();
		
		place.setText(p.getInfo());
		lat.setText(p.getLatStr());
		lon.setText(p.getLonStr());
	}
	
	protected void saveContent(ChartNatal chart) {
		chart.setInfo(info.getText());

		Date m = chart.getMoment();

		m.setDateStr(date.getText());
		m.setTimeStr(time.getText());
		m.setTimeZoneStr(timeZone.getText());
		m.setTimeZoneId(timeId.getText());

		Place p = chart.getPlace();
		
		p.setInfo(place.getText());
		p.setLatStr(lat.getText());
		p.setLonStr(lon.getText());
	}
	
	protected void saveContent() {
		pointsPanel.spotsModel.commit();
		saveContent(chart);
		chart.setHouse(houses.getSelectedIndex());
		
		if (vars != null) {
			chart.setVars(vars);
		}
	}
	
	protected void addContent() {	
		PlaceLabel("Info");
		info = new JTextField(30);
		Place(info);

		PlaceLabel("Date");
		date = new WebDateField();
		date.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
		
		now = new JButton(I18n.get("Now"));
		
		now.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Date moment = new Date();
				
				moment.now();
				
				date.setText(moment.getDateStr());
				time.setText(moment.getTimeStr());
			}
		});
		
		Place(date, now);

		PlaceLabel("Time");
		time = new JTextField(15);
		timeZone = new JTextField(15);
		Place(time, timeZone);

		PlaceLabel("Timezone");
		timeId = new JTextField(30);
		Place(timeId);

		PlaceLabel("Place");
		place = new JTextField(15);
		atlas = new JButton(I18n.get("Atlas"));
		
		final DialogEditNatal parent = this;
		
		atlas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogAtlas	atlas = new DialogAtlas(parent);

				atlas.setLocationRelativeTo(parent);
				atlas.setVisible(true);
			}
		});

		Place(place, atlas);

		PlaceLabel("Coord");
		lat = new JTextField(15);
		lon = new JTextField(15);
		Place(lat, lon);

		PlaceLabel("Houses");
		
		final String[] housesList = {
			"Equal",
			"Alcabitius",
			"Campanus",
			"Horizontal",
			"Koch",
			"Morinus",
			"Porphyry",
			"Placidus",
			"Regiomontanus",
			"Polich/Page",
			"Krusinski-Pisa-Goelzer",
			"Equal Vehlow",
			"Equal, whole sign",
			"Axial rotation system"
		};
		
		houses = new JComboBox<String>(housesList);
		Place(houses);
	}
	
	private void addButtons() {
		JPanel					panel = new JPanel();
		BoxLayout 				bottom = new BoxLayout(panel, BoxLayout.X_AXIS); 
		final DialogEditNatal	parent = this;
		
		panel.setLayout(bottom);

		save = new WebButton(I18n.get("Save"));
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ChartNatal chart = new ChartNatal();
	
				saveContent(chart);
				
				DialogCharts dialog = new DialogCharts(chart);

				dialog.setLocationRelativeTo(parent);
				dialog.setVisible(true);
			}
		});
		
		save.setMargin(new Insets(0, 20, 0, 20));

		load = new WebButton(I18n.get("Load"));

		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogCharts dialog = new DialogCharts(new ChartNatal(), parent);

				dialog.setLocationRelativeTo(parent);
				dialog.setVisible(true);
			}
		});

		load.setMargin(new Insets(0, 20, 0, 20));
		
		ok = new WebButton(I18n.get("Ok"));
		
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				saveContent();
				
				try {
					listener.onEdit(chart);
				} catch (Exception e) {
					sys.onError("onEdit", e);
				}
			}
		});
		
		ok.setMargin(new Insets(0, 20, 0, 20));
		
		panel.add(save);
		panel.add(load);
		panel.add(Box.createHorizontalGlue());
		panel.add(ok);

		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		getContentPane().add(panel, BorderLayout.SOUTH);
	}

	public void onAtlas(String name, double lat, double lon, String zone) {
		Place atlas = new Place();
		
		atlas.setLon(lon);
		atlas.setLat(lat);

		place.setText(name);
		timeId.setText(zone);
		this.lon.setText(atlas.getLonStr());
		this.lat.setText(atlas.getLatStr());
	}

	public void onEdit(ChartNatal chart) {
		vars = chart.getVars();
		loadContent(chart);
	}

}
