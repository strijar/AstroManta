package ru.strijar.astromanta.pc;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.alee.laf.tree.WebTree;

import ru.strijar.astro.Spot;

@SuppressWarnings("serial")
public class EditPoints {
	protected WebTree		tree;
	protected SpotsModel 	spotsModel = new SpotsModel();
	
	class SpotItem {
		protected Spot		spot;
		protected int		visible;
		
		public SpotItem(Spot spot) {
			this.spot = spot;

			visible = 0;
			
			if (spot.getVisible()) {
    			if (spot.getAspected()) {
    				visible = spot.getAspecting() ? 4 : 2;
    			} else {
    				visible = spot.getAspecting() ? 3 : 1;
    			}
    		}
		}

		public void setVisible(int visible) {
			this.visible = visible;
		}
		
		public String toString() {
			return I18n.get(spot.getName());
		}
		
		public void commit() {
			switch (visible) {
				case 0:
					spot.setVisible(false);
					break;

				case 1:
					spot.setVisible(true);
					spot.setAspected(false);
					spot.setAspecting(false);
					break;

				case 2:
					spot.setAspected(true);
					spot.setAspecting(false);
					break;

				case 3:
					spot.setAspected(false);
					spot.setAspecting(true);
					break;

				case 4:
					spot.setAspected(true);
					spot.setAspecting(true);
					break;
			}
		}
	}
	
	class SpotsModel implements TreeModel {
		private ArrayList<String>					category = new ArrayList<String>();
		private	HashMap<String, Vector<SpotItem>>	root = new HashMap<String, Vector<SpotItem>>();
		
		public void add(String name, SpotItem item) {
			Vector<SpotItem>	cat = root.get(name);
			
			if (cat == null) {
				cat = new Vector<SpotItem>();
				root.put(name, cat);
				category.add(name);
			}
			
			cat.add(item);
		}
		
		public void commit() {
			for (Vector<SpotItem> group : root.values())
				for (SpotItem item : group)
					item.commit();
		}
		
		public Object getRoot() {
			return root;
		}

		public int getChildCount(Object parent) {
			if (parent == root) {
				return root.size();
			} else {
				return root.get(parent).size();
			}
		}

		public Object getChild(Object parent, int index) {
			if (parent == root) {
				return category.get(index);
			} else {
				return root.get(parent).elementAt(index);
			}
		}

		public boolean isLeaf(Object node) {
			if (node == root) 
				return false;
	
			if (root.containsKey(node))
				return false;
			
			return true;
		}

		public void valueForPathChanged(TreePath path, Object data) {
			SpotItem point = (SpotItem) path.getLastPathComponent();
			
			point.setVisible((Integer)data);
		}

		public int getIndexOfChild(Object parent, Object node) {
			return 0;
		}

		public void removeTreeModelListener(TreeModelListener arg0) {
		}

		public void addTreeModelListener(TreeModelListener arg0) {
		}

	}

	static class PointTreeCellEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener {
		private WebTree			editedTree;
		private JComboBox		iCombo;
		
		final private String[]	iLabels = {
				I18n.get("Invisible"),
				I18n.get("Visible"),
				I18n.get("Aspected only"),
				I18n.get("Aspecting only"),
				I18n.get("Aspected and aspecting")
			};
		
        public PointTreeCellEditor(WebTree tree) {
        	editedTree = tree;
        	iCombo = new JComboBox(iLabels);
        	
        	iCombo.addActionListener(this);
        }

        public boolean isCellEditable(EventObject event) {
        	boolean res = super.isCellEditable(event);
        	
        	if (res) {
        		Object obj = editedTree.getLastSelectedPathComponent();
        		res = editedTree.getModel().isLeaf(obj);
        	}
        	
        	return res;
        }

		@Override
		public Object getCellEditorValue() {
			return iCombo.getSelectedIndex();
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
			if (leaf) {
				SpotItem item = (SpotItem) value;
				
				iCombo.setSelectedIndex(item.visible);
			}
	
			return iCombo;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			stopCellEditing();
		}
        
    }
	
	public EditPoints(ArrayList<Spot> points) {
		super();
		
		for (Spot spot : points) {
			spotsModel.add(I18n.get(spot.getCategory()), new SpotItem(spot));
		}
		
		tree = new WebTree(spotsModel);
		TreeCellEditor editor = new PointTreeCellEditor(tree);

		tree.setEditable(true);
		tree.setRootVisible(false);
		tree.setCellEditor(editor);

		MouseListener ml = new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        int row = tree.getRowForLocation(e.getX(), e.getY());

		        if (row != -1) {
		            if (e.getClickCount() == 1) {
				        TreePath path = tree.getPathForLocation(e.getX(), e.getY());

		            	tree.startEditingAtPath(path);
		            }
		        }
		    }
		};
		
		tree.addMouseListener(ml);
		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "startEditing");
	}
}
