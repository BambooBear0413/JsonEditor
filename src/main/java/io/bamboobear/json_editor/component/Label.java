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
	
	public Label() {
	}
	
	public Label(TranslatableText text) {
		setText(text);
	}
	
	public Label(TranslatableText text, int horizontalAlignment) {
		super("", horizontalAlignment);
		setText(text);
	}
	
	public Label(Icon icon) {
		super(icon);
	}
	
	public Label(Icon icon, int horizontalAlignment) {
		super(icon, horizontalAlignment);
	}
	
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
