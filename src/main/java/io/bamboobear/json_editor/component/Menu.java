package io.bamboobear.json_editor.component;

import javax.swing.JMenu;

import io.bamboobear.json_editor.lang.TranslatableText;

public class Menu extends JMenu{
	private static final long serialVersionUID = -2466703463330942926L;
	
	private TranslatableText text;
	
	public Menu() { this(null); }
	
	public Menu(TranslatableText text) {
		setText(text);
	}
	
	@Override
	public String getText() {
		return (text == null) ? super.getText() : text.getDisplayText();
	}
		
	public void setText(TranslatableText text) {
		this.text = text;
	}
}
