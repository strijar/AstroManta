package ru.strijar.astromanta.pc;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.alee.laf.button.WebButton;
import com.alee.laf.tree.WebTree;

import ru.strijar.astro.ChartNatal;
import ru.strijar.astromanta.pc.ChartsModel.Node;
import ru.strijar.astromanta.pc.listener.EditChartListener;

@SuppressWarnings("serial")
public class DialogCharts extends JDialog {
	private ChartDB				chartDB = new ChartDB();
	private ChartsModel			model;
	@SuppressWarnings("rawtypes")
	private WebTree				tree;
	private JPanel				panel = new JPanel();
	private WebButton			ok;
	
	private ChartsModel.Node	selected;

	@SuppressWarnings("rawtypes")
	private void makeContent() {
		setModal(true);
		setSize(640, 480);
		
		chartDB.open();
		
		model = new ChartsModel(chartDB);
		tree = new WebTree(model);
		tree.setRootVisible(false);
		
		JScrollPane	scroll = new JScrollPane(tree);
		getContentPane().add(scroll, BorderLayout.CENTER);

		BoxLayout bottom = new BoxLayout(panel, BoxLayout.X_AXIS); 

		panel.setLayout(bottom);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		ok = new WebButton(I18n.get("Ok"));
		ok.setMargin(new Insets(0, 20, 0, 20));
		ok.setEnabled(false);
	}
	
	// Save constructor
	
	protected DialogCharts(final ChartNatal chart) {
		super();
		makeContent();

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selected.isChart()) {
					chartDB.saveToChart(chart, selected.getId());
				} else {
					chartDB.saveToCategory(chart, selected.getId());
				}
				chartDB.close();
				dispose();
			}
		});

		final JDialog parent = this;
		
		WebButton new_cat = new WebButton(I18n.get("Add category"));
		new_cat.setMargin(new Insets(0, 10, 0, 10));
		
		new_cat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(parent, I18n.get("Add category"), "");
				
				if (name != null && name.length() > 0) {
					try {
						chartDB.insertCategory(name);
						
						model = new ChartsModel(chartDB);
						tree.setModel(model);
					} catch (SQLException e1) {
					}
				}
			}
		});

		final WebButton remove = new WebButton(I18n.get("Remove"));
		remove.setMargin(new Insets(0, 10, 0, 10));
		remove.setVisible(false);
		
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selected.isChart()) {
					chartDB.removeChart(selected.getId());
				} else {
					chartDB.removeCategory(selected.getId());
				}

				model = new ChartsModel(chartDB);
				tree.setModel(model);
			}
		});

		final WebButton rename = new WebButton(I18n.get("Rename"));
		rename.setMargin(new Insets(0, 10, 0, 10));
		rename.setVisible(false);
		
		rename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(parent, I18n.get("Rename"), selected.getInfo());

				if (name != null && name.length() > 0) {
					chartDB.renameCategory(selected.getId(), name);
					
					model = new ChartsModel(chartDB);
					tree.setModel(model);
				}
			}
		});
		
		panel.add(new_cat);
		panel.add(remove);
		panel.add(rename);
		panel.add(Box.createHorizontalGlue());
		panel.add(ok);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selected = (Node) tree.getLastSelectedPathComponent();
				
				ok.setEnabled(selected != null);
				remove.setVisible(selected != null);
				rename.setVisible(selected != null && !selected.isChart());
			}
		});

		setTitle(I18n.get("Save chart"));
	}

	// Load constructor

	protected DialogCharts(final ChartNatal chart, final EditChartListener listener) {
		super();
		makeContent();

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chartDB.load(chart, selected.getId());
				chartDB.close();
				listener.onEdit(chart);
				dispose();
			}
		});

		panel.add(Box.createHorizontalGlue());
		panel.add(ok);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selected = (Node) tree.getLastSelectedPathComponent();
				
				if (selected != null) {
					ok.setEnabled(selected.isChart());
				} else {
					ok.setEnabled(false);
				}
			}
		});

		setTitle(I18n.get("Load chart"));
	}

}
