package ru.strijar.astromanta.pc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.strijar.astromanta.pc.listener.AtlasListener;

@SuppressWarnings("serial")
public class DialogAtlas extends JDialog implements DocumentListener, ListSelectionListener, ActionListener {
	private JTextField			place;
	private JList<AtlasItem>	list;
	private JButton				ok;
	private AtlasListener		listener;

	class AtlasItem {
		protected String	name;
		protected String	reg; 
		protected double	lat;
		protected double	lon;
		protected String	zone;
		
		public AtlasItem(ResultSet res) {
			try {
				name = res.getString("name");
				reg =  res.getString("reg");
				lat = res.getDouble("lat");
				lon = res.getDouble("lon");
				zone = res.getString("zone");
			} catch (SQLException e) {
			}
		}
		
		public String toString() {
			return String.format("<html><b>%s</b><br>%s</html>", name, reg);
		}
	}

	private Connection 					db = null;
	private PreparedStatement			stat = null;
	private DefaultListModel<AtlasItem>	listModel;
	private AtlasItem					selected = null;
	
	public DialogAtlas(AtlasListener listener) {
		super();
		
		setTitle(I18n.get("Atlas"));
		setModal(true);
		
		this.listener = listener;

		place = new JTextField();

		place.getDocument().addDocumentListener(this);
				
		JPanel panel = new JPanel();
		
		JLabel label = new JLabel(I18n.get("Place"));
		label.setBorder(new EmptyBorder(0, 10, 0, 10));

		panel.add(label);
		panel.add(place);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		getContentPane().add(panel, BorderLayout.NORTH);
		
		listModel = new DefaultListModel<AtlasItem>();
		list = new JList<AtlasItem>(listModel);

		list.addListSelectionListener(this);
		
		JScrollPane scroll = new JScrollPane(list);
		getContentPane().add(scroll, BorderLayout.CENTER);
		
		ok = new JButton(I18n.get("Ok"));
		ok.setEnabled(false);
		
		ok.addActionListener(this);
		
		getContentPane().add(ok, BorderLayout.SOUTH);
		setSize(new Dimension(640, 480));
		openDB();
	}

	private void openDB() {
		try {
			db = DriverManager.getConnection("jdbc:sqlite:atlas.db");
			stat = db.prepareStatement(
				"SELECT city.name AS name, reg.name AS reg, lat, lon, time_zone.name AS zone" +
				" FROM city,reg,time_zone" +
				" WHERE city.name LIKE ?" + 
				" AND city.reg = reg.id AND city.time_zone = time_zone.id" +
				" ORDER BY city.name LIMIT 25"
			);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void readDB() {
		if (db == null)
			return;
		
		String text = place.getText();
		
		if (text.length() == 0) {
			selected = null;
			listModel.clear();
			ok.setEnabled(false);
			return;
		}

		text = text.substring(0, 1).toUpperCase() + text.substring(1) + "%";
		
		try {
			stat.setString(1, text);
			
			ResultSet res = stat.executeQuery();

			listModel.clear();
			
			while(res.next()) {
				listModel.add(0, new AtlasItem(res));
			}
			
			res.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void changedUpdate(DocumentEvent e) {
		readDB();
	}

	public void insertUpdate(DocumentEvent e) {
		readDB();
	}

	public void removeUpdate(DocumentEvent e) {
		readDB();
	}

	public void valueChanged(ListSelectionEvent e) {
		selected = (AtlasItem) list.getSelectedValue();
		ok.setEnabled(true);
	}

	public void actionPerformed(ActionEvent arg0) {
		try {
			stat.close();
			db.close();
		} catch (SQLException e) {
		}
		
		dispose();
		listener.onAtlas(selected.name, selected.lat, selected.lon, selected.zone);
	}
}
