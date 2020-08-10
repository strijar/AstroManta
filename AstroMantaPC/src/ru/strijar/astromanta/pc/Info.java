package ru.strijar.astromanta.pc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.*;

/**
 * Панель с информацией (для вывода HTML)
 */

public class Info {
	private JEditorPane		editorPane;
	protected JScrollPane	scrollPane;
	private float			scale = 0.5f;

	private class ScaleImageView extends ImageView {
		RenderingHints iHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		public ScaleImageView(Element elem) {
			super(elem);
		}
				
		public float getPreferredSpan(int axis) {
			return super.getPreferredSpan(axis) * scale;
		}

		public void paint(Graphics g, Shape bounds) {
		     Rectangle		r = bounds instanceof Rectangle ? (Rectangle) bounds : bounds.getBounds();
		     Graphics2D 	g2d = (Graphics2D) g.create();
		     
             g2d.addRenderingHints(iHints);
             
		     g2d.drawImage(getImage(), r.x, r.y, r.width, r.height, null);
		     g2d.dispose();
		  }
	}
	
	@SuppressWarnings("serial")
	private class HTMLKit extends HTMLEditorKit {
		class HTMLFactory extends HTMLEditorKit.HTMLFactory {
			public View create(Element element) {
				if (element.getName().equals("img")) {
					View view = new ScaleImageView(element);
					
					return view;
				} else {
					return super.create(element);
				}
			}
		}
		
		public ViewFactory getViewFactory() {
			return new HTMLFactory();
		}
	};
	
    public Info() {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        
        scrollPane = new JScrollPane(editorPane);
        
        HTMLKit kit = new HTMLKit();
        editorPane.setEditorKit(kit);
        editorPane.setContentType("text/html");
        
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color: black; font-family: monospace; font-size:16pt; }");
    }
    
    /**
     * Вывести текст
     *
     */
    public void out(final String text) {
    	int caretPosition = editorPane.getCaretPosition();
    	
		editorPane.setText(
			String.format("<body style=\"font-family:monospace; font-size:%dpt\">%s</body>", (int) (32*scale), text)
		);
		editorPane.setCaretPosition(Math.min(caretPosition, text.length()));
    }
}
