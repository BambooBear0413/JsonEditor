package io.bamboobear.json_editor.component;

import java.awt.Cursor;

import javax.swing.JMenu;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class Menu extends JMenu{
	private TranslatableText text;
	
	public Menu() { this(null); }
	
	public Menu(TranslatableText text) {
		setText(text);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	@Override public String getText() { return (text == null) ? super.getText() : text.getDisplayText(); }
		
	public void setText(TranslatableText text) { this.text = text; }
}
