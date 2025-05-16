package io.bamboobear.json_editor.component;

import java.awt.Cursor;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import io.bamboobear.json_editor.lang.TranslatableText;

@SuppressWarnings("serial")
public class MenuItem extends JMenuItem{
	private TranslatableText text;

	public MenuItem()                      { this(null, null); }
	public MenuItem(TranslatableText text) { this(text, null); }
	public MenuItem(Icon icon)             { this(null, icon); }
	
	public MenuItem(TranslatableText text, Icon icon) {
		super(icon);
		setText(text);
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	@Override public String getText() { return (text == null) ? super.getText() : text.getDisplayText(); }
	
	public void setText(TranslatableText text) { this.text = text; }
}
