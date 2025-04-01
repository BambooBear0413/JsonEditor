package io.bamboobear.json_editor.component;

import java.awt.Font;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import io.bamboobear.json_editor.Main;
import io.bamboobear.json_editor.lang.TranslatableText;

public class Button extends JButton{
	private static final long serialVersionUID = 3778328896469590819L;

	private TranslatableText text;
	private boolean useCustomFont;
	
	public Button(){
	}
	
	public Button(Icon icon) {
		super(icon);
	}
	
	public Button(TranslatableText text) {
		setText(text);
	}
	
	public Button(Action action) {
		super(action);
	}
	
	public Button(TranslatableText text, Icon icon) {
		super(icon);
		setText(text);
	}
	
	@Override
	public String getText() {
		return text == null ? super.getText() : text.getDisplayText();
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
