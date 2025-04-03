package io.bamboobear.json_editor.component;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

public class Label extends JLabel{
	private static final long serialVersionUID = 1834615599606614377L;

	private TranslatableText text;
	private boolean useCustomFont = false;
	
	public Label()                                               { this(null, null, LEADING); }
	public Label(TranslatableText text)                          { this(text, null, LEADING); }
	public Label(TranslatableText text, int horizontalAlignment) { this(text, null, horizontalAlignment); }
	public Label(Icon icon)                                      { this(null, icon, LEADING); }
	public Label(Icon icon, int horizontalAlignment)             { this(null, icon, horizontalAlignment); }
	
	public Label(TranslatableText text, Icon icon, int horizontalAlignment) {
		super(icon, horizontalAlignment);
		setText(text);
	}
	
	@Override
	public String getText() {
		return (text == null) ? super.getText() : text.getDisplayText();
	}
	
	public void setText(TranslatableText text) {
		this.text = text;
	}
	
	@Override
	public void setFont(Font font) {
		useCustomFont = true;
		super.setFont(font);
	}
	
	@Override
	public Font getFont() {
		return useCustomFont ? super.getFont() : Main.getFont();
	}
}
